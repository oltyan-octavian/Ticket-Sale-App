using System;
using System.Runtime.Serialization;

namespace Model
{
    [Serializable]
    public class MatchDTO : ISerializable
    {
        private int id;
        private string type;
        private int availableSeats;
        private int team1ID;
        private int team2ID;
        private int seatPrice;

        public MatchDTO(int id, string type, int team1ID, int team2ID, int availableSeats, int seatPrice)
        {
            this.id = id;
            this.type = type;
            this.team1ID = team1ID;
            this.team2ID = team2ID;
            this.availableSeats = availableSeats;
            this.seatPrice = seatPrice;
        }

        public int Id => id;

        public string Type => type;

        public int AvailableSeats => availableSeats;

        public int Team1ID => team1ID;

        public int Team2ID => team2ID;

        public int SeatPrice => seatPrice;

        public override string ToString()
        {
            return $"MatchDTO{{id={id}, type='{type}', availableSeats={availableSeats}, team1ID={team1ID}, team2ID={team2ID}, seatPrice={seatPrice}}}";
        }

        // Required for deserialization
        public MatchDTO(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException(nameof(info));

            id = info.GetInt32("Id");
            type = info.GetString("Type");
            team1ID = info.GetInt32("Team1ID");
            team2ID = info.GetInt32("Team2ID");
            availableSeats = info.GetInt32("AvailableSeats");
            seatPrice = info.GetInt32("SeatPrice");
        }

        // Required for serialization
        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException(nameof(info));

            info.AddValue("Id", id);
            info.AddValue("Type", type);
            info.AddValue("Team1ID", team1ID);
            info.AddValue("Team2ID", team2ID);
            info.AddValue("AvailableSeats", availableSeats);
            info.AddValue("SeatPrice", seatPrice);
        }
    }
}
