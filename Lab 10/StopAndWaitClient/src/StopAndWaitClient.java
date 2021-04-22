import java.io.*;
import java.net.*;

public class StopAndWaitClient {
    final static int MAX_PACKET_SIZE = 1024;
    public static void main(String argv[]){
        // Read arguments from command line, if not specified use defaults
        String path = "test.txt";
        if (argv.length > 0) path = argv[0];

        int timeout = 1000;
        if (argv.length > 1) timeout = Integer.parseInt(argv[1]);

        String remoteHost = "localhost";
        if (argv.length > 2) remoteHost = argv[2];

        int remotePort = 8080;
        if (argv.length > 3) remotePort = Integer.parseInt(argv[3]);

        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
        }
        catch (Exception e){
            System.out.println("Error creating socket: " + e);
            return;
        }

        BufferedReader fileReader;
        try {
            fileReader = new BufferedReader(new FileReader(new File(path)));
        }
        catch (FileNotFoundException e){
            System.out.println("File does not exist");
            return;
        }

        char [] sendBuffer = new char [MAX_PACKET_SIZE];
        byte [] receiveBuffer = new byte [MAX_PACKET_SIZE];
        int number = 0;
        boolean fileNameSent = false;

        while (true){
            // Send first file name, then file data (split into frames of MAX_PACKET_SIZE)
            int bufSize = 1;
            try {
                if (fileNameSent) {
                    sendBuffer[0] = (char) number;
                    bufSize += fileReader.read(sendBuffer, 1, sendBuffer.length - 1);
                }
                else{
                    String fileNamePacket = (char)number + "Filename: " + path;
                    bufSize = fileNamePacket.length();
                    fileNamePacket.getChars(0, bufSize, sendBuffer, 0);
                }
            }
            catch (IOException e){
                System.out.println("Error reading file: " + e);
                return;
            }

            if (bufSize == 0) break;

            // Try sending current frame until we receive a correct ACK
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(
                            new String(sendBuffer).getBytes(), bufSize,
                            InetAddress.getByName(remoteHost), remotePort);
                    socket.send(packet);
                    System.out.println("Sent packet with number: " + number);
                }
                catch (Exception e) {
                    System.out.println("Error sending packet: " + e);
                    return;
                }
                try{
                    DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivedPacket);
                    String receivedMessage = new String(receiveBuffer);
                    if (receivedMessage.startsWith("ACK " + number)){
                        System.out.println("Received ACK packet with correct number " + number);
                        break;
                    }
                    else{
                        System.out.println("Received incorrect ACK message: " + receivedMessage + ", retry");
                    }
                }
                catch (SocketTimeoutException e){
                    System.out.println("Timeout, retry");
                }
                catch (IOException e){
                    System.out.println("Error receiving answer: " + e);
                    return;
                }
            }
            // Update current number
            number = 1 - number;
            if (!fileNameSent) fileNameSent = true;
        }
    }
}
