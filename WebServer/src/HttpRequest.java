import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HttpRequest implements Runnable {
    private final static String CRLF = "\r\n";
    private final static String NotFoundHTMLCode =
            "<HTML><HEAD><TITLE> Not Found </TITLE></HEAD><BODY> Not Found </BODY></HTML>";
    private Socket socket;

    public HttpRequest(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        InputStream is = socket.getInputStream();
        DataOutputStream os = new
                DataOutputStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String requestLine = br.readLine();
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        String fileName = "." + tokens.nextToken();

        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            contentTypeLine = "Content-Type: " + fileNameMap.getContentTypeFor(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = NotFoundHTMLCode;
        }
        os.writeBytes(statusLine + contentTypeLine + CRLF);
        if (fileExists) {
            fis.transferTo(os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }
        os.close();
        br.close();
        socket.close();
    }
}