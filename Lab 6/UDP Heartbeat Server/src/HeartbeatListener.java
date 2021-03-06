import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class HeartbeatListener implements Runnable {
    private Map<String, Long> lastMessage;
    private DatagramSocket socket;

    HeartbeatListener(Map <String, Long> lastMessage, DatagramSocket socket){
        this.lastMessage = lastMessage;
        this.socket = socket;
    }

    public void run(){
        byte[] receiveBuffer = new byte[1024];
        byte[] sendBuffer = new byte[1024];

        while (true) {
            try {
                // Получаем сообщение от клиента.
                DatagramPacket inputPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(inputPacket);

                // Извлекаем нужную информацию из сообщения.
                String receivedMessage = new String(inputPacket.getData());
                String [] splittedMessage = receivedMessage.split(" ");
                System.out.println("Message received: " + splittedMessage[0]);

                int index = Integer.parseInt(splittedMessage[1]);
                long sendTime = Long.parseLong(splittedMessage[2]);
                long receiveTime = new Date().getTime();
                String client = inputPacket.getAddress().toString() + ":" + inputPacket.getPort();

                synchronized (lastMessage) {
                    // Если этого клиента не было в списке, то добавляем его туда.
                    if (!lastMessage.containsKey(client)){
                        System.out.println("Client " + client + " has connected!");
                    }
                    // Обновляем время последнего сообщения.
                    lastMessage.put(client, receiveTime);
                }
                System.out.println("Delivery time: " + (receiveTime - sendTime));
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
