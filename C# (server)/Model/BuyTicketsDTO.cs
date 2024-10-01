using System;
using System.Runtime.Serialization;

namespace Model
{
    [Serializable]
    public class BuyTicketsDTO : ISerializable
    {
        private int matchID;
        private int price;
        private string clientName;
        private int numberOfSeats;

        public BuyTicketsDTO(int matchID, int price, string clientName, int numberOfSeats)
        {
            this.matchID = matchID;
            this.price = price;
            this.clientName = clientName;
            this.numberOfSeats = numberOfSeats;
        }

        public int MatchID
        {
            get => matchID;
            set => matchID = value;
        }

        public int Price
        {
            get => price;
            set => price = value;
        }

        public string ClientName
        {
            get => clientName;
            set => clientName = value;
        }

        public int NumberOfSeats
        {
            get => numberOfSeats;
            set => numberOfSeats = value;
        }

        public override string ToString()
        {
            return $"BuyTicketsDTO{{matchID={matchID}, price={price}, clientName='{clientName}', numberOfSeats={numberOfSeats}}}";
        }

        // Required for deserialization
        public BuyTicketsDTO(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException(nameof(info));

            matchID = info.GetInt32("MatchID");
            price = info.GetInt32("Price");
            clientName = info.GetString("ClientName");
            numberOfSeats = info.GetInt32("NumberOfSeats");
        }

        // Required for serialization
        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException(nameof(info));

            info.AddValue("MatchID", matchID);
            info.AddValue("Price", price);
            info.AddValue("ClientName", clientName);
            info.AddValue("NumberOfSeats", numberOfSeats);
        }
    }
}
