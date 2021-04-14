import java.awt.*;
import java.io.*;
import java.net.*;

public class HttpRequest implements Runnable {
    private final Socket socket;
    public HttpRequest(Socket socket){
        this.socket = socket;
    }

    public void run(){
        String query = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            query = reader.readLine();
        }
        catch (IOException e){
            System.out.println("Error reading query: " + e);
            return;
        }
        System.out.println(query);
        if (query.startsWith("Launch: ")){
            String path = query.substring("Launch: ".length());
            try {
                Desktop.getDesktop().open(new File(path));
            }
            catch (Exception e){
                System.out.println("Error launching file: " + e);
            }
        }
        else if (query.startsWith("Run command: ")){
            String command = query.substring("Run command: ".length());
            try {
                Runtime.getRuntime().exec("cmd.exe /c start " + command);
            }
            catch (Exception e){
                System.out.println("Error running command file: " + e);
            }
        }
        else{
            System.out.println("Incorrect query");
        }
    }
}
