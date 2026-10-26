package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.exception.BusinessException;
import com.liuzhen._02301020417.mapper.DoctorMapper;
import com.liuzhen._02301020417.mapper.DoctorScheduleMapper;
import com.liuzhen._02301020417.mapper.PatientMapper;
import com.liuzhen._02301020417.mapper.ReservationMapper;
import com.liuzhen._02301020417.mapper.UserMapper;
import com.liuzhen._02301020417.model.Doctor;
import com.liuzhen._02301020417.model.DoctorSchedule;
import com.liuzhen._02301020417.model.Patient;
import com.liuzhen._02301020417.model.Reservation;
import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.AuditLogService;
import com.liuzhen._02301020417.service.RegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final ReservationMapper reservationMapper;
    private final DoctorScheduleMapper doctorScheduleMapper;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    public RegistrationServiceImpl(ReservationMapper reservationMapper,
                                   DoctorScheduleMapper doctorScheduleMapper,
                                   DoctorMapper doctorMapper,
                                   PatientMapper patientMapper,
                                   UserMapper userMapper,
                                   AuditLogService auditLogService) {
        this.reservationMapper = reservationMapper;
        this.doctorScheduleMapper = doctorScheduleMapper;
        this.doctorMapper = doctorMapper;
        this.patientMapper = patientMapper;
        this.userMapper = userMapper;
        this.auditLogService = auditLogService;
    }

    //挂号事务入口
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation register(Reservation reservation, LoginUser operator) {
        if (reservation.getDoctorId() == null) {
            throw new BusinessException("请选择医生");
        }

        if (reservation.getPatientId() == null
                && operator != null && "patient".equalsIgnoreCase(operator.getRole())) {
            throw new BusinessException("未找到患者档案，请联系管理员");
        }
        if (reservation.getPatientId() != null) {
            Patient patient = patientMapper.getPatientById(reservation.getPatientId());
            if (patient == null) {
                throw new BusinessException("患者档案不存在");
            }
            reservation.setPatientName(patient.getName());
            reservation.setPatientPhone(patient.getContact());
            if (patient.getAge() != null) {
                reservation.setPatientAge(patient.getAge());
            }
        }
        if (reservation.getPatientName() == null || reservation.getPatientName().isBlank()) {
            throw new BusinessException("请填写患者姓名");
        }
        if (reservation.getPatientPhone() == null || reservation.getPatientPhone().isBlank()) {
            throw new BusinessException("请填写联系电话");
        }
        if (reservation.getPatientAge() == null) {
            throw new BusinessException("请填写患者年龄");
        }

        DoctorSchedule schedule;
        if (reservation.getScheduleId() != null) {
            schedule = doctorScheduleMapper.getScheduleForUpdate(reservation.getScheduleId());
            if (schedule == null) {
                throw new BusinessException("排班不存在");
            }
            if (!reservation.getDoctorId().equals(schedule.getDoctorId())) {
                throw new BusinessException("排班与医生不匹配");
            }
        } else {
            LocalDate preferredDate = LocalDate.now();
            if (reservation.getReservationTime() != null) {
                preferredDate = new java.sql.Date(reservation.getReservationTime().getTime()).toLocalDate();
                if (preferredDate.isBefore(LocalDate.now())) {
                    preferredDate = LocalDate.now();
                }
            }
            schedule = doctorScheduleMapper.findAvailableScheduleForUpdateByDate(
                    reservation.getDoctorId(), preferredDate);
            if (schedule == null) {
                schedule = doctorScheduleMapper.findAvailableScheduleForUpdate(reservation.getDoctorId());
            }
            if (schedule == null) {
                throw new BusinessException("该医生暂无可用号源");
            }
        }

        if (schedule.getStatus() == null || schedule.getStatus() != 1) {
            throw new BusinessException("该排班已停诊");
        }
        if (schedule.getRemainingSlots() == null || schedule.getRemainingSlots() <= 0) {
            throw new BusinessException("号源不足");
        }
        if (schedule.getScheduleDate() == null
                || new java.sql.Date(schedule.getScheduleDate().getTime()).toLocalDate().isBefore(LocalDate.now())) {
            throw new BusinessException("该排班已过期，请选择今日或之后的班次");
        }
        if (reservation.getPatientId() != null
                && reservationMapper.countByPatientAndSchedule(
                        reservation.getPatientId(), schedule.getScheduleId()) > 0) {
            throw new BusinessException("您已预约该班次，请勿重复挂号");
        }
        if (doctorScheduleMapper.decreaseSlots(schedule.getScheduleId()) == 0) {
            throw new BusinessException("号源不足");
        }

        reservation.setScheduleId(schedule.getScheduleId());
        reservation.setDepartmentId(schedule.getDepartmentId());
        reservation.setReservationStatus(1);
        reservation.setVisitStatus(0);
        reservation.setRegisterStatus(0);
        reservation.setReservationTime(buildReservationTime(schedule));
        reservationMapper.addReservation(reservation);
        if (reservation.getPatientId() != null) {
            patientMapper.updatePatientDoctor(reservation.getPatientId(), schedule.getDoctorId());
        }

        auditLogService.record(operator, "REGISTER",
                "挂号单ID=" + reservation.getId() + "，患者=" + reservation.getPatientName()
                        + "，医生ID=" + reservation.getDoctorId() + "，排班ID=" + schedule.getScheduleId());

        return reservation;
    }

    @Override
    public List<Reservation> listByDoctor(Integer doctorId, Integer registerStatus) {
        return reservationMapper.getReservationByDoctorIdAndStatus(doctorId, registerStatus);
    }

    @Override
    public List<Reservation> listMine(LoginUser operator, Integer registerStatus) {
        Doctor doctor = doctorMapper.getDoctorByUserId(operator.getId());
        if (doctor == null) {
            throw new BusinessException(403, "当前账号未绑定医生信息");
        }
        return listByDoctor(doctor.getDoctorId(), registerStatus);
    }

    @Override
    public List<Reservation> listMineForPatient(LoginUser operator) {
        User user = userMapper.getUserById(operator.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserInfo userInfo = userMapper.getUserInfoByUserId(operator.getId());
        String name = userInfo != null && userInfo.getRealname() != null
                ? userInfo.getRealname() : user.getUsername();
        String phone = user.getPhone();
        List<Patient> patients = patientMapper.getAllPatient();
        Patient patient = patients.stream()
                .filter(item -> name.equals(item.getName()))
                .filter(item -> phone == null || phone.isBlank() || phone.equals(item.getContact()))
                .findFirst()
                .orElse(null);
        if (patient == null) {
            throw new BusinessException("未找到患者档案，请联系管理员");
        }
        return reservationMapper.getReservationByPatientId(patient.getPatientId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation markVisited(Integer id, LoginUser operator) {
        Doctor doctor = doctorMapper.getDoctorByUserId(operator.getId());
        if (doctor == null) {
            throw new BusinessException(403, "当前账号未绑定医生信息");
        }
        Reservation reservation = reservationMapper.getReservationForUpdate(id);
        if (reservation == null) {
            throw new BusinessException("挂号单不存在");
        }
        if (!doctor.getDoctorId().equals(reservation.getDoctorId())) {
            throw new BusinessException(403, "只能操作自己的挂号单");
        }
        if (reservation.getReservationStatus() == null || reservation.getReservationStatus() != 1) {
            throw new BusinessException("该预约尚未生效");
        }
        if (reservation.getVisitStatus() != null && reservation.getVisitStatus() == 1) {
            return reservation;
        }
        reservationMapper.updateVisitStatus(id, 1);
        reservation.setVisitStatus(1);
        auditLogService.record(operator, "VISIT",
                "挂号单ID=" + id + "，患者=" + reservation.getPatientName() + "，标记已就诊");
        return reservation;
    }

    private Date buildReservationTime(DoctorSchedule schedule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getScheduleDate());
        String startTime = schedule.getStartTime();
        if (startTime != null && startTime.length() >= 5) {
            String[] parts = startTime.split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
        }
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
