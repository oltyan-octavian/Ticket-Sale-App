package ro.mpp2024;

import java.io.Serializable;

public class BuyTicketsDTO implements Serializable {

    private int matchID;
    private int price;
    private String clientName;
    private int numberOfSeats;

    public BuyTicketsDTO(int matchID, int price, String clientName, int numberOfSeats) {
        this.matchID = matchID;
        this.price = price;
        this.clientName = clientName;
        this.numberOfSeats = numberOfSeats;
    }

    public Integer getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public String toString() {
        return "BuyTicketsDTO{" +
                "matchID=" + matchID +
                ", price=" + price +
                ", clientName='" + clientName + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                '}';
    }
}
