package com.liuzhen._02301020417;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthRbacTest extends BaseApiTest {

    @Test
    void loginReturnsTokenWithoutPassword() throws Exception {
        mockMvc.perform(get("/login")
                        .param("username", "admin")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("admin"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void anonymousRequestReturns401() throws Exception {
        mockMvc.perform(get("/getAllDrug"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void doctorCannotCharge() throws Exception {
        String token = login("doctor");
        mockMvc.perform(post("/api/charges/1")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void cashierCannotAddDrug() throws Exception {
        String token = login("cashier");
        mockMvc.perform(post("/addDrug")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void pharmacistCannotPrescribe() throws Exception {
        String token = login("pharmacist");
        mockMvc.perform(post("/addPrescription")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }
}
