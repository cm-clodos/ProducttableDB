package com.example.producttabledb;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TableController implements Initializable {
    @FXML
    private TableView<Product> table;
    @FXML
    private TableColumn<Product, Integer> tcID;
    @FXML
    private TableColumn<Product, String> tcName;
    @FXML
    private TableColumn<Product, Double> tcPrice;
    @FXML
    private TableColumn<Product, Integer> tcQuantity;
    @FXML
    private TextField tfQuantity, tfPrice, tfName;
    @FXML
    private Button btnADD, btnDELETE, btnCHANGE;
    @FXML
    private Text actionText;

    private int ID;

    ObservableList<Product> productsList = FXCollections.observableArrayList();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            DBConnection conn = new DBConnection();
            tcID.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
            tcName.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
            tcPrice.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
            tcQuantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));

            table.setItems(conn.showAllDBProducts());

        } catch (SQLException e) {
            e.printStackTrace();
        }





    }
    @FXML
    public void addProduct(ActionEvent event) throws SQLException {
        Product product = new Product();
        product.setName(tfName.getText());
        product.setPrice(Double.parseDouble(tfPrice.getText()));
        product.setQuantity(Integer.parseInt(tfQuantity.getText()));

        //Product wird mit add in die DB gespeichert
        DBConnection dbConnection = new DBConnection();
        dbConnection.insert(product);

        table.setItems(dbConnection.showAllDBProducts());
        actionText.setText("Product wurde der DB hinzugefügt.");


    }

    @FXML
    public void deleteProduct(ActionEvent event) throws SQLException {
        Product selectedProduct = table.getSelectionModel().getSelectedItem();
        ID = selectedProduct.getId();
        System.out.println(ID);

        String sql = "DELETE FROM product WHERE id=" + ID;
        DBConnection dbConnection = new DBConnection();
        dbConnection.delete(sql);

        table.setItems(dbConnection.showAllDBProducts());
        actionText.setText("Product wurde aus der DB gelöscht.");


    }
}