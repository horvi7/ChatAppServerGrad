package main.java.org.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.*;


/**
 * This test class's test cases check if the files, behind the server are read and modified properly.
 */
class FileHandlerTest {

    /**
     * <code>FileHandler</code> instance to check the methods of this class.
     */
    private FileHandler fileHandler;
    /**
     * Representing the client side sockets in these tests.
     */
    private DatagramSocket socket;
    /**
     * Instantiating the required classe.
     */
    @BeforeEach
    public void init() {

        fileHandler = new FileHandler();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Checks if the file is created, if we want to add a new username to the list and the file does not exist yet.
     */
    @Test
    void addContactTest() {
        fileHandler.addContact("Alma");
        assertTrue(fileHandler.getContactFile().exists());
    }

    /**
     * Checks if the given String is added to the file.
     * Used, when logging in and saving new usernames to file.
     */
    @Test
    void addContactAndReadTest() {
        fileHandler.addContact("Alma");

        try {
            String line, prev = "";
            BufferedReader reader = new BufferedReader(new FileReader(fileHandler.getContactFile()));
            while ((line = reader.readLine()) != null) {
                prev = line;
            }
            assertEquals("Alma", prev);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <code>checkIfRegistered</code> should check if the given user is already among the registered users.
     * In this test a valid registration is simulated and checked if it appears among the registered users.
     * @throws IOException
     */
    @Test
    void checkIfRegisteredWithValidNameTest() throws IOException {
        String validName = "Alma";
        fileHandler.addContact(validName);
        boolean b = fileHandler.checkIfRegistered(validName);
        assertTrue(b);

    }

    /**
     * If input is left empty or a null value is added to the contact list, the function should return false
     * This functionality is checked here.
     * @throws IOException
     */
    @Test
    void checkIfRegisteredWithInvalidNameTest() throws IOException {
        String invalidName = null;
        fileHandler.addContact(invalidName);
        boolean b = fileHandler.checkIfRegistered(invalidName);
        assertFalse(b);

    }
    /**
     * It's checked if the <code>fileHandler.getContactsFromFile()</code> appropriately reads the file and reuturns the appropriate String value.
     */
    @Test
    void getContactsFromFileTest() throws IOException {
        String contactsFromFile = fileHandler.getContactsFromFile();
        String contactsReadInTest = "";
        String s;
        BufferedReader reader = new BufferedReader(new FileReader(fileHandler.getContactFile()));
        while ((s = reader.readLine()) != null) {
            contactsReadInTest += s + ",";
        }
        assertEquals(contactsReadInTest, contactsFromFile);
    }

    /**
     * A message is tried to be saved with valid elements to the file, which stores the messages.
     * The test checks if it really appears in the file in the appropriate format.
     * @throws IOException
     */
    @Test
    void saveValidMessageToFileTest() throws IOException {
        fileHandler.saveMessageToFile("alma", "korte", "szia");
        File messageFile = new File("messages.txt");
        BufferedReader reader = new BufferedReader(new FileReader(messageFile));
        String line, prev;
        boolean b = false;
        while ((line = reader.readLine()) != null) {
            prev = line;
            if (prev.equals("alma,korte,szia")) b = true;
        }
        assertTrue(b);
    }

    /**
     * If the user tries to send a message without typing anything in, the program should let this message to be saved
     * into the file with the messages, since such an empty line should not appear in a chat application.
     * @throws IOException
     */
    @Test
    void saveInvalidMessageToFileTest() throws IOException {
        fileHandler.saveMessageToFile("korte", "alma", "");
        File messageFile = new File("messages.txt");
        BufferedReader reader = new BufferedReader(new FileReader(messageFile));
        String line, prev;
        boolean b = false;
        while ((line = reader.readLine()) != null) {
            prev = line;
            if (prev.equals("korte,alma,")) b = true;
        }
        assertFalse(b);
    }

    /**
     * The test checks if the messages on the client side are the same, as the messages that are directly read from the file.
     * @throws IOException
     */
    @Test
    void sendMessagesToClientTest() throws IOException {
        DatagramSocket receiverSocket = new DatagramSocket(50000);
        /**
         * "alma" sending a message to "korte"
         */
        fileHandler.sendMessagesToClient(InetAddress.getLocalHost(), 50000, socket, "alma", "korte");
        DatagramPacket packet = new DatagramPacket(new byte[20], 20);
        receiverSocket.setSoTimeout(1000);
        String receivedInSocket = "";
        while (true) {
            try {
                /**
                 * The socket, which represent the client side is receiving the messages sent from the server
                 */
                receiverSocket.receive(packet);
                String line = new String(packet.getData());
                /**
                 * After parsing these received packets, the unused buffer of the byte[] should be modified not to affect the test
                 */
                line.replace("\0", "");
                receivedInSocket += line;
            } catch (SocketTimeoutException e) {
                break;
            }
        }
        BufferedReader reader = new BufferedReader(new FileReader("messages.txt"));
        String readInTest = "", lineInFile;
        while ((lineInFile = reader.readLine()) != null) {
            String[] messageElements = lineInFile.split(",");
            if ((messageElements[0].equals("alma") && messageElements[1].equals("korte")) || (messageElements[1].equals("alma") && messageElements[0].equals("korte"))) {
                readInTest += lineInFile;
            }
        }
        assertEquals(readInTest, receivedInSocket);
    }
}