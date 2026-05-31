package com.smartbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundTransferDto {
    @NotEmpty(message = "Sender account number is required")
    private String senderAccountNumber;
    
    @NotEmpty(message = "Recipient account number is required")
    private String recipientAccountNumber;
    
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotEmpty(message = "Description is required")
    private String description;
}
