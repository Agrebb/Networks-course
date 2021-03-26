import java.net.*;

public final class UDP_Server {
    public static void main(String argv[]) throws Exception {
        int defaultPort = 8080;

        //Получаем номер порта из командной строки
        int port = defaultPort;
        if (argv.length > 0) port = Integer.parseInt(argv[0]);

        //Создаём сокет и буферы для хранения данных
        DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receiveBuffer = new byte[1024];
        byte[] sendBuffer = new byte[1024];

        while (true) {
            try {
                //Получаем сообщение от клиента
                DatagramPacket inputPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(inputPacket);
                String receivedData = new String(inputPacket.getData());

                //Заменяем символы сообщения на заглавные и копируем его в sendBuffer
                sendBuffer = receivedData.toUpperCase().getBytes();

                //Имитирует 20% потерю пакетов
                if (Math.random() < 0.2)
                    continue;

                //Создаём UDP-пакет с преобразованными данными
                DatagramPacket outputPacket = new DatagramPacket(
                        sendBuffer, sendBuffer.length,
                        inputPacket.getAddress(), inputPacket.getPort()
                );

                //Отправляем пакет клиенту
                serverSocket.send(outputPacket);
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
