package com.clinicai.account.clinicai_account.business;

import com.clinicai.account.clinicai_account.dto.AccountDTO;
import com.clinicai.account.clinicai_account.ports.provided.AccountService;
import com.clinicai.account.clinicai_account.ports.required.AccountRepositoryPort;
import com.clinicai.account.clinicai_account.ports.required.EmailServicePort;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepositoryPort repository;
    private final EmailServicePort emailService;

    public AccountServiceImpl(AccountRepositoryPort repository, EmailServicePort emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Override
    public void register(AccountDTO account) {
        if (repository.findByEmail(account.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        // Generate UUID for new account if id is not set
        if (account.getId() == null) {
            account.setId(UUID.randomUUID().toString());
        }

        emailService.sendWelcomeEmail(account.getEmail());

        repository.save(account);


        System.out.println("[RabbitMQ] AccountRegisteredEvent sent → " + account.getEmail());
    }

    @Override
    public AccountDTO login(String email, String password) {
        AccountDTO account = repository.findByEmail(email);
        if (account == null || !account.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }
        return account;
    }

    public AccountDTO getAccountById(String id) {
        return repository.findById(Long.valueOf(id));
    }

}