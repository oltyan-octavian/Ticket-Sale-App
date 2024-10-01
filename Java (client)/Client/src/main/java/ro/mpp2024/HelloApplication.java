package ro.mpp2024;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2024.protobuffprotocol.ServicesRpcProxyProto;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Properties serverProps = new Properties();
        try {
            serverProps.load(new FileReader("bd.properties"));
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.properties " + e);
        }


        Properties propertiesClient = new Properties();
        try {
            propertiesClient.load(new FileReader("client.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find client.properties " + e);
        }


        var portInt = Integer.parseInt(propertiesClient.getProperty("port"));
        var host = propertiesClient.getProperty("host");
        IService server= new ServicesRpcProxyProto(host, portInt);



        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginWindowController controller = fxmlLoader.getController();
        controller.setServ(server);
        stage.setTitle("Login!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}