package ro.mpp2024;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShowServicesImpl implements IService{
    private IRepository<Integer, Match> matchRepository;
    private IRepository<Integer, Team> teamRepository;
    private IRepository<Integer, Ticket> ticketRepository;
    private IRepository<Integer, User> userRepository;
    private Map<String, Observer> loggedClients;
    private final int defaultThreadsNo=5;

    public ShowServicesImpl(IRepository<Integer, Match> matchRepository, IRepository<Integer, Team> teamRepository, IRepository<Integer, Ticket> ticketRepository, IRepository<Integer, User> userRepository) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        loggedClients= new ConcurrentHashMap<>();
    }
    @Override
    public synchronized Optional<User> login(String username, String password, Observer client) {
        var utilizator = getAccountByName(username);
        if (utilizator!= null) {
            var parola = utilizator.getPassword();
            if(!password.equals(parola)){
                throw new IllegalArgumentException("Parola incorecta");
            }
            else {
                if (loggedClients.get(username) != null)
                    throw new IllegalArgumentException("Utilizatorul este deja autentificat");
                loggedClients.put(username, client);
                return Optional.of(utilizator);
            }
        }
        return Optional.empty();
    }

    public synchronized User getAccountByName(String username){
        Iterable<User> allusers=userRepository.findAll();
        for (User us:allusers){
            if (Objects.equals(us.getName(), username))
            {
                return us;
            }
        }
        return null;
    }

    @Override
    public synchronized void logout(String username){
        if (loggedClients.get(username) == null)
            throw new IllegalArgumentException("Utilizatorul nu este autentificat");
        loggedClients.remove(username);
    }

    @Override
    public synchronized MatchDTO getMatch(int id){
        Match match = matchRepository.findOne(id);
        MatchDTO matchDTO = new MatchDTO(match.getId(), match.getType(), match.getTeam1ID(), match.getTeam2ID(), match.getAvailableSeats(), match.getSeatPrice());
        return matchDTO;
    }

    @Override
    public synchronized Collection<String> getListMatches(String search) {
        Collection<String> collection = new ArrayList<>();
        Iterable<Match> matches = matchRepository.findAll();
        matches.forEach(match -> {
            String toCollection;
            if(match.getAvailableSeats() == 0)
            {
                toCollection = match.getId() + " | " + match.getType() + ": " + teamRepository.findOne(match.getTeam1ID()).getName() + " VS " + teamRepository.findOne(match.getTeam2ID()).getName() + " | Seat Price: " + match.getSeatPrice() + " | SOLD OUT";}
            else{
                toCollection = match.getId() + " | " + match.getType() + ": " + teamRepository.findOne(match.getTeam1ID()).getName() + " VS " + teamRepository.findOne(match.getTeam2ID()).getName() + " | Seat Price: " + match.getSeatPrice() + " | Available Seats: " + match.getAvailableSeats();}
            if(search == null || toCollection.contains(search))
                collection.add(toCollection);
        });
        return collection;
    }

    @Override
    public synchronized boolean addTicket(BuyTicketsDTO tick){
        Ticket ticket = new Ticket(tick.getMatchID(), tick.getPrice(), tick.getClientName(), tick.getNumberOfSeats());
        Ticket bool = ticketRepository.save(ticket);
        Match match = matchRepository.findOne(tick.getMatchID());
        match.setAvailableSeats(match.getAvailableSeats()-tick.getNumberOfSeats());
        matchRepository.update(match);
        notifyClients();
        return bool != null;
    }

    private void notifyClients(){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for(var client : loggedClients.values()){
            if(client == null)
                continue;
            executor.execute(client::update);
        }
    }
}
