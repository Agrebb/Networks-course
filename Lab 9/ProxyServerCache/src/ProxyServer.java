import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    final static String ImageExtensions [] = {"jpg", "png", "jpeg"};
    final static String CRLF = "\r\n";

    public static boolean isImage (String fileExtension){
        for (String extension : ImageExtensions) {
            if (extension.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    public static void sendImage(OutputStream stream, String imageExtension, BufferedImage Image){
        System.out.println("Sending image...");
        DataOutputStream dataStream = new DataOutputStream(stream);
        try {
            if (Image != null) {
                String status_ok = "HTTP/1.0 200 OK" + CRLF + "Proxy-agent: ProxyServer/1.0" + CRLF + CRLF;
                dataStream.writeBytes(status_ok);
                dataStream.flush();

                ImageIO.write(Image, imageExtension, stream);
                stream.flush();
            } else {
                System.out.println("Image wasn't received from server");
            }
        }
        catch (Exception e){
            System.out.println("Error sending image: " + e);
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

        DataOutputStream sendStream = null;
        try{
            sendStream = new DataOutputStream(clientSocket.getOutputStream());
        }
        catch (Exception e){
            System.out.println("Error creating output stream: " + e);
            return;
        }

        int hash = request.toString().hashCode();
        //Cache file name uses hash of request.
        String fileName = "cached/cached_" + hash + '.' + request.fileExtension;
        File cache = new File(fileName);

        //If this file exists, send it to client.
        if (cache.exists() && cache.isFile()){
            System.out.println("Cached file found: " + fileName);
            //If it is image then send as image.
            if (ProxyServer.isImage(request.fileExtension)){
                try {
                    BufferedImage cachedImage = ImageIO.read(cache);
                    sendImage(sendStream, request.fileExtension, cachedImage);
                    sendStream.flush();
                    clientSocket.close();
                }
                catch (IOException e){
                    System.out.println("Error reading cached image: " + e);
                }
            }
            //If it's not image send line by line.
            else{
                try {
                    BufferedReader cachedFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(cache)));

                    String line;
                    while ((line = cachedFileReader.readLine()) != null) {
                        sendStream.writeBytes(line);
                    }
                    sendStream.flush();
                    clientSocket.close();
                }
                catch (Exception e){
                    System.out.println("Error reading cached file: " + e);
                }
            }
        }
        //If file doesn't exists, create it, send request to server and save the response.
        else {
            System.out.println("Cached file not found, creating one");
            try {
                cache.createNewFile();
            }
            catch (IOException e){
                System.out.println("Error creating cache file");
                return;
            }

            try {
                serverSocket = new Socket(request.host, request.port);
                System.out.println("Sending request to server...");
                DataOutputStream serverStream = new DataOutputStream(serverSocket.getOutputStream());
                serverStream.writeBytes(request.toString());
                serverStream.flush();
            } catch (UnknownHostException e) {
                System.out.println("Unknown host " + request.host + " :" + e);
                return;
            } catch (IOException e) {
                System.out.println("Error sending request to server: " + e);
                return;
            }

            try {
                System.out.println("Reading response from server...");
                response = new HttpResponse(serverSocket.getInputStream(), request.fileExtension);
                if (response.createdSuccessfully) {
                    System.out.println("Response received");
                } else {
                    System.out.println("Error parsing response");
                    return;
                }
                if (!response.is_image){
                    sendStream.writeBytes(response.toString());
                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(cache));
                    fileWriter.write(response.toString());
                    fileWriter.flush();
                    fileWriter.close();
                }
                else{
                    sendImage(sendStream, request.fileExtension, response.Image);
                    ImageIO.write(response.Image, request.fileExtension, cache);
                }

                sendStream.flush();
                clientSocket.close();
                serverSocket.close();

                System.out.println("Response successfully sent to client");
            } catch (IOException e) {
                return;
            }
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