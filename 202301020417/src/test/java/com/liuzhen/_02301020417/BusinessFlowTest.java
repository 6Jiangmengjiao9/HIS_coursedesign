package com.liuzhen._02301020417;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BusinessFlowTest extends BaseApiTest {

    @Test
    void prescriptionPriceIsCalculatedByBackend() throws Exception {
        String doctorToken = login("doctor");
        int prescriptionId = createPrescription(doctorToken, 1, 1, 2);

        mockMvc.perform(get("/api/prescriptions/" + prescriptionId)
                        .header("Authorization", bearer(doctorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalFee").value(25.0))
                .andExpect(jsonPath("$.data.status").value(0))
                .andExpect(jsonPath("$.data.items[0].medicineId").value(1))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2));

        BigDecimal total = jdbcTemplate.queryForObject(
                "SELECT total_fee FROM prescription WHERE prescription_id = ?", BigDecimal.class, prescriptionId);
        assertEquals(0, new BigDecimal("25.00").compareTo(total));
        Integer registerStatus = jdbcTemplate.queryForObject(
                "SELECT register_status FROM simple_reservation WHERE id = 1", Integer.class);
        assertEquals(1, registerStatus);
    }

    @Test
    void chargeDeductsStockAndWritesAuditLog() throws Exception {
        String doctorToken = login("doctor");
        String cashierToken = login("cashier");
        int prescriptionId = createPrescription(doctorToken, 1, 1, 2);

        mockMvc.perform(post("/api/charges/" + prescriptionId)
                        .header("Authorization", bearer(cashierToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.amount").value(25.0));

        assertEquals(8, drugStock(1));
        Integer status = jdbcTemplate.queryForObject(
                "SELECT status FROM prescription WHERE prescription_id = ?", Integer.class, prescriptionId);
        assertEquals(1, status);
        BigDecimal amount = jdbcTemplate.queryForObject(
                "SELECT amount FROM charge_record WHERE prescription_id = ?", BigDecimal.class, prescriptionId);
        assertEquals(0, new BigDecimal("25.00").compareTo(amount));
        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM audit_log WHERE action = 'CHARGE'", Integer.class);
        assertTrue(auditCount != null && auditCount > 0);
    }

    @Test
    void insufficientStockReturns400AndRollsBack() throws Exception {
        String doctorToken = login("doctor");
        String cashierToken = login("cashier");
        int prescriptionId = createPrescription(doctorToken, 1, 1, 20);

        mockMvc.perform(post("/api/charges/" + prescriptionId)
                        .header("Authorization", bearer(cashierToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        assertEquals(10, drugStock(1));
        Integer status = jdbcTemplate.queryForObject(
                "SELECT status FROM prescription WHERE prescription_id = ?", Integer.class, prescriptionId);
        assertEquals(0, status);
        Integer chargeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM charge_record WHERE prescription_id = ?", Integer.class, prescriptionId);
        assertEquals(0, chargeCount);
    }

    @Test
    void registrationDecrementsSlotsAndRejectsWhenEmpty() throws Exception {
        String patientToken = login("patient");
        Map<String, Object> body = new HashMap<>();
        body.put("doctorId", 1);
        body.put("patientId", 2);
        body.put("patientName", "患者乙");
        body.put("patientAge", 58);
        body.put("patientPhone", "13800000002");
        body.put("departmentId", "D001");

        mockMvc.perform(post("/api/register-records")
                        .header("Authorization", bearer(patientToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT remaining_slots FROM doctor_schedule WHERE schedule_id = 1", Integer.class);
        assertEquals(4, remaining);

        jdbcTemplate.update("UPDATE doctor_schedule SET remaining_slots = 0 WHERE schedule_id = 1");
        mockMvc.perform(post("/api/register-records")
                        .header("Authorization", bearer(patientToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void pharmacistDispensesPaidPrescriptionAndWritesAuditLog() throws Exception {
        String doctorToken = login("doctor");
        String cashierToken = login("cashier");
        String pharmacistToken = login("pharmacist");
        int prescriptionId = createPrescription(doctorToken, 1, 1, 1);

        mockMvc.perform(post("/api/charges/" + prescriptionId)
                        .header("Authorization", bearer(cashierToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/dispenses/" + prescriptionId)
                        .header("Authorization", bearer(pharmacistToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Integer prescriptionStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM prescription WHERE prescription_id = ?", Integer.class, prescriptionId);
        assertEquals(2, prescriptionStatus);
        Integer registerStatus = jdbcTemplate.queryForObject(
                "SELECT register_status FROM simple_reservation WHERE id = 1", Integer.class);
        assertEquals(2, registerStatus);
        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM audit_log WHERE action = 'DISPENSE'", Integer.class);
        assertTrue(auditCount != null && auditCount > 0);
    }
}
