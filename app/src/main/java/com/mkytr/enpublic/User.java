package com.mkytr.enpublic;

/**
 * Created by mkytr on 15.12.2017.
 */

public class User {
}

class UserSignup{
    private String username;
    private String name;
    private String email;
    private String password;

    public UserSignup(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
