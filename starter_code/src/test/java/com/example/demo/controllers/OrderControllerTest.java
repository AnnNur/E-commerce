package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {
    private static OrderController orderController;
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final OrderRepository orderRepository = mock(OrderRepository.class);
    long userId = 1L;
    String userName = "testName";
    String password = "testPassword";
    User user;
    Item item;

    @BeforeClass
    public static void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Before
    public void init() {
        user = new User(userId, userName, password, null);
        item = new Item(1L, "item", BigDecimal.valueOf(80), "brand new item");
        Cart cart = new Cart(1L, Collections.singletonList(item), user, BigDecimal.valueOf(5));
        user.setCart(cart);
    }

    @Test
    public void submit_ShouldSaveOrder_ForUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit(user.getUsername());
        assertThat(userOrderResponseEntity.getBody().getItems(), hasItem(item));
        assertNotNull(userOrderResponseEntity.getBody().getItems());
        assertEquals(item.getName(), userOrderResponseEntity.getBody().getItems().get(0).getName());
        verify(userRepository, atLeastOnce()).findByUsername(userName);
        assertEquals(userName, userOrderResponseEntity.getBody().getUser().getUsername());
        assertEquals(200, userOrderResponseEntity.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser_ShouldReturn_ListOfOrders() {
        UserOrder order = new UserOrder(1L, Collections.singletonList(item), user, BigDecimal.valueOf(5));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(order));

        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser(user.getUsername());

        verify(userRepository, atLeastOnce()).findByUsername(userName);
        assertEquals(userName, ordersForUser.getBody().get(0).getUser().getUsername());
        assertEquals(200, ordersForUser.getStatusCodeValue());
    }
}
