package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.Transaction.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(Account account) {
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new IllegalArgumentException("Account with email already exists: " + account.getEmail());
        }
        return accountRepository.save(account);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account deposit(Long accountId, Double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");

        Account account = getAccount(accountId);
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        transactionRepository.save(new Transaction(
                account, TransactionType.DEPOSIT, amount, account.getBalance(),
                "Deposit of ₹" + amount
        ));

        return account;
    }

    public Account withdraw(Long accountId, Double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");

        Account account = getAccount(accountId);
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        transactionRepository.save(new Transaction(
                account, TransactionType.WITHDRAWAL, amount, account.getBalance(),
                "Withdrawal of ₹" + amount
        ));

        return account;
    }

    public void transfer(Long fromId, Long toId, Double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive");
        if (fromId.equals(toId)) throw new IllegalArgumentException("Cannot transfer to the same account");

        Account from = getAccount(fromId);
        Account to = getAccount(toId);

        if (from.getBalance() < amount) throw new IllegalArgumentException("Insufficient balance");

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        transactionRepository.save(new Transaction(
                from, TransactionType.TRANSFER_OUT, amount, from.getBalance(),
                "Transfer to account #" + toId
        ));
        transactionRepository.save(new Transaction(
                to, TransactionType.TRANSFER_IN, amount, to.getBalance(),
                "Transfer from account #" + fromId
        ));
    }

    public List<Transaction> getTransactionHistory(Long accountId) {
        getAccount(accountId); // validates account exists
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);
    }

    public boolean deleteAccount(Long id) {
        Account account = getAccount(id);
        if (account.getBalance() > 0) {
            throw new IllegalArgumentException("Cannot delete account with remaining balance");
        }
        accountRepository.delete(account);
        return true;
    }
}
