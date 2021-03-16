package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.ProductCategoryDao;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDaoDb extends BaseDaoDb implements ProductCategoryDao {
    @Override
    public void add(ProductCategory category) {
        String sql = "INSERT INTO public.product_category (name, department, description) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, category.getName());
            statement.setString(2, category.getDepartment());
            statement.setString(3, category.getDescription());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            category.setId(resultSet.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("ProductCategoryDaoDb add(): " + e.getSQLState());
        }
    }

    @Override
    public ProductCategory find(int id) {
        String sql = "SELECT id, name, description, department FROM product_category WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                String department = rs.getString("department");

                ProductCategory category = new ProductCategory(name, description, department);
                category.setId(rs.getInt("id"));

                return category;
            }

            throw new DataNotFoundException("No such category");

        } catch (SQLException e) {
            throw new RuntimeException("ProductCategoryDaoDb find(): " + e.getSQLState());
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM public.product_category WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ProductCategoryDaoDb remove(): " + e.getSQLState());
        }
    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE product_category RESTART IDENTITY CASCADE;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("ProductCategoryDaoDb clear(): " + e.getSQLState());
        }
    }

    @Override
    public List<ProductCategory> getAll() {

        List<ProductCategory> categories = new ArrayList<>();
        String sql = "SELECT id FROM public.product_category;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductCategory category = find(rs.getInt("id"));
                categories.add(category);
            }

            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("ProductCategoryDaoDb getAll(): " + e.getSQLState());
        }

    }

    @Override
    void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS public.product_category (" +
                "id serial NOT NULL PRIMARY KEY," +
                "name text NOT NULL," +
                "description text NOT NULL, " +
                "department text NOT NULL);";
        String triggerMethod = "createProductCategoryTable";
        checkForExecutionSuccess(create, triggerMethod);

        String drop = "ALTER TABLE ONLY public.product " +
                "DROP CONSTRAINT IF EXISTS product_product_category;";
        triggerMethod = "dropConstraintOfProductTable";
        checkForExecutionSuccess(drop, triggerMethod);

        String constraint = "ALTER TABLE ONLY public.product " +
                "ADD CONSTRAINT product_product_category FOREIGN KEY (category_id) " +
                "REFERENCES public.product_category(id) " +
                "ON DELETE CASCADE;"; // necessary?
        triggerMethod = "addConstraintToProductTable";
        checkForExecutionSuccess(constraint, triggerMethod);
    }
}
