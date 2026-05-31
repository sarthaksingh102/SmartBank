package com.smartbank.controller;

import com.smartbank.entity.User;
import com.smartbank.entity.Account;
import com.smartbank.service.UserService;
import com.smartbank.service.AccountService;
import com.smartbank.service.BillPaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("")
public class BillPaymentController {

    private final UserService userService;
    private final AccountService accountService;
    private final BillPaymentService billPaymentService;

    public BillPaymentController(UserService userService, AccountService accountService,
                                BillPaymentService billPaymentService) {
        this.userService = userService;
        this.accountService = accountService;
        this.billPaymentService = billPaymentService;
    }

    @GetMapping("/bills")
    public String billsPage(Authentication authentication, Model model) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            return "bills";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading bills page: " + e.getMessage());
            return "bills";
        }
    }

    @PostMapping("/pay-bill")
    public String payBill(@RequestParam Long accountId,
                         @RequestParam String billType,
                         @RequestParam String provider,
                         @RequestParam String billerId,
                         @RequestParam BigDecimal amount,
                         Authentication authentication, Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Amount must be greater than 0");
                User user = userService.getUserByEmail(authentication.getName());
                model.addAttribute("accounts", accountService.getAccountsByUser(user));
                return "bills";
            }
            
            User user = userService.getUserByEmail(authentication.getName());
            Account account = accountService.getAccountById(accountId);

            if (!account.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "Unauthorized access");
                return "error/403";
            }

            billPaymentService.payBill(account, billType, provider, billerId, amount);
            redirectAttributes.addFlashAttribute("success", "Bill payment successful!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            return "bills";
        }
    }

}
