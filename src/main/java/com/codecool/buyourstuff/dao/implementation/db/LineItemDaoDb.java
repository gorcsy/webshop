package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.LineItemDao;
import com.codecool.buyourstuff.model.Cart;
import com.codecool.buyourstuff.model.LineItem;
import com.codecool.buyourstuff.model.Product;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LineItemDaoDb extends BaseDaoDb implements LineItemDao {

    @Override
    public void add(LineItem lineItem) {
        String addition = "INSERT INTO public.line_item (product_id, cart_id, quantity) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(addition, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, lineItem.getProduct().getId());
            ps.setInt(2, lineItem.getCartId());
            ps.setInt(3, lineItem.getQuantity());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            lineItem.setId(rs.getInt(1));
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void remove(LineItem lineItem) {
        String removal = "DELETE FROM public.line_item WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(removal)) {
            ps.setInt(1, lineItem.getId());
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void clear() {
        String clearStatement = "TRUNCATE TABLE public.line_item RESTART IDENTITY CASCADE;";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(clearStatement);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void update(LineItem lineItem, int quantity) {
        String update = "UPDATE public.line_item SET quantity = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(update)) {

            ps.setInt(1, quantity);
            ps.setInt(2, lineItem.getId());
            ps.executeUpdate();

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public LineItem find(int id) {
        String selection = "SELECT product_id, cart_id, quantity FROM public.line_item WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(selection)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int productId = rs.getInt(1);
                Product product = DataManager.getProductDao().find(productId);
                int cartId = rs.getInt(2);
                int quantity = rs.getInt(3);
                LineItem lineItem = new LineItem(product, cartId, quantity);
                lineItem.setId(id);

                return lineItem;
            }

            throw new DataNotFoundException("No such line-item");

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public List<LineItem> getBy(Cart cart) {
        List<LineItem> lineItems = new ArrayList<>();
        int cartId = cart.getId();
        String selection = "SELECT id, product_id, quantity FROM public.line_item WHERE cart_id = ?;";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(selection)) {

            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int lineItemId = rs.getInt(1);
                int productId = rs.getInt(2);
                Product product = DataManager.getProductDao().find(productId);
                int quantity = rs.getInt(3);
                LineItem lineItem = new LineItem(product, cartId, quantity);
                lineItem.setId(lineItemId);
                lineItems.add(lineItem);
            }

            return lineItems;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void createTable() {

        String creation = "CREATE TABLE IF NOT EXISTS public.line_item" +
                " (id SERIAL PRIMARY KEY," +
                "product_id INT NOT NULL," +
                "cart_id INT NOT NULL," +
                "quantity INT NOT NULL)";
        String triggerMethod = "createLineItemTable";
        checkForExecutionSuccess(creation, triggerMethod);

        String drop1 = "ALTER TABLE ONLY public.line_item " +
                "DROP CONSTRAINT IF EXISTS cart_line_item;";
        triggerMethod = "dropConstraint1OfLineItemTable";
        checkForExecutionSuccess(drop1, triggerMethod);

        String drop2 = "ALTER TABLE ONLY public.line_item " +
                "DROP CONSTRAINT IF EXISTS line_item_product;";
        triggerMethod = "dropConstraint2OfLineItemTable";
        checkForExecutionSuccess(drop2, triggerMethod);

        String constraint1 = "ALTER TABLE ONLY public.line_item " +
                "ADD CONSTRAINT cart_line_item FOREIGN KEY (cart_id) " +
                "REFERENCES public.cart(id) " +
                "ON DELETE CASCADE;";  // necessary?
        triggerMethod = "addConstraint1ToLineItemTable";
        checkForExecutionSuccess(constraint1, triggerMethod);

        String constraint2 = "ALTER TABLE ONLY public.line_item " +
                "ADD CONSTRAINT line_item_product FOREIGN KEY (product_id) " +
                "REFERENCES public.product(id) " +
                "ON DELETE CASCADE;";  // necessary?
        triggerMethod = "addConstraint2ToLineItemTable";
        checkForExecutionSuccess(constraint2, triggerMethod);
    }
}
