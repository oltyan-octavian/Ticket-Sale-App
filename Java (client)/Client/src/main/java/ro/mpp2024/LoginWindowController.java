package ro.mpp2024;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginWindowController implements Observer{

    private IService server;

    @FXML
    TextField loginName;
    @FXML
    PasswordField loginPassword;
    @FXML
    TextField registerName;
    @FXML
    PasswordField registerPassword;
    @FXML
    BorderPane loginPane;
    @FXML
    BorderPane registerPane;

    public LoginWindowController() {
    }

    public void setServ(IService serv) {
        this.server = serv;
    }

    public void initialize() {
    }

    @FXML
    public void viewLogin(){
        registerPane.setVisible(false);
        loginPane.setVisible(true);
    }
    @FXML
    public void viewRegister(){
        loginPane.setVisible(false);
        registerPane.setVisible(true);
    }

    public void login() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ro/mpp2024/MainWindow.fxml"));

            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ticket Sale");
            // Create the dialog Stage.

            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            String name = loginName.getText();
            String password = loginPassword.getText();
            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setServ(server);
            try{
                Optional selected = server.login(name, password, mainWindowController);
                User loggeduser = (User) selected.get();
                if(selected.isPresent()){
                    mainWindowController.initModel();
                    mainWindowController.setUser(loggeduser);
                    dialogStage.show();
                }
                else {
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"User Login","The name or password is incorrect!");
                }
                } catch (Exception e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
            e.printStackTrace();
        }
    }

    public void register(){
        /*String name=registerName.getText();
        String password=registerPassword.getText();
        try {
            if (server.addUser(name, password)) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"User Register","The user was registered successfully!");
            } else {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"User Register","The user was not registered successfully!");
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        viewLogin();*/
    }

    @Override
    public void update() {

    }
}
