package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.SupplierDao;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SupplierDaoFileTest {

    private final SupplierDao SUPPLIER_DAO_FILE = new SupplierDaoFile("supplier_test.json");
    private Supplier supplier1;
    private Supplier supplier2;
    private Supplier supplier3;


    @BeforeEach
    void setup() {
        supplier1 = new Supplier("test1", "test1");
        supplier2 = new Supplier("test2", "test2");
        supplier3 = new Supplier("test3", "test3");
    }
    @AfterEach
    void tearDown() {
        SUPPLIER_DAO_FILE.clear();
    }

    @Test
    void testAdd() {
        SUPPLIER_DAO_FILE.add(supplier1);
        assertEquals(1, SUPPLIER_DAO_FILE.getAll().size());
    }

    @Test
    void testAdd_Null_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> SUPPLIER_DAO_FILE.add(null));
    }
    @Test
    void testFind_validId() {
        SUPPLIER_DAO_FILE.add(supplier1);

        Supplier result = SUPPLIER_DAO_FILE.find(supplier1.getId());
        assertEquals(supplier1.getId(), result.getId());
    }

    @Test
    void testFind_invalidId_exceptionThrown() {
        assertThrows(DataNotFoundException.class, () -> SUPPLIER_DAO_FILE.find(-1));
    }

    @Test
    void testRemove_existingId() {
        SUPPLIER_DAO_FILE.add(supplier1);
        assertNotNull(SUPPLIER_DAO_FILE.find(supplier1.getId()));

        SUPPLIER_DAO_FILE.remove(supplier1.getId());
        assertThrows(DataNotFoundException.class, () -> SUPPLIER_DAO_FILE.find(supplier1.getId()));
    }

    @Test
    void testRemove_nonExistingId() {
        SUPPLIER_DAO_FILE.add(supplier1);

        assertThrows(DataNotFoundException.class, () -> SUPPLIER_DAO_FILE.find(11));
    }

    @Test
    void tesClear() {
        SUPPLIER_DAO_FILE.add(supplier1);
        SUPPLIER_DAO_FILE.add(supplier2);
        SUPPLIER_DAO_FILE.add(supplier3);
        SUPPLIER_DAO_FILE.clear();
        assertNull(SUPPLIER_DAO_FILE.getAll());
    }

    @Test
    void getAll() {
        SUPPLIER_DAO_FILE.add(supplier1);
        SUPPLIER_DAO_FILE.add(supplier2);
        SUPPLIER_DAO_FILE.add(supplier3);

        assertEquals(3, SUPPLIER_DAO_FILE.getAll().size());
    }
}