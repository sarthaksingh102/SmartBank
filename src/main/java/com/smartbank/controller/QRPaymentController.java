package com.smartbank.controller;

import com.smartbank.entity.User;
import com.smartbank.entity.Account;
import com.smartbank.entity.QRPayment;
import com.smartbank.service.UserService;
import com.smartbank.service.AccountService;
import com.smartbank.service.QRPaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("")
public class QRPaymentController {

    private final UserService userService;
    private final AccountService accountService;
    private final QRPaymentService qrPaymentService;

    public QRPaymentController(UserService userService, AccountService accountService,
                              QRPaymentService qrPaymentService) {
        this.userService = userService;
        this.accountService = accountService;
        this.qrPaymentService = qrPaymentService;
    }

    @GetMapping("/qr-payment")
    public String qrPaymentPage(Authentication authentication, Model model) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            return "qr-payment";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading QR payment page: " + e.getMessage());
            return "qr-payment";
        }
    }

    @PostMapping("/generate-qr")
    public String generateQR(@RequestParam Long accountId,
                            @RequestParam BigDecimal amount,
                            @RequestParam String description,
                            Authentication authentication, Model model) {
        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Amount must be greater than 0");
                User user = userService.getUserByEmail(authentication.getName());
                model.addAttribute("accounts", accountService.getAccountsByUser(user));
                return "qr-payment";
            }
            
            User user = userService.getUserByEmail(authentication.getName());
            Account account = accountService.getAccountById(accountId);

            if (!account.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "Unauthorized access");
                return "error/403";
            }

            QRPayment qrPayment = qrPaymentService.generateQRPayment(account, amount, description);
            if (qrPayment == null) {
                model.addAttribute("error", "Failed to generate QR payment");
                model.addAttribute("accounts", accountService.getAccountsByUser(user));
                return "qr-payment";
            }
            
            String qrImage = qrPaymentService.generateQRCodeImage(qrPayment.getQrCode());

            model.addAttribute("qrPayment", qrPayment);
            model.addAttribute("qrImage", qrImage);
            model.addAttribute("accounts", accountService.getAccountsByUser(user));

            return "qr-payment";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            return "qr-payment";
        }
    }

}
