package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.*;
//import ro.mpp2024.dto.RegisterParticipantDTO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ClientRpcWorkerProto implements Runnable, Observer {

    private IService server;

    private Socket connection;

    private InputStream input;

    private OutputStream output;

    private volatile boolean connected;


    public ClientRpcWorkerProto(IService competitionServices, Socket connection) {
        this.server = competitionServices;
        this.connection = connection;
        try {
            output = connection.getOutputStream();
//            output.flush();
            input = connection.getInputStream();
            connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
//                Object request = input.readObject();
                Protocol.Request request = Protocol.Request.parseDelimitedFrom(input);

                Protocol.Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            input.close();
            output.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Error " + e);
        }

    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();


    private Protocol.Response handleRequest(Protocol.Request request) {
        Protocol.Response response = null;
        if (request.getRequestType() == Protocol.Request.RequestType.LOGIN) {
            System.out.println("Login request ...");
            User userDTO =  ProtoUtils.getUser(request);
            try {
                System.out.println("Login request ..." + userDTO.getName() + " " + userDTO.getPassword());
                var optional = server.login(userDTO.getName(), userDTO.getPassword(), this);
                User user = (User) optional.get();
                if (optional!=null) {
                    return ProtoUtils.createOkResponse(user);
                }
                else{
                    connected = false;
                    return ProtoUtils.createErrorResponse("Nu exista acest utilizator");

                }
            } catch (Exception e) {
                connected = false;
                return ProtoUtils.createErrorResponse("Eroare la login");
            }
        }
        if (request.getRequestType() == Protocol.Request.RequestType.LOGOUT) {
            System.out.println("Logout request ...");
            String username = request.getUsername();
            try {
                server.logout(username);
                connected = false;
                return ProtoUtils.createOkResponse();
            } catch (Exception e) {
                connected = false;
                return ProtoUtils.createErrorResponse("Eroare la logout");
            }
        }
        if (request.getRequestType() == Protocol.Request.RequestType.SIGNUP) {
            System.out.println("Register request ...");
            return null;
//            Utilizator userDTO = (Utilizator) request.getData();
//            try {
//                server.(userDTO.getUsername(), userDTO.getPassword());
//                return new Response.Builder().type(ResponseType.OK).build();
//            } catch (AppException e) {
//                connected = false;
//                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
//            }
        }


        if (request.getRequestType() == Protocol.Request.RequestType.GET_MATCHES) {
            System.out.println("Get matches request ...");
            try {
                var allCompetitionsDTO = server.getListMatches(request.getFiltru());
                return ProtoUtils.createGetMatchesResponse((List<String>) allCompetitionsDTO);
            } catch (Exception e) {
                connected = false;
                return ProtoUtils.createErrorResponse("Eroare la get matches");
            }
        }
        if(request.getRequestType() == Protocol.Request.RequestType.GET_MATCH) {
            System.out.println("Get match request ...");
            try {
                var matchDTO = server.getMatch(request.getIdUser());
                return ProtoUtils.createGetMatchResponse(matchDTO);
            } catch (Exception e) {
                connected = false;
                return ProtoUtils.createErrorResponse("Eroare la get match");
            }
        }

        if (request.getRequestType() == Protocol.Request.RequestType.BUY_TICKETS) {
            System.out.println("Buy Tickets request ...");
            Protocol.BuyTicketsDTO register = request.getTicketdto();
            BuyTicketsDTO buyTicketsDTO = new BuyTicketsDTO(register.getMatchID(), register.getPrice(), register.getClientName(), register.getNumberOfSeats());
            try {
                server.addTicket(buyTicketsDTO);
                return ProtoUtils.createOkResponse();
            } catch (Exception e) {
                connected = false;
                return ProtoUtils.createErrorResponse("Eroare la ticket");
            }
        }
        return response;
    }


    private void sendResponse(Protocol.Response response) {
        try {
            System.out.println("Sending response " + response);
            response.writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        try {
            sendResponse(ProtoUtils.createOkResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
