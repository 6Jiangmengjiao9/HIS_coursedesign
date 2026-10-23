package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.DrugMapper;
import com.liuzhen._02301020417.model.Drug;
import com.liuzhen._02301020417.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DrugServiceImpl implements DrugService {

    @Autowired
    DrugMapper drugMapper;

    @Override
    public List<Drug> getAllDrug() {
        return drugMapper.getAllDrug();
    }

    @Override
    public List<Drug> getDrugPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        return drugMapper.getDrugPage(offset, pageSize);
    }

    @Override
    public int getDrugCount() {
        return drugMapper.getDrugCount();
    }

    @Override
    public Drug getDrugById(Integer drugId) {
        return drugMapper.getDrugById(drugId);
    }

    @Transactional
    @Override
    public int addDrug(Drug drug) {
        // 默认在售
        if (drug.getStatus() == null) {
            drug.setStatus(1);
        }
        return drugMapper.addDrug(drug);
    }

    @Transactional
    @Override
    public int updateDrug(Drug drug) {
        return drugMapper.updateDrug(drug);
    }

    @Transactional
    @Override
    public int deleteDrug(Integer drugId) {
        return drugMapper.deleteDrug(drugId);
    }
}