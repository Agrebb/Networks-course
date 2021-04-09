import java.io.*;

public class HttpResponse {
    final static String CRLF = "\r\n";

    private String statusLine = "", headers = "";
    private int contentLength = -1;

    final int BUF_SIZE = 1000;
    final int MAX_SIZE = 200000;
    private char [] body = new char[MAX_SIZE];

    public boolean createdSuccessfully = false;

    public HttpResponse(BufferedReader source){
        try {
            String line = source.readLine();
            boolean firstLine = true;
            while (!line.isEmpty()){
                if (!firstLine){
                    headers += line + CRLF;
                }
                else{
                    firstLine = false;
                    statusLine = line;
                }
                if (line.startsWith("Content-Length:") || line.startsWith("Content-length")){
                    String [] args = line.split(" ");
                    contentLength = Integer.parseInt(args[1]);
                }
                line = source.readLine();
            }
            System.out.println("Headers received.");
        }
        catch (Exception e){
            System.out.println("Error reading headers: " + e);
            return;
        }

        try{
            int status = Integer.parseInt(statusLine.split(" ") [1]);
            System.out.println("Status: " +  status);

            if (status < 400) { //if status - not failed, then reading body.
                if (contentLength == -1) {
                    char[] buffer = new char[BUF_SIZE];

                    int shift = 0;
                    while (shift < MAX_SIZE) {
                        int answer = source.read(buffer, 0, BUF_SIZE);
                        if (answer == -1) break;
                        for (int i = 0; i < answer && shift < MAX_SIZE; i++) {
                            body[shift] = buffer[i];
                            shift++;
                        }
                    }
                    contentLength = shift;
                } else {
                    if (contentLength > MAX_SIZE) {
                        System.out.println("Response body is too large");
                        return;
                    }
                    source.read(body, 0, contentLength);
                }
                System.out.println("Body received, content length: " + contentLength);
            }
            else{  // if status - failed, returning sample web page
                String failed = "<html><body>Status " + status + "</body></html>";
                for (int i = 0; i < failed.length(); i++) body[i] = failed.charAt(i);
            }
        }
        catch (Exception e){
            System.out.println("Error reading response body: " + e);
            return;
        }
        createdSuccessfully = true;
    }

    public String toString() {
        StringBuilder result = new StringBuilder(statusLine + CRLF + headers + CRLF);
        for (int i = 0; i < contentLength; i++) result.append(body[i]);
        return result.toString();
    }
}
