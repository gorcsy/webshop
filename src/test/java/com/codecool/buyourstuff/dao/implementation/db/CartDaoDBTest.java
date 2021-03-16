package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.CartDao;
import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.model.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class CartDaoDBTest {

    private static final CartDao cartDao = DataManager.getCartDao();
    private Cart testCart;

    @BeforeEach
    public void setup() {
        clearTable();
        testCart = new Cart("EUR");
    }

    @Test
    public void testAddCartDao() {
        cartDao.add(testCart);
        assertEquals(1, testCart.getId());
    }

    @Test
    public void testFindCart(){
        cartDao.add(testCart);
        assertEquals("EUR", cartDao.find(1).getCurrency().toString());
    }

    @Test
    public void testRemove(){
        cartDao.add(testCart);
        Cart testCart2 = new Cart( "HUF");
        cartDao.add(testCart2);
        cartDao.remove(1);
        assertEquals("HUF",cartDao.getAll().get(0).getCurrency().toString());
    }

    @Test
    public void testGetAllInFile(){
        cartDao.add(testCart);
        Cart cart2 = new Cart( "HUF");
        cartDao.add(cart2);
        assertEquals(2, cartDao.getAll().size());
        assertEquals(2, cartDao.getAll().get(1).getId());
    }

    @Test
    public void testClearFile(){
        cartDao.add(testCart);
        Cart cart = new Cart( "HUF");
        cartDao.add(cart);
        cartDao.clear();
        assertEquals(0,cartDao.getAll().size());
    }

    private void clearTable() {
        cartDao.clear();
    }
}

