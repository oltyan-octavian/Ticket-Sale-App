using System;
using System.Net.Sockets;
using Service;

namespace Networking.utils
{
    public class RpcConcurrentServer : AbstractConcurrentServer
    {
        private IService showServices;

        public RpcConcurrentServer(int port, IService showServices) : base(port)
        {
            this.showServices = showServices;
            Console.WriteLine("RPCConcurrentServer");
        }

        protected override System.Threading.Thread CreateWorker(TcpClient client)
        {
            ShowClientRpcWorker worker = new ShowClientRpcWorker(showServices, client);
            System.Threading.Thread tw = new System.Threading.Thread(worker.Run);
            return tw;
        }
    }
}