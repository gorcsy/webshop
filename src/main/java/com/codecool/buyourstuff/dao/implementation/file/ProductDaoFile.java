package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.ProductDao;
import com.codecool.buyourstuff.model.Product;
import com.codecool.buyourstuff.model.ProductCategory;
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
import java.util.ArrayList;
import java.util.List;

public class ProductDaoFile implements ProductDao {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileName;
    private static final Type LIST_TYPE = new TypeToken<List<Product>>() {
    }.getType();
    private List<Product> data;

    public ProductDaoFile() {
        this.fileName = "product.json";
        readIntoMemory();
    }

    public ProductDaoFile(String fileName) {
        this.fileName = fileName;
        readIntoMemory();
    }

    private void readIntoMemory() {
        createFile();
        data = readAll();
        if (data == null) {
            data = new ArrayList<>();
        }
    }

    private void createFile() {
        if (!Files.exists(Paths.get(fileName))) {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Product> readAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            return gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Product product) {
        product.setId(data.size() + 1);
        data.add(product);
        writeDataToFile();
    }

    private void writeDataToFile() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product find(int id) {
        for (Product product : data) {
            if (product.getId() == id) {
                return product;
            }
        }

        throw new DataNotFoundException("No such product");
    }

    @Override
    public void remove(int id) {
        Product toRemove = find(id);
        if (toRemove != null) {
            data.remove(toRemove);
        }
        writeDataToFile();
    }

    @Override
    public void clear() {
        data.clear();
        writeDataToFile();
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        List<Product> products = new ArrayList<>();
        for (Product product : data) {
            if (product.getSupplier().getId() == supplier.getId()) {
                products.add(product);
            }
        }
        return products;
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        List<Product> products = new ArrayList<>();
        for (Product product : data) {
            if (product.getProductCategory().getId() == productCategory.getId()) {
                products.add(product);
            }
        }
        return products;
    }

    @Override
    public List<Product> getAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            data = gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
