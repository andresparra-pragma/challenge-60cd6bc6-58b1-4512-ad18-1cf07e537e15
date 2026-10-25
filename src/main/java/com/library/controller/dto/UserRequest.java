package com.library.controller.dto;

import com.library.model.User;

public record UserRequest(String name, String email, Integer loanLimit) {
    public User toUser() {
        return new User(name, email, loanLimit == null ? 0 : loanLimit);
    }
}
