package com.clinicai.account.clinicai_account.adapter.rest;

import com.clinicai.account.clinicai_account.dto.AccountDTO;
import com.clinicai.account.clinicai_account.ports.provided.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody AccountDTO account) {
        service.register(account);
        return "Registered successfully";
    }

    @PostMapping("/login")
    public AccountDTO login(@RequestParam String email, @RequestParam String password) {
        return service.login(email, password);
    }

    @GetMapping("/{id}")
   public AccountDTO getAccountById(@PathVariable String id) {
      return service.getAccountById(id);
   }
}