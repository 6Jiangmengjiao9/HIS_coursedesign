package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.exception.BusinessException;
import com.liuzhen._02301020417.mapper.DoctorMapper;
import com.liuzhen._02301020417.mapper.DrugMapper;
import com.liuzhen._02301020417.mapper.PrescriptionItemMapper;
import com.liuzhen._02301020417.mapper.PrescriptionMapper;
import com.liuzhen._02301020417.mapper.ReservationMapper;
import com.liuzhen._02301020417.model.Doctor;
import com.liuzhen._02301020417.model.Drug;
import com.liuzhen._02301020417.model.Prescription;
import com.liuzhen._02301020417.model.PrescriptionItem;
import com.liuzhen._02301020417.model.Reservation;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.AuditLogService;
import com.liuzhen._02301020417.service.PrescriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final DrugMapper drugMapper;
    private final DoctorMapper doctorMapper;
    private final ReservationMapper reservationMapper;
    private final AuditLogService auditLogService;

    public PrescriptionServiceImpl(PrescriptionMapper prescriptionMapper,
                                   PrescriptionItemMapper prescriptionItemMapper,
                                   DrugMapper drugMapper,
                                   DoctorMapper doctorMapper,
                                   ReservationMapper reservationMapper,
                                   AuditLogService auditLogService) {
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionItemMapper = prescriptionItemMapper;
        this.drugMapper = drugMapper;
        this.doctorMapper = doctorMapper;
        this.reservationMapper = reservationMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    public List<Prescription> getAllPrescription() {
        return prescriptionMapper.getAllPrescription();
    }

    @Override
    public List<Prescription> getPrescriptionPage(Integer pageNum, Integer pageSize) {
        int page = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return prescriptionMapper.getPrescriptionPage((page - 1) * size, size);
    }

    @Override
    public int getPrescriptionCount() {
        return prescriptionMapper.getPrescriptionCount();
    }

    @Override
    public Prescription getPrescriptionById(Integer prescriptionId) {
        return prescriptionMapper.getPrescriptionById(prescriptionId);
    }

    @Override
    public List<Prescription> getPrescriptionByPatientId(Integer patientId) {
        return prescriptionMapper.getPrescriptionByPatientId(patientId);
    }

    @Override
    public List<Prescription> getPrescriptionByDoctorId(Integer doctorId) {
        return prescriptionMapper.getPrescriptionByDoctorId(doctorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPrescription(Prescription prescription) {
        if (prescription.getPrescriptionDate() == null) {
            prescription.setPrescriptionDate(new Date());
        }
        prescription.setStatus(0);
        prescription.setTotalFee(BigDecimal.ZERO);
        return prescriptionMapper.addPrescription(prescription) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePrescription(Prescription prescription) {
        Prescription existing = prescriptionMapper.getPrescriptionForUpdate(prescription.getPrescriptionId());
        if (existing == null) {
            throw new BusinessException("处方不存在");
        }
        if (existing.getStatus() != null && existing.getStatus() != 0) {
            throw new BusinessException("已收费或已发药的处方不能修改");
        }

        prescription.setStatus(existing.getStatus());
        prescription.setRegisterId(existing.getRegisterId());
        prescription.setPatientId(existing.getPatientId());
        prescription.setDoctorId(existing.getDoctorId());
        if (prescription.getPrescriptionDate() == null) {
            prescription.setPrescriptionDate(existing.getPrescriptionDate());
        }

        if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
            List<PrescriptionItem> items = buildItems(prescription.getItems());
            prescription.setTotalFee(calculateTotal(items));
            prescription.setDrugInfo(summarize(items));
            prescriptionItemMapper.deleteByPrescriptionId(prescription.getPrescriptionId());
            for (PrescriptionItem item : items) {
                item.setPrescriptionId(prescription.getPrescriptionId());
            }
            prescriptionItemMapper.batchInsert(items);
        } else {
            prescription.setTotalFee(existing.getTotalFee());
            prescription.setDrugInfo(prescription.getDrugInfo() == null
                    ? existing.getDrugInfo() : prescription.getDrugInfo());
        }
        return prescriptionMapper.updatePrescription(prescription) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePrescription(Integer prescriptionId) {
        Prescription existing = prescriptionMapper.getPrescriptionForUpdate(prescriptionId);
        if (existing == null) {
            throw new BusinessException("处方不存在");
        }
        if (existing.getStatus() != null && existing.getStatus() != 0) {
            throw new BusinessException("已收费或已发药的处方不能删除");
        }
        prescriptionItemMapper.deleteByPrescriptionId(prescriptionId);
        return prescriptionMapper.deletePrescription(prescriptionId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Prescription createPrescription(Prescription request, LoginUser operator) {
        if (request == null || request.getRegisterId() == null) {
            throw new BusinessException("请选择挂号单");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("处方至少包含一种药品");
        }

        Integer doctorId = resolveDoctorId(request, operator);
        Reservation register = reservationMapper.getReservationForUpdate(request.getRegisterId());
        if (register == null) {
            throw new BusinessException("挂号单不存在");
        }
        if (!doctorId.equals(register.getDoctorId())) {
            throw new BusinessException(403, "只能为自己接诊的挂号单开方");
        }
        if (register.getRegisterStatus() != null && register.getRegisterStatus() != 0) {
            throw new BusinessException("该挂号单已开方或已完成");
        }

        List<PrescriptionItem> items = buildItems(request.getItems());
        BigDecimal totalFee = calculateTotal(items);

        Prescription prescription = new Prescription();
        prescription.setRegisterId(register.getId());
        prescription.setPatientId(register.getPatientId());
        prescription.setDoctorId(doctorId);
        prescription.setPrescriptionDate(new Date());
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setDrugInfo(summarize(items));
        prescription.setTotalFee(totalFee);
        prescription.setStatus(0);
        prescriptionMapper.addPrescription(prescription);

        for (PrescriptionItem item : items) {
            item.setPrescriptionId(prescription.getPrescriptionId());
        }
        prescriptionItemMapper.batchInsert(items);
        reservationMapper.updateRegisterStatus(register.getId(), 1);

        auditLogService.record(operator, "PRESCRIBE",
                "处方ID=" + prescription.getPrescriptionId() + "，挂号单ID=" + register.getId()
                        + "，金额=" + totalFee);

        prescription.setItems(items);
        return prescription;
    }

    @Override
    public Prescription getDetail(Integer prescriptionId) {
        Prescription prescription = prescriptionMapper.getPrescriptionById(prescriptionId);
        if (prescription != null) {
            prescription.setItems(prescriptionItemMapper.getByPrescriptionId(prescriptionId));
        }
        return prescription;
    }

    @Override
    public List<Prescription> getByStatus(Integer status) {
        List<Prescription> prescriptions = prescriptionMapper.getPrescriptionByStatus(status);
        for (Prescription prescription : prescriptions) {
            prescription.setItems(prescriptionItemMapper.getByPrescriptionId(prescription.getPrescriptionId()));
        }
        return prescriptions;
    }

    private Integer resolveDoctorId(Prescription request, LoginUser operator) {
        if (operator != null && "doctor".equalsIgnoreCase(operator.getRole())) {
            Doctor doctor = doctorMapper.getDoctorByUserId(operator.getId());
            if (doctor == null) {
                throw new BusinessException(403, "当前账号未绑定医生信息");
            }
            return doctor.getDoctorId();
        }
        if (request.getDoctorId() != null) {
            return request.getDoctorId();
        }
        throw new BusinessException("无法确定开方医生");
    }

    private List<PrescriptionItem> buildItems(List<PrescriptionItem> requests) {
        List<PrescriptionItem> items = new ArrayList<>();
        for (PrescriptionItem request : requests) {
            if (request.getMedicineId() == null) {
                throw new BusinessException("药品不能为空");
            }
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                throw new BusinessException("药品数量必须大于 0");
            }
            Drug drug = drugMapper.getDrugForUpdate(request.getMedicineId());
            if (drug == null) {
                throw new BusinessException("药品不存在");
            }
            if (drug.getStatus() == null || drug.getStatus() != 1) {
                throw new BusinessException("药品已下架：" + drug.getDrugName());
            }
            if (drug.getUnitPrice() == null) {
                throw new BusinessException("药品未设置单价：" + drug.getDrugName());
            }
            PrescriptionItem item = new PrescriptionItem();
            item.setMedicineId(drug.getDrugId());
            item.setQuantity(request.getQuantity());
            item.setUnitPrice(drug.getUnitPrice());
            item.setMedicineName(drug.getDrugName());
            item.setSpecification(drug.getSpecification());
            item.setDosageForm(drug.getDosageForm());
            items.add(item);
        }
        return items;
    }

    private BigDecimal calculateTotal(List<PrescriptionItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (PrescriptionItem item : items) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private String summarize(List<PrescriptionItem> items) {
        List<String> parts = new ArrayList<>();
        for (PrescriptionItem item : items) {
            StringBuilder part = new StringBuilder(item.getMedicineName());
            if (item.getSpecification() != null && !item.getSpecification().isBlank()) {
                part.append(" ").append(item.getSpecification());
            }
            if (item.getDosageForm() != null && !item.getDosageForm().isBlank()) {
                part.append(" ").append(item.getDosageForm());
            }
            part.append(" x").append(item.getQuantity());
            parts.add(part.toString());
        }
        return String.join("；", parts);
    }
}
