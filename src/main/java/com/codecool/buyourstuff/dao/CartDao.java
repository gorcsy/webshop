package com.codecool.buyourstuff.dao;

import com.codecool.buyourstuff.model.Cart;

import java.util.List;


public interface CartDao {
    void add(Cart cart);
    Cart find(int id);
    void remove(int id);
    void clear();
    List<Cart> getAll();
}
