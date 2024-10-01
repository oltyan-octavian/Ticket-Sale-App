using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading.Tasks;
using Service;
using Model;

namespace Networking
{
    public class ShowServicesRpcProxy : IService
    {
        private string host;
        private int port;

        private Observer client;
        public BinaryFormatter binaryFormatter { get; set; }
        private TcpClient connection;
        public NetworkStream networkStream { get; set; }
        private volatile bool finished;
        private BlockingCollection<Response> queueResponses;

        public ShowServicesRpcProxy(string host, int port)
        {
            this.host = host;
            this.port = port;
            binaryFormatter = new BinaryFormatter();
            queueResponses = new BlockingCollection<Response>();
        }

        public User Login(string username, string password, Observer client)
        {
            InitializeConnection();
            UserDTO userDTO = new UserDTO(username, password);
            Request request = new Request.Builder().Type(RequestType.LOGIN).Data(userDTO).Build();
            SendRequest(request);
            Response response = ReadResponse();
            if (response.Type == ResponseType.OK)
            {
                this.client = client;
                UserDTO receivedUserDTO = (UserDTO)response.Data;
                User user = new User(receivedUserDTO.Name, receivedUserDTO.Password);
                return user;
            }
            if (response.Type == ResponseType.ERROR)
            {
                string error = response.Data.ToString();
                CloseConnection();
                throw new ArgumentException(error);
            }
            return null;
        }

        public void Logout(string username)
        {
            Request request = new Request.Builder().Type(RequestType.LOGOUT).Data(username).Build();
            SendRequest(request);
            Response response = ReadResponse();
            if (response.Type == ResponseType.ERROR)
            {
                string error = response.Data.ToString();
                throw new ArgumentException(error);
            }
        }

        public MatchDTO GetMatch(int i)
        {
            Request request = new Request.Builder().Type(RequestType.GET_MATCH).Data(i).Build();
            SendRequest(request);
            Response response = ReadResponse();
            if (response.Type == ResponseType.GET_MATCH)
            {
                return (MatchDTO)response.Data;
            }
            if (response.Type == ResponseType.ERROR)
            {
                string error = response.Data.ToString();
                throw new ArgumentException(error);
            }
            return null;
        }

        public bool AddTicket(BuyTicketsDTO ticket)
        {
            Request request = new Request.Builder().Type(RequestType.BUY_TICKETS).Data(ticket).Build();
            SendRequest(request);
            Response response = ReadResponse();
            if (response.Type == ResponseType.ERROR)
            {
                string error = response.Data.ToString();
                throw new ArgumentException(error);
            }
            return true;
        }

        private void CloseConnection()
        {
            finished = true;
            try
            {
                connection.Close();
                client = null;
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }
        }

        private void SendRequest(Request request)
        {
            try
            {
                MemoryStream memoryStream = new MemoryStream();
                binaryFormatter.Serialize(memoryStream, request);
                networkStream.Write(memoryStream.GetBuffer(), 0, (int)memoryStream.Length);
                networkStream.Flush();
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error sending object " + e);
            }
        }

        private Response ReadResponse()
        {
            Response response = null;
            try
            {
                networkStream = connection.GetStream();
                byte[] buffer = new byte[1024]; // Adjust buffer size as needed
                int bytesRead = networkStream.Read(buffer, 0, buffer.Length);
                MemoryStream receivedStream = new MemoryStream(buffer);
                response = (Response)binaryFormatter.Deserialize(receivedStream);
                networkStream.Flush();
            }
            catch (Exception exception)
            {
                Console.WriteLine(exception);
            }
            return response;
        }

        private void InitializeConnection()
        {
            try
            {
                connection = new TcpClient();
                connection.Connect(host, port);

                networkStream = connection.GetStream();
        
                finished = false;
                StartReader();
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }
        }

        private void StartReader()
        {
            Task tw = Task.Run(() => ReaderThread.Run(this));
        }

        public ICollection<string> GetListMatches(string search)
        {
            Request request = new Request.Builder().Type(RequestType.GET_MATCHES).Data(search).Build();
            SendRequest(request);
            Response response = ReadResponse();
            if (response.Type == ResponseType.ERROR)
            {
                string error = response.Data.ToString();
                throw new ArgumentException(error);
            }
            return (ICollection<string>)response.Data;
        }

        private void HandleUpdate(Response response)
        {
            if (response.Type == ResponseType.UPDATE)
            {
                client.Update();
            }
        }

        private bool IsUpdateResponse(Response response)
        {
            return response.Type == ResponseType.UPDATE;
        }

        private class ReaderThread
        {
            public static void Run(ShowServicesRpcProxy proxy)
            {
                while (!proxy.finished)
                {
                    try
                    {
                            proxy.networkStream = proxy.connection.GetStream();
                            byte[] buffer = new byte[1024]; // Adjust buffer size as needed
                            int bytesRead = proxy.networkStream.Read(buffer, 0, buffer.Length);
                            MemoryStream receivedStream = new MemoryStream(buffer);
                            Response receivedResponse = (Response)proxy.binaryFormatter.Deserialize(receivedStream);
                            //object obj = proxy.binaryFormatter.Deserialize(proxy.networkStream);
                            //Response receivedResponse = (Response)obj;
                            Console.WriteLine("Response received " + receivedResponse);
                            if (proxy.IsUpdateResponse(receivedResponse))
                            {
                                proxy.HandleUpdate(receivedResponse);
                            }
                            else
                            {
                                try
                                {
                                    proxy.queueResponses.Add(receivedResponse);
                                }
                                catch (Exception e)
                                {
                                    Console.WriteLine(e);
                                }
                            }
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine("Reading error " + e);
                    }
                }
            }
        }
    }
}
