package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.ProductCategoryDao;
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
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDaoFile implements ProductCategoryDao {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileName;
    private static final Type LIST_TYPE = new TypeToken<List<ProductCategory>>() {
    }.getType();
    private List<ProductCategory> data = new ArrayList<>();

    public ProductCategoryDaoFile() {
        this.fileName = "product_category.json";
    }

    public ProductCategoryDaoFile(String fileName) {
        this.fileName = fileName;
    }

    private void writeDataToFile() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(ProductCategory category) {
        category.setId(data.size() + 1);
        data.add(category);
        writeDataToFile();
    }

    @Override
    public ProductCategory find(int id) {
        for (ProductCategory category : data) {
            if (category.getId() == id) {
                return category;
            }
        }
        throw new DataNotFoundException("No such category");
    }

    @Override
    public void remove(int id) {
        ProductCategory toRemove = find(id);
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
    public List<ProductCategory> getAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            data = gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
}
