package com.smartbank.service;

import com.smartbank.entity.BillPayment;
import com.smartbank.entity.Account;
import com.smartbank.repository.BillPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final AccountService accountService;

    public BillPaymentService(BillPaymentRepository billPaymentRepository, 
                            AccountService accountService) {
        this.billPaymentRepository = billPaymentRepository;
        this.accountService = accountService;
    }

    public BillPayment payBill(Account account, String billType, String provider, 
                              String billerId, BigDecimal amount) {
        if (account == null) {
            throw new RuntimeException("Account cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        try {
            // Deduct from account
            accountService.updateBalance(account, amount.negate());

            BillPayment billPayment = new BillPayment();
            billPayment.setAccount(account);
            billPayment.setBillType(billType);
            billPayment.setProvider(provider);
            billPayment.setBillerId(billerId);
            billPayment.setAmount(amount);
            billPayment.setStatus(BillPayment.BillStatus.COMPLETED);
            billPayment.setPaymentDate(LocalDateTime.now());
            billPayment.setReferenceNumber(generateReferenceNumber());

            return billPaymentRepository.save(billPayment);
        } catch (Exception e) {
            throw new RuntimeException("Error processing bill payment: " + e.getMessage(), e);
        }
    }

    public List<BillPayment> getBillPaymentHistory(Account account) {
        if (account == null) {
            throw new RuntimeException("Account cannot be null");
        }
        return billPaymentRepository.findByAccount(account);
    }

    private String generateReferenceNumber() {
        return "BILL" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

}
