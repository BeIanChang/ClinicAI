package com.clinicai.account.clinicai_account.adapter.repository;

import com.clinicai.account.clinicai_account.dto.AccountDTO;
import com.clinicai.account.clinicai_account.ports.required.AccountRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountRepository implements AccountRepositoryPort {

    private final AccountMongoRepository mongoRepository;

    public AccountRepository(AccountMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public List<AccountDTO> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public AccountDTO findById(Long id) {
        // MongoDB id 是 String，转换 Long 为 String
        return mongoRepository.findById(String.valueOf(id)).orElse(null);
    }

    public AccountDTO findByEmail(String email) {
        return mongoRepository.findByEmail(email);
    }

    @Override
    public void save(AccountDTO account) {
        mongoRepository.save(account);
    }
}
