package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private static CartController cartController;
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final CartRepository cartRepository = mock(CartRepository.class);
    private static final ItemRepository itemRepository = mock(ItemRepository.class);
    long userId = 1L;
    String userName = "testName";
    String password = "testPassword";
    User user;
    Cart cart;

    @BeforeClass
    public static void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Before
    public void init() {
        user = new User(userId, userName, password, null);
        cart = new Cart(1L, new ArrayList<>(), user, BigDecimal.valueOf(5));
        user.setCart(cart);
    }

    @Test
    public void addToCart_ShouldSaveCartWithItems_ForUser() {
        User user = new User(userId, userName, password, null);
        Cart cart = new Cart(1L, new ArrayList<>(), user, BigDecimal.valueOf(5));
        user.setCart(cart);

        Item item = new Item(1L, "item", BigDecimal.valueOf(80), "brand new item");
        ModifyCartRequest request = new ModifyCartRequest(userName, 1L, 1);

        when(userRepository.findByUsername(userName)).thenReturn(user);
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(request);
        Cart cartResponseEntityBody = cartResponseEntity.getBody();

        assertNotNull(cartResponseEntityBody.getItems());
        assertEquals(user, cartResponseEntityBody.getUser());
        verify(userRepository, atLeastOnce()).findByUsername(userName);
        verify(itemRepository, atLeastOnce()).findById(1L);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_ShouldDeleteItems() {
        Item item = new Item(1L, "item", BigDecimal.valueOf(80), "brand new item");
        ModifyCartRequest request = new ModifyCartRequest(userName, 1L, 10);

        when(userRepository.findByUsername(userName)).thenReturn(user);
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(request);
        Cart cartResponseEntityBody = cartResponseEntity.getBody();

        assertThat(cartResponseEntityBody.getItems(), is(empty()));
        assertEquals(user, cartResponseEntityBody.getUser());
        verify(userRepository, atLeastOnce()).findByUsername(userName);
        verify(itemRepository, atLeastOnce()).findById(1L);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void addToCart_ShouldReturnNotFound_ForNonExistingUser() {
        ModifyCartRequest request = new ModifyCartRequest("NonExisting" + userName, 1L, 1);

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(request);
        assertThat(cartResponseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void addToCart_ShouldReturnNotFound_ForNonExistingItem() {
        ModifyCartRequest request = new ModifyCartRequest(userName, 2L, 1);

        when(userRepository.findByUsername(userName)).thenReturn(user);

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(request);
        assertThat(cartResponseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}
