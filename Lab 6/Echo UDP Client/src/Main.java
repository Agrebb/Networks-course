public class Main {
    public static void main(String args[]) {
        if (args.length < 2){
            System.out.println("Two arguments required.");
            System.exit(-1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        UDP_Client Client = new UDP_Client(host, port);
        Client.run();
    }
}
