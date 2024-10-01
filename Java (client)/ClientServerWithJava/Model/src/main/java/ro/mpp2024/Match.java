package ro.mpp2024;
public class Match extends Entity<Integer> {
    private String type;
    private int availableSeats;
    private int team1ID;
    private int team2ID;

    private int seatPrice;

    public Match(int id, String type, int availableSeats, int team1ID, int team2ID, int seatPrice) {
        super(id);
        this.type = type;
        this.availableSeats = availableSeats;
        this.team1ID = team1ID;
        this.team2ID = team2ID;
        this.seatPrice = seatPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getTeam1ID() {
        return team1ID;
    }

    public void setTeam1ID(int team1ID) {
        this.team1ID = team1ID;
    }

    public int getTeam2ID() {
        return team2ID;
    }

    public void setTeam2ID(int team2ID) {
        this.team2ID = team2ID;
    }

    public int getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(int seatPrice) {
        this.seatPrice = seatPrice;
    }

    @Override
    public String toString() {
        return "Match{" +
                "type='" + type + '\'' +
                ", availableSeats=" + availableSeats +
                ", team1ID=" + team1ID +
                ", team2ID=" + team2ID +
                ", seatPrice=" + seatPrice +
                '}';
    }
}
