package com.liuzhen._02301020417.model;

import lombok.Data;

import java.util.Date;

@Data
public class Case {
    private Integer recordId;
    private Integer patientId;
    private String chiefComplaint;
    private String presentIllness;
    private String pastHistory;
    private String physicalExam;
    private String auxiliaryExam;
    private String diagnosis;
    private Date entryTime;
}