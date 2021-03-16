package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.*;
import com.codecool.buyourstuff.model.*;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import com.codecool.buyourstuff.util.BaseData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LineItemDaoDbTest {
    private static final ProductDao productDao = DataManager.getProductDao();
    private static final LineItemDao lineItemDao = DataManager.getLineItemDao();
    private static final UserDao userDao = DataManager.getUserDao();
    private static final CartDao cartDao = DataManager.getCartDao();

    private final User testUser = BaseData.defaultUsers().get(0);
    private final Product testProduct = BaseData.defaultProducts().get(0);
    private final Product testProduct2 = BaseData.defaultProducts().get(1);
    private LineItem testLineItem;
    private LineItem testLineItem2;

    @BeforeEach
    public void setup() {
        clearTables();
        DataManager.init();

        productDao.add(testProduct);
        productDao.add(testProduct2);
        userDao.add(testUser);

        testLineItem = new LineItem(testProduct, testUser.getCartId(), 2);
        testLineItem2 = new LineItem(testProduct2, testUser.getCartId(), 11);

    }

    @Test
    public void TestAdd() {
        lineItemDao.add(testLineItem);
        lineItemDao.add(testLineItem2);

        assertNotEquals(0, testLineItem.getId());
        assertNotEquals(0, testLineItem2.getId());
    }

    @Test
    public void TestFindExistingId() {
        for (int i = 0; i < 7; i++) {
            lineItemDao.add(testLineItem);
        }

        LineItem foundLineItem = lineItemDao.find(7);

        assertNotNull(foundLineItem);
    }

    @Test
    public void TestFindNotExistingId() {
        for (int i = 0; i < 7; i++) {
            lineItemDao.add(testLineItem);
        }

        assertThrows(DataNotFoundException.class, () -> lineItemDao.find(8));
    }

    @Test
    public void TestUpdateLineItem() {
        lineItemDao.add(testLineItem);
        int testLineItemId = testLineItem.getId();

        lineItemDao.update(testLineItem, 88);

        assertEquals(88, lineItemDao.find(testLineItemId).getQuantity());
    }

    @Test
    public void TestGetBy() {
        lineItemDao.add(testLineItem);
        lineItemDao.add(testLineItem2);

        int cartId = testUser.getCartId();
        Cart cart = cartDao.find(cartId);

        List<LineItem> actual = lineItemDao.getBy(cart);

        List<LineItem> expected = Arrays.asList(testLineItem, testLineItem2);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getQuantity(), actual.get(i).getQuantity());
            assertEquals(expected.get(i).getProduct().getName(), actual.get(i).getProduct().getName());
        }
    }

    @Test
    public void TestRemove() {
        lineItemDao.add(testLineItem);
        int testLineItemId = testLineItem.getId();
        String exceptionMessage = "";

        lineItemDao.remove(testLineItem);

        try {
            lineItemDao.find(testLineItemId);
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        assertEquals("No such line-item", exceptionMessage);
    }

    @Test
    public void TestClear() {
        List<Integer> lineItemIds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            lineItemDao.add(testLineItem);
            lineItemIds.add(testLineItem.getId());
        }
        String exceptionMessage = "";

        lineItemDao.clear();

        for (int id : lineItemIds) {
            try {
                lineItemDao.find(id);
            } catch (Exception e) {
                exceptionMessage = e.getMessage();
            }
            assertEquals("No such line-item", exceptionMessage);
        }
    }

    private void clearTables() {
        DataManager.clear();
    }
}
