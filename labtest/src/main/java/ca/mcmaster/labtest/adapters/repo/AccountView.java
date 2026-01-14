package ca.mcmaster.labtest.adapters.repo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("accounts")
public class AccountView {
    @Id
    private String id;
    private String name;
    private String email;
    private List<String> roles;

    public AccountView() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
