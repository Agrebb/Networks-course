import java.net.*;

public final class WebServer {
    public static void main(String argv[]) throws
            Exception {
        //int port = (new Integer(argv[0])).intValue();
        int port = 8080;
        ServerSocket socket = new ServerSocket(port);

        while (true) {
            Socket connection = socket.accept();
            HttpRequest request = new HttpRequest(connection);
            request.run();
            //Thread thread = new Thread(request);
            //thread.start();
        }
    }
}