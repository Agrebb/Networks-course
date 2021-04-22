import java.io.*;
import java.net.*;

public class StopAndWaitServer {
    final static int MAX_PACKET_SIZE = 1024;
    public static void main(String argv[]) {

        //Get port number from command line, if not specified use default
        int port = 8080;
        if (argv.length > 0) port = Integer.parseInt(argv[0]);

        DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(port);
        }
        catch (Exception e){
            System.out.println("Error creating socket: " + e);
            return;
        }

        byte[] receiveBuffer = new byte[MAX_PACKET_SIZE];
        BufferedWriter fileWriter = null;

        int lastNumber = 1;
        while (true) {
            try {
                //Receive message from client
                for (int i = 0; i < receiveBuffer.length; i++) receiveBuffer[i] = 0;
                DatagramPacket inputPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(inputPacket);
                String receivedData = new String(inputPacket.getData());

                //Simulate package loss on "client->server"
                if (Math.random() < 0.3)
                    continue;

                //Extract number from message
                int number = (int)receivedData.charAt(0);
                String ackMessage = "ACK " + number;

                int lastIndex = 0;
                for (int i = receivedData.length() - 1; i >= 0; i--){
                    if (receivedData.charAt(i) != 0){
                        lastIndex = i;
                        break;
                    }
                }
                String message = receivedData.substring(1, lastIndex + 1);

                // If message has correct number, process it
                if (number != lastNumber) {
                    System.out.println("Received packet with correct number " + number);
                    lastNumber = number;
                    // If message is file name? create new fileWriter
                    if (message.startsWith("Filename: ")) {
                        String fileName = message.substring("Filename: ".length());
                        File file = new File("test/" + fileName);
                        if (!file.exists()) file.createNewFile();
                        fileWriter = new BufferedWriter(new FileWriter(new File(fileName)));

                    } else {
                        fileWriter.write(message);
                        fileWriter.flush();
                    }
                }
                else{
                    System.out.println("Received packet with incorrect number " + number + ", discard");
                }

                //Simulate package loss on "server->client"
                if (Math.random() < 0.3)
                    continue;

                //Send ACK to client
                DatagramPacket outputPacket = new DatagramPacket(
                        ackMessage.getBytes(), ackMessage.length(),
                        inputPacket.getAddress(), inputPacket.getPort()
                );
                serverSocket.send(outputPacket);
            }
            catch (Exception e){
                System.out.println("Server error: " + e);
            }
        }
    }
}
