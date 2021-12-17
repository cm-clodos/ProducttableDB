package com.example.producttabledb;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
    private Button btnADD, btnDELETE, btnEDIT;
    @FXML
    private Text actionText;

    private int ID;

    ObservableList<Product> productsList = FXCollections.observableArrayList();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            DBConnection dbConnection = new DBConnection();
            tcID.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
            tcName.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
            tcPrice.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
            tcQuantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));

            table.setItems(dbConnection.showAllDBProducts());
            //erstes Produkt der tabelle wird ausgewählt.
            table.getSelectionModel().selectFirst();

            //verfolgt jeden neuen select und gibt die Value der productrows in den textfields wieder
            table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
                @Override
                public void changed(ObservableValue<? extends Product> observableValue, Product product, Product t1) {
                    if (t1 != null) {
                        Product selectedProduct = table.getSelectionModel().getSelectedItem();
                        tfName.setText(selectedProduct.getName());
                        tfPrice.setText(String.valueOf(selectedProduct.getPrice()));
                        tfQuantity.setText(String.valueOf(selectedProduct.getQuantity()));
                    }
                }

            });



        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
    @FXML
    public void addProductBtn(ActionEvent event) throws SQLException {
        createAddAlertBox();

    }

    @FXML
    public void deleteProductBtn(ActionEvent event) throws SQLException {
        Product selectedProduct = table.getSelectionModel().getSelectedItem();
        ID = selectedProduct.getId();
        System.out.println(ID);
        createDeleteAlertBox();

    }

    @FXML
    public void editProductBtn(ActionEvent event) throws SQLException {
        Product selectedProduct = table.getSelectionModel().getSelectedItem();
        ID = selectedProduct.getId();
        createEditAlertBox();

    }

    public void setTextfieldEmpty(){

        tfName.setText("");
        tfPrice.setText("");
        tfQuantity.setText("");
    }

    public void createDeleteAlertBox() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete?");
        alert.setContentText("Warning! You are deleting products! Are you shure?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()){
            System.out.println("Alert closed");
        } else if(result.get()==ButtonType.OK){
            deleteInDB();
        }else if (result.get()==ButtonType.CANCEL){
            actionText.setText("Löschvorgang abgebrochen");
            setTextfieldEmpty();
        }


    }
    public void createEditAlertBox() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit?");
        alert.setContentText("Warning! You are editing products! Are you shure?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()){
            System.out.println("Alert closed");
        } else if(result.get()==ButtonType.OK){
            editInDB();
        }else if (result.get()==ButtonType.CANCEL){
            actionText.setText("Editvorgang abgebrochen");
            setTextfieldEmpty();
        }


    }

    public void createAddAlertBox() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ADD?");
        alert.setContentText("Warning! You are adding products! Are you shure?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()){
            System.out.println("Alert closed");
        } else if(result.get()==ButtonType.OK){
            addInDB();
        }else if (result.get()==ButtonType.CANCEL){
            actionText.setText("Addingvorgang abgebrochen");
            setTextfieldEmpty();
        }


    }
    //DB funktionen
    public void deleteInDB() throws SQLException {
        String sql = "DELETE FROM product WHERE id=" + ID;
        DBConnection dbConnection = new DBConnection();
        dbConnection.delete(sql);

        table.setItems(dbConnection.showAllDBProducts());
        actionText.setText("Product wurde aus der DB gelöscht.");

        dbConnection.closeConnection();

        setTextfieldEmpty();
    }

    public void editInDB() throws SQLException {
        String name = tfName.getText();
        double price = Double.parseDouble(tfPrice.getText());
        int quantity = Integer.parseInt(tfQuantity.getText());

        String sql = "UPDATE product SET name='" + name + "', price=" + price + " , quantity=" + quantity + " WHERE id=" + ID;
        DBConnection dbConnection = new DBConnection();
        dbConnection.update(sql);

        table.setItems(dbConnection.showAllDBProducts());
        actionText.setText("Product wurde in der DB geändert.");
        dbConnection.closeConnection();

        setTextfieldEmpty();

    }

    public void addInDB() throws SQLException {
        Product product = new Product();
        product.setName(tfName.getText());
        product.setPrice(Double.parseDouble(tfPrice.getText()));
        product.setQuantity(Integer.parseInt(tfQuantity.getText()));

        //Product wird mit add in die DB gespeichert
        DBConnection dbConnection = new DBConnection();
        dbConnection.insert(product);

        table.setItems(dbConnection.showAllDBProducts());
        actionText.setText("Product wurde der DB hinzugefügt.");
        dbConnection.closeConnection();

        setTextfieldEmpty();


    }

    // EDIT Button Update befehl
}