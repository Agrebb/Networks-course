import java.io.*; import java.net.*;
public class EmailSender
{
    public static void main(String[] args) throws Exception
    {
        // Establishing TCP connection.
        Socket socket = new Socket("ASPMX.L.GOOGLE.COM", 25);
        InputStream is = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String response = reader.readLine();
        if (!response.startsWith("220")) {
            System.out.println(response);
            throw new Exception("код ответа 220 не получен от сервера.");
        }
        OutputStream os = socket.getOutputStream();

        // Sending HELO command
        String command = "HELO x\r\n";
        os.write(command.getBytes("US-ASCII"));
        response = reader.readLine();
        if (!response.startsWith("250")) {
            System.out.println(response);
            throw new Exception("код ответа 250 не получен от сервера.");
        }
        // Setting sender address.
        command = "MAIL FROM: <grebenn0109@gmail.com>\r\n";
        os.write(command.getBytes("US-ASCII"));
        response = reader.readLine();
        if (!response.startsWith("250")) {
            System.out.println(response);
            throw new Exception("код ответа 250 не получен от сервера.");
        }
        // Setting receiver address.
        command = "RCPT TO: <grebenn0109@gmail.com>\r\n";
        os.write(command.getBytes("US-ASCII"));
        response = reader.readLine();
        if (!response.startsWith("250")) {
            System.out.println(response);
            throw new Exception("код ответа 250 не  получен от сервера.");
        }
        // Putting data into message.
        command = "DATA\r\n";
        os.write(command.getBytes("US-ASCII"));
        response = reader.readLine();
        if (!response.startsWith("354")) {
            System.out.println(response);
            throw new Exception("код ответа 354 не получен от сервера.");
        }
        os.write("SUBJECT: test\r\n\r\n".getBytes("US-ASCII"));
        os.write("test data\r\n".getBytes("US-ASCII"));
        os.write(".\r\n".getBytes("US-ASCII"));
        response = reader.readLine();
        if (!response.startsWith("250")) {
            System.out.println(response);
            throw new Exception("код ответа 250 не получен от сервера.");
        }
        // Closing connection.
        command = "QUIT\r\n";
        os.write(command.getBytes("US-ASCII"));
        os.close();
    }
}