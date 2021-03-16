package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.dao.CartDao;
import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.UserDao;
import com.codecool.buyourstuff.model.Cart;
import com.codecool.buyourstuff.model.User;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserDaoFile implements UserDao {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<User>>() {
    }.getType();
    private final String fileName;
    private List<User> data = new ArrayList<>();

    public UserDaoFile() {
        this.fileName = "user.json";
        readIntoMemory();
    }

    public UserDaoFile(String fileName) {
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

    private List<User> readAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            return gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(User user) {
        if (isNameAvailable(user.getName())) {
            Cart cart = getNewCart();

            user.setCartId(cart.getId());
            user.setId(data.size() + 1);
            data.add(user);
        }
        writeUserListToFile();
    }

    private Cart getNewCart() {
        CartDao cartDao = DataManager.getCartDao();
        Cart cart = new Cart("USD");
        cartDao.add(cart);

        return cart;
    }

    @Override
    public User find(String userName, String password) {
        List<User> users = getAll();
        if (users != null) {
            for (User user : users) {
                if (user.getName().equals(userName)) {
                    if (!BCrypt.checkpw(password, user.getPassword())) {
                        throw new DataNotFoundException("Wrong password");
                    }
                    return user;
                }
            }
        }
        throw new DataNotFoundException("Wrong username");
    }

    @Override
    public void clear() {
        data.clear();
        writeUserListToFile();
    }

    @Override
    public boolean isNameAvailable(String username) {
        return data
                .stream()
                .map(User::getName)
                .noneMatch(name -> name.equals(username));
    }

    private void writeUserListToFile() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAll() {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            data = gson.fromJson(reader, LIST_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
