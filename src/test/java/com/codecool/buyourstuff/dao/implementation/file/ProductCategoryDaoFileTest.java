package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.ProductCategoryDao;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryDaoFileTest {

    private static final ProductCategoryDao PRODUCT_CATEGORY_DAO_FILE = new ProductCategoryDaoFile("product_category_test.json");
    private ProductCategory productCategory1;
    private ProductCategory productCategory2;
    private ProductCategory productCategory3;

    @BeforeEach
    void setup() {
        productCategory1 = new ProductCategory("test1", "test1", "test1");
        productCategory2 = new ProductCategory("test2", "test2", "test2");
        productCategory3 = new ProductCategory("test3", "test3", "test3");
    }

    @AfterEach
    void tearDown() {
        PRODUCT_CATEGORY_DAO_FILE.clear();
    }

    @Test
    void testAdd() {
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory1);
        assertEquals(1, PRODUCT_CATEGORY_DAO_FILE.getAll().size());
    }

    @Test
    void testAdd_Null_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> PRODUCT_CATEGORY_DAO_FILE.add(null));
    }

    @Test
    void testFind_validId() {
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory1);

        ProductCategory result = PRODUCT_CATEGORY_DAO_FILE.find(productCategory1.getId());
        assertEquals(productCategory1.getId(), result.getId());
    }

    @Test
    void testFind_invalidId_exceptionThrown() {
        assertThrows(DataNotFoundException.class, () -> PRODUCT_CATEGORY_DAO_FILE.find(-1));
    }

    @Test
    void testRemove_existingId() {
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory1);
        assertNotNull(PRODUCT_CATEGORY_DAO_FILE.find(productCategory1.getId()));

        PRODUCT_CATEGORY_DAO_FILE.remove(productCategory1.getId());
        assertThrows(DataNotFoundException.class, () -> PRODUCT_CATEGORY_DAO_FILE.find(productCategory1.getId()));
    }
    @Test
    void testRemove_nonExistingId() {
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory1);

        assertThrows(DataNotFoundException.class, () -> PRODUCT_CATEGORY_DAO_FILE.find(11));
    }

    @Test
    void testClear() {
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory1);
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory2);
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory3);
        PRODUCT_CATEGORY_DAO_FILE.clear();

        assertEquals(new ArrayList<>(), PRODUCT_CATEGORY_DAO_FILE.getAll());
    }

    @Test
    void getAll() {
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory1);
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory2);
        PRODUCT_CATEGORY_DAO_FILE.add(productCategory3);
        int size = PRODUCT_CATEGORY_DAO_FILE.getAll().size();

        assertEquals(3,size);
    }
}