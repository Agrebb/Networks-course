import java.io.*;

public class HttpRequest {
    final static int defaultPort = 80;
    final static String CRLF = "\r\n";

    private String method = "", URI = "", version = "", headers = "";
    public String host;
    public int port;

    public boolean createdSuccessfully = false;

    public HttpRequest(BufferedReader source){
        try{
            String header = source.readLine();
            String [] args = header.split(" ");
            method = args[0];
            URI = args[1];
            version = args[2];

            args = URI.split("/");
            URI = "";
            for (int i = 2; i < args.length; i++) URI += "/" + args[i];
            if (URI.isEmpty()) URI = "/";

            args = args[1].split(":");
            host = args[0];
            if (args.length > 1) port = Integer.parseInt(args[1]);
            else port = defaultPort;

            System.out.println("Host: " + host);
            System.out.println("Port: " + port);
        }
        catch (Exception e){
            System.out.println("Error parsing header: " + e);
            return;
        }

        if (method.equals("GET")){
            try {
                String line = source.readLine();
                while (!line.isEmpty()) {
                    if (!line.startsWith("Host:")) headers += line + CRLF;
                    line = source.readLine();
                }
            }
            catch (Exception e){
                System.out.println("Error reading message from socket: " + e);
                return;
            }
        }
        else{
            System.out.println("Only GET method is supported");
            return;
        }
        createdSuccessfully = true;
    }

    public String toString(){
        String request = method + " " + URI + " " + version + CRLF;
        request += headers;
        request += "Host: " + host + ":" + port + CRLF;
        request += "Connection: close" + CRLF + CRLF;
        return request;
    }
}
