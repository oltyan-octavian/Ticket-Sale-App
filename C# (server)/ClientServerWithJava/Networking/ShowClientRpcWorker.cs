using System;
using System.IO;
using System.Net.Sockets;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading;
using Service;
using Model;

namespace Networking
{
    public class ShowClientRpcWorker : IDisposable, Observer
    {
        private readonly IService server;
        private NetworkStream networkStream;
        private readonly TcpClient connection;
        private readonly BinaryFormatter formatter;
        private volatile bool connected;

        public ShowClientRpcWorker(IService server, TcpClient connection)
        {
            this.server = server ?? throw new ArgumentNullException(nameof(server));
            this.connection = connection ?? throw new ArgumentNullException(nameof(connection));
            formatter = new BinaryFormatter(); 
            connected = true;
        }

        public void Run()
        {
            while (connected)
            {
                try
                {
                    //networkStream = connection.GetStream();
                    //object request = formatter.Deserialize(networkStream);
                    networkStream = connection.GetStream();
                    byte[] buffer = new byte[1024]; // Adjust buffer size as needed
                    int bytesRead = networkStream.Read(buffer, 0, buffer.Length);
                    MemoryStream receivedStream = new MemoryStream(buffer);
                    Request request = (Request)formatter.Deserialize(receivedStream);
                    Response response = HandleRequest((Request)request);
                    if (response != null)
                    {
                        SendResponse(response);
                    }
                }
                catch (IOException e)
                {
                    Console.WriteLine("Error " + e);
                }

                Thread.Sleep(1000);
            }

            try
            {
                connection.Close();
            }
            catch (IOException e)
            {
                Console.WriteLine("Error " + e);
            }
        }

        private static readonly Response OkResponse = new Response.Builder().Type(ResponseType.OK).Build();

        private Response HandleRequest(Request request)
        {
            switch (request.Type)
            {
                case RequestType.LOGIN:
                    Console.WriteLine("Login request ...");
                    UserDTO userDTO = (UserDTO)request.Data;
                    try
                    {
                        var optional = server.Login(userDTO.Name, userDTO.Password, this);
                        if (optional != null)
                        {
                            return new Response.Builder().Type(ResponseType.OK).Data(userDTO).Build();
                        }
                        else
                        {
                            connected = false;
                            return new Response.Builder().Type(ResponseType.ERROR).Data("Invalid username or password").Build();
                        }
                    }
                    catch (Exception e)
                    {
                        connected = false;
                        return new Response.Builder().Type(ResponseType.ERROR).Data(e.Message).Build();
                    }
                case RequestType.LOGOUT:
                    Console.WriteLine("Logout request ...");
                    string username = (string)request.Data;
                    try
                    {
                        server.Logout(username);
                        return OkResponse;
                    }
                    catch (Exception e)
                    {
                        connected = false;
                        return new Response.Builder().Type(ResponseType.ERROR).Data(e.Message).Build();
                    }
                case RequestType.SIGNUP:
                    Console.WriteLine("Register request ...");
                    // Handle signup request
                    return null;
                case RequestType.GET_MATCHES:
                    Console.WriteLine("Get matches request ...");
                    try
                    {
                        return new Response.Builder().Type(ResponseType.GET_MATCHES).Data(server.GetListMatches((string)request.Data)).Build();
                    }
                    catch (Exception e)
                    {
                        connected = false;
                        return new Response.Builder().Type(ResponseType.ERROR).Data(e.Message).Build();
                    }
                case RequestType.GET_MATCH:
                    Console.WriteLine("Get match request ...");
                    int id = (int)request.Data;
                    try
                    {
                        return new Response.Builder().Type(ResponseType.GET_MATCH).Data(server.GetMatch(id)).Build();
                    }
                    catch (Exception e)
                    {
                        connected = false;
                        return new Response.Builder().Type(ResponseType.ERROR).Data(e.Message).Build();
                    }
                case RequestType.BUY_TICKETS:
                    Console.WriteLine("Buy tickets request ...");
                    BuyTicketsDTO purchase = (BuyTicketsDTO)request.Data;
                    try
                    {
                        server.AddTicket(purchase);
                        return OkResponse;
                    }
                    catch (Exception e)
                    {
                        connected = false;
                        return new Response.Builder().Type(ResponseType.ERROR).Data(e.Message).Build();
                    }
                default:
                    return null;
            }
        }

        private void SendResponse(Response response)
        {
            try
            {
                MemoryStream memoryStream = new MemoryStream();
                formatter.Serialize(memoryStream, response);
                networkStream.Write(memoryStream.GetBuffer(), 0, (int)memoryStream.Length);
                networkStream.Flush();
                //formatter.Serialize(networkStream, response);
                //networkStream.Flush();
            }
            catch (IOException e)
            {
                // Handle exception
            }
        }

        public void Dispose()
        {
            connection?.Dispose();
        }
        
        public void Update()
        {
            Response response = new Response.Builder().Type(ResponseType.UPDATE).Data(null).Build();
            SendResponse(response);
        }

    }
}
