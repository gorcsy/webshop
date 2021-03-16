package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.model.Product;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import com.codecool.buyourstuff.util.BaseData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoFileTest {
    private static final ProductDaoFile productDao = new ProductDaoFile("product_test.json");
    private static final ProductCategory category = BaseData.defaultProductCategories().get(0);
    private static final ProductCategory category2 = BaseData.defaultProductCategories().get(1);
    private static final Supplier supplier = BaseData.defaultSuppliers().get(0);
    private static final Supplier supplier2 = BaseData.defaultSuppliers().get(1);
    private static final Product testProduct = new Product("test product",
            new BigDecimal("0.00"), "EUR", "test description",
            category, supplier);
    private static final Product testProduct2 = new Product("test product 2",
            new BigDecimal("0.00"), "HUF", "test description",
            category2, supplier2);

    @BeforeAll
    public static void setup() {
        category2.setId(1);
        category2.setId(2);
        supplier.setId(1);
        supplier2.setId(2);
    }

    @Test
    public void testAdd() {
        productDao.add(testProduct);

        assertNotEquals(0, testProduct.getId());
    }

    @Test
    public void testFind() {
        productDao.add(testProduct);
        int id = testProduct.getId();
        Product foundProduct = productDao.find(id);

        assertEquals(testProduct, foundProduct);
    }

    @Test
    public void testFindNotExistingId() {

        assertThrows(DataNotFoundException.class, () -> productDao.find(9));
    }

    @Test
    public void testClear() {
        productDao.add(testProduct);
        productDao.add(testProduct);
        productDao.clear();

        assertEquals(new ArrayList<>(), productDao.getAll());
    }

    @Test
    public void testGetByCategory() {
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                productDao.add(testProduct);
            } else {
                productDao.add(testProduct2);
            }
        }

        List<Product> productsOfCategory1 = productDao.getBy(category);
        List<Product> productsOfCategory2 = productDao.getBy(category2);
        assertEquals(4, productsOfCategory1.size());
        assertEquals(6, productsOfCategory2.size());
    }

    @Test
    public void testGetBySupplier() {
        for (int i = 0; i < 10; i++) {
            if (i % 7 == 0) {
                productDao.add(testProduct);
            } else {
                productDao.add(testProduct2);
            }
        }

        List<Product> productsOfSupplier1 = productDao.getBy(supplier);
        List<Product> productsOfSupplier2 = productDao.getBy(supplier2);
        assertEquals(2, productsOfSupplier1.size());
        assertEquals(8, productsOfSupplier2.size());
    }

    @Test
    public void testGetAll() {
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                productDao.add(testProduct);
            } else {
                productDao.add(testProduct2);
            }
        }

        List<Product> products = productDao.getAll();

        assertEquals(10, products.size());
    }

    @Test
    public void testRemove() {
        productDao.add(testProduct);
        int id = testProduct.getId();

        productDao.remove(id);

        assertThrows(DataNotFoundException.class, () -> productDao.find(id));
    }

    @AfterEach
    public void clear() {
        productDao.clear();
    }
}
