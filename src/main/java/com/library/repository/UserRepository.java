package com.library.repository;

import com.library.model.User;

@org.springframework.stereotype.Repository
public class UserRepository extends InMemoryRepository<User, String> {
}
