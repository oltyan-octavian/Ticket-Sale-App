package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TeamDBRepository implements ITeamRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(TeamDBRepository.class);

    public TeamDBRepository(Properties props){
        logger.info("Initializing TeamDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public Team save(Team entity) {
        logger.traceEntry("saving team {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("insert into teams(name) values (?) ")){
            statement.setString(1, entity.getName());
            int result = statement.executeUpdate();
        }catch(SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return entity;
    }

    @Override
    public Team delete(Integer aLong) {
        return null;
    }

    @Override
    public Team update(Team entity) {
        return null;
    }

    @Override
    public Team findOne(Integer aLong) {
        logger.traceEntry("finding team with id {} ",aLong);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from teams where id = ?")) {
            statement.setInt(1, aLong);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    Team team = new Team(id, name);
                    return team;
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return null;
    }

    @Override
    public Iterable<Team> findAll() {
        logger.traceEntry("finding all teams");
        List<Team> teams = new ArrayList<>();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from teams")){
            try(var result = statement.executeQuery()){
                while(result.next()){
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    Team team = new Team(id, name);
                    teams.add(team);
                }
            }}
        catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return teams;
    }

}
