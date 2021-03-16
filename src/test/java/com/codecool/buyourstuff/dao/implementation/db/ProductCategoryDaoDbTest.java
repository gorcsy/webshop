package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.ProductCategoryDao;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductCategoryDaoDbTest {
    private static final ProductCategoryDao categoryDao = DataManager.getProductCategoryDao();
    private ProductCategory testCategory;
    private ProductCategory testCategory2;

    @BeforeEach
    public void setup() {
        clearTable();
        testCategory = new ProductCategory("test category", "test description", "test department");
        testCategory2 = new ProductCategory("test category 2", "test description", "test department");
    }

    @Test
    public void TestAdd() {
        categoryDao.add(testCategory);
        categoryDao.add(testCategory2);

        assertNotEquals(0, testCategory.getId());
        assertNotEquals(0, testCategory2.getId());
    }

    @Test
    public void TestAddNull() {

        assertThrows(NullPointerException.class, () -> categoryDao.add(null));
    }

    @Test
    public void TestFindExistingId() {
        for (int i = 0; i < 10; i++) {
            categoryDao.add(testCategory);
        }

        ProductCategory foundCategory = categoryDao.find(10);

        assertEquals("test category", foundCategory.getName());
    }

    @Test
    public void TestFindNotExistingId() {
        for (int i = 0; i < 10; i++) {
            categoryDao.add(testCategory);
        }

        assertThrows(DataNotFoundException.class, () -> categoryDao.find(11));
    }

    @Test
    public void TestRemoveExistingId() {
        for (int i = 0; i < 5; i++) {
            categoryDao.add(testCategory);
        }

        assertNotNull(categoryDao.find(5));
        categoryDao.remove(5);
        assertThrows(DataNotFoundException.class, () -> categoryDao.find(5));
    }

    @Test
    public void TestRemoveNotExistingId() {
        for (int i = 0; i < 12; i++) {
            categoryDao.add(testCategory);
        }

        assertThrows(DataNotFoundException.class, () -> categoryDao.find(13));
    }

    @Test
    public void TestClear() {
        categoryDao.clear();

        assertThrows(DataNotFoundException.class, () -> categoryDao.find(1));
    }

    @Test
    public void TestGetAll() {
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                categoryDao.add(testCategory);
            } else {
                categoryDao.add(testCategory2);
            }
        }

        List<ProductCategory> categories = categoryDao.getAll();

        assertEquals(10, categories.size());

        int counter = 0;
        for (ProductCategory category : categories) {
            if (counter % 3 == 0)
                assertEquals("test category", category.getName());
            else
                assertEquals("test category 2", category.getName());

            counter++;
        }
    }

    private void clearTable() {
        categoryDao.clear();
    }
}
