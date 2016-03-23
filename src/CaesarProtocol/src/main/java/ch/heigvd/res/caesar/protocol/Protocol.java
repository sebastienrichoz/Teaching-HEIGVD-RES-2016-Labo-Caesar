package ch.heigvd.res.caesar.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Richoz & Akesson
 */
public class Protocol {

    public static final int PORT = 2205;
    public static final int BUFFER_SIZE = 40;
    public String message;
    byte offset;
    byte[] messageLength = new byte[4];
    public Protocol(String message) {
        offset = (byte) new Random().nextInt();
        this.message = message;
        messageLength = ByteBuffer.allocate(4).putInt(message.length()).array();
    }
    public static int byteArrayToInt(byte[] b)
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
            (b[1] & 0xFF) << 16 |
            (b[0] & 0xFF) << 24;
    }
    public static String getDecodedMessage(byte[] messageToDecode) throws UnsupportedEncodingException {
        byte offset = messageToDecode[0];
        byte[] messageLength = Arrays.copyOfRange(messageToDecode, 1, 5);
        int size = byteArrayToInt(messageLength);
        byte[] messageBytes = Arrays.copyOfRange(messageToDecode, 5, messageToDecode.length);
        for (int i = 0; i < size; i++) {
            messageBytes[i] -= offset;
        }
        return new String(messageBytes, "UTF-8").substring(0, size);
    }

    public byte[] getEncodedMessage() {
        offset = (byte) (new Random().nextInt()+1);
        byte[] messageBytes = message.getBytes();
        int encodedMessageLength = messageBytes.length;
        byte[] encodedMessage = new byte[encodedMessageLength + 5];

        encodedMessage[0] = offset;
        for(int i = 0; i< messageBytes.length; i++) {
            messageBytes[i] += offset;
        }
        byte[] result = new byte[1 + 4 + messageBytes.length];
        result[0] = offset;
        System.arraycopy(messageLength, 0, result, 1, 4);
        System.arraycopy(messageBytes, 0, result, 5, messageBytes.length);
        return result;
    }

}
