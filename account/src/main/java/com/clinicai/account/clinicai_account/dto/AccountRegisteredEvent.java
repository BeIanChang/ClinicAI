package com.clinicai.account.clinicai_account.dto;

import java.util.List;

public class AccountRegisteredEvent {
    private String email;
    private String name;
    private List<String> roles;

    public AccountRegisteredEvent() {}

    public AccountRegisteredEvent(String email, String name, List<String> roles) {
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
