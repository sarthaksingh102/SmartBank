package com.smartbank.service;

import com.smartbank.entity.ScheduledTransaction;
import com.smartbank.entity.Account;
import com.smartbank.repository.ScheduledTransactionRepository;
import com.smartbank.repository.AccountRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class ScheduledTransactionService {

    private final ScheduledTransactionRepository scheduledTransactionRepository;
    private final AccountRepository accountRepository;

    public ScheduledTransactionService(ScheduledTransactionRepository scheduledTransactionRepository,
                                      AccountRepository accountRepository) {
        this.scheduledTransactionRepository = scheduledTransactionRepository;
        this.accountRepository = accountRepository;
    }

    public ScheduledTransaction scheduleTransfer(Account account, String recipientAccount,
                                                 String recipientName, BigDecimal amount,
                                                 String description, LocalDateTime scheduledDate) {
        if (account == null) {
            throw new RuntimeException("Account cannot be null");
        }
        if (recipientAccount == null || recipientAccount.trim().isEmpty()) {
            throw new RuntimeException("Recipient account cannot be empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        if (scheduledDate == null || scheduledDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Scheduled date must be in the future");
        }
        
        // Validate recipient account exists
        accountRepository.findByAccountNumber(recipientAccount)
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));
        
        ScheduledTransaction transaction = new ScheduledTransaction();
        transaction.setAccount(account);
        transaction.setRecipientAccount(recipientAccount);
        transaction.setRecipientName(recipientName);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setScheduledDate(scheduledDate);
        transaction.setStatus(ScheduledTransaction.ScheduleStatus.SCHEDULED);

        return scheduledTransactionRepository.save(transaction);
    }

    public List<ScheduledTransaction> getScheduledTransactions(Account account) {
        return scheduledTransactionRepository.findByAccount(account);
    }

    @Scheduled(fixedRate = 60000) // Execute every minute
    public void executeScheduledTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledTransaction> transactions = 
            scheduledTransactionRepository.findByStatusAndScheduledDateBefore(
                ScheduledTransaction.ScheduleStatus.SCHEDULED, now);

        for (ScheduledTransaction transaction : transactions) {
            try {
                Account account = transaction.getAccount();
                if (account == null) {
                    transaction.setStatus(ScheduledTransaction.ScheduleStatus.FAILED);
                    scheduledTransactionRepository.save(transaction);
                    continue;
                }
                
                if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                    transaction.setStatus(ScheduledTransaction.ScheduleStatus.FAILED);
                    scheduledTransactionRepository.save(transaction);
                    continue;
                }
                
                // Verify recipient account still exists
                Optional<Account> recipientOpt = accountRepository.findByAccountNumber(transaction.getRecipientAccount());
                if (recipientOpt.isEmpty()) {
                    transaction.setStatus(ScheduledTransaction.ScheduleStatus.FAILED);
                    scheduledTransactionRepository.save(transaction);
                    continue;
                }

                Account recipientAccount = recipientOpt.get();

                // Deduct from sender
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
                accountRepository.save(account);

                // Credit to recipient (BUG FIX: was missing before)
                recipientAccount.setBalance(recipientAccount.getBalance().add(transaction.getAmount()));
                accountRepository.save(recipientAccount);

                // Mark as executed
                transaction.setStatus(ScheduledTransaction.ScheduleStatus.EXECUTED);
                transaction.setExecutedDate(LocalDateTime.now());
                transaction.setReferenceNumber(generateReferenceNumber());
                scheduledTransactionRepository.save(transaction);
            } catch (Exception e) {
                transaction.setStatus(ScheduledTransaction.ScheduleStatus.FAILED);
                scheduledTransactionRepository.save(transaction);
            }
        }
    }

    private String generateReferenceNumber() {
        return "SCH" + System.currentTimeMillis() + new Random().nextInt(10000);
    }

}
