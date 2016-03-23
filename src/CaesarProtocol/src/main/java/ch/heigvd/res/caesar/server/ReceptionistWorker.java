package ch.heigvd.res.caesar.server;

import ch.heigvd.res.caesar.protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Richoz & Akesson
 */
public class ReceptionistWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(CaesarServer.class.getName());

    @Override
    public void run() {
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(Protocol.PORT);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", Protocol.PORT);
            try {
                Socket clientSocket = serverSocket.accept();
                LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                new Thread(new ServantWorker(clientSocket)).start();
            } catch (IOException ex) {
                Logger.getLogger(CaesarServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
