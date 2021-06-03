package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    private static UserController userController;
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final CartRepository cartRepository = mock(CartRepository.class);
    private static final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    long userId = 1L;
    String userName = "testName";
    String password = "testPassword";

    @BeforeClass
    public static void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() {
        String hashedPassword = "hashedPassword";
        when(encoder.encode(password)).thenReturn(hashedPassword);
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(userName);
        request.setPassword(password);
        request.setConfirmPassword(password);

        ResponseEntity<User> responseEntity = userController.createUser(request);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User createdUser = responseEntity.getBody();
        assertNotNull(createdUser);
        assertEquals(0, createdUser.getId());
        assertEquals(userName, createdUser.getUsername());
        assertEquals(hashedPassword, createdUser.getPassword());
    }

    @Test
    public void findById_happy_path() {
        User user = new User(userId, userName, password, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> userResponseEntity = userController.findById(1L);
        User userResponseEntityBody = userResponseEntity.getBody();

        assertEquals(200, userResponseEntity.getStatusCodeValue());
        verify(userRepository, times(1)).findById(userId);
        assertNotNull(userResponseEntityBody.getUsername());
        assertEquals(userName, userResponseEntityBody.getUsername());
        assertThat(userResponseEntityBody.getId(), is(1L));
    }

    @Test
    public void findByUserName_happy_path() {
        User user = new User(userId, userName, password, null);
        when(userRepository.findByUsername(userName)).thenReturn(user);

        ResponseEntity<User> userResponseEntity = userController.findByUserName(userName);
        User userResponseEntityBody = userResponseEntity.getBody();

        assertEquals(200, userResponseEntity.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername(userName);
        assertNotNull(userResponseEntityBody.getUsername());
        assertNotEquals("fakeName", userResponseEntityBody.getUsername());
        assertEquals(userName, userResponseEntityBody.getUsername());
        assertThat(userResponseEntityBody.getId(), is(1L));
    }
}

