package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.SupplierDao;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SupplierDaoDbTest {
    private static final SupplierDao supplierDao = DataManager.getSupplierDao();
    private Supplier testSupplier;
    private Supplier testSupplier2;

    @BeforeEach
    public void setup() {
        clearTable();
        testSupplier = new Supplier("test supplier", "test description");
        testSupplier2 = new Supplier("test supplier 2", "test description");
    }

    @Test
    public void TestAdd() {
        supplierDao.add(testSupplier);
        supplierDao.add(testSupplier2);

        assertNotEquals(0, testSupplier.getId());
        assertNotEquals(0,testSupplier2.getId());
    }

    @Test
    public void TestAddNull() {

        assertThrows(NullPointerException.class, () -> supplierDao.add(null));
    }

    @Test
    public void TestFindExistingId() {
        for (int i = 0; i < 10; i++) {
            supplierDao.add(testSupplier);
        }

        Supplier foundSupplier = supplierDao.find(10);

        assertEquals("test supplier", foundSupplier.getName());
    }

    @Test
    public void TestFindNotExistingId() {
        for (int i = 0; i < 10; i++) {
            supplierDao.add(testSupplier);
        }

        assertThrows(DataNotFoundException.class, () -> supplierDao.find(11));
    }

    @Test
    public void TestRemoveExistingId() {
        for (int i = 0; i < 5; i++) {
            supplierDao.add(testSupplier);
        }

        assertNotNull(supplierDao.find(5));
        supplierDao.remove(5);
        assertThrows(DataNotFoundException.class, () -> supplierDao.find(5));
    }

    @Test
    public void TestRemoveNotExistingId() {
        for (int i = 0; i < 12; i++) {
            supplierDao.add(testSupplier);
        }

        assertThrows(DataNotFoundException.class, () -> supplierDao.find(13));
    }

    @Test
    public void TestClear() {

        supplierDao.clear();

        assertThrows(DataNotFoundException.class, () -> supplierDao.find(1));
    }

    @Test
    public void TestGetAll() {
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                supplierDao.add(testSupplier);
            } else {
                supplierDao.add(testSupplier2);
            }
        }

        List<Supplier> suppliers = supplierDao.getAll();

        assertEquals(10, suppliers.size());

        int counter = 0;
        for (Supplier supplier : suppliers) {
            if (counter % 3 == 0)
                assertEquals("test supplier", supplier.getName());
            else
                assertEquals("test supplier 2", supplier.getName());

            counter++;
        }
    }

    private void clearTable() {
        supplierDao.clear();
    }
}
