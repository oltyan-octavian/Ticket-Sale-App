package ro.mpp2024;

import java.util.Collection;
import java.util.Optional;

public interface IService <ID>{
    //boolean addUser(String name, String password);

    //User connect(String name, String password);

    Collection<String> getListMatches(String search);

    Optional<User> login(String username, String password, Observer client);

    void logout(String username);

    MatchDTO getMatch(int i);

    boolean addTicket(BuyTicketsDTO ticket);
}
