package com.liuzhen._02301020417;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VisitMarkingTest extends BaseApiTest {

    @Test
    void doctorMarksOwnRegisterRecordVisited() throws Exception {
        String token = login("doctor");

        mockMvc.perform(post("/api/register-records/1/visit")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.visitStatus").value(1));

        Integer visitStatus = jdbcTemplate.queryForObject(
                "SELECT visit_status FROM simple_reservation WHERE id = 1", Integer.class);
        assertEquals(1, visitStatus);

        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM audit_log WHERE action = 'VISIT'", Integer.class);
        assertEquals(1, auditCount);
    }

    @Test
    void repeatedMarkIsIdempotent() throws Exception {
        String token = login("doctor");

        mockMvc.perform(post("/api/register-records/1/visit")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/register-records/1/visit")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM audit_log WHERE action = 'VISIT'", Integer.class);
        assertEquals(1, auditCount);
    }

    @Test
    void doctorCannotMarkOtherDoctorsRegisterRecord() throws Exception {
        jdbcTemplate.update("UPDATE simple_reservation SET doctor_id = 2 WHERE id = 1");
        String token = login("doctor");

        mockMvc.perform(post("/api/register-records/1/visit")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        Integer visitStatus = jdbcTemplate.queryForObject(
                "SELECT visit_status FROM simple_reservation WHERE id = 1", Integer.class);
        assertEquals(0, visitStatus);
    }

    @Test
    void patientCannotMarkVisited() throws Exception {
        String token = login("patient");

        mockMvc.perform(post("/api/register-records/1/visit")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        Integer visitStatus = jdbcTemplate.queryForObject(
                "SELECT visit_status FROM simple_reservation WHERE id = 1", Integer.class);
        assertEquals(0, visitStatus);
    }
}
