import java.io.*;
import java.net.*;

public class ProxyServer {
    private static ServerSocket socket;

    public static void createSocket(int port){
        try {
            socket = new ServerSocket(port);
        }
        catch (Exception e){
            System.out.println("Can't create socket " + e);
            System.exit(-1);
        }
    }

    public static void handle (Socket clientSocket){
        Socket serverSocket;
        HttpRequest request;
        HttpResponse response;

        try{
            InputStreamReader clientStream = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader clientReader = new BufferedReader(clientStream);
            System.out.println("Reading request from client...");
            request = new HttpRequest(clientReader);
            if (request.createdSuccessfully){
                System.out.println("Request received.");
            }
            else{
                System.out.println("Error parsing request.");
                return;
            }
        }
        catch (Exception e){
            System.out.println("Error reading request from client: " + e);
            return;
        }

        try{
            serverSocket = new Socket(request.host, request.port);
            DataOutputStream sendStream = new DataOutputStream(serverSocket.getOutputStream());
            System.out.println("Sending request to server...");
            sendStream.writeBytes(request.toString());
        }
        catch (UnknownHostException e){
            System.out.println("Unknown host " + request.host + " :" + e);
            return;
        }
        catch (IOException e){
            System.out.println("Error sending request to server: " + e);
            return;
        }

        try{
            DataInputStream serverStream = new DataInputStream(serverSocket.getInputStream());
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(serverStream));
            System.out.println("Reading response from server...");
            response = new HttpResponse(serverReader);
            if (response.createdSuccessfully){
                System.out.println("Response received");
            }
            else{
                System.out.println("Error parsing response");
                return;
            }
            DataOutputStream sendStream = new DataOutputStream(clientSocket.getOutputStream());
            sendStream.writeBytes(response.toString());

            clientSocket.close();
            serverSocket.close();

            System.out.println("Response successfully sent to client");
        }
        catch(IOException e){
            return;
        }
    }

    public static void main (String [] args){
        int port = 8080;
        try{
            port = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e){
            System.out.println("Not an integer, using default port.");
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("No port specified, using default port.");
        }

        createSocket(port);

        while (true){
            System.out.println();
            try{
                Socket client = socket.accept();
                handle(client);
            }
            catch (Exception e){
                System.out.println("Error reading request from client: " + e);
                continue;
            }
        }
    }
}