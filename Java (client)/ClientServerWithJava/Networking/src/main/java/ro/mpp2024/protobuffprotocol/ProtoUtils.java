package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.*;
import ro.mpp2024.BuyTicketsDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtoUtils {

    public static Protocol.Request createLoginRequest(User user) {
        Protocol.UserDTO user2 = Protocol.UserDTO.newBuilder().setName(user.getName()).setPassword(user.getPassword()).build();
        Protocol.Request request = Protocol.Request.newBuilder().setRequestType(Protocol.Request.RequestType.LOGIN).setUserdto(user2).build();
        return request;
    }

    public static Protocol.Request createLogoutRequest(String username) {
        Protocol.Request request = Protocol.Request.newBuilder().setRequestType(Protocol.Request.RequestType.LOGOUT).setUsername(username).build();
        return request;
    }

    public static Protocol.Request createGetMatchRequest(int id) {
        Protocol.Request request = Protocol.Request.newBuilder().setRequestType(Protocol.Request.RequestType.GET_MATCH).setIdUser(id).build();
        return request;
    }

    public static Protocol.Request createGetMatchesRequest(String search) {
        Protocol.Request request = Protocol.Request.newBuilder().setRequestType(Protocol.Request.RequestType.GET_MATCHES).setFiltru(search).build();
        return request;
    }

    public static Protocol.Request createBuyTicketsRequest(int matchID, int price, String clientName, int numberOfSeats) {
        Protocol.BuyTicketsDTO participant =
                Protocol.BuyTicketsDTO.newBuilder()
                        .setMatchID(matchID)
                        .setPrice(price)
                        .setClientName(clientName)
                        .setNumberOfSeats(numberOfSeats)
                        .build();

        Protocol.Request request = Protocol.Request.newBuilder().setRequestType(Protocol.Request.RequestType.BUY_TICKETS).setTicketdto(participant).build();
        return request;
    }


    public static Protocol.Response createOkResponse() {
        Protocol.Response response = Protocol.Response.newBuilder().setResponseType(Protocol.Response.ResponseType.OK).build();
        return response;
    }

    public static Protocol.Response createOkResponse(User user) {
        Protocol.UserDTO user2 = Protocol.UserDTO.newBuilder().setName(user.getName()).setPassword(user.getPassword()).build();
        Protocol.Response response = Protocol.Response.newBuilder().setResponseType(Protocol.Response.ResponseType.OK).setUserdto(user2).build();
        return response;
    }

    public static Protocol.Response createErrorResponse(String text) {
        Protocol.Response response = Protocol.Response.newBuilder().setResponseType(Protocol.Response.ResponseType.ERROR).setError(text).build();
        return response;
    }

    public static Protocol.Response createGetMatchesResponse(List<String> competitions) {
        Protocol.Response.Builder responseBuilder = Protocol.Response.newBuilder().setResponseType(Protocol.Response.ResponseType.GET_MATCHES);
        //matches is repeated string in .proto file
        responseBuilder.addAllMatches(competitions);
        return responseBuilder.build();
    }

    public static Protocol.Response createGetMatchResponse(MatchDTO match) {
        Protocol.Response.Builder responseBuilder = Protocol.Response.newBuilder().setResponseType(Protocol.Response.ResponseType.GET_MATCH);
        Protocol.MatchDTO matchDTO = Protocol.MatchDTO.newBuilder()
                .setId(match.getId())
                .setType(match.getType())
                .setAvailableSeats(match.getAvailableSeats())
                .setTeam1ID(match.getTeam1ID())
                .setTeam2ID(match.getTeam2ID())
                .setSeatPrice(match.getSeatPrice())
                .build();
        responseBuilder.setMatchdto(matchDTO);
        return responseBuilder.build();
    }

    public static User getUser(Protocol.Response response){
        User user = new User("", "");
        user.setName(response.getUserdto().getName());
        user.setPassword(response.getUserdto().getPassword());
        return user;
    }

    public static User getUser(Protocol.Request request){
        User user = new User("", "");
        user.setName(request.getUserdto().getName());
        user.setPassword(request.getUserdto().getPassword());
        return user;
    }


    public static Protocol.Response createUpdateResponse() {
        Protocol.Response response = Protocol.Response.newBuilder().setResponseType(Protocol.Response.ResponseType.UPDATE).build();
        return response;
    }


    public static Long getId(Protocol.Request request){
        return (long) request.getIdUser();
    }



}
