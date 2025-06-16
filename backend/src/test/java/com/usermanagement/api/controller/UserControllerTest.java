package com.usermanagement.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.api.dto.UserDeactivationRequest;
import com.usermanagement.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deactivateUser_shouldReturnNoContent_whenAdminDeactivatesUser() throws Exception {
        UserDeactivationRequest request = new UserDeactivationRequest();
        request.setReason("Violation of terms");
        Mockito.when(userService.getAdminUserId()).thenReturn(1L);
        Mockito.doNothing().when(userService).deactivateUser(2L, request, 1L);

        mockMvc.perform(post("/api/v1/users/2/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}

