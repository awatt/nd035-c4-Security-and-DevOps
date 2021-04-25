package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private UserController userController;

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        cartController = new CartController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void add_and_remove_cart_item_path() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        final ResponseEntity<User> response = userController.createUser(r);
        User u = response.getBody();

        Item i = new Item();
        i.setId((long) 1);
        i.setName("widget");
        i.setPrice(BigDecimal.valueOf(10.00));
        i.setDescription("it gets wids");

        when(userRepo.findByUsername(u.getUsername())).thenReturn(u);
        when(itemRepo.findById(i.getId())).thenReturn(java.util.Optional.of(i));

        assertNotNull(response.getBody().getCart());

        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(i.getId());
        req.setUsername(u.getUsername());
        req.setQuantity(2);

        final ResponseEntity<Cart> addToCartResponse = cartController.addTocart(req);
        assertEquals(200, addToCartResponse.getStatusCodeValue());
        Cart addedCart = addToCartResponse.getBody();
        assertNotNull(addedCart);
        assertEquals(addedCart.getTotal(),BigDecimal.valueOf(20.00));

        req.setQuantity(1);
        final ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromcart(req);
        Cart removedCart = removeFromCartResponse.getBody();
        assertNotNull(removedCart);
        assertEquals(removedCart.getTotal(),BigDecimal.valueOf(10.00));

        when(userRepo.findByUsername(u.getUsername())).thenReturn(null);

        final ResponseEntity<Cart> addToCartResponse_no_user = cartController.addTocart(req);
        assertEquals(404, addToCartResponse_no_user.getStatusCodeValue());

        final ResponseEntity<Cart> removeFromCartResponse_no_user = cartController.removeFromcart(req);
        assertEquals(404, removeFromCartResponse_no_user.getStatusCodeValue());

        when(userRepo.findByUsername(u.getUsername())).thenReturn(u);
        when(itemRepo.findById(i.getId())).thenReturn(Optional.<Item>empty());

        final ResponseEntity<Cart> addToCartResponse_no_item = cartController.addTocart(req);
        assertEquals(404, addToCartResponse_no_item.getStatusCodeValue());

        final ResponseEntity<Cart> removeFromCartResponse_no_item = cartController.removeFromcart(req);
        assertEquals(404, removeFromCartResponse_no_item.getStatusCodeValue());

    }


}
