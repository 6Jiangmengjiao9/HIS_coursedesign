package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.Prescription;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.PrescriptionService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class PrescriptionController {

    @Autowired
    PrescriptionService prescriptionService;

    @RequestMapping("/getAllPrescription")
    public Result getAllPrescription() {
        List<Prescription> result = prescriptionService.getAllPrescription();
        return Result.ok().put("data", result);
    }

    @RequestMapping("/getPrescriptionPage")
    public Result getPrescriptionPage(Integer pageNum, Integer pageSize) {
        List<Prescription> list = prescriptionService.getPrescriptionPage(pageNum, pageSize);
        int total = prescriptionService.getPrescriptionCount();
        return Result.ok()
                .put("data", list)
                .put("total", total)
                .put("pageNum", pageNum == null ? 1 : pageNum)
                .put("pageSize", pageSize == null ? 10 : pageSize);
    }

    @RequestMapping("/getPrescriptionById")
    public Result getPrescriptionById(Integer prescriptionId) {
        Prescription prescription = prescriptionService.getDetail(prescriptionId);
        if (prescription != null) {
            return Result.ok().put("data", prescription);
        }
        return Result.error().put("msg", "未找到该处方");
    }

    @GetMapping("/api/prescriptions/{prescriptionId}")
    public Result getPrescriptionDetail(@PathVariable Integer prescriptionId) {
        Prescription prescription = prescriptionService.getDetail(prescriptionId);
        if (prescription == null) {
            return Result.error(404, "未找到该处方");
        }
        return Result.ok().put("data", prescription);
    }

    @RequestMapping("/getPrescriptionByPatientId")
    public Result getPrescriptionByPatientId(Integer patientId) {
        List<Prescription> result = prescriptionService.getPrescriptionByPatientId(patientId);
        return Result.ok().put("data", result);
    }

    @RequestMapping("/getPrescriptionByDoctorId")
    public Result getPrescriptionByDoctorId(Integer doctorId) {
        List<Prescription> result = prescriptionService.getPrescriptionByDoctorId(doctorId);
        return Result.ok().put("data", result);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @RequestMapping("/addPrescription")
    public Result addPrescription(@RequestBody Prescription prescription,
                                  @AuthenticationPrincipal LoginUser operator) {
        Prescription created = prescriptionService.createPrescription(prescription, operator);
        return Result.ok().put("msg", "添加成功").put("data", created);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @RequestMapping("/updatePrescription")
    public Result updatePrescription(@RequestBody Prescription prescription) {
        boolean flag = prescriptionService.updatePrescription(prescription);
        if (flag) {
            return Result.ok().put("msg", "更新成功");
        }
        return Result.error().put("msg", "更新失败");
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @RequestMapping("/deletePrescription")
    public Result deletePrescription(Integer prescriptionId) {
        boolean flag = prescriptionService.deletePrescription(prescriptionId);
        if (flag) {
            return Result.ok().put("msg", "删除成功");
        }
        return Result.error().put("msg", "删除失败");
    }

    @PreAuthorize("hasRole('CASHIER')")
    @GetMapping("/api/prescriptions/pending")
    public Result getPendingPrescriptions() {
        return Result.ok().put("data", prescriptionService.getByStatus(0));
    }

    @PreAuthorize("hasRole('PHARMACIST')")
    @GetMapping("/api/prescriptions/paid")
    public Result getPaidPrescriptions() {
        return Result.ok().put("data", prescriptionService.getByStatus(1));
    }
}
