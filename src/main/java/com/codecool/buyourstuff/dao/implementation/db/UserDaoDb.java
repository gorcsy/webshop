package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.UserDao;
import com.codecool.buyourstuff.model.Cart;
import com.codecool.buyourstuff.model.User;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDaoDb extends BaseDaoDb implements UserDao {

    @Override
    public void add(User user) {
        Cart cart = new Cart("USD");
        DataManager.getCartDao().add(cart);
        user.setCartId(cart.getId());

        String sql = "INSERT INTO public.user (username, password, cart_id) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getCartId());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            user.setId(resultSet.getInt("id"));
        } catch (SQLException e) {
            throw new RuntimeException("UserDaoDb add(): " + e.getSQLState());
        }
    }

    @Override
    public User find(String userName, String password) {
        String sql = "SELECT * FROM public.user where username = '" + userName + "'";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            ResultSet res = statement.executeQuery();
            if (res.next()) {
                if (BCrypt.checkpw(password, res.getString("password"))) {
                    User user = new User(res.getString("username"), res.getString("password"));
                    user.setCartId(res.getInt("cart_id"));

                    return user;
                } else {
                    throw new DataNotFoundException("Wrong password");
                }
            }

            throw new DataNotFoundException("Wrong username");

        } catch (SQLException e) {
            throw new RuntimeException("UserDaoDb find(): " + e.getSQLState());
        }

    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE public.user RESTART IDENTITY CASCADE;";
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {

            statement.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("UserDaoDb clear(): " + e.getSQLState());
        }
    }

    @Override
    public boolean isNameAvailable(String username) {
        String sql = "SELECT username FROM public.user";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            ResultSet res = statement.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (res.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = res.getString(i);
                    if (username.equals(columnValue)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("UserDaoDb isNameAvailable(): " + e.getSQLState());
        }
    }

    @Override
    void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS public.user (" +
                "id serial NOT NULL," +
                "username text NOT NULL," +
                "password text NOT NULL," +
                "cart_id int NOT NULL);";
        String triggerMethod = "createUserTable";
        checkForExecutionSuccess(sql, triggerMethod);

        String drop = "ALTER TABLE ONLY public.user " +
                "DROP CONSTRAINT IF EXISTS user_cart;";
        triggerMethod = "dropConstraintOfUserTable";
        checkForExecutionSuccess(drop, triggerMethod);

        String constraint = "ALTER TABLE ONLY public.user " +
                "ADD CONSTRAINT user_cart FOREIGN KEY (cart_id) " +
                "REFERENCES public.cart(id) ON DELETE CASCADE;";
        triggerMethod = "addConstraintToUserTable";
        checkForExecutionSuccess(constraint, triggerMethod);
    }

}


