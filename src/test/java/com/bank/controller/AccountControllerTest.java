package com.bank.controller;

import com.bank.model.Account;
import com.bank.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private AccountService accountService;

    private Account sampleAccount() {
        Account a = new Account("Riya Patel", "riya@bank.com", 8000.0, Account.AccountType.SAVINGS);
        a.setId(1L);
        return a;
    }

    // --- Test 1: POST /api/accounts → 201 Created ---
    @Test
    void createAccount_ShouldReturn201() throws Exception {
        Account a = sampleAccount();
        when(accountService.createAccount(any())).thenReturn(a);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(a)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holderName").value("Riya Patel"))
                .andExpect(jsonPath("$.balance").value(8000.0));
    }

    // --- Test 2: GET /api/accounts → 200 with list ---
    @Test
    void getAllAccounts_ShouldReturn200() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(List.of(sampleAccount()));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // --- Test 3: GET /api/accounts/{id} → 200 ---
    @Test
    void getAccount_ExistingId_ShouldReturn200() throws Exception {
        when(accountService.getAccount(1L)).thenReturn(sampleAccount());

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("riya@bank.com"));
    }

    // --- Test 4: GET /api/accounts/{id} → 404 if not found ---
    @Test
    void getAccount_InvalidId_ShouldReturn404() throws Exception {
        when(accountService.getAccount(99L)).thenThrow(new IllegalArgumentException("Account not found"));

        mockMvc.perform(get("/api/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // --- Test 5: POST /api/accounts/{id}/deposit → 200 ---
    @Test
    void deposit_ShouldReturn200() throws Exception {
        Account updated = sampleAccount();
        updated.setBalance(10000.0);
        when(accountService.deposit(eq(1L), eq(2000.0))).thenReturn(updated);

        mockMvc.perform(post("/api/accounts/1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 2000.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10000.0));
    }
}
