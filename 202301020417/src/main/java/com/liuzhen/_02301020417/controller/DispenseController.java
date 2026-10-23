package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.Prescription;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.DispenseService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class DispenseController {

    private final DispenseService dispenseService;

    public DispenseController(DispenseService dispenseService) {
        this.dispenseService = dispenseService;
    }

    @PreAuthorize("hasRole('PHARMACIST')")
    @PostMapping("/api/dispenses/{prescriptionId}")
    public Result dispense(@PathVariable Integer prescriptionId,
                           @AuthenticationPrincipal LoginUser pharmacist) {
        Prescription prescription = dispenseService.dispense(prescriptionId, pharmacist);
        return Result.ok().put("msg", "发药成功").put("data", prescription);
    }
}
