using System;
using System.Net;
using System.Net.Sockets;

namespace Networking.utils
{
    public abstract class AbstractServer
    {
        private int port;
        private TcpListener server;

        public AbstractServer(int port)
        {
            this.port = port;
        }

        public void Start()
        {
            try
            {
                server = new TcpListener(IPAddress.Any, port);
                server.Start();
                Console.WriteLine("Server started on port " + port);
                while (true)
                {
                    Console.WriteLine("Waiting for clients ...");
                    TcpClient client = server.AcceptTcpClient();
                    Console.WriteLine("Client connected ...");
                    ProcessRequest(client);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }
            finally
            {
                server.Stop();
            }
        }

        protected abstract void ProcessRequest(TcpClient client);

        public void Stop()
        {
            server.Stop();
            Console.WriteLine("Server stopped.");
        }
    }
}