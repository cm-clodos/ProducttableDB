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
import java.util.Arrays;
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
    private Button btnADD, btnDELETE, btnEDIT, btnCLEAR;
    @FXML
    private Text actionText;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    private int ID;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnADD.setDisable(true);
        btnDELETE.setDisable(true);
        btnEDIT.setDisable(true);

        //Checkbox für ID is visible ergänzen
        tcID.setVisible(false);


        try {
            DBConnection dbConnection = new DBConnection();
            tcID.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
            tcName.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
            tcPrice.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
            tcQuantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));
            getProductList();


            table.setItems(productList);

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

                        btnDisableSelectMode();


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
    public void deleteProductBtn(ActionEvent event) {
        Product selectedProduct = table.getSelectionModel().getSelectedItem();
        ID = selectedProduct.getId();
        System.out.println(ID);
        try {
            createDeleteAlertBox();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            System.out.println("Produkt konnte nicht gelöscht werden");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Sie müssen ein Produkt auswählen!");

        }


    }

    @FXML
    public void editProductBtn(ActionEvent event) throws SQLException {
        Product selectedProduct = table.getSelectionModel().getSelectedItem();
        ID = selectedProduct.getId();
        createEditAlertBox();

    }

    @FXML
    public void clearTextfields(ActionEvent event) {
        setTextfieldEmptyAndBtnDisabled();


    }

    public void setTextfieldEmptyAndBtnDisabled() {

        tfName.setText("");
        tfPrice.setText("");
        tfQuantity.setText("");
        btnADD.setDisable(true);
        btnDELETE.setDisable(true);
        btnEDIT.setDisable(true);

    }


    @FXML
    public void handleKeyRelease() {
        String textName = tfName.getText();
        String textPrice = tfPrice.getText();
        String textQuantity = tfQuantity.getText();
        boolean disableButtons = textName.trim().isEmpty() || textPrice.trim().isEmpty() || textQuantity.trim().isEmpty();

        btnADD.setDisable(disableButtons);
        btnDELETE.setDisable(disableButtons);
        btnEDIT.setDisable(disableButtons);

    }

    public ObservableList<Product> getProductList() {
        try {
            DBConnection dbConnection = new DBConnection();
            productList = dbConnection.showAllDBProducts();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        return productList;
    }


    public void btnDisableSelectMode() {
        boolean disbaleButtons = table.getSelectionModel().getSelectedItems().isEmpty();
        if (!disbaleButtons) {
            btnADD.setDisable(false);
            btnDELETE.setDisable(false);
            btnEDIT.setDisable(false);
        }


    }

    public void createDeleteAlertBox() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete?");
        alert.setContentText("Warning! You are deleting products! Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()) {
            System.out.println("Alert closed");
        } else if (result.get() == ButtonType.OK) {
            deleteInDB();
        } else if (result.get() == ButtonType.CANCEL) {
            actionText.setText("Löschvorgang abgebrochen");
            setTextfieldEmptyAndBtnDisabled();
        }


    }

    public void createEditAlertBox() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit?");
        alert.setContentText("Warning! You are editing products! Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()) {
            System.out.println("Alert closed");
        } else if (result.get() == ButtonType.OK) {
            editInDB();
        } else if (result.get() == ButtonType.CANCEL) {
            actionText.setText("Editvorgang abgebrochen");
            setTextfieldEmptyAndBtnDisabled();
        }


    }

    public void createAddAlertBox() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ADD?");
        alert.setContentText("Warning! You are adding products! Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()) {
            System.out.println("Alert closed");
        } else if (result.get() == ButtonType.OK) {
            addInDB();
        } else if (result.get() == ButtonType.CANCEL) {
            actionText.setText("Addingvorgang abgebrochen");
            setTextfieldEmptyAndBtnDisabled();
        }


    }

    public void createExceptionAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setContentText("Error! Your input Name: has ONLY Digits! \n OR Price/Quantity is NOT a number!");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()) {
            System.out.println("Alert closed");
        } else if (result.get() == ButtonType.OK) {
            System.out.println("Nix passiert");
        }

    }

    //DB funktionen
    public void deleteInDB() throws SQLException {
        String sql = "DELETE FROM product WHERE id=" + ID;
        DBConnection dbConnection = new DBConnection();
        dbConnection.delete(sql);

        //table.setItems(dbConnection.showAllDBProducts());
        Product selectedProduct = table.getSelectionModel().getSelectedItem();
        productList.remove(selectedProduct);

        table.setItems(productList);
        actionText.setText("Product wurde aus der DB gelöscht.");

        dbConnection.closeConnection();

        setTextfieldEmptyAndBtnDisabled();
    }

    public void editInDB() throws SQLException {
        boolean verified = verifyInputs();

        if (verified) {
            String name = tfName.getText();
            double price = Double.parseDouble(tfPrice.getText());
            int quantity = Integer.parseInt(tfQuantity.getText());

            String sql = "UPDATE product SET name='" + name + "', price=" + price + " , quantity=" + quantity + " WHERE id=" + ID;
            DBConnection dbConnection = new DBConnection();
            dbConnection.update(sql);


            table.setItems(getProductList());
            actionText.setText("Product wurde in der DB geändert.");
            dbConnection.closeConnection();

            setTextfieldEmptyAndBtnDisabled();

        }
    }

    public void addInDB() throws SQLException {

        boolean verfied = verifyInputs();

        if (verfied) {
            Product product = new Product();
            //Product wird mit add in die DB gespeichert
            product.setName(tfName.getText());
            product.setPrice(Double.parseDouble(tfPrice.getText()));
            product.setQuantity(Integer.parseInt(tfQuantity.getText()));
            DBConnection dbConnection = new DBConnection();
            dbConnection.insert(product);
            productList.add(product);

            table.setItems(productList);
            //actionText.setText("Product wurde der DB hinzugefügt.");
            dbConnection.closeConnection();

            setTextfieldEmptyAndBtnDisabled();

        }
    }

    public boolean verifyInputs() throws SQLException {

        boolean isVerify = false;

        boolean illegalArgumentException = false;
        try {
            checkInputName(tfName.getText());
        } catch (IllegalArgumentException exception) {
            illegalArgumentException = true;
            exception.printStackTrace();
        }

        boolean numberFormatException = false;

        try {
            checkInputPrice(tfPrice.getText());
            checkInputQuantity(tfQuantity.getText());
        } catch (NumberFormatException ex) {
            numberFormatException = true;
            ex.printStackTrace();

        }

        if (numberFormatException || illegalArgumentException) {
            createExceptionAlert();
        } else {
            isVerify = true;


        }
        return isVerify;
    }

    public String checkInputPrice(String input){
        int inputLength = input.length();
        boolean isDigit = false;

        for (int i = 0; i < inputLength; i++) {
            if (input.charAt(i) >= '0' && input.charAt(i) <= '9' || input.charAt(i) =='.') {
                isDigit = true;
            } else {
                throw new NumberFormatException();
            }
        }
        if (isDigit) {
            return input;
        }
        return input;
    }




    public String checkInputQuantity(String input) {

        int inputLength = input.length();
        boolean isDigit = false;

        for (int i = 0; i < inputLength; i++) {
            if (input.charAt(i) >= '0' && input.charAt(i) <= '9') {
                isDigit = true;
            } else {
                throw new NumberFormatException();
            }
        }
        if (isDigit) {
            return input;
        }
        return input;
    }


    public String checkInputName(String input) {
        //Checkt ob String nur aus Zahlen besteht.
        int inputLength = input.length();

        for (int i = 0; i < inputLength; i++) {
            if (input.charAt(i) >= '0' && input.charAt(i) <= '9') {
                throw new IllegalArgumentException();
            } else {
                return input;
            }

        }
        return input;

    }


}