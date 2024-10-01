using System;
using System.IO;
using System.Net.Sockets;
using System.Threading;
using Model;
using Networking;
using Service;
using Proto=ClientServerWithJava.Networking;
using Google.Protobuf;

namespace Networking.protobuffprotocol
{
    public class CompetitionClientRpcWorkerProto : Observer
    {
        private IService _server;
        private TcpClient _connection;
        private NetworkStream _stream;
        private volatile bool _connected;
        
        
        public CompetitionClientRpcWorkerProto(IService server, TcpClient connection)
        {
            _server = server;
            _connection = connection;
            try
            {
                _stream = connection.GetStream();
                //_formatter = new BinaryFormatter();
                _connected = true;
            }
            catch (IOException e)
            {
                Console.WriteLine(e.StackTrace);
            }
        }
        
        public void Run()
        {
            try
            {
                while (_connected)
                {
                    if (_stream.CanRead && _stream.DataAvailable)
                    {
                        try
                        {
                            //var request = _formatter.Deserialize(_stream);
                            Proto.Request request = Proto.Request.Parser.ParseDelimitedFrom(_stream);
                            Proto.Response response = HandleRequest(request);
                            if (response != null)
                            {
                                SendResponse(response);
                            }
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine(e.StackTrace);
                            _connected = false;
                        }
                    }
                    else
                    {
                        Thread.Sleep(100);
                    }
                }
            }
            finally
            {
                try
                {
                    _stream.Close();
                    _connection.Close();
                }
                catch (Exception e)
                {
                    Console.WriteLine("Error " + e);
                }
            }
        }
        
        
        private Proto.Response HandleRequest(Proto.Request request)
        {
            Proto.Response response = null;
            Proto.Request.Types.RequestType requestType = request.RequestType;
            switch (requestType)
            {
                case Proto.Request.Types.RequestType.Login:
                    Console.WriteLine("Login request");
                    User user = ProtoUtils.GetUser(request);
                    try
                    {
                        User userOptional;
                        
                        userOptional = _server.Login(user.name, user.password, this);
                        
                        if (userOptional != null)
                        {
                            return ProtoUtils.CreateOkResponse(userOptional);
                        }
                        else
                        {
                            _connected = false;
                            return ProtoUtils.CreateErrorResponse("Invalid username or password");
                        }
                    }
                    catch (Exception e)
                    {
                        _connected = false;
                        return ProtoUtils.CreateErrorResponse("Authentication failed " + e.Message);
                    }
                case Proto.Request.Types.RequestType.Logout:
                    Console.WriteLine("Logout request");
                    string username = request.Username;
                    try
                    {
                        _server.Logout(username);
                        _connected = false;
                        return ProtoUtils.CreateOkResponse();
                    }
                    catch (Exception e)
                    {
                        _connected = false;
                        return ProtoUtils.CreateErrorResponse("Logout failed " + e.Message);
                    }
                case Proto.Request.Types.RequestType.GetMatches:
                    Console.WriteLine("Get matches request");
                    try
                    {

                        return ProtoUtils.CreateGetMatchesResponse(_server.GetListMatches(request.Filtru));

                    }
                    catch (Exception e)
                    {
                        _connected = false;
                        return ProtoUtils.CreateErrorResponse("Error getting shows " + e.Message);
                    }
                case Proto.Request.Types.RequestType.GetMatch:
                    Console.WriteLine("Get match request");
                    try
                    {
                        return ProtoUtils.CreateGetMatchResponse(_server.GetMatch(request.IdUser));
                    }
                    catch (Exception e)
                    {
                        _connected = false;
                        return ProtoUtils.CreateErrorResponse("Error getting match " + e.Message);
                    }
                case Proto.Request.Types.RequestType.BuyTickets:
                    Console.WriteLine("Buy tickets request");
                    try
                    {
                        Proto.BuyTicketsDTO ticketdto = request.Ticketdto;
                        BuyTicketsDTO ticket = new BuyTicketsDTO(ticketdto.MatchID, ticketdto.Price,
                            ticketdto.ClientName, ticketdto.NumberOfSeats);
                        _server.AddTicket(ticket);
                        return ProtoUtils.CreateOkResponse();
                    }
                    catch (Exception e)
                    {
                        _connected = false;
                        return ProtoUtils.CreateErrorResponse("Error buying ticket " + e.Message);
                    }
                default:
                    return ProtoUtils.CreateErrorResponse("Invalid request type");
            }
        }
        
        
        private void SendResponse(Proto.Response response)
        {
            lock (_stream)
            {
                try
                {
                    response.WriteDelimitedTo(_stream);
                    //_formatter.Serialize(_stream, response);
                    _stream.Flush();
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.StackTrace);
                }
            }
        }
        
        
        public void Update()
        {
            Console.WriteLine("Update received");
            Proto.Response response = ProtoUtils.CreateUpdateResponse();
            SendResponse(response);
        }
        
        
        public void registerParticipant()
        {
            Console.WriteLine("registered participant");
            Proto.Response response = ProtoUtils.CreateUpdateResponse();
            SendResponse(response);
        }
    }
}