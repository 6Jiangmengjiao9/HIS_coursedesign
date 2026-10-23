package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {
    public List<Reservation> getAllReservation();

    // 分页查询预约列表
    public List<Reservation> getReservationPage(Integer offset, Integer pageSize);

    // 预约总数（用于分页）
    public int getReservationCount();

    public int addReservation(Reservation reservation);
    public int deleteReservation(Integer reservationId);
    public int updateReservation(Reservation reservation);

    Reservation getReservationById(Integer id);

    Reservation getReservationForUpdate(Integer id);

    List<Reservation> getReservationByDoctorIdAndStatus(@Param("doctorId") Integer doctorId,
                                                        @Param("registerStatus") Integer registerStatus);

    List<Reservation> getReservationByPatientId(Integer patientId);

    int countByPatientAndSchedule(@Param("patientId") Integer patientId,
                                  @Param("scheduleId") Integer scheduleId);

    int updateRegisterStatus(@Param("id") Integer id, @Param("registerStatus") Integer registerStatus);

    int updateVisitStatus(@Param("id") Integer id, @Param("visitStatus") Integer visitStatus);
}
