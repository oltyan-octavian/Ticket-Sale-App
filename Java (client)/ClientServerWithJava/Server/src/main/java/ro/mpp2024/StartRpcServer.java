package ro.mpp2024;

import ro.mpp2024.utils.AbstractServer;
//import ro.mpp2024.utils.RpcConcurrentServer;

import java.io.FileReader;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(new FileReader("bd.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find bd.properties " + e);
        }
        Properties propertiesServer = new Properties();
        try {
            propertiesServer.load(new FileReader("server.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find server.properties " + e);
        }
        MatchDBRepository matchRepo = new MatchDBRepository(serverProps);
        TeamDBRepository teamRepo = new TeamDBRepository(serverProps);
        TicketDBRepository ticketRepo = new TicketDBRepository(serverProps);
        UserDBRepository userRepo = new UserDBRepository(serverProps);
        IService service = new ShowServicesImpl(matchRepo, teamRepo, ticketRepo, userRepo);
        System.out.println("Starting server on port " + propertiesServer.getProperty("port"));
        var portInt = Integer.parseInt(propertiesServer.getProperty("port"));
        //AbstractServer server = new RpcConcurrentServer(portInt, service);
        try{
            //server.start();
        } catch (Exception e) {
            System.err.println("Error starting the server " + e.getMessage());
        }
    }
}
