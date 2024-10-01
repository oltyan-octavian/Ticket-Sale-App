package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserDBRepository implements IUserRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(UserDBRepository.class);

    public UserDBRepository(Properties props){
        logger.info("Initializing UserDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public User save(User entity) {
        logger.traceEntry("saving user {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("insert into users(name, password) values (?, ?) ")){
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getPassword());
            int result = statement.executeUpdate();
            return entity;
        }catch(SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return null;
    }

    @Override
    public User delete(Integer aLong) {
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public User findOne(Integer aLong) {
        logger.traceEntry("finding user with id {} ",aLong);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from users where id = ?")) {
            statement.setInt(1, aLong);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    String password = result.getString("password");
                    User user = new User(id, name, password);
                    return user;
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry("finding all users");
        List<User> users = new ArrayList<>();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from users")){
            try(var result = statement.executeQuery()){
                while(result.next()){
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    String password = result.getString("password");
                    User user = new User(id, name, password);
                    users.add(user);
                }
            }}
        catch (SQLException e){
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        return users;
    }

}
