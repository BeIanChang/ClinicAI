package com.clinicai.account.clinicai_account.ports.provided;


import com.clinicai.account.clinicai_account.dto.AccountDTO;

public interface AccountService {
    void register(AccountDTO account);
    AccountDTO login(String email, String password);
    AccountDTO getAccountById(String id);
}