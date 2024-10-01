package ro.mpp2024;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainWindowController implements Observer {
    private IService server;
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    ObservableList<String> listModel = FXCollections.observableArrayList();

    public void setServ(IService serv) {
        this.server = serv;
    }

    public void initialize() {
    }

    @FXML
    TextField searchBox;

    @FXML
    ListView<String> listView;

    @FXML
    TextField nameOfClient;

    @FXML
    TextField numberOfSeats;

    @FXML
    Button logOutButton;

    public void initModel(){
        listModel.setAll(server.getListMatches(""));
        listView.setItems(listModel);
    }

    @FXML
    public synchronized void handleSearch(){
        String search = searchBox.getText();
        listModel.setAll(server.getListMatches(search));
        listView.setItems(listModel);
    }

    @FXML
    public void checkMatch(){
        String string = listView.getSelectionModel().getSelectedItem();
        String[] parts = string.split("\\|");
        MatchDTO matchDTO = server.getMatch(Integer.parseInt(parts[0].strip()));
        Match match = new Match(matchDTO.getId(), matchDTO.getType(), matchDTO.getAvailableSeats(), matchDTO.getTeam1ID(), matchDTO.getTeam2ID(),  matchDTO.getSeatPrice());
        if(match.getAvailableSeats()==0){
            numberOfSeats.setText("SOLD OUT");
            numberOfSeats.setEditable(false);
            numberOfSeats.styleProperty().setValue("-fx-text-inner-color: red;");
        }
        else{
            numberOfSeats.setText("");
            numberOfSeats.setEditable(true);
            numberOfSeats.styleProperty().setValue("-fx-text-inner-color: black;");
        }
    }

    @FXML
    public void sellTicket(){
        String string = listView.getSelectionModel().getSelectedItem();
        String[] parts = string.split("\\|");
        MatchDTO matchDTO = server.getMatch(Integer.parseInt(parts[0].strip()));
        Match match = new Match(matchDTO.getId(), matchDTO.getType(), matchDTO.getAvailableSeats(), matchDTO.getTeam1ID(), matchDTO.getTeam2ID(),  matchDTO.getSeatPrice());
        int seats = Integer.parseInt(numberOfSeats.getText());
        String name = nameOfClient.getText();
        int price = seats * match.getSeatPrice();
        if(seats > match.getAvailableSeats()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Ticket Sale","You selected too many seats!");
        }
        else {
            BuyTicketsDTO buyTicketsDTO = new BuyTicketsDTO(match.getId(), price, name, seats);
            boolean ok = server.addTicket(buyTicketsDTO);
            if(ok){
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Ticket Sale","Ticket was successfully sold!");
            }
            else{
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Ticket Sale","Ticket was not sold!");
            }
        }
        nameOfClient.setText("");
        numberOfSeats.setText("");
        initModel();
    }

    @FXML
    private void logOut(){
        if(user == null){
            return;
        }
        try {
            server.logout(user.getName());
            //close
            Stage stage = (Stage) logOutButton.getScene().getWindow();
            stage.close();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update() {
        Platform.runLater(()->{
            initModel();
        });

    }
}
