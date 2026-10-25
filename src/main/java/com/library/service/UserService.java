package com.library.service;

import com.library.exception.DuplicateResourceException;
import com.library.exception.UserNotFoundException;
import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        if (userRepository.existsById(user.getId())) {
            throw new DuplicateResourceException("usuario", user.getId());
        }
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findById(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public boolean isUserUnderLoanLimit(String email) {
        return getUserByEmail(email).canBorrow();
    }
}
