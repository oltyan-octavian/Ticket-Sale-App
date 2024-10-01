package ro.mpp2024;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ShowServicesRpcProxy implements IService{
    private String host;
    private int port;

    private Observer client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private volatile boolean finished;
    private BlockingQueue<Response> queueResponses;

    public ShowServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        queueResponses = new LinkedBlockingQueue<>();
    }

    @Override
    public Optional<User> login(String username, String password, Observer client) {
        initializeConnection();
        UserDTO utilizator = new UserDTO(username, password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(utilizator).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.OK) {
            this.client = client;
            UserDTO userDTO = (UserDTO) response.getData();
            User user = new User(userDTO.getName(), userDTO.getPassword());
            return Optional.of(user);
        }
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            closeConnection();
            throw new IllegalArgumentException(error);
        }
        return Optional.empty();
    }

    @Override
    public void logout(String username) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(username).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
    }

    @Override
    public MatchDTO getMatch(int i) {
        Request request = new Request.Builder().type(RequestType.GET_MATCH).data(i).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.GET_MATCH) {
            return (MatchDTO) response.getData();
        }
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return null;
    }

    @Override
    public boolean addTicket(BuyTicketsDTO ticket) {
        Request request = new Request.Builder().type(RequestType.BUY_TICKETS).data(ticket).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return true;
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) {
        try {
            output.writeObject(request);
            output.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error sending object " + e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            response = queueResponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }
    @Override
    public synchronized Collection<String> getListMatches(String search) {
        Request request = new Request.Builder().type(RequestType.GET_MATCHES).data(search).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return (Collection<String>) response.getData();
    }

    private void handleUpdate(Response response) {
        if (response.getType() == ResponseType.UPDATE) {
            client.update();
        }
    }
    private boolean isUpdateResponse(Response response) {
        return response.getType() == ResponseType.UPDATE;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received " + response);
                    if (response instanceof Response) {
                        Response response1 = (Response) response;
                        if (isUpdateResponse(response1)) {
                            handleUpdate(response1);
                        } else {
                            try {
                                queueResponses.put((Response) response);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
