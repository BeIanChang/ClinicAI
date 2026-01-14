package com.clinicai.account.clinicai_account.ports.required;

import com.clinicai.account.clinicai_account.dto.AccountDTO;
import java.util.List;

public interface AccountRepositoryPort {
   void save(AccountDTO account);
   AccountDTO findById(Long id);
   List<AccountDTO> findAll();
   AccountDTO findByEmail(String email);
}
