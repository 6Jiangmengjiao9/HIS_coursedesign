package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.ChargeRecord;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.ChargeService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ChargeController {

    private final ChargeService chargeService;

    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @PreAuthorize("hasRole('CASHIER')")
    @PostMapping("/api/charges/{prescriptionId}")
    public Result charge(@PathVariable Integer prescriptionId,
                         @AuthenticationPrincipal LoginUser cashier) {
        ChargeRecord record = chargeService.charge(prescriptionId, cashier);
        return Result.ok().put("msg", "收费成功").put("data", record);
    }
}
