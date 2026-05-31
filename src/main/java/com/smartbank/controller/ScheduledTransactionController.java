package com.smartbank.controller;

import com.smartbank.entity.User;
import com.smartbank.entity.Account;
import com.smartbank.service.UserService;
import com.smartbank.service.AccountService;
import com.smartbank.service.ScheduledTransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("")
public class ScheduledTransactionController {

    private final UserService userService;
    private final AccountService accountService;
    private final ScheduledTransactionService scheduledTransactionService;

    public ScheduledTransactionController(UserService userService, AccountService accountService,
                                         ScheduledTransactionService scheduledTransactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.scheduledTransactionService = scheduledTransactionService;
    }

    @GetMapping("/schedule-transfer")
    public String scheduleTransferPage(Authentication authentication, Model model) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            return "schedule-transfer";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading schedule transfer page: " + e.getMessage());
            return "schedule-transfer";
        }
    }

    @PostMapping("/schedule-transfer")
    public String scheduleTransfer(@RequestParam Long accountId,
                                  @RequestParam String recipientAccount,
                                  @RequestParam String recipientName,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam String description,
                                  @RequestParam String scheduledDate,
                                  Authentication authentication, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            Account account = accountService.getAccountById(accountId);

            if (!account.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "Unauthorized access");
                return "error/403";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime scheduledDateTime = LocalDateTime.parse(scheduledDate, formatter);
            
            // Validate that scheduled date is in the future
            if (scheduledDateTime.isBefore(LocalDateTime.now())) {
                model.addAttribute("error", "Scheduled date must be in the future");
                model.addAttribute("accounts", accountService.getAccountsByUser(user));
                return "schedule-transfer";
            }

            scheduledTransactionService.scheduleTransfer(account, recipientAccount, 
                                                         recipientName, amount, 
                                                         description, scheduledDateTime);

            redirectAttributes.addFlashAttribute("success", "Transfer scheduled successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            return "schedule-transfer";
        }
    }

}
