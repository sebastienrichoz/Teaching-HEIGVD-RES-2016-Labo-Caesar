package ch.heigvd.res.caesar.server;

import ch.heigvd.res.caesar.protocol.Protocol;
import java.util.logging.Logger;

/**
 * Created by Richoz & Akesson
 */
public class CaesarServer {

    private static final Logger LOG = Logger.getLogger(CaesarServer.class.getName());
    int port;

    private CaesarServer() {
        this.port = Protocol.PORT;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS::%1$tL] Server > %5$s%n");
        LOG.info("Caesar server starting...");
        LOG.info("Protocol constant: " + Protocol.PORT);
        ReceptionistWorker receptionist = new ReceptionistWorker();
        receptionist.run();
    }

}
