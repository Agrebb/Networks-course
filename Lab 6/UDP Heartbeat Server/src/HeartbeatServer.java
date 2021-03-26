import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class HeartbeatServer {
    public static void main(String args[]) throws Exception {
        if (args.length < 2){
            System.out.println("Two arguments required.");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        int timeout = Integer.parseInt(args[1]);

        DatagramSocket serverSocket = new DatagramSocket(port);

        Map<String, Long> lastMessage = new HashMap<>(){};
        AliveChecker aliveChecker = new AliveChecker(lastMessage, timeout);
        Thread threadChecker = new Thread(aliveChecker);
        HeartbeatListener heartbeatListener = new HeartbeatListener(lastMessage, serverSocket);
        Thread listener = new Thread(heartbeatListener);

        listener.start();
        threadChecker.start();
    }
}
