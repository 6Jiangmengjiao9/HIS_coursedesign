package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.Drug;
import com.liuzhen._02301020417.service.DrugService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class DrugController {

    @Autowired
    DrugService drugService;

    /**
     * 查询所有药�?     */
    @RequestMapping("/getAllDrug")
    public Result getAllDrug() {
        List<Drug> result = drugService.getAllDrug();
        return Result.ok().put("data", result);
    }

    /**
     * 药品信息分页查询
     * @param pageNum  当前页码（从1开始）
     * @param pageSize 每页数量
     */
    @RequestMapping("/getDrugPage")
    public Result getDrugPage(Integer pageNum, Integer pageSize){
        List<Drug> list = drugService.getDrugPage(pageNum, pageSize);
        int total = drugService.getDrugCount();
        return Result.ok()
                .put("data", list)
                .put("total", total)
                .put("pageNum", pageNum == null ? 1 : pageNum)
                .put("pageSize", pageSize == null ? 10 : pageSize);
    }

    /**
     * 根据药品ID查询
     */
    @RequestMapping("/getDrugById")
    public Result getDrugById(Integer drugId) {
        Drug drug = drugService.getDrugById(drugId);
        if (drug != null) {
            return Result.ok().put("data", drug);
        }
        return Result.error().put("msg", "未找到该药品");
    }

    /**
     * 新增药品
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addDrug")
    public Result addDrug(@RequestBody Drug drug) {
        int flag = drugService.addDrug(drug);
        if (flag > 0) {
            return Result.ok().put("msg", "添加成功");
        } else {
            return Result.error().put("msg", "添加失败");
    }
    }

    /**
     * 更新药品
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateDrug")
    public Result updateDrug(@RequestBody Drug drug) {
        int flag = drugService.updateDrug(drug);
        if (flag > 0) {
            return Result.ok().put("msg", "更新成功");
        } else {
            return Result.error().put("msg", "更新失败");
        }
    }

    /**
     * 删除药品
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deleteDrug")
    public Result deleteDrug(Integer drugId) {
        int flag = drugService.deleteDrug(drugId);
        if (flag > 0) {
            return Result.ok().put("msg", "删除成功");
        } else {
            return Result.error().put("msg", "删除失败");
        }
    }
}