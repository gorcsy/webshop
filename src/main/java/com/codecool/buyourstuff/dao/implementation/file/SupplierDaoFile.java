package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.SupplierDao;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoFile implements SupplierDao {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileName;
    private static final Type LIST_TYPE = new TypeToken<List<Supplier>>() {
    }.getType();
    private List<Supplier> supplierList = new ArrayList<>();

    public SupplierDaoFile() {
        this.fileName = "supplier.json";
    }

    public SupplierDaoFile(String fileName) {
        this.fileName = fileName;
    }

    private void writeSupplierListToFile() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(supplierList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Supplier supplier) {
        supplier.setId(supplierList.size() + 1);
        supplierList.add(supplier);
        writeSupplierListToFile();
    }

    @Override
    public Supplier find(int id) {
        for (Supplier supplier : supplierList) {
            if (supplier.getId() == id) {
                return supplier;
            }
        }

        throw new DataNotFoundException("No Supplier with the given id was found");

    }

    @Override
    public void remove(int id) {
        Supplier toRemove = find(id);
        if (toRemove != null) {
            supplierList.remove(toRemove);
        }
        writeSupplierListToFile();
    }

    @Override
    public void clear() {
        try {
            Files.newBufferedWriter(Paths.get(fileName), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Supplier> getAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            supplierList = gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return supplierList;
    }
}
