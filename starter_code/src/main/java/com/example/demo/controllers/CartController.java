package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                log.error(String.format("User with userName %s not found when adding to cart.", request.getUsername()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Optional<Item> item = itemRepository.findById(request.getItemId());
            if (!item.isPresent()) {
                log.error("Item not found when adding to cart.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Cart cart = user.getCart();
            IntStream.range(0, request.getQuantity())
                    .forEach(i -> cart.addItem(item.get()));
            cartRepository.save(cart);
            log.info("Cart saved successfully with added items");
            return ResponseEntity.ok(cart);
        } catch (Exception exception) {
            log.error("Exception when adding item to cart: ", exception);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                log.error(String.format("User with userName %s not found when removing items from cart.", request.getUsername()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Optional<Item> item = itemRepository.findById(request.getItemId());
            if (!item.isPresent()) {
                log.error("Item not found when removing items from cart.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Cart cart = user.getCart();
            IntStream.range(0, request.getQuantity())
                    .forEach(i -> cart.removeItem(item.get()));
            cartRepository.save(cart);
            log.info("Cart saved successfully after removing items");
            return ResponseEntity.ok(cart);
        } catch (Exception exception) {
            log.error("Exception when removing item from cart: ", exception);
            return ResponseEntity.status(500).build();
        }
    }
}
