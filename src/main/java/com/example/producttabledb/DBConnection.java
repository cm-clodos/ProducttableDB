package com.example.producttabledb;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.*;


public class DBConnection {

    private final static String URL = "jdbc:mariadb://localhost:3306/products";
    private final static String USER = "root";
    private final static String PASSWORD = "root";
    private Statement statement;
    private Connection connection;

    public DBConnection() throws SQLException {
        this.createConnection(URL, USER, PASSWORD);

    }

    public void createConnection(String url, String user, String password)  {
       // (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD))
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = this.connection.createStatement();
        System.out.println(sql);
        return statement.executeQuery(sql);
    }

    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO product (name, price, quantity) VALUE ('" + product.getName() + "', " + product.getPrice() + ", " + product.getQuantity() + ");";
        this.executeQuery(sql);
    }

    public ObservableList<Product> showAllDBProducts() throws SQLException {
        String sql = "SELECT DISTINCT * FROM product ";
        ResultSet resultSet = this.executeQuery(sql);
        ObservableList<Product> dbProducts = FXCollections.observableArrayList();

        while (resultSet.next()){
            dbProducts.add(new Product(resultSet.getInt("id"),resultSet.getString("name"), resultSet.getDouble("price"), resultSet.getInt("quantity")));
        }
        return dbProducts;
    }

    //Delete Methode

    public void delete(String sql) throws SQLException {
        this.executeQuery(sql);

    }
    public void update(String sql) throws SQLException {
        this.executeQuery(sql);
    }




}
