package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DrugMapper {

    // 查询所有药品
    List<Drug> getAllDrug();

    // 分页查询药品列表
    List<Drug> getDrugPage(Integer offset, Integer pageSize);

    // 药品总数（用于分页）
    int getDrugCount();

    // 根据药品ID查询
    Drug getDrugById(Integer drugId);

    // 新增药品
    int addDrug(Drug drug);

    // 更新药品
    int updateDrug(Drug drug);

    // 删除药品
    int deleteDrug(Integer drugId);

    Drug getDrugForUpdate(Integer drugId);

    int decreaseStock(@Param("drugId") Integer drugId, @Param("quantity") Integer quantity);
}