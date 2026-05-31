package com.smartbank.controller;

import com.smartbank.entity.User;
import com.smartbank.entity.Account;
import com.smartbank.entity.Transaction;
import com.smartbank.service.UserService;
import com.smartbank.service.AccountService;
import com.smartbank.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class DashboardController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public DashboardController(UserService userService, AccountService accountService,
                              TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            List<Account> accounts = accountService.getAccountsByUser(user);

            model.addAttribute("user", user);
            model.addAttribute("accounts", accounts != null ? accounts : List.of());
            model.addAttribute("totalAccounts", accounts != null ? accounts.size() : 0);

            return "dashboard";
        } catch (Exception e) {
            // Redirect to login on critical failure instead of rendering
            // broken template without required model attributes
            return "redirect:/login?error=session";
        }
    }

    @GetMapping("/account/{id}")
    public String viewAccount(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            Account account = accountService.getAccountById(id);
            User user = userService.getUserByEmail(authentication.getName());

            if (!account.getUser().getId().equals(user.getId())) {
                return "error/403";
            }

            List<Transaction> transactions = transactionService.getTransactionHistory(account);

            model.addAttribute("account", account);
            model.addAttribute("transactions", transactions != null ? transactions : List.of());
            model.addAttribute("user", user);

            return "account-details";
        } catch (Exception e) {
            // Redirect to dashboard instead of rendering broken template
            return "redirect:/dashboard";
        }
    }

}
