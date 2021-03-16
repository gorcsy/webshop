package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.LineItemDao;
import com.codecool.buyourstuff.model.Cart;
import com.codecool.buyourstuff.model.LineItem;
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

public class LineItemDaoFile implements LineItemDao {

    private final String fileName;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<LineItem>>() {
    }.getType();
    private List<LineItem> lineItemList;
    private int currentId;

    public LineItemDaoFile() {
        fileName = "line_item.json";
        readIntoMemory();
    }

    public LineItemDaoFile(String fileName) { //to save tests into a different file
        this.fileName = fileName;
        readIntoMemory();
    }

    private void readIntoMemory() {
        createFile();
        lineItemList = readAll();
        if (lineItemList == null) {
            lineItemList = new ArrayList<>();
        }
        initCurrentId();
    }

    private void initCurrentId() {
        if (lineItemList.size() == 0) {
            currentId = 0;
        } else {
            currentId = lineItemList.get(lineItemList.size() - 1).getId();
        }
    }

    @Override
    public void add(LineItem lineItem) {
        currentId++;
        lineItem.setId(currentId);
        lineItemList.add(lineItem);
        writeLineItemListToFile();
    }

    @Override
    public void remove(LineItem lineItem) {
        LineItem itemToRemove = find(lineItem.getId());
        if (itemToRemove != null) {
            lineItemList.remove(itemToRemove);
        }
        writeLineItemListToFile();
    }

    @Override
    public void clear() {
        try {
            Files.newBufferedWriter(Paths.get(fileName), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lineItemList.clear();
        initCurrentId();
    }

    @Override
    public void update(LineItem lineItem, int quantity) {
        LineItem itemToUpdate = find(lineItem.getId());
        itemToUpdate.setQuantity(quantity);
        writeLineItemListToFile();
    }

    @Override
    public LineItem find(int id) {
        for (LineItem lineItem : lineItemList) {
            if (lineItem.getId() == id) {
                return lineItem;
            }
        }
        throw new DataNotFoundException("No such line-item");
    }

    @Override
    public List<LineItem> getBy(Cart cart) {
        List<LineItem> lineItems = new ArrayList<>();
        for (LineItem lineItem : lineItemList) {
            if (cart.getId() == lineItem.getCartId()) {
                lineItems.add(lineItem);
            }
        }
        return lineItems;
    }

    private void writeLineItemListToFile() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(lineItemList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<LineItem> readAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            return gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
