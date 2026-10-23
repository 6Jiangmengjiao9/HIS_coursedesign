package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.exception.BusinessException;
import com.liuzhen._02301020417.mapper.PrescriptionItemMapper;
import com.liuzhen._02301020417.mapper.PrescriptionMapper;
import com.liuzhen._02301020417.mapper.ReservationMapper;
import com.liuzhen._02301020417.model.Prescription;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.AuditLogService;
import com.liuzhen._02301020417.service.DispenseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispenseServiceImpl implements DispenseService {

    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final ReservationMapper reservationMapper;
    private final AuditLogService auditLogService;

    public DispenseServiceImpl(PrescriptionMapper prescriptionMapper,
                               PrescriptionItemMapper prescriptionItemMapper,
                               ReservationMapper reservationMapper,
                               AuditLogService auditLogService) {
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionItemMapper = prescriptionItemMapper;
        this.reservationMapper = reservationMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Prescription dispense(Integer prescriptionId, LoginUser pharmacist) {
        Prescription prescription = prescriptionMapper.getPrescriptionForUpdate(prescriptionId);
        if (prescription == null) {
            throw new BusinessException("处方不存在");
        }
        if (prescription.getStatus() == null || prescription.getStatus() != 1) {
            throw new BusinessException("只能发放已收费的处方");
        }

        prescriptionMapper.updatePrescriptionStatus(prescriptionId, 2);
        if (prescription.getRegisterId() != null) {
            reservationMapper.updateRegisterStatus(prescription.getRegisterId(), 2);
        }
        auditLogService.record(pharmacist, "DISPENSE",
                "处方ID=" + prescriptionId + "，发药完成");

        prescription.setStatus(2);
        prescription.setItems(prescriptionItemMapper.getByPrescriptionId(prescriptionId));
        return prescription;
    }
}
