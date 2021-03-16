package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.CartDao;
import com.codecool.buyourstuff.model.Cart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;


public class CartDaoFileTest {

    private static final CartDao CART_DAO_FILE = new CartDaoFile("cart_test.json");

    @AfterEach
    public void cleanUp(){
        CART_DAO_FILE.clear();
    }

    @Test
    public void testAddToFile(){
        Cart cart = new Cart();
        CART_DAO_FILE.add(cart);
        assertEquals("USD", cart.getCurrency().toString());
    }

    @Test
     public void testFindInFile(){
        Cart cart = new Cart( "USD");
        CART_DAO_FILE.add(cart);
        assertEquals("USD", CART_DAO_FILE.find(1).getCurrency().toString());
    }

    @Test
    public void testRemove(){
        Cart cart = new Cart( "EUR");
        Cart cart2 = new Cart( "HUF");
        CART_DAO_FILE.add(cart);
        CART_DAO_FILE.add(cart2);
        CART_DAO_FILE.remove(1);
        assertEquals("HUF",CART_DAO_FILE.getAll().get(0).getCurrency().toString());
    }

    @Test
    public void testGetAllInFile(){
        Cart cart = new Cart( "EUR");
        Cart cart2 = new Cart( "HUF");
        CART_DAO_FILE.add(cart);
        CART_DAO_FILE.add(cart2);
        assertEquals(2, CART_DAO_FILE.getAll().size());
        assertEquals(2, CART_DAO_FILE.getAll().get(1).getId());
    }

    @Test
    public void testClearFile(){
        Cart cart = new Cart( "EUR");
        CART_DAO_FILE.add(cart);
        CART_DAO_FILE.clear();
        assertNull(CART_DAO_FILE.getAll());
    }
}
