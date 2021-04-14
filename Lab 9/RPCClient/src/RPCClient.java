import java.io.*;
import java.net.*;

public class RPCClient {
    public static void main(String args[]){
        String host = "localhost";
        int port = 8080;
        String queryType = "Run";
        String launchQuery = "C:\\Windows\\System32\\calc.exe";
        String runQuery = "mkdir test";
        String CRLF = "\r\n";

        if (args.length > 0) host = args[0];
        if (args.length > 1) port = Integer.parseInt(args[1]);
        if (args.length > 2) queryType = args[2];
        if (args.length > 3){
            if (queryType.equals("Launch")) launchQuery = args[3];
            else if (queryType.equals("Run")) runQuery = args[3];
        }

        try {
            Socket serverSocket = new Socket(host, port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
            if (queryType.equals("Launch")) {
                writer.write("Launch: " + launchQuery + CRLF);
            }
            else{
                writer.write("Run command: " + runQuery + CRLF);
            }
            writer.flush();
            writer.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
