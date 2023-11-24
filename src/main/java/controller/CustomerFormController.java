package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dto.CustomerDto;
import dto.tm.CustomerTm;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CustomerFormController implements Initializable {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtSalary;

    @FXML
    private Label custName;

    @FXML
    private TableView<CustomerTm> tblCustomer;

    @FXML
    private TableColumn colId;

    @FXML
    private TableColumn colName;

    @FXML
    private TableColumn colAddress;

    @FXML
    private TableColumn colSalary;

    @FXML
    private TableColumn colOption;

    @FXML
    void reloadButtonOnAction(ActionEvent event) {
        loadCustomerTable();
        tblCustomer.refresh();
        clearFields();

    }

    private void clearFields() {
        tblCustomer.refresh();
        txtSalary.clear();
        txtAddress.clear();
        txtName.clear();
        txtId.clear();
        txtId.setEditable(true);
    }

    @FXML
    void saveButtonOnAction(ActionEvent event)   {
        CustomerDto c=new CustomerDto(
                txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText()
                ));
        String sql = "INSERT INTO Customer VALUES('" + c.getId() + "','" + c.getName() + "','" + c.getAddress() + "'," + c.getSalary() + ")";
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int res = stm.executeUpdate(sql);
            if (res > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Saved").show();
                loadCustomerTable();
                clearFields();
            }

        }catch (SQLIntegrityConstraintViolationException ex){
                new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void updateButtonOnAction(ActionEvent event) {
        CustomerDto c=new CustomerDto(
                txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText()
                ));
        String sql = "UPDATE Customer SET name='"+c.getName()+"', address='"+c.getAddress()+"', salary="+c.getSalary()+" WHERE id='"+c.getId()+"'";
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int res = stm.executeUpdate(sql);
            if (res > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer"+c.getId()+"  Updated").show();
                loadCustomerTable();
                clearFields();
            }

        }catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadCustomerTable();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

    }

    private void setData(CustomerTm newValue) {
        if(newValue != null) {
            txtId.setEditable(false);
            txtId.setText(newValue.getId());
            txtName.setText(newValue.getName());
            txtAddress.setText(newValue.getAddress());
            txtSalary.setText(String.valueOf(newValue.getSalary()));
        }

    }

    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Customer";
        try {

            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet res = stm.executeQuery(sql);

            while(res.next()){
                Button btn = new Button("Delete");
                CustomerTm c = new CustomerTm(
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getDouble(4),
                    btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteCustomer(c.getId());
                });

                tmList.add(c);
            }


            tblCustomer.setItems(tmList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }

    private void deleteCustomer(String id) {
        String sql = "DELETE FROM Customer WHERE id='"+id+"'";
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int res = stm.executeUpdate(sql);
            if(res>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted").show();
                loadCustomerTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage)tblCustomer.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
