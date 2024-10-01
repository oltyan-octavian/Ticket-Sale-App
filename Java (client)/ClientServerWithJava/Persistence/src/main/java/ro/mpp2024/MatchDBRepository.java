package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MatchDBRepository implements IMatchRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(MatchDBRepository.class);

    public MatchDBRepository(Properties props){
        logger.info("Initializing MatchDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public Match save(Match entity) {
        logger.traceEntry("saving match {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("insert into matches(availableSeats, team1ID, team2ID, type, seatPrice) values (?, ?, ?, ?, ?) ")){
            statement.setInt(1, entity.getAvailableSeats());
            statement.setInt(2, entity.getTeam1ID());
            statement.setInt(3, entity.getTeam2ID());
            statement.setString(4, entity.getType());
            statement.setInt(5, entity.getSeatPrice());
            int result = statement.executeUpdate();
        }catch(SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return entity;
    }

    @Override
    public Match delete(Integer aLong) {
        return null;
    }

    @Override
    public Match update(Match entity) {
        logger.traceEntry("updating match with id {} ",entity.getId());
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("update matches set availableSeats=? where id=?")) {
            statement.setInt(1,entity.getAvailableSeats());
            statement.setInt(2,entity.getId());
            statement.executeUpdate();
            return entity;
        }
        catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return null;
    }

    @Override
    public Match findOne(Integer aLong) {
        logger.traceEntry("finding match with id {} ",aLong);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from matches where id = ?")) {
            statement.setInt(1, aLong);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String type = result.getString("type");
                    int availableSeats = result.getInt("availableSeats");
                    int team1ID = result.getInt("team1ID");
                    int team2ID = result.getInt("team2ID");
                    int seatPrice = result.getInt("seatPrice");
                    Match match = new Match(id, type, availableSeats, team1ID, team2ID, seatPrice);
                    return match;
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return null;
    }

    @Override
    public Iterable<Match> findAll() {
        logger.traceEntry("finding all matches");
        List<Match> matches = new ArrayList<>();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from matches")){
            try(var result = statement.executeQuery()){
                while(result.next()){
                    int id = result.getInt("id");
                    String type = result.getString("type");
                    int availableSeats = result.getInt("availableSeats");
                    int team1ID = result.getInt("team1ID");
                    int team2ID = result.getInt("team2ID");
                    int seatPrice = result.getInt("seatPrice");
                    Match match = new Match(id, type, availableSeats, team1ID, team2ID, seatPrice);
                    matches.add(match);
                }
            }}
        catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return matches;
    }

}
