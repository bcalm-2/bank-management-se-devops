package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account("Test User", "test@bank.com", 5000.0, Account.AccountType.SAVINGS);
        testAccount.setId(1L);
    }

    // --- Test 1: Create account successfully ---
    @Test
    void createAccount_ShouldReturnSavedAccount() {
        when(accountRepository.existsByEmail("test@bank.com")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account result = accountService.createAccount(testAccount);

        assertThat(result).isNotNull();
        assertThat(result.getHolderName()).isEqualTo("Test User");
        assertThat(result.getBalance()).isEqualTo(5000.0);
        verify(accountRepository, times(1)).save(testAccount);
    }

    // --- Test 2: Duplicate email should throw ---
    @Test
    void createAccount_DuplicateEmail_ShouldThrow() {
        when(accountRepository.existsByEmail("test@bank.com")).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(testAccount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    // --- Test 3: Deposit increases balance ---
    @Test
    void deposit_ShouldIncreaseBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);
        when(transactionRepository.save(any())).thenReturn(new Transaction());

        Account result = accountService.deposit(1L, 2000.0);

        assertThat(result.getBalance()).isEqualTo(7000.0);
    }

    // --- Test 4: Deposit with negative amount should throw ---
    @Test
    void deposit_NegativeAmount_ShouldThrow() {
        assertThatThrownBy(() -> accountService.deposit(1L, -500.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    // --- Test 5: Withdrawal decreases balance ---
    @Test
    void withdraw_ShouldDecreaseBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);
        when(transactionRepository.save(any())).thenReturn(new Transaction());

        Account result = accountService.withdraw(1L, 1000.0);

        assertThat(result.getBalance()).isEqualTo(4000.0);
    }

    // --- Test 6: Insufficient balance should throw ---
    @Test
    void withdraw_InsufficientBalance_ShouldThrow() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> accountService.withdraw(1L, 99999.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient");
    }

    // --- Test 7: Transfer between two accounts ---
    @Test
    void transfer_ShouldUpdateBothBalances() {
        Account fromAccount = new Account("From", "from@bank.com", 10000.0, Account.AccountType.SAVINGS);
        fromAccount.setId(1L);
        Account toAccount = new Account("To", "to@bank.com", 5000.0, Account.AccountType.SAVINGS);
        toAccount.setId(2L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(any())).thenReturn(new Transaction());

        accountService.transfer(1L, 2L, 3000.0);

        assertThat(fromAccount.getBalance()).isEqualTo(7000.0);
        assertThat(toAccount.getBalance()).isEqualTo(8000.0);
    }

    // --- Test 8: Same-account transfer should throw ---
    @Test
    void transfer_SameAccount_ShouldThrow() {
        assertThatThrownBy(() -> accountService.transfer(1L, 1L, 100.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same account");
    }

    // --- Test 9: Account not found should throw ---
    @Test
    void getAccount_NotFound_ShouldThrow() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    // --- Test 10: Delete account with balance should throw ---
    @Test
    void deleteAccount_WithBalance_ShouldThrow() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> accountService.deleteAccount(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("remaining balance");
    }
}
