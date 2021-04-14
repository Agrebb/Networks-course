import java.net.*;

public final class RPCServer {

    public static void main(String argv[]) throws Exception {
        int defaultPort = 8080;

        int port = defaultPort;
        if (argv.length > 0) port = Integer.parseInt(argv[0]);

        ServerSocket socket = new ServerSocket(port);

        while (true) {
            Socket connection = socket.accept();
            HttpRequest request = new HttpRequest(connection);
            Thread thread = new Thread(request);
            thread.start();
        }
    }
}