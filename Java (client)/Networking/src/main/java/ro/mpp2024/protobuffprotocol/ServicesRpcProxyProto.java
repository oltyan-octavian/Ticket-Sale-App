package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRpcProxyProto implements IService {

    private String host;
    private int port;

    private Observer client;
    private InputStream input;
    private OutputStream output;
    private Socket connection;
    private volatile boolean finished;
    private BlockingQueue<Protocol.Response> queueResponses;

    public ServicesRpcProxyProto(String host, int port) {
        this.host = host;
        this.port = port;
        queueResponses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
//            output.flush();
            input = connection.getInputStream();
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public Collection<String> getListMatches(String search) {
        sendRequest(ProtoUtils.createGetMatchesRequest(search));
        Protocol.Response response = readResponse();
        if (response.getResponseType() == Protocol.Response.ResponseType.GET_MATCHES) {
            return response.getMatchesList();
        }
        if (response.getResponseType() == Protocol.Response.ResponseType.ERROR) {
            String error = response.getError();
            throw new IllegalArgumentException(error);
        }
        return null;
    }

    @Override
    public Optional<User> login(String username, String password, Observer client) {
        initializeConnection();
        User user = new User(username, password);
//        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(ProtoUtils.createLoginRequest(user));
        Protocol.Response response = readResponse();
        if (response.getResponseType() == Protocol.Response.ResponseType.OK) {
            this.client = client;
            return Optional.of(ProtoUtils.getUser(response));
        }
        if (response.getResponseType() == Protocol.Response.ResponseType.ERROR) {
            String error = response.getError();
            closeConnection();
            throw new IllegalArgumentException(error);
        }

        return null;
    }

    @Override
    public void logout(String username) {
//        Request request = new Request.Builder().type(RequestType.LOGOUT).data(username).build();
        sendRequest(ProtoUtils.createLogoutRequest(username));
        var response = readResponse();
        if (response.getResponseType() == Protocol.Response.ResponseType.ERROR) {
            String error = response.getError();
            throw new IllegalArgumentException(error);
        }

    }

    @Override
    public MatchDTO getMatch(int i) {
        Request request = new Request.Builder().type(RequestType.GET_MATCH).data(i).build();
        sendRequest(ProtoUtils.createGetMatchRequest(i));
        Protocol.Response response = readResponse();
        if (response.getResponseType() == Protocol.Response.ResponseType.GET_MATCH) {
            Protocol.MatchDTO matchdto = response.getMatchdto();
            return new MatchDTO(matchdto.getId(), matchdto.getType(), matchdto.getTeam1ID(), matchdto.getTeam1ID(), matchdto.getAvailableSeats(), matchdto.getSeatPrice());
        }
        if (response.getResponseType() == Protocol.Response.ResponseType.ERROR) {
            String error = response.getError();
            throw new IllegalArgumentException(error);
        }
        return null;
    }

    @Override
    public boolean addTicket(BuyTicketsDTO ticket) {
        sendRequest(ProtoUtils.createBuyTicketsRequest(ticket.getMatchID(), ticket.getPrice(), ticket.getClientName(), ticket.getNumberOfSeats()));
        Protocol.Response response = readResponse();
        if(response.getResponseType() == Protocol.Response.ResponseType.OK){
            return true;
        }
        if (response.getResponseType() == Protocol.Response.ResponseType.ERROR) {
            String error = response.getError();
        }
        return false;
    }


    private void sendRequest(Protocol.Request request) {
        try {
            request.writeDelimitedTo(output);
            output.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error sending object " + e);
        }
    }

    private Protocol.Response readResponse() {
        Protocol.Response response = null;
        try {
            response = queueResponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }/////////////////////////////////////////////////////////////////////////////////////////



    private void handleUpdate(Protocol.Response response) {
        if (response.getResponseType() == Protocol.Response.ResponseType.UPDATE) {
            client.update();
        }
    }
    private boolean isUpdateResponse(Protocol.Response response) {
        return response.getResponseType() == Protocol.Response.ResponseType.UPDATE;
    }
    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Protocol.Response response = Protocol.Response.parseDelimitedFrom(input);
                    System.out.println("Response received " + response);

                    if (isUpdateResponse(response)){
                        handleUpdate(response);
                    }
                    else{
                        try{
                            queueResponses.put(response);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
