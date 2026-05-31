package com.smartbank.service;

import com.smartbank.dto.FundTransferDto;
import com.smartbank.entity.Account;
import com.smartbank.entity.Transaction;
import com.smartbank.repository.TransactionRepository;
import com.smartbank.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, 
                            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public Transaction performFundTransfer(FundTransferDto dto) {
        if (dto == null || dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid transfer amount");
        }
        
        Account senderAccount = accountRepository.findByAccountNumber(dto.getSenderAccountNumber())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        
        Account recipientAccount = accountRepository.findByAccountNumber(dto.getRecipientAccountNumber())
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));

        if (senderAccount.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        if (senderAccount.getAccountNumber().equals(recipientAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        // Deduct from sender
        senderAccount.setBalance(senderAccount.getBalance().subtract(dto.getAmount()));
        accountRepository.save(senderAccount);

        // Add to recipient
        recipientAccount.setBalance(recipientAccount.getBalance().add(dto.getAmount()));
        accountRepository.save(recipientAccount);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(senderAccount);
        transaction.setTransactionType("TRANSFER");
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setRecipientAccount(dto.getRecipientAccountNumber());
        
        // Safely set recipient name
        if (recipientAccount.getUser() != null) {
            transaction.setRecipientName(recipientAccount.getUser().getFullName());
        }
        
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setReferenceNumber(generateReferenceNumber());
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Account account) {
        return transactionRepository.findByAccountOrderByTransactionDateDesc(account);
    }

    public Transaction createTransaction(Account account, String type, BigDecimal amount, 
                                        String description, String recipientAccount) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setRecipientAccount(recipientAccount);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setReferenceNumber(generateReferenceNumber());
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    private String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis() + new Random().nextInt(10000);
    }

}
