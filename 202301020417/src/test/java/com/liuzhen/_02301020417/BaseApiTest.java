package com.liuzhen._02301020417;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseApiTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @BeforeEach
    void resetData() {
        jdbcTemplate.execute("DELETE FROM audit_log");
        jdbcTemplate.execute("DELETE FROM charge_record");
        jdbcTemplate.execute("DELETE FROM prescription_item");
        jdbcTemplate.execute("DELETE FROM prescription");
        jdbcTemplate.execute("DELETE FROM simple_reservation");
        jdbcTemplate.execute("DELETE FROM doctor_schedule");
        jdbcTemplate.execute("DELETE FROM drug");
        jdbcTemplate.execute("DELETE FROM medical_record");
        jdbcTemplate.execute("DELETE FROM patient");
        jdbcTemplate.execute("DELETE FROM doctor");
        jdbcTemplate.execute("DELETE FROM userinfo");
        jdbcTemplate.execute("DELETE FROM department");
        jdbcTemplate.execute("DELETE FROM consulting_room");
        jdbcTemplate.execute("DELETE FROM user");

        String hash = passwordEncoder.encode("123456");
        insertUser(1, "admin", hash, "admin");
        insertUser(2, "doctor", hash, "doctor");
        insertUser(3, "cashier", hash, "cashier");
        insertUser(4, "pharmacist", hash, "pharmacist");
        insertUser(5, "patient", hash, "patient");

        jdbcTemplate.update("INSERT INTO department(department_id, department_name, location, phone) VALUES(?, ?, ?, ?)",
                "D001", "全科门诊", "1楼", "0731-00000000");
        jdbcTemplate.update("INSERT INTO userinfo(id, userid, realname, gender) VALUES(1, 2, '张医生', 1)");
        jdbcTemplate.update("INSERT INTO userinfo(id, userid, realname, gender) VALUES(2, 5, '患者甲', 1)");
        jdbcTemplate.update("INSERT INTO doctor(doctor_id, user_id, name, department_id, title, userinfo_id) "
                + "VALUES(1, '2', '张医生', 'D001', '主治医师', 1)");
        jdbcTemplate.update("INSERT INTO patient(patient_id, name, gender, age, id_card, contact, create_time, doctor_id) "
                + "VALUES(1, '患者甲', '男', 30, '430***********1234', '13800000001', now(), 1)");
        jdbcTemplate.update("INSERT INTO patient(patient_id, name, gender, age, id_card, contact, create_time, doctor_id) "
                + "VALUES(2, '患者乙', '女', 58, '430***********5678', '13800000002', now(), 1)");
        insertDrug(1, "阿莫西林胶囊", "12.50", 10);
        insertDrug(2, "布洛芬片", "5.00", 3);
        jdbcTemplate.update("INSERT INTO doctor_schedule(schedule_id, doctor_id, schedule_date, shift_type, start_time, "
                        + "end_time, department_id, room_number, max_patients, remaining_slots, status) "
                        + "VALUES(1, 1, ?, '上午', '08:00', '12:00', 'D001', '101', 5, 5, 1)",
                LocalDate.now().toString());
        jdbcTemplate.update("INSERT INTO simple_reservation(id, department_id, doctor_id, patient_id, schedule_id, "
                        + "patient_name, patient_age, patient_phone, reservation_status, visit_status, register_status, creat_time) "
                        + "VALUES(1, 'D001', 1, 1, 1, '患者甲', 30, '13800000001', 1, 0, 0, now())");
    }

    private void insertUser(int id, String username, String password, String role) {
        jdbcTemplate.update("INSERT INTO user(id, username, password, email, phone, status, create_time, update_time, role) "
                        + "VALUES(?, ?, ?, ?, ?, 1, now(), now(), ?)",
                id, username, password, username + "@his.local", "13800000000", role);
    }

    private void insertDrug(int id, String name, String price, int stock) {
        jdbcTemplate.update("INSERT INTO drug(drug_id, drug_name, drug_type, indication, applicable_department, "
                        + "dosage_form, specification, manufacturer, unit_price, stock_quantity, status) "
                        + "VALUES(?, ?, '测试', '测试', 'D001', '片剂', '测试规格', '测试药厂', ?, ?, 1)",
                id, name, new BigDecimal(price), stock);
    }

    protected String login(String username) throws Exception {
        MvcResult result = mockMvc.perform(get("/login")
                        .param("username", username)
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }

    protected int createPrescription(String doctorToken, int registerId, int drugId, int quantity) throws Exception {
        Map<String, Object> body = Map.of(
                "registerId", registerId,
                "patientId", 1,
                "doctorId", 1,
                "diagnosis", "测试诊断",
                "totalFee", 0.01,
                "items", List.of(Map.of("medicineId", drugId, "quantity", quantity))
        );
        MvcResult result = mockMvc.perform(post("/addPrescription")
                        .header("Authorization", bearer(doctorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("data").get("prescriptionId").asInt();
    }

    protected int drugStock(int drugId) {
        Integer stock = jdbcTemplate.queryForObject("SELECT stock_quantity FROM drug WHERE drug_id = ?",
                Integer.class, drugId);
        return stock == null ? -1 : stock;
    }
}
