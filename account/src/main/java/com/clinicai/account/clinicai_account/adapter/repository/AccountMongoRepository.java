package com.clinicai.account.clinicai_account.adapter.repository;

import com.clinicai.account.clinicai_account.dto.AccountDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountMongoRepository extends MongoRepository<AccountDTO, String> {
    AccountDTO findByEmail(String email);
}
