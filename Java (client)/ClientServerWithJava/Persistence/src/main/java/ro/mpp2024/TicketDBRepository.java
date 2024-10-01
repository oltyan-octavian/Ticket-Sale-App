package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TicketDBRepository implements ITicketRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(TicketDBRepository.class);

    public TicketDBRepository(Properties props){
        logger.info("Initializing TicketDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public Ticket save(Ticket entity) {
        logger.traceEntry("saving ticket {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("insert into tickets(matchID, price, clientName, numberOfSeats) values (?, ?, ?, ?) ")){
            statement.setInt(1, entity.getMatchID());
            statement.setInt(2, entity.getPrice());
            statement.setString(3, entity.getClientName());
            statement.setInt(4, entity.getNumberOfSeats());
            int result = statement.executeUpdate();
            return entity;
        }catch(SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return null;
    }

    @Override
    public Ticket delete(Integer aLong) {
        return null;
    }

    @Override
    public Ticket update(Ticket entity) {
        return null;
    }

    @Override
    public Ticket findOne(Integer aLong) {
        logger.traceEntry("finding ticket with id {} ",aLong);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from tickets where id = ?")) {
            statement.setInt(1, aLong);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String clientName = result.getString("clientName");
                    int matchID = result.getInt("matchID");
                    int price = result.getInt("price");
                    int numberOfSeats = result.getInt("numberOfSeats");
                    Ticket ticket = new Ticket(id, matchID, price, clientName, numberOfSeats);
                    return ticket;
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return null;
    }

    @Override
    public Iterable<Ticket> findAll() {
        logger.traceEntry("finding all tickets");
        List<Ticket> tickets = new ArrayList<>();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from tickets")){
            try(var result = statement.executeQuery()){
                while(result.next()){
                    int id = result.getInt("id");
                    String clientName = result.getString("clientName");
                    int matchID = result.getInt("matchID");
                    int price = result.getInt("price");
                    int numberOfSeats = result.getInt("numberOfSeats");
                    Ticket ticket = new Ticket(id, matchID, price, clientName, numberOfSeats);
                    tickets.add(ticket);
                }
            }}
        catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return tickets;
    }

}
