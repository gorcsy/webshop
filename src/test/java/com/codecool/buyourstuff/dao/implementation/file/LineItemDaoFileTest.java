package com.codecool.buyourstuff.dao.implementation.file;


import com.codecool.buyourstuff.dao.LineItemDao;
import com.codecool.buyourstuff.model.Cart;
import com.codecool.buyourstuff.model.LineItem;
import com.codecool.buyourstuff.model.Product;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.List;


import static org.junit.Assert.assertEquals;

class LineItemDaoFileTest {

    private final LineItemDao lineItemDao = new LineItemDaoFile("line_item_test.json");
    private LineItem lineItem;
    private LineItem lineItem2;
    private LineItem lineItem3;
    private Cart cart;

    @BeforeEach
    void setUp() {
        ProductCategory productCategory = new ProductCategory("", "", "");
        Supplier supplier = new Supplier("", "");
        Product product = new Product("test", BigDecimal.valueOf(31), "USD", "", productCategory, supplier);
        product.setId(1);
        lineItem = new LineItem(product, 2, 3);
        lineItem2 = new LineItem(product, 2, 5);
        lineItem3 = new LineItem(product, 3, 2);
        cart = new Cart();
        cart.setId(2);
    }

    @AfterEach
    void tearDown() {
        lineItemDao.clear();
    }

    @Test
    void testAdd() {
        lineItemDao.add(lineItem);
        LineItem lI = lineItemDao.find(lineItem.getId());

        assertEquals(lI, lineItem);
    }

    @Test
    void testRemove() {
        lineItemDao.add(lineItem);
        lineItemDao.add(lineItem2);
        lineItemDao.remove(lineItem);

        assertEquals(1, lineItemDao.getBy(cart).size());
    }

    @Test
    void testClear() {
        lineItemDao.add(lineItem);
        lineItemDao.add(lineItem2);
        lineItemDao.clear();

        assertEquals(0, lineItemDao.getBy(cart).size());
    }

    @Test
    void testUpdate() {
        lineItemDao.add(lineItem);
        lineItemDao.update(lineItem, 8);

        assertEquals(8, lineItemDao.find(lineItem.getId()).getQuantity());
    }

    @Test
    void testFind() {
        lineItemDao.add(lineItem);
        lineItemDao.add(lineItem2);
        lineItemDao.add(lineItem3);

        assertEquals(1, lineItem.getId());
        assertEquals(2, lineItem2.getId());
        assertEquals(3, lineItem3.getId());
    }

    @Test
    void testFindWhenOneRemoved() {
        lineItemDao.add(lineItem);
        lineItemDao.add(lineItem2);
        lineItemDao.add(lineItem3);
        lineItemDao.remove(lineItem2);

        assertEquals(1, lineItem.getId());
        assertEquals(3, lineItem3.getId());
    }
    @Test
    void testFindWhenOneRemovedThenAdded() {
        lineItemDao.add(lineItem);
        lineItemDao.add(lineItem2);
        lineItemDao.add(lineItem3);
        lineItemDao.remove(lineItem2);
        lineItemDao.add(lineItem2);

        assertEquals(1, lineItem.getId());
        assertEquals(3, lineItem3.getId());
        assertEquals(4, lineItem2.getId());
    }

    @Test
    void testFindAddedAfterClearing() {
        lineItemDao.add(lineItem);
        lineItemDao.add(lineItem2);
        lineItemDao.add(lineItem3);
        lineItemDao.clear();
        lineItemDao.add(lineItem2);

        assertEquals(1, lineItem2.getId());
    }

    @Test
    void testGetBy() {
        lineItemDao.add(lineItem);
        lineItemDao.add(lineItem2);
        lineItemDao.add(lineItem3);
        List<LineItem> lineItems = lineItemDao.getBy(cart);

        assertEquals(2, lineItems.size());
    }
}