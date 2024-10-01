package ro.mpp2024.utils;

import ro.mpp2024.IService;
import ro.mpp2024.protobuffprotocol.ClientRpcWorkerProto;

import java.net.Socket;

public class RpcConcurentServerProto extends AbstractConcurentServer {

    private IService showServices;
    public RpcConcurentServerProto(int port, IService showServices) {
        super(port);
        this.showServices = showServices;
        System.out.println("RPCConcurrentServer - PROTOOOOOOOOOOOO");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorkerProto worker = new ClientRpcWorkerProto(showServices, client);
        Thread tw = new Thread(worker);
        return tw;
    }
}
