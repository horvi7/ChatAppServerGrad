package main.java.org.example;

public class Main {
    /**
     * Instantiating and calling the appropriate methods to make the server work.
     * Broadcaster thread is used to send server IP Address to the clients.
     * ChatAppServer thread is used for handling the incoming requests.
     */
    public static void main(String[] args){
        Broadcaster broadcaster = new Broadcaster();
        Thread broadcasterThread = new Thread(broadcaster);
        broadcasterThread.start();

        ChatAppServer server = new ChatAppServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
