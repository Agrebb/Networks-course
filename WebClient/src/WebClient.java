import java.io.*;
import java.net.*;

public class WebClient {

    public static void main(String argv[]) throws Exception {
        String defaultHost = "http://localhost";
        String defaultPort = "8080";
        String defaultFile = "test.txt";
        String CRLF = "\r\n";

        String host = defaultHost, port = defaultPort, file = defaultFile;
        if (argv.length > 0) host = argv[0];
        if (argv.length > 1) port = argv[1];
        if (argv.length > 2) file = argv[2];

        String path = host + ":" + port + "/" + file;

        try {
            HttpURLConnection connection;
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = inputReader.readLine()) != null) {
                response.append(inputLine);
                response.append(CRLF);
            }
            inputReader.close();

            System.out.println(response.toString());
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
