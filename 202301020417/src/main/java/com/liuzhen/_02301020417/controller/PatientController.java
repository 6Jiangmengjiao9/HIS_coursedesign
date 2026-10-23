package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.Case;
import com.liuzhen._02301020417.model.Patient;
import com.liuzhen._02301020417.service.PatientService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class PatientController {
    @Autowired
    PatientService patientService;

    @RequestMapping("/getAllPatient")
    public Result patientList(){
        List<Patient> result = patientService.getAllPatient();
        return Result.ok().put("data",result);
    }

    /**
     * 患者信息分页查询
     * @param pageNum  当前页码（从1开始）
     * @param pageSize 每页数量
     */
    @RequestMapping("/getPatientPage")
    public Result getPatientPage(Integer pageNum, Integer pageSize){
        List<Patient> list = patientService.getPatientPage(pageNum, pageSize);
        int total = patientService.getPatientCount();
        return Result.ok()
                .put("data", list)
                .put("total", total)
                .put("pageNum", pageNum == null ? 1 : pageNum)
                .put("pageSize", pageSize == null ? 10 : pageSize);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/addPatient")
    public Result addPatient(@RequestBody Patient patient){


        boolean flag = patientService.register(patient);
        if(flag){
            return Result.ok().put("msg","添加成功");
        }else{
            return Result.error().put("msg","添加失败");
        }
    }
    @RequestMapping("/updatePatient")
    public Result updatePatient(@RequestBody Patient patient){
        boolean flag = patientService.updatePatientAndCase(patient);
        if(flag){
            return Result.ok().put("msg","更新成功");
        }else{
            return Result.error().put("msg","更新失败");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deletePatient")
    public Result deletePatient(Integer patientId){
        boolean flag = patientService.deletePatientAndCase(patientId);
        if(flag){
            return Result.ok().put("msg","删除成功");
        }else{
            return Result.error().put("msg","删除失败");
        }
    }
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @RequestMapping("/addMedicalRecordByPatientId")
    public Result addMedicalRecordByPatientId(@RequestBody Case caseInfo){
        int affected = patientService.addMedicalRecordByPatientId(caseInfo);
        if (affected > 0) {
            return Result.ok().put("msg","新增病历成功").put("data",affected);
        }
        return Result.error().put("msg","新增病历失败");
    }

    /**
     * 根据患者ID查询该患者所有病历
     */
    @RequestMapping("/getCasesByPatientId")
    public Result getCasesByPatientId(Integer patientId){
        List<Case> list = patientService.getCasesByPatientId(patientId);
        return Result.ok().put("data", list);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @RequestMapping("/updateMedicalRecordByPatientId")
    public Result updateMedicalRecordByPatientId(@RequestBody Case caseInfo){
        int affected = patientService.updateMedicalRecordByPatientId(caseInfo);
        if (affected > 0) {
            return Result.ok().put("msg","更新病历成功").put("data",affected);
        }
        return Result.error().put("msg","更新病历失败");
    }
}
