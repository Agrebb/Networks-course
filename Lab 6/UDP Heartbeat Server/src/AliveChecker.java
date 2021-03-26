import java.util.*;

public class AliveChecker implements Runnable {
    private Map<String, Long> lastMessage;
    private final static int delay = 100;
    private final int aliveTimeout;

    AliveChecker (Map <String, Long> lastMessage, int aliveTimeout){
        this.lastMessage = lastMessage;
        this.aliveTimeout = aliveTimeout;
    }

    public void run(){
        while (true){
            synchronized (lastMessage) {
                Set<String> clients = lastMessage.keySet();
                for (String client : clients) {
                    if (new Date().getTime() - lastMessage.get(client) > aliveTimeout) {
                        lastMessage.remove(client);
                        System.out.println("Client " + client + " seems disconnected.");
                    }
                }
            }
            try {
                Thread.sleep(delay);
            }
            catch (InterruptedException e){
                System.out.println(e);
                break;
            }
        }
    }
}
