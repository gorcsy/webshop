package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.SupplierDao;
import com.codecool.buyourstuff.model.Supplier;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoDb extends BaseDaoDb implements SupplierDao {
    @Override
    public void add(Supplier supplier) {
        String sql = "INSERT INTO public.supplier (name, description) " +
                "VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getDescription());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            resultSet.next();
            supplier.setId(resultSet.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("SupplierDaoDb add(): " + e.getSQLState());
        }
    }

    @Override
    public Supplier find(int id) {
        String sql = "SELECT id, name, description FROM supplier WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");

                Supplier supplier = new Supplier(name, description);
                supplier.setId(rs.getInt("id"));

                return supplier;
            }
            throw new DataNotFoundException("No Supplier with the give id was found!");

        } catch (SQLException e) {
            throw new RuntimeException("ProductCategoryDaoDb find(): " + e.getSQLState());
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM public.supplier WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SupplierDaoDb remove(): " + e.getSQLState());
        }
    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE public.supplier RESTART IDENTITY CASCADE;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException(("SupplierDaoDb clear(): " + e.getSQLState()));
        }
    }

    @Override
    public List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();

        String sql = "SELECT id FROM public.supplier;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Supplier supplier = find(rs.getInt("id"));
                suppliers.add(supplier);
            }

            return suppliers;
        } catch (SQLException e) {
            throw new RuntimeException("SupplierDaoDb getAll(): " + e.getSQLState());
        }
    }

    @Override
    void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS public.supplier (" +
                "id serial NOT NULL PRIMARY KEY," +
                "name text NOT NULL," +
                "description text NOT NULL);";
        String triggerMethod = "createSupplierTable";
        checkForExecutionSuccess(create, triggerMethod);

        String drop = "ALTER TABLE ONLY public.product " +
                "DROP CONSTRAINT IF EXISTS product_supplier;";
        triggerMethod = "dropConstraintOfProductTable";
        checkForExecutionSuccess(drop, triggerMethod);

        String constraint = "ALTER TABLE ONLY public.product " +
                "ADD CONSTRAINT product_supplier FOREIGN KEY (supplier_id) " +
                "REFERENCES public.supplier(id) " +
                "ON DELETE CASCADE;"; // necessary?
        triggerMethod = "addConstraintToProductTable";
        checkForExecutionSuccess(constraint, triggerMethod);
    }
}
