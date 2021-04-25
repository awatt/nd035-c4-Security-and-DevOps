package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void find_item_path() {
        Item i = new Item();
        i.setId((long) 1);
        i.setName("widget");
        i.setPrice(BigDecimal.valueOf(10.00));
        i.setDescription("it gets wids");

        List<Item> iList = new ArrayList<>();
        iList.add(i);

        when(itemRepo.findAll()).thenReturn(iList);

        ResponseEntity<List<Item>> findAllResponse = itemController.getItems();
        assertEquals(200, findAllResponse.getStatusCodeValue());
        List<Item> foundItems = findAllResponse.getBody();
        assertNotNull(foundItems);
        assertEquals(foundItems.get(0).getName(),"widget");

        when(itemRepo.findById((long) 1)).thenReturn(java.util.Optional.of(i));

        ResponseEntity<Item> findByIdResponse = itemController.getItemById((long) 1);
        assertEquals(200, findByIdResponse.getStatusCodeValue());
        Item foundItem = findByIdResponse.getBody();
        assertNotNull(foundItem);
        assertEquals(foundItem.getName(),"widget");

        when(itemRepo.findByName("testUser")).thenReturn(iList);

        ResponseEntity<List<Item>> findByNameResponse = itemController.getItemsByName("testUser");
        assertEquals(200, findByNameResponse.getStatusCodeValue());
        List<Item> foundByNameItems = findAllResponse.getBody();
        assertNotNull(foundByNameItems);
        assertEquals(foundByNameItems.get(0).getName(),"widget");

        when(itemRepo.findByName("testUser")).thenReturn(null);

        ResponseEntity<List<Item>> findByNameResponse_no_items = itemController.getItemsByName("testUser");
        assertEquals(404, findByNameResponse_no_items.getStatusCodeValue());

    }

}
