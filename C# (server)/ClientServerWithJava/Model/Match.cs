namespace Model
{

    public class Match : Entity<int>
    {
        public string type { get; set; }

        public int availableSeats { get; set; }

        public int team1ID { get; set; }

        public int team2ID { get; set; }
        
        public int seatPrice { get; set; }

        public Match(int id, string type, int availableSeats, int team1Id, int team2Id, int seatPrice) : base(id)
        {
            this.type = type;
            this.availableSeats = availableSeats;
            team1ID = team1Id;
            team2ID = team2Id;
            this.seatPrice = seatPrice;
        }

        public Match(string type, int availableSeats, int team1Id, int team2Id, int seatPrice)
        {
            this.type = type;
            this.availableSeats = availableSeats;
            team1ID = team1Id;
            team2ID = team2Id;
            this.seatPrice = seatPrice;
        }
    }
}