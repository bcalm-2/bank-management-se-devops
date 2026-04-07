package com.bank.config;

import com.bank.model.Account;
import com.bank.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AccountService accountService;

    public DataSeeder(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) {
        Account a1 = accountService.createAccount(
                new Account("Vikram Singh", "vikram@bank.com", 10000.0, Account.AccountType.SAVINGS));
        Account a2 = accountService.createAccount(
                new Account("Srashti Dwivedi", "priya@bank.com", 25000.0, Account.AccountType.CURRENT));

        accountService.deposit(a1.getId(), 5000.0);
        accountService.withdraw(a2.getId(), 3000.0);
        accountService.transfer(a2.getId(), a1.getId(), 2000.0);

        System.out.println("\n========================================");
        System.out.println("  Bank Management System Started!");
        System.out.println("  API Base: http://localhost:8080/api/accounts");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("  Demo accounts seeded: ID 1 and ID 2");
        System.out.println("========================================\n");
    }
}
