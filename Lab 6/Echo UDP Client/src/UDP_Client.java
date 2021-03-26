import java.io.IOException;
import java.net.*;
import java.util.*;

public class UDP_Client implements Runnable {
    private final String remoteHost;
    private final int remotePort;

    static private final int QUERIES_NUM = 10;
    static private boolean[] replies = new boolean[QUERIES_NUM];
    static private long[] rtt = new long[QUERIES_NUM];

    static private final int TIMEOUT = 1000;

    UDP_Client(String host, int port){
        this.remoteHost = host;
        this.remotePort = port;
    }

    private DatagramSocket socket;
    private void initSocket() throws Exception{
        //Создаём сокет и устанавливаем задержку
        socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);
    }

    public void run(){
        try {
            initSocket();
        }
        catch (Exception e){
            System.out.println("Socket initialization failed: " + e);
            return;
        }

        for (int i = 0; i < QUERIES_NUM; i++){
            // Создаём сообщение, содержащее индекс и время отправки
            long currentTime = new Date().getTime();
            String message = "Ping " + i + " " + currentTime + " ";
            replies[i] = false;

            //Создаём пакет для отправки
            DatagramPacket packet;
            try {
                packet = new DatagramPacket(
                        message.getBytes(), message.length(), InetAddress.getByName(remoteHost), remotePort);
            }
            catch (UnknownHostException e){
                System.out.println("Unknown host:" + e);
                continue;
            }

            //Отправляем пакет
            try {
                socket.send(packet);
            }
            catch (IOException e){
                System.out.println("Sending message error:" + e);
                continue;
            }

            byte [] receiveBuffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            String receivedMessage;
            //Ждём ответное сообщение
            try{
                socket.receive(receivedPacket);

                //Если получили ответ, то извлекаем оттуда время отправки и сохраняем RTT
                receivedMessage = new String(receivedPacket.getData());
                String [] splittedMessage = receivedMessage.split(" ");
                System.out.println("Message received: " + splittedMessage[0]);

                int index = Integer.parseInt(splittedMessage[1]);
                long sendTime = Long.parseLong(splittedMessage[2]);
                rtt[index] = new Date().getTime() - sendTime;
                replies[index] = true;
                System.out.println("RTT time: " + rtt[index]);
            }
            catch (SocketTimeoutException e){
                System.out.println("Request timed out.");
                continue;
            }
            catch (IOException e){
                System.out.println("Socket reading error:" + e);
                continue;
            }
        }

        //Высчитываем и выводим статистику
        int num_replies = 0;
        long rtt_summ = 0;
        long rtt_max = 0;
        long rtt_min = (long)1e9;
        for (int i = 0; i < QUERIES_NUM; i++){
            if (replies[i]){
                num_replies++;
                rtt_summ += rtt[i];
                rtt_max = Math.max(rtt_max, rtt[i]);
                rtt_min = Math.min(rtt_min, rtt[i]);
            }
        }
        System.out.println("Statistics:");
        if (num_replies > 0){
            System.out.println("Average RTT: " + (double)rtt_summ / num_replies + "ms");
            System.out.println("Minimum RTT: " + rtt_min + "ms");
            System.out.println("Maximum RTT: " + rtt_max + "ms");
        }
        System.out.println("Packet loss: " + Math.round((1 - (double)num_replies / QUERIES_NUM) * 100) + "%");
    }
}
