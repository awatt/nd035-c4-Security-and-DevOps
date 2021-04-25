package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserController userController;

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private UserRepository userRepo = mock(UserRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        userController = new UserController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void submit_order_path() {
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

        Cart userCart = u.getCart();
        userCart.addItem(i);

        when(userRepo.findByUsername(u.getUsername())).thenReturn(u);

        ResponseEntity<UserOrder> orderResponse = orderController.submit(u.getUsername());
        UserOrder order = orderResponse.getBody();
        assertNotNull(order);
        assertEquals(order.getTotal(), BigDecimal.valueOf(10.00));

        List<UserOrder> uOrders = new ArrayList<>();
        uOrders.add(order);

        when(orderRepo.findByUser(u)).thenReturn(uOrders);

        ResponseEntity<List<UserOrder>> userOrdersResponse = orderController.getOrdersForUser(u.getUsername());
        List<UserOrder> userOrders = userOrdersResponse.getBody();
        assertNotNull(userOrders);
        assertEquals(userOrders.size(), 1);
        assertEquals(userOrders.get(0).getTotal(), BigDecimal.valueOf(10.00));

        when(userRepo.findByUsername(u.getUsername())).thenReturn(null);

        ResponseEntity<UserOrder> orderResponse_no_user = orderController.submit(u.getUsername());
        assertEquals(404, orderResponse_no_user.getStatusCodeValue());

        ResponseEntity<List<UserOrder>> userOrdersResponse_no_user = orderController.getOrdersForUser(u.getUsername());
        assertEquals(404, userOrdersResponse_no_user.getStatusCodeValue());

    }

}
