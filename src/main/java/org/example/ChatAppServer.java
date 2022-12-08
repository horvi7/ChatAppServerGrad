package main.java.org.example;
import java.io.IOException;
import java.net.*;

/**
 * Managing the incoming data, coming from the sockets.
 * Giving the appropriate service based on the incoming query.
 */
public class ChatAppServer implements Runnable {
    /**
     * Socket, used for communication with the clients.
     */
    private DatagramSocket socket;
    /**
     * Instance of the <code>FileHandler</code> class to manage the data behind the server.
     */
    private FileHandler fileHandler;

    /**
     * Thread managing and serving, responding to the incoming queries.
     */
    @Override
    public void run() {
        fileHandler = new FileHandler();
        /**
         * Server is always "listening" on port 50000
         */
        try {
            socket = new DatagramSocket(50000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                /**
                 * Getting the IPv4 Address and use Port of the client (sender of query).
                 * Splitting and interpreting the query of the client.
                 */
                DatagramPacket packet = new DatagramPacket(new byte[10000], 10000);
                socket.receive(packet);
                int receivedFromPort = packet.getPort();
                InetAddress receivedFromIP = packet.getAddress();
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);

                String messageElements[] = message.split(",");

                /**
                 * If a login message arrives, it means thath the client wants to log in.
                 * In this case it must be checked if the username is already in use or not.
                 * If registered, the registered users (contacts) must be sent back to the client
                 * Else the new user has to be saved to the registered users abd the registered users (contacts) must be sent back to the client.
                 */
                if (messageElements[0].equals("login")) {
                    String username = messageElements[1];
                    if (fileHandler.checkIfRegistered(username)) {
                        //szerver visszaküldi az összes felhasználót
                        String contacts = fileHandler.getContactsFromFile();
                        sendContactsToClient(packet, contacts, receivedFromIP, receivedFromPort);
                    } else {
                        fileHandler.addContact(messageElements[1]); //hozzáadja a nevet a felhasználólistához
                        String contacts = fileHandler.getContactsFromFile(); //és visszaküldi az összes felhasználót
                        sendContactsToClient(packet, contacts, receivedFromIP, receivedFromPort);
                    }
                    /**
                     * if the server receives a message starting with "msg", it means that the client
                     * sent a message to another client, so the message should be saved to the server.
                     */
                } else if (messageElements[0].equals("msg")) {
                    fileHandler.saveMessageToFile(messageElements[1], messageElements[2], messageElements[3]);
                    System.out.println(messageElements[3]);
                    /**
                     * if the server receives a message starting with "get", it means that the client
                     * is asking for messages, which were previously exchanged between the clients.
                     */
                } else if (messageElements[0].equals("get")) {
                    String from = messageElements[1];
                    String to = messageElements[2];

                    fileHandler.sendMessagesToClient(receivedFromIP, receivedFromPort, socket, from, to);

                }

            } catch (IOException e) {
                //kiegeszites majd

            }
        }
    }

    /**
     * Sending a String of users, separated with commas to the client, who asked for these information.
     * Sending these information through the server's socket.
     * @param packet <code>DatagramPacket</code> what should be sent through the socket to the client
     * @param contacts <code>String</code> of th users separated by commas
     * @param receivedFromIP IPv4 Address of the client, where these information should be sent
     * @param receivedFromPort Port of the client, where these information should be sent
     * @throws IOException
     */
    private void sendContactsToClient(DatagramPacket packet, String contacts, InetAddress receivedFromIP, int receivedFromPort) throws IOException {
        packet = new DatagramPacket(contacts.getBytes(), contacts.getBytes().length, receivedFromIP, receivedFromPort);
        socket.send(packet);
    }


}

