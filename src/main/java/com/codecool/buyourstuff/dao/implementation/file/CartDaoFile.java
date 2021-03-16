package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.CartDao;
import com.codecool.buyourstuff.model.Cart;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CartDaoFile implements CartDao {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<Cart>>() {
    }.getType();
    private List<Cart> data = new ArrayList<>();

    private final String fileName;

    public CartDaoFile() {
        this.fileName = "cart.json";
        readIntoMemory();
    }

    public CartDaoFile(String fileName) {
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

    private List<Cart> readAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            return gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Cart cart) {
        if (data == null) {
            data = new ArrayList<>();
        }
        cart.setId(data.size() + 1);
        data.add(cart);

        writeCartListToFile();
    }

    @Override
    public Cart find(int id) {
        List<Cart> carts = getAll();
        for (Cart cart : carts) {
            if (cart.getId() == id) {
                return cart;
            }
        }
        throw new DataNotFoundException("No such cart");
    }

    @Override
    public void remove(int id) {
        Cart toRemove = find(id);
        if (toRemove != null) {
            data.remove(toRemove);

        }
        writeCartListToFile();
    }

    private void writeCartListToFile() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public List<Cart> getAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            data = gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
