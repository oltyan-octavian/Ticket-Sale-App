using System;
using System.Net.Sockets;
using System.Threading;

namespace Networking.utils
{
    public abstract class AbstractConcurrentServer : AbstractServer
    {
        public AbstractConcurrentServer(int port) : base(port)
        {
            Console.WriteLine("AbstractConcurrentServer");
        }

        protected override void ProcessRequest(TcpClient client)
        {
            Thread tw = CreateWorker(client);
            tw.Start();
        }

        protected abstract Thread CreateWorker(TcpClient client);
    }
}