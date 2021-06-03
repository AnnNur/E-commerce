package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ItemControllerTest {
    private static ItemController itemController;
    private static final ItemRepository itemRepository = mock(ItemRepository.class);
    Item item;
    Item item2;

    @BeforeClass
    public static void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Before
    public void init() {
        item = new Item(1L, "item", BigDecimal.valueOf(80), "brand new item");
        item2 = new Item(2L, "item2", BigDecimal.valueOf(50), "brand new item 2");
    }

    @Test
    public void getItems_ShouldReturn_ListOfAllItems() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item, item2));

        ResponseEntity<List<Item>> listResponseEntity = itemController.getItems();

        assertEquals(200, listResponseEntity.getStatusCodeValue());
        verify(itemRepository, atLeastOnce()).findAll();
        assertNotNull(listResponseEntity.getBody());
        assertThat(Objects.requireNonNull(listResponseEntity.getBody()).size(), is(2));
        assertEquals("item", listResponseEntity.getBody().get(0).getName());
    }

    @Test
    public void getItemById_ShouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> itemResponseEntity = itemController.getItemById(1L);

        assertEquals(200, itemResponseEntity.getStatusCodeValue());
        verify(itemRepository, atLeastOnce()).findById(1L);
        assertEquals(Optional.of(1L).get(), itemResponseEntity.getBody().getId());
    }

    @Test
    public void getItemsByName_ShouldReturn_ListOfAllItems() {
        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(item, item2));
        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItemsByName("item");

        assertEquals(200, itemResponseEntity.getStatusCodeValue());
        verify(itemRepository, atLeastOnce()).findByName("item");
        assertNotNull(itemResponseEntity.getBody());
        assertThat(Objects.requireNonNull(itemResponseEntity.getBody()).size(), is(2));
        assertEquals("item", itemResponseEntity.getBody().get(0).getName());
    }

    @Test
    public void getItemsByName_ShouldReturnNotFound_ForNonExistingItemName() {
        String wrongName = "nonExisting";
        when(itemRepository.findByName(wrongName)).thenReturn(null);

        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItemsByName(wrongName);

        assertThat(itemResponseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}