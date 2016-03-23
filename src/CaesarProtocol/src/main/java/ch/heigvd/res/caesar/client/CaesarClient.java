package ch.heigvd.res.caesar.client;

import ch.heigvd.res.caesar.protocol.Protocol;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Richoz & Akesson
 */
public class CaesarClient {

    private static final Logger LOG = Logger.getLogger(CaesarClient.class.getName());

    Socket clientSocket;
    InputStream in;
    OutputStream out;
    boolean connected = false;

    /*
     * This method is used to connect to the server and to inform the server that
     * the user "behind" the client has a name (in other words, the HELLO command
     * is issued after successful connection).
     *
     * @param serverAddress the IP address used by the Presence Server
     * @param serverPort the port used by the Presence Server
     */
    public void connect(String serverAddress, int serverPort) {
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            connected = true;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
            cleanup();
            return;
        }

        // Let us send an Ave Caesar message to inform the server that a client
        // arrived
        try {
            out.write(new Protocol("Ave Caesar !").getEncodedMessage());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Write the string passed in argument to the server
     *
     * @param str the string to send to server
     */
    public void write(String str) {
        Protocol message = new Protocol(str);
        try {
            out.write(message.getEncodedMessage());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Read the data sent from the server. The data is
     *
     */
    public String read() throws UnsupportedEncodingException {
        byte[] encodedMessage = new byte[Protocol.BUFFER_SIZE];

        try {
            in.read(encodedMessage);
            if(Protocol.getDecodedMessage(encodedMessage).equalsIgnoreCase("bye"))
                disconnect();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

            return Protocol.getDecodedMessage(encodedMessage);
    }

    public void disconnect() {
        LOG.log(Level.INFO, "Requested to be disconnected.");
        connected = false;
        try {
            out.write(new Protocol("BYE").getEncodedMessage());
        } catch (IOException ex) {
            Logger.getLogger(CaesarClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        cleanup();
    }

    private void cleanup() {

        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(CaesarClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS::%1$tL] Client > %5$s%n");
        LOG.info("Caesar client starting...");
        LOG.info("Protocol port server: " + Protocol.PORT);
        CaesarClient client = new CaesarClient();
        String messageReceived = null;
        client.connect("localhost", Protocol.PORT);
        try {
            messageReceived = client.read();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LOG.info(messageReceived);
        while (client.isConnected()) {
            Scanner sc = new Scanner(System.in);
            // Write his message for the server
            System.out.println("Write your message for Caesar : ");
            // Empty the line before reading another one
            String str = sc.nextLine();
            client.write(str);
            try {
                messageReceived = client.read();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("Message received from Caesar : " + messageReceived);
        }

        client.disconnect();
    }

}