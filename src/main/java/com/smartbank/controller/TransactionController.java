package com.smartbank.controller;

import com.smartbank.dto.FundTransferDto;
import com.smartbank.entity.User;
import com.smartbank.entity.Account;
import com.smartbank.service.UserService;
import com.smartbank.service.AccountService;
import com.smartbank.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("")
public class TransactionController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransactionController(UserService userService, AccountService accountService,
                                TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/transfer")
    public String transferPage(Authentication authentication, Model model) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            model.addAttribute("transfer", new FundTransferDto());
            return "transfer";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load transfer page: " + e.getMessage());
            return "transfer";
        }
    }

    @PostMapping("/transfer")
    public String performTransfer(@ModelAttribute FundTransferDto dto, 
                                 Authentication authentication, Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            
            // Get sender account and verify ownership
            Account senderAccount = accountService.getAccountByNumber(dto.getSenderAccountNumber());
            if (!senderAccount.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "Unauthorized: You do not own this account");
                model.addAttribute("accounts", accountService.getAccountsByUser(user));
                model.addAttribute("transfer", dto);
                return "transfer";
            }
            
            transactionService.performFundTransfer(dto);
            redirectAttributes.addFlashAttribute("success", "Fund transfer successful!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            model.addAttribute("transfer", dto);
            return "transfer";
        }
    }

}
