package ro.mpp2024.utils;

import ro.mpp2024.ShowClientRpcWorker;
import ro.mpp2024.IService;

import java.net.Socket;

public class RpcConcurentServer extends AbstractConcurentServer {

    private IService service;

    public RpcConcurentServer(int port, IService service) {
        super(port);
        this.service = service;
        System.out.println("RPCConcurrentServer");
    }
    @Override
    protected Thread createWorker(Socket client) {
        ShowClientRpcWorker worker = new ShowClientRpcWorker(service, client);
        Thread tw = new Thread(worker);
        return tw;
    }
}
