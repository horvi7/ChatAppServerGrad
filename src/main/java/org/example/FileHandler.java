package main.java.org.example;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This class is used to handle the incoming data from sockets and saving it to files.
 * Manages the file, where the contacts and messages are stored.
 * Saves the incoming data to files and responses to data queries.
 */
public class FileHandler {
    /**
     * <code>File</code>, which stores the registered users.
     */
    private File contactFile = new File("contacts.txt");
    /**
     * <code>File</code>, which stores the sent messages.
     */
    private File messageFile = new File("messages.txt");

    /**
     * Saves the <code>contact</code> to the <code>contactFile</code>.
     * This method is only called, when a new username is given on the login panel.
     * @param contact a newly given username
     */
    public void addContact(String contact) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(contactFile, true))) {
            bw.write(contact+'\n');
        } catch (IOException e) {
            System.out.println("Database corrupted");
        }
    }

    /**
     *This method is called after the login panel.
     * Checks if a username like the input already exists.
     * @param username checked if already part of the file with the name of the users
     * @return true, if <code>username</code> already exists among the usernames
     * @throws IOException happens if the file with the username is corrupted
     */
    public boolean checkIfRegistered(String username) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(contactFile))) {
            String s;
            while((s = reader.readLine()) != null){
                if(s.equals(username)) return true;
            }
            return false;
        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     *Returns all the usernames stored on the server separated by commas.
     * @return <code>String</code> of usernames separated by commas.
     * @throws IOException happens if the file with the username is corrupted
     */
    public String getContactsFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(contactFile));
        String s, msg="";
        /**
         * All the contacts are concatenated into a single <code>String</code> seperated by commas.
         */
        while((s = reader.readLine()) != null){
            msg+=(s+",");
        }
        return msg;

    }

    /**
     * If a message is sent from a client to another client, the message will be saved on the server to make it accessible later too.
     * @param from sender of the message
     * @param to recipient of the message
     * @param message the sent message from client to client, meanwhile saved to server
     */
    public void saveMessageToFile(String from, String to, String message) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(messageFile, true));
            if(!message.equals("")) {
                writer.newLine();
                writer.write(from + "," + to + "," + message);
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * If a client wants to see his/her messages in the application, the server must serve this client with the appropriate messages,
     * which were exchanged between these two clients.
     * These messages are sent with the use of socket.
     * @param ip IPv4 Address of the client, who asked for these messages
     * @param port Port of the client's socket to which the messages must be sent
     * @param socket <code>DatagramSocket</code> of the server, used to send messages to the client, through the network
     * @param from Sender of the messages, that the client asked for (the client itself)
     * @param to Recipient of the messages, that client asked for
     */
    public void sendMessagesToClient(InetAddress ip, int port, DatagramSocket socket, String from, String to){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(messageFile));
            String s="";
            while((s = reader.readLine())!=null){
                String[] messageElements = s.split(",");
                /**
                 * If either the sender-recipient or recipient-sender match the parameters <code>from</code> and <code>to</code>,
                 * this lin has to be sent back to the client.
                 */
                if((messageElements[0].equals(from) && messageElements[1].equals(to)) || (messageElements[1].equals(from) && messageElements[0].equals(to))) {
                    DatagramPacket msg = new DatagramPacket(s.getBytes(), s.getBytes().length, ip, port);
                    socket.send(msg);
                    System.out.println(s);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Returns a file with the registered users.
     * @return File, which includes the saved users on server.
     */
    public File getContactFile() {
        return contactFile;
    }

}
