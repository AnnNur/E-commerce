package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error(String.format("User not found by userName: %s.", username));
            return ResponseEntity.notFound().build();
        }
        log.info(String.format("User found by name: %s.", username));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            User user = new User();
            user.setUsername(createUserRequest.getUsername());
            log.info(String.format("Username set with %s", createUserRequest.getUsername()));

            Cart cart = new Cart();
            cartRepository.save(cart);
            user.setCart(cart);
            if (createUserRequest.getPassword().length() < 7 ||
                    !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
                log.error(String.format("Cannot create user %s. Error with user password. Either length is less than 7 or password and confirmation password do not match.", createUserRequest.getUsername()));
                return ResponseEntity.badRequest().build();
            }
            user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
            userRepository.save(user);
            log.info("User saved successfully");
            return ResponseEntity.ok(user);
        } catch (Exception exception) {
            log.error(String.format("Exception caught when creating user: %s", exception.getMessage()));
            return ResponseEntity.status(500).build();
        }
    }
}
