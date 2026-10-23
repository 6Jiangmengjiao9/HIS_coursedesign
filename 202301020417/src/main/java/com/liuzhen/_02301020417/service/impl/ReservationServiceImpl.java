package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.ReservationMapper;
import com.liuzhen._02301020417.model.Reservation;
import com.liuzhen._02301020417.service.ReservationService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    ReservationMapper reservationMapper;
    @Override
    public List<Reservation> getAllReservation() {
        return reservationMapper.getAllReservation();
    }

    @Override
    public List<Reservation> getReservationPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        return reservationMapper.getReservationPage(offset, pageSize);
    }

    @Override
    public int getReservationCount() {
        return reservationMapper.getReservationCount();
    }
    @Override
    public int addReservation(Reservation reservation) {
        return reservationMapper.addReservation(reservation);
    }
    @Override
    public int deleteReservation(Integer reservationId) {
        return reservationMapper.deleteReservation(reservationId);
    }
    @Override
    public int updateReservation(Reservation reservation) {
        return reservationMapper.updateReservation(reservation);
    }
}
