package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.exception.BusinessException;
import com.liuzhen._02301020417.mapper.ChargeRecordMapper;
import com.liuzhen._02301020417.mapper.DrugMapper;
import com.liuzhen._02301020417.mapper.PrescriptionItemMapper;
import com.liuzhen._02301020417.mapper.PrescriptionMapper;
import com.liuzhen._02301020417.model.ChargeRecord;
import com.liuzhen._02301020417.model.Drug;
import com.liuzhen._02301020417.model.Prescription;
import com.liuzhen._02301020417.model.PrescriptionItem;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.AuditLogService;
import com.liuzhen._02301020417.service.ChargeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class ChargeServiceImpl implements ChargeService {

    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final DrugMapper drugMapper;
    private final ChargeRecordMapper chargeRecordMapper;
    private final AuditLogService auditLogService;

    public ChargeServiceImpl(PrescriptionMapper prescriptionMapper,
                             PrescriptionItemMapper prescriptionItemMapper,
                             DrugMapper drugMapper,
                             ChargeRecordMapper chargeRecordMapper,
                             AuditLogService auditLogService) {
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionItemMapper = prescriptionItemMapper;
        this.drugMapper = drugMapper;
        this.chargeRecordMapper = chargeRecordMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChargeRecord charge(Integer prescriptionId, LoginUser cashier) {
        Prescription prescription = prescriptionMapper.getPrescriptionForUpdate(prescriptionId);
        if (prescription == null) {
            throw new BusinessException("处方不存在");
        }
        if (prescription.getStatus() == null || prescription.getStatus() != 0) {
            throw new BusinessException("该处方不是待收费状态");
        }

        List<PrescriptionItem> items = prescriptionItemMapper.getByPrescriptionId(prescriptionId);
        if (items.isEmpty()) {
            throw new BusinessException("处方没有药品明细");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (PrescriptionItem item : items) {
            Drug drug = drugMapper.getDrugForUpdate(item.getMedicineId());
            if (drug == null) {
                throw new BusinessException("药品不存在，无法收费");
            }
            if (drug.getStockQuantity() == null || drug.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException("库存不足：" + drug.getDrugName()
                        + "，剩余 " + (drug.getStockQuantity() == null ? 0 : drug.getStockQuantity()));
            }
            if (drugMapper.decreaseStock(drug.getDrugId(), item.getQuantity()) == 0) {
                throw new BusinessException("库存不足：" + drug.getDrugName());
            }
            BigDecimal price = drug.getUnitPrice() == null ? BigDecimal.ZERO : drug.getUnitPrice();
            total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        total = total.setScale(2, RoundingMode.HALF_UP);

        ChargeRecord record = new ChargeRecord();
        record.setPrescriptionId(prescriptionId);
        record.setChargeTime(new Date());
        record.setAmount(total);
        record.setCashierId(cashier.getId());
        chargeRecordMapper.addChargeRecord(record);

        prescriptionMapper.updateTotalFeeAndStatus(prescriptionId, total, 1);
        auditLogService.record(cashier, "CHARGE",
                "处方ID=" + prescriptionId + "，收费金额=" + total);
        return record;
    }
}
