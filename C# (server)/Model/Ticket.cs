namespace Model
{

    public class Ticket : Entity<int>
    {
        public long matchID { get; set; }

        public int price { get; set; }

        public string clientName { get; set; }

        public int numberOfSeats { get; set; }

        public Ticket(int id, long matchId, int price, string clientName, int numberOfSeats) : base(id)
        {
            matchID = matchId;
            this.price = price;
            this.clientName = clientName;
            this.numberOfSeats = numberOfSeats;
        }
        
        public Ticket(long matchId, int price, string clientName, int numberOfSeats)
        {
            matchID = matchId;
            this.price = price;
            this.clientName = clientName;
            this.numberOfSeats = numberOfSeats;
        }
    }
}