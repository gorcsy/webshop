package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.ProductDao;
import com.codecool.buyourstuff.model.Product;
import com.codecool.buyourstuff.model.ProductCategory;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoDb extends BaseDaoDb implements ProductDao {

    @Override
    public void add(Product product) {
        String sql = "INSERT INTO public.product (name, description, default_price, default_currency, " +
                "category_id, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getDefaultPrice());
            statement.setString(4, product.getDefaultCurrency().toString());
            statement.setInt(5, product.getProductCategory().getId());
            statement.setInt(6, product.getSupplier().getId());

            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();

            product.setId(resultSet.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException("ProductDaoDb add(): " + e.getSQLState());
        }
    }

    @Override
    public Product find(int id) {
        String sql = "SELECT name, description, default_price, default_currency, category_id, supplier_id " +
                "FROM public.product WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                BigDecimal defaultPrice = rs.getBigDecimal("default_price");
                String defaultCurrency = rs.getString("default_currency");

                int categoryId = rs.getInt("category_id");
                ProductCategory category = DataManager.getProductCategoryDao().find(categoryId);

                int supplierId = rs.getInt("supplier_id");
                Supplier supplier = DataManager.getSupplierDao().find(supplierId);

                Product product = new Product(name, defaultPrice, defaultCurrency, description, category, supplier);
                product.setId(id);

                return product;
            }

            throw new DataNotFoundException("No such product");

        } catch (SQLException e) {
            throw new RuntimeException("ProductDaoDb find(): " + e.getSQLState());
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM public.product WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            if (find(id) != null)
                stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("ProductDaoDb remove(): " + e.getSQLState());
        }
    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE product RESTART IDENTITY CASCADE;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("ProductDaoDb remove(): " + e.getSQLState());
        }
    }

    @Override
    public List<Product> getBy(Supplier supplier) {

        List<Product> products = new ArrayList<>();
        String sql = "SELECT product.id, product.name, product.description, default_price, default_currency, category_id " +
                "FROM product INNER JOIN supplier ON supplier.id = product.supplier_id WHERE product.supplier_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplier.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                BigDecimal defaultPrice = rs.getBigDecimal("default_price");
                String defaultCurrency = rs.getString("default_currency");

                // get category by id
                int categoryId = rs.getInt("category_id");
                ProductCategory category = DataManager.getProductCategoryDao().find(categoryId);

                Product product = new Product(name, defaultPrice, defaultCurrency, description, category, supplier);
                product.setId(productId);

                products.add(product);
            }
            return products;

        } catch (SQLException e) {
            throw new RuntimeException("ProductDaoDb getBy(): " + e.getSQLState());
        }
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT product.id, product.name, product.description, default_price, default_currency, supplier_id " +
                "FROM product INNER JOIN product_category ON product_category.id = product.category_id WHERE product.category_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productCategory.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                BigDecimal defaultPrice = rs.getBigDecimal("default_price");
                String defaultCurrency = rs.getString("default_currency");

                // get supplier by id
                int supplierId = rs.getInt("supplier_id");
                Supplier supplier = DataManager.getSupplierDao().find(supplierId);

                Product product = new Product(name, defaultPrice, defaultCurrency, description,
                        productCategory, supplier);
                product.setId(productId);

                products.add(product);
            }
            return products;

        } catch (SQLException e) {
            throw new RuntimeException("ProductDaoDb getBy(): " + e.getSQLState());
        }
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id FROM public.product;";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(find(rs.getInt("id")));
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("ProductDaoDb getAll(): " + e.getSQLState());
        }
    }

    @Override
    void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS public.product (" +
                "id serial NOT NULL PRIMARY KEY," +
                "name text NOT NULL, " +
                "description text NOT NULL," +
                "default_price decimal NOT NULL, " +
                "default_currency text NOT NULL, " +
                "category_id int NOT NULL, " +
                "supplier_id int NOT NULL);";
        String triggerMethod = "createProductTable";
        checkForExecutionSuccess(create, triggerMethod);

    }
}
