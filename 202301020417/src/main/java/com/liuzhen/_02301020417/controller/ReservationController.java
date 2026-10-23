package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.Reservation;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.RegistrationService;
import com.liuzhen._02301020417.service.ReservationService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @Autowired
    RegistrationService registrationService;

    @RequestMapping("/getAllReservation")
    public Result getAllReservation() {
        List<Reservation> result = reservationService.getAllReservation();
        return Result.ok().put("data", result);
    }

    @RequestMapping("/getReservationPage")
    public Result getReservationPage(Integer pageNum, Integer pageSize) {
        List<Reservation> list = reservationService.getReservationPage(pageNum, pageSize);
        int total = reservationService.getReservationCount();
        return Result.ok()
                .put("data", list)
                .put("total", total)
                .put("pageNum", pageNum == null ? 1 : pageNum)
                .put("pageSize", pageSize == null ? 10 : pageSize);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','CASHIER')")
    @RequestMapping(value = "/addReservation", method = RequestMethod.POST)
    public Result addReservation(@RequestBody(required = false) Reservation reservation,
                                 @AuthenticationPrincipal LoginUser operator) {
        if (reservation == null) {
            return Result.error().put("msg", "请求数据不能为空");
        }
        if (reservation.getReservationTime() == null) {
            reservation.setReservationTime(new Date());
        }
        Reservation created = registrationService.register(reservation, operator);
        return Result.ok().put("msg", "挂号成功").put("data", created);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','CASHIER')")
    @RequestMapping(value = "/api/register-records", method = RequestMethod.POST)
    public Result createRegisterRecord(@RequestBody Reservation reservation,
                                       @AuthenticationPrincipal LoginUser operator) {
        Reservation created = registrationService.register(reservation, operator);
        return Result.ok().put("msg", "挂号成功").put("data", created);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/api/register-records/mine")
    public Result myRegisterRecords(Integer registerStatus,
                                    @AuthenticationPrincipal LoginUser operator) {
        int status = registerStatus == null ? 0 : registerStatus;
        return Result.ok().put("data", registrationService.listMine(operator, status));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @RequestMapping(value = "/api/register-records/{id}/visit", method = RequestMethod.POST)
    public Result markVisited(@PathVariable Integer id,
                              @AuthenticationPrincipal LoginUser operator) {
        return Result.ok().put("msg", "已标记为已就诊")
                .put("data", registrationService.markVisited(id, operator));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/api/register-records/my")
    public Result myPatientRegisterRecords(@AuthenticationPrincipal LoginUser operator) {
        return Result.ok().put("data", registrationService.listMineForPatient(operator));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deleteReservation")
    public Result deleteReservation(Integer id) {
        int flag = reservationService.deleteReservation(id);
        if (flag > 0) {
            return Result.ok().put("msg", "删除成功");
        }
        return Result.error().put("msg", "删除失败");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/updateReservation")
    public Result updateReservation(@RequestBody Reservation reservation) {
        int flag = reservationService.updateReservation(reservation);
        if (flag > 0) {
            return Result.ok().put("msg", "更新成功");
        }
        return Result.error().put("msg", "更新失败");
    }
}
