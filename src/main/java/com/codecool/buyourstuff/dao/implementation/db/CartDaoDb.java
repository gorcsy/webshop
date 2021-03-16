package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.CartDao;
import com.codecool.buyourstuff.model.Cart;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDaoDb extends BaseDaoDb implements CartDao {

    @Override
    public void add(Cart cart) {
        String sql = "INSERT INTO public.cart (currency) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, cart.getCurrency().toString());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            cart.setId(resultSet.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("CartDaoDb add(): " + e.getSQLState());
        }
    }

    @Override
    public Cart find(int id) {
        String sql = "SELECT currency FROM public.cart WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Cart cart = new Cart(resultSet.getString("currency"));
                cart.setId(id);
                return cart;
            }
            throw new DataNotFoundException("No such cart");

        } catch (SQLException e) {
            throw new RuntimeException("CartDaoDb find(): " + e.getSQLState());
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM public.cart WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("CartDaoDb remove(): " + e.getSQLState());
        }
    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE public.cart RESTART IDENTITY CASCADE;";
        try (Connection conn = getConnection()) {
            conn.prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("CartDaoDb clear(): " + e.getSQLState());
        }
    }

    @Override
    public List<Cart> getAll() {
        List<Cart> res = new ArrayList<>();

        String sql = "SELECT * FROM public.cart";
        try (Connection conn = getConnection();
             ResultSet resultSet = conn.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                Cart cart = new Cart(resultSet.getString("currency"));
                cart.setId(resultSet.getInt("id"));
                res.add(cart);
            }
            return res;
        } catch (SQLException e) {
            throw new RuntimeException("CartDaoDb getAll(): " + e.getSQLState());
        }
    }

    @Override
    void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS public.cart (" +
                "id serial NOT NULL PRIMARY KEY," +
                "currency text NOT NULL);";
        String triggerMethod = "createCartTable";
        checkForExecutionSuccess(create, triggerMethod);

    }
}
