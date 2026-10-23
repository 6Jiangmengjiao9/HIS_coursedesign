package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.Reservation;
import com.liuzhen._02301020417.security.LoginUser;

import java.util.List;

public interface RegistrationService {
    Reservation register(Reservation reservation, LoginUser operator);

    List<Reservation> listByDoctor(Integer doctorId, Integer registerStatus);

    List<Reservation> listMine(LoginUser operator, Integer registerStatus);

    List<Reservation> listMineForPatient(LoginUser operator);
}
