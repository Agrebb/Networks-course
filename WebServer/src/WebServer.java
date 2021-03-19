import java.net.*;
import java.util.concurrent.Semaphore;

public final class WebServer {

    public static void main(String argv[]) throws Exception {
        int defaultPort = 8080;
        int defaultConcurrencyLevel = 2;

        int port = defaultPort, concurrencyLevel = defaultConcurrencyLevel;
        if (argv.length > 0) port = Integer.parseInt(argv[0]);
        if (argv.length > 1) concurrencyLevel = Integer.parseInt(argv[1]);

        ServerSocket socket = new ServerSocket(port);
        Semaphore semaphore = new Semaphore(concurrencyLevel, true);

        while (true) {
            Socket connection = socket.accept();
            HttpRequest request = new HttpRequest(connection, semaphore);
            Thread thread = new Thread(request);
            thread.start();
        }
    }
}