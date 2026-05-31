package com.smartbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private String recipientAccount;
    private String recipientName;
    private String status;
    private LocalDateTime transactionDate;
    private String referenceNumber;
}
