
using System;
using System.Collections;
using System.Collections.Generic;
using Model;
using Networking;
using Proto=ClientServerWithJava.Networking;
using User=Model.User;


namespace Networking.protobuffprotocol
{
    public class ProtoUtils
    {
        
        public static User GetUser(Proto.Request request)
        {
            User user = new User(request.Userdto.Name, request.Userdto.Password);
            return user;
        }
        
        
        public static Proto.Response CreateOkResponse()
        {
            Proto.Response response = new Proto.Response { ResponseType = Proto.Response.Types.ResponseType.Ok };
            return response;
        }
        
        public static Proto.Response CreateOkResponse(User user)
        {
            Proto.Response response = new Proto.Response { ResponseType = Proto.Response.Types.ResponseType.Ok };
            Proto.UserDTO protoUser = new Proto.UserDTO { Name = user.name, Password = user.password };
            response.Userdto = protoUser;
            return response;
        }

        public static Proto.Response CreateGetMatchesResponse(ICollection<string> matches)
        {
            Proto.Response response = new Proto.Response { ResponseType = Proto.Response.Types.ResponseType.GetMatches };
            response.Matches.AddRange(matches);
            return response;
        }

        public static Proto.Response CreateGetMatchResponse(MatchDTO match)
        {
            Proto.Response response = new Proto.Response { ResponseType = Proto.Response.Types.ResponseType.GetMatch };
            Proto.MatchDTO matchDTO = new Proto.MatchDTO
            {
                Id = match.Id,
                Type = match.Type,
                AvailableSeats = match.AvailableSeats,
                Team1ID = match.Team1ID,
                Team2ID = match.Team1ID,
                SeatPrice = match.SeatPrice
            };
            response.Matchdto = matchDTO;
            return response;
        }
        
        public static Proto.Response CreateErrorResponse(string message)
        {
            Proto.Response response = new Proto.Response { ResponseType = Proto.Response.Types.ResponseType.Error, Error = message };
            return response;
        }
        
        public static Proto.Response CreateUpdateResponse()
        {
            Proto.Response response = new Proto.Response{ResponseType = Proto.Response.Types.ResponseType.Update};
            return response;
        }
        
        
        public static long GetId(Proto.Request request)
        {
            return request.IdUser;
        } 
        
        
    }
}