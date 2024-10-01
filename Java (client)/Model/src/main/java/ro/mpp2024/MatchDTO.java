package ro.mpp2024;

import java.io.Serializable;

public class MatchDTO implements Serializable {
    private String type;
    private int availableSeats;
    private int team1ID;
    private int team2ID;
    private int id;

    private int seatPrice;

    public MatchDTO(int id, String type, int team1ID, int team2ID, int availableSeats, int seatPrice) {
        this.id = id;
        this.type = type;
        this.availableSeats = availableSeats;
        this.team1ID = team1ID;
        this.team2ID = team2ID;
        this.seatPrice = seatPrice;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getTeam1ID() {
        return team1ID;
    }

    public int getTeam2ID() {
        return team2ID;
    }

    public int getSeatPrice() {
        return seatPrice;
    }

    @Override
    public String toString() {
        return "MatchDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", availableSeats=" + availableSeats +
                ", team1ID=" + team1ID +
                ", team2ID=" + team2ID +
                ", seatPrice=" + seatPrice +
                '}';
    }
}
