using System;
using System.Collections.Generic;
using System.IO;
using Persistence;
using Networking.utils;
using System.Configuration;
using System.Net.Sockets;
using System.Threading;
using log4net.Config;
using Networking;
using Networking.protobuffprotocol;
using Service;

namespace Server
{
    public class StartRpcServer
    {
        private static int defaultPort = 55555;

        public static void Main(string[] args)
        {
            XmlConfigurator.Configure(new System.IO.FileInfo("C:\\Users\\oltya\\Documents\\Laboratoare Anul II\\Semestrul II\\MPP\\C#\\ClientServer\\Client\\log4net.config"));
            // BasicConfigurator.Configure();
            Console.WriteLine("Configuration Settings for basket {0}",GetConnectionStringByName("MyConnection"));
            IDictionary<String, string> serverProps = new SortedList<string, string>();
            serverProps.Add("ConnectionString", GetConnectionStringByName("MyConnection"));
            var propertiesServer = LoadPropertiesFromFile("server.properties");

            var matchRepo = new MatchDBRepository(serverProps);
            var teamRepo = new TeamDBRepository(serverProps);
            var ticketRepo = new TicketDBRepository(serverProps);
            var userRepo = new UserDBRepository(serverProps);
            var service = new ShowServicesImpl(matchRepo, teamRepo, ticketRepo, userRepo);

            Console.WriteLine("Starting server on port " + propertiesServer["port"]);
            var portInt = int.Parse(propertiesServer["port"]);
            var server = new SerialServerProto("127.0.0.1", portInt, service);

            try
            {
                server.Start();
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("Error starting the server " + e.Message);
            }
        }

        private static IDictionary<string, string> LoadPropertiesFromFile(string fileName)
        {
            var properties = new Dictionary<string, string>();
            try
            {
                using (var fileStream = new FileStream(fileName, FileMode.Open))
                using (var reader = new StreamReader(fileStream))
                {
                    string line;
                    while ((line = reader.ReadLine()) != null)
                    {
                        // Assuming properties file format is key=value
                        var parts = line.Split('=');
                        if (parts.Length == 2)
                        {
                            properties[parts[0]] = parts[1];
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw new Exception($"Cannot load properties from {fileName}: {e.Message}");
            }
            return properties;
        }
        
        static string GetConnectionStringByName(string name)
        {
            // Assume failure.
            string returnValue = null;

            // Look for the name in the connectionStrings section.
            ConnectionStringSettings settings =ConfigurationManager.ConnectionStrings[name];

            // If found, return the connection string.
            if (settings != null)
                returnValue = settings.ConnectionString;

            return returnValue;
        }
        
        public class SerialServer : ConcurrentServer
        {
            private IService server;
            
            private ShowClientRpcWorker worker;

            public SerialServer(string host, int port, IService server) : base(host, port)
            {
                this.server = server;
                Console.WriteLine("SerialServer...");
            }

            protected override Thread createWorker(TcpClient client)
            {
                worker = new ShowClientRpcWorker(server, client);
                return new Thread(new ThreadStart(worker.Run));
            }
        }
        
        public class SerialServerProto : ConcurrentServer
        {
            private IService server;
            private CompetitionClientRpcWorkerProto worker;

            public SerialServerProto(string host, int port, IService server) : base(host, port)
            {
                this.server = server;
                Console.WriteLine("SerialServerProto...");
            }

            protected override Thread createWorker(TcpClient client)
            {
                worker = new CompetitionClientRpcWorkerProto(server, client);
                return new Thread(new ThreadStart(worker.Run));
            }
        }
    }
}
