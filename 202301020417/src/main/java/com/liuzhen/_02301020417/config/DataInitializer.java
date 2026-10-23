package com.liuzhen._02301020417.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String defaultPassword;

    public DataInitializer(JdbcTemplate jdbcTemplate,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.seed-users:false}") boolean enabled,
                           @Value("${app.default-password:123456}") String defaultPassword) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        ensureUser("admin", "admin", "admin@his.local");
        ensureUser("doctor", "doctor", "doctor@his.local");
        ensureUser("cashier", "cashier", "cashier@his.local");
        ensureUser("pharmacist", "pharmacist", "pharmacist@his.local");
        ensureUser("patient", "patient", "patient@his.local");

        ensureDepartment();
        ensureDoctor();
        ensurePatient();
        ensureDrug();
        ensureSchedule();
    }

    private void ensureUser(String username, String role, String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user WHERE username = ?", Integer.class, username);
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.update(
                "INSERT INTO user(username, password, email, phone, status, create_time, update_time, role) "
                        + "VALUES(?, ?, ?, ?, 1, now(), now(), ?)",
                username, passwordEncoder.encode(defaultPassword), email, "13800000000", role);
    }

    private void ensureDepartment() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM department", Integer.class);
        if (count == null || count == 0) {
            jdbcTemplate.update("INSERT INTO department(department_id, department_name, introduction, location, phone) "
                    + "VALUES('D001', '全科门诊', '示例科室', '门诊楼 1 层', '0731-00000000')");
        }
    }

    private void ensureDoctor() {
        Integer userId = jdbcTemplate.query("SELECT id FROM user WHERE username = 'doctor'",
                rs -> rs.next() ? rs.getInt(1) : null);
        if (userId == null) {
            return;
        }
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM doctor WHERE user_id = ?", Integer.class, String.valueOf(userId));
        if (count != null && count > 0) {
            return;
        }
        Integer userInfoCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM userinfo WHERE userid = ?", Integer.class, userId);
        if (userInfoCount == null || userInfoCount == 0) {
            jdbcTemplate.update("INSERT INTO userinfo(userid, realname, gender, avatar) VALUES(?, '张医生', 1, NULL)",
                    userId);
        }
        Integer userInfoId = jdbcTemplate.queryForObject(
                "SELECT id FROM userinfo WHERE userid = ? ORDER BY id LIMIT 1", Integer.class, userId);
        String departmentId = jdbcTemplate.queryForObject(
                "SELECT department_id FROM department ORDER BY department_id LIMIT 1", String.class);
        jdbcTemplate.update("INSERT INTO doctor(user_id, name, department_id, title, userinfo_id) VALUES(?, ?, ?, ?, ?)",
                String.valueOf(userId), "张医生", departmentId, "主治医师", userInfoId);
    }

    private void ensurePatient() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM patient", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        Integer doctorId = jdbcTemplate.query("SELECT doctor_id FROM doctor ORDER BY doctor_id LIMIT 1",
                rs -> rs.next() ? rs.getInt(1) : null);
        jdbcTemplate.update("INSERT INTO patient(name, gender, age, address, id_card, contact, create_time, doctor_id) "
                        + "VALUES('示例患者', '男', 30, '示例地址', '430***********1234', '13800000001', now(), ?)",
                doctorId);
    }

    private void ensureDrug() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM drug", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.update("INSERT INTO drug(drug_name, drug_type, indication, applicable_department, dosage_form, "
                        + "specification, manufacturer, unit_price, stock_quantity, status) "
                        + "VALUES('阿莫西林胶囊', '抗生素', '细菌感染', 'D001', '胶囊', '0.25g*24粒', '示例药厂', ?, 100, 1)",
                new BigDecimal("12.50"));
    }

    private void ensureSchedule() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM doctor_schedule", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        List<Integer> doctorIds = jdbcTemplate.queryForList(
                "SELECT doctor_id FROM doctor ORDER BY doctor_id", Integer.class);
        if (doctorIds.isEmpty()) {
            return;
        }
        LocalDate today = LocalDate.now();
        int sequence = 0;
        for (int day = 0; day < 14; day++) {
            LocalDate date = today.plusDays(day);
            boolean weekend = date.getDayOfWeek() == DayOfWeek.SATURDAY
                    || date.getDayOfWeek() == DayOfWeek.SUNDAY;
            int perShift = weekend ? Math.min(2, doctorIds.size()) : Math.min(4, doctorIds.size());
            for (int shift = 0; shift < 2; shift++) {
                int maxPatients = weekend ? (shift == 0 ? 15 : 12) : (shift == 0 ? 20 : 15);
                for (int k = 0; k < perShift; k++) {
                    int doctorId = doctorIds.get(sequence % doctorIds.size());
                    String doctorDepartment = jdbcTemplate.queryForObject(
                            "SELECT department_id FROM doctor WHERE doctor_id = ?", String.class, doctorId);
                    jdbcTemplate.update("INSERT INTO doctor_schedule(doctor_id, schedule_date, shift_type, "
                                    + "start_time, end_time, department_id, room_number, max_patients, "
                                    + "remaining_slots, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 1)",
                            doctorId, date.toString(),
                            shift == 0 ? "上午" : "下午",
                            shift == 0 ? LocalTime.of(8, 0).toString() : LocalTime.of(14, 0).toString(),
                            shift == 0 ? LocalTime.of(12, 0).toString() : LocalTime.of(17, 30).toString(),
                            doctorDepartment,
                            "R" + String.format("%02d", doctorId) + (shift == 0 ? "A" : "B"),
                            maxPatients, maxPatients);
                    sequence++;
                }
            }
        }
    }
}
