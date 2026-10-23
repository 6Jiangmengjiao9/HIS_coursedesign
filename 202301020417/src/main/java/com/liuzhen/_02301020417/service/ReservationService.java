package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.Reservation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReservationService {
    List<Reservation> getAllReservation();

    // 分页查询预约列表
    List<Reservation> getReservationPage(Integer pageNum, Integer pageSize);

    // 获取预约总数（用于分页）
    int getReservationCount();

    int addReservation(Reservation reservation);
    int deleteReservation(Integer reservationId);
    int updateReservation(Reservation reservation);
}
