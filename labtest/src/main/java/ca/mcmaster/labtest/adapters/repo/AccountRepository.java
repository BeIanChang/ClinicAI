package ca.mcmaster.labtest.adapters.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<AccountView, String> {
}
