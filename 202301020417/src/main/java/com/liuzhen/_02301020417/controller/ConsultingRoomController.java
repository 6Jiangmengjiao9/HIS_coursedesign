package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.ConsultingRoom;
import com.liuzhen._02301020417.service.ConsultingRoomService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class ConsultingRoomController {

    @Autowired
    ConsultingRoomService consultingRoomService;

    /**
     * 查询所有诊�?
     */
    @RequestMapping("/getAllConsultingRoom")
    public Result getAllConsultingRoom() {
        List<ConsultingRoom> result = consultingRoomService.getAllConsultingRoom();
        return Result.ok().put("data", result);
    }

    /**
     * 根据诊室ID查询
     */
    @RequestMapping("/getConsultingRoomById")
    public Result getConsultingRoomById(Integer roomId) {
        ConsultingRoom room = consultingRoomService.getConsultingRoomById(roomId);
        if (room != null) {
            return Result.ok().put("data", room);
        }
        return Result.error().put("msg", "未找到该诊室");
    }

    /**
     * 根据科室ID查询诊室列表
     */
    @RequestMapping("/getConsultingRoomByDepartmentId")
    public Result getConsultingRoomByDepartmentId(String departmentId) {
        List<ConsultingRoom> result = consultingRoomService.getConsultingRoomByDepartmentId(departmentId);
        return Result.ok().put("data", result);
    }

    /**
     * 新增诊室
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addConsultingRoom")
    public Result addConsultingRoom(@RequestBody ConsultingRoom room) {
        int flag = consultingRoomService.addConsultingRoom(room);
        if (flag > 0) {
            return Result.ok().put("msg", "添加诊室成功");
        }
        return Result.error().put("msg", "添加诊室失败");
    }

    /**
     * 更新诊室
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateConsultingRoom")
    public Result updateConsultingRoom(@RequestBody ConsultingRoom room) {
        int flag = consultingRoomService.updateConsultingRoom(room);
        if (flag > 0) {
            return Result.ok().put("msg", "更新诊室成功");
        }
        return Result.error().put("msg", "更新诊室失败");
    }

    /**
     * 删除诊室
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deleteConsultingRoom")
    public Result deleteConsultingRoom(Integer roomId) {
        int flag = consultingRoomService.deleteConsultingRoom(roomId);
        if (flag > 0) {
            return Result.ok().put("msg", "删除诊室成功");
        }
        return Result.error().put("msg", "删除诊室失败");
    }
}


