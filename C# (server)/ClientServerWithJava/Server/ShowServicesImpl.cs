using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Service;
using Model;
using Persistence;

namespace Server
{
    public class ShowServicesImpl : IService
    {
        private IRepository<int, Match> matchRepository;
        private IRepository<int, Team> teamRepository;
        private IRepository<int, Ticket> ticketRepository;
        private IRepository<int, User> userRepository;
        private Dictionary<string, Observer> loggedClients;
        private readonly int defaultThreadsNo = 5;
        private readonly object lockObject = new object();

        public ShowServicesImpl(IRepository<int, Match> matchRepository, IRepository<int, Team> teamRepository, IRepository<int, Ticket> ticketRepository, IRepository<int, User> userRepository)
        {
            this.matchRepository = matchRepository;
            this.teamRepository = teamRepository;
            this.ticketRepository = ticketRepository;
            this.userRepository = userRepository;
            loggedClients = new Dictionary<string, Observer>();
        }

        public User Login(string username, string password, Observer client)
        {
                var utilizator = GetAccountByName(username);
                if (utilizator != null)
                {
                    var parola = utilizator.password;
                    if (password != parola)
                    {
                        throw new ArgumentException("Parola incorecta");
                    }
                    else
                    {
                        if (loggedClients.ContainsKey(username))
                            throw new ArgumentException("Utilizatorul este deja autentificat");
                        loggedClients[username] = client;
                        return utilizator;
                    }
                }

                return null;
        }

        public User GetAccountByName(string username)
        {
            lock (lockObject)
            {
                var allUsers = userRepository.findAll();
                foreach (var us in allUsers)
                {
                    if (us.name == username)
                    {
                        return us;
                    }
                }

                return null;
            }
        }

        public void Logout(string username)
        {
            lock (lockObject)
            {
                if (!loggedClients.ContainsKey(username))
                    throw new ArgumentException("Utilizatorul nu este autentificat");
                loggedClients.Remove(username);
            }
        }

        public MatchDTO GetMatch(int id)
        {
            lock (lockObject)
            {
                var match = matchRepository.findOne(id);
                return new MatchDTO(match.id, match.type, match.team1ID, match.team2ID, match.availableSeats,
                    match.seatPrice);
            }
        }

        public ICollection<string> GetListMatches(string search)
        {
            lock (lockObject)
            {
                var collection = new List<string>();
                var matches = matchRepository.findAll();
                foreach (var match in matches)
                {
                    string toCollection;
                    if (match.availableSeats == 0)
                    {
                        toCollection =
                            $"{match.id} | {match.type}: {teamRepository.findOne(match.team1ID).name} VS {teamRepository.findOne(match.team2ID).name} | Seat Price: {match.seatPrice} | SOLD OUT";
                    }
                    else
                    {
                        toCollection =
                            $"{match.id} | {match.type}: {teamRepository.findOne(match.team1ID).name} VS {teamRepository.findOne(match.team2ID).name} | Seat Price: {match.seatPrice} | Available Seats: {match.availableSeats}";
                    }

                    if (search == null || toCollection.Contains(search))
                        collection.Add(toCollection);
                }

                return collection;
            }
        }

        public bool AddTicket(BuyTicketsDTO tick)
        {
            lock (lockObject)
            {
                var ticket = new Ticket(tick.MatchID, tick.Price, tick.ClientName, tick.NumberOfSeats);
                var boolTicket = ticketRepository.save(ticket);
                var match = matchRepository.findOne(tick.MatchID);
                match.availableSeats -= tick.NumberOfSeats;
                matchRepository.update(match);
                NotifyClients();
                return boolTicket != null;
            }
        }

        private void NotifyClients()
        {
            lock (lockObject)
            {
                var tasks = new List<Task>();
                foreach (var client in loggedClients.Values)
                {
                    if (client == null)
                        continue;
                    tasks.Add(Task.Run(() => client.Update()));
                }

                Task.WaitAll(tasks.ToArray());
            }
        }
    }
}
