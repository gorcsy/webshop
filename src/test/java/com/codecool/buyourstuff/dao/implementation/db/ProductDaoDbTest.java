package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.ProductCategoryDao;
import com.codecool.buyourstuff.dao.ProductDao;
import com.codecool.buyourstuff.dao.SupplierDao;
import com.codecool.buyourstuff.model.Product;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import com.codecool.buyourstuff.util.BaseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoDbTest {

    private static final ProductDao productDao = DataManager.getProductDao();
    private static final SupplierDao supplierDao = DataManager.getSupplierDao();
    private static final ProductCategoryDao categoryDao = DataManager.getProductCategoryDao();

    private final ProductCategory testCategory = BaseData.defaultProductCategories().get(0);
    private final ProductCategory testCategory2 = BaseData.defaultProductCategories().get(1);
    private final Supplier testSupplier = BaseData.defaultSuppliers().get(0);
    private final Supplier testSupplier2 = BaseData.defaultSuppliers().get(1);
    private Product testProduct;
    private Product testProduct2;

    @BeforeEach
    public void setup() {
        clearTables();
        categoryDao.add(testCategory);
        categoryDao.add(testCategory2);
        supplierDao.add(testSupplier);
        supplierDao.add(testSupplier2);
        testProduct = new Product("Test product", new BigDecimal("0.00"),
                "HUF", "Test product description",
                testCategory, testSupplier);
        testProduct2 = new Product("Test product 2", new BigDecimal("0.00"),
                "EUR", "Test product 2 description",
                testCategory2, testSupplier2);
    }

    @Test
    public void TestAdd() {
        productDao.add(testProduct);
        productDao.add(testProduct2);

        assertNotEquals(0, testProduct.getId());
        assertNotEquals(0, testProduct2.getId());
    }

    @Test
    public void TestAddNull() {

        assertThrows(NullPointerException.class, () -> productDao.add(null));
    }

    @Test
    public void TestFindExistingId() {
        for (int i = 0; i < 10; i++) {
            productDao.add(testProduct);
        }

        Product foundProduct = productDao.find(10);

        assertEquals("Test product", foundProduct.getName());
    }

    @Test
    public void TestFindNotExistingId() {
        for (int i = 0; i < 10; i++) {
            productDao.add(testProduct);
        }

        assertThrows(DataNotFoundException.class, () -> productDao.find(11));
    }

    @Test
    public void TestRemoveExistingId() {
        for (int i = 0; i < 5; i++) {
            productDao.add(testProduct);
        }

        assertNotNull(productDao.find(5));
        productDao.remove(5);
        assertThrows(DataNotFoundException.class, () -> productDao.find(5));
    }

    @Test
    public void TestRemoveNotExistingId() {
        for (int i = 0; i < 12; i++) {
            productDao.add(testProduct);
        }

        assertThrows(DataNotFoundException.class, () -> productDao.find(13));
    }

    @Test
    public void TestClear() {
        productDao.clear();

        assertThrows(DataNotFoundException.class, () -> productDao.find(1));
    }

    @Test
    public void TestGetByCategory() {
        for (int i = 0; i < 10; i++) {
            productDao.add(testProduct);
            if (i % 2 == 0) {
                productDao.add(testProduct2);
            }
        }

        List<Product> category1products = productDao.getBy(testCategory);
        List<Product> category2products = productDao.getBy(testCategory2);

        assertEquals(10, category1products.size());
        assertEquals(5, category2products.size());
    }

    @Test
    public void TestGetByCategoryNotExisting() {
        productDao.add(testProduct);

        List<Product> products = productDao.getBy(testCategory2);

        assertEquals(new ArrayList<>(), products);
    }

    @Test
    public void TestGetBySupplier() {
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                productDao.add(testProduct);
            }
            productDao.add(testProduct2);
        }

        List<Product> supplier1products = productDao.getBy(testSupplier);
        List<Product> supplier2products = productDao.getBy(testSupplier2);

        assertEquals(4, supplier1products.size());
        assertEquals(10, supplier2products.size());
    }

    @Test
    public void TestGetBySupplierNotExisting() {
        productDao.add(testProduct);

        List<Product> products = productDao.getBy(testSupplier2);

        assertEquals(new ArrayList<>(), products);
    }

    @Test
    public void TestGetAll() {
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                productDao.add(testProduct);
            } else {
                productDao.add(testProduct2);
            }
        }

        List<Product> products = productDao.getAll();

        assertEquals(10, products.size());

        int counter = 0;
        for (Product product : products) {
            if (counter % 3 == 0)
                assertEquals("Test product", product.getName());
            else
                assertEquals("Test product 2", product.getName());

            counter++;
        }
    }

    @Test
    public void TestGetAllNoProduct() {
        List<Product> products = productDao.getAll();

        assertEquals(new ArrayList<>(), products);
    }

    private void clearTables() {
        productDao.clear();
        categoryDao.clear();
        supplierDao.clear();
    }
}
