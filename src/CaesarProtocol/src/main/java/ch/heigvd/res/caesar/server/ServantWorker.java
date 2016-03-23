package ch.heigvd.res.caesar.server;

import ch.heigvd.res.caesar.protocol.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Richoz & Akesson
 */
public class ServantWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(CaesarServer.class.getName());

    Socket clientSocket;
    InputStream in = null;
    OutputStream out = null;

    public ServantWorker(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(CaesarServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        Protocol message;
        String messageString;
        byte[] receivedMessage = new byte[Protocol.BUFFER_SIZE];
        boolean shouldRun = true;


        try {
            LOG.info("Reading until client sends BYE or closes the connection...");
            while ((shouldRun) && (in.read(receivedMessage)) != -1) {
                messageString = Protocol.getDecodedMessage(receivedMessage);
                if(messageString.equalsIgnoreCase("bye")) {
                    shouldRun = false;
                    message = new Protocol("bye");
                } else {
                    message = new Protocol(messageString);
                    LOG.info("The received decoded message is: " + messageString);
                }
                out.write(message.getEncodedMessage());
                out.flush();
            }

            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();

        } catch (IOException ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
