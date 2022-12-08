package main.java.org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the server's broadcast function, which should send the IP Address of the server to the clients periodically.
 */
class BroadcasterTest {
    /**
     * Representing the socket of the receiver side (socket of the client)
     */
    private DatagramSocket receiverSocket;

    /**
     * Instantiating the Broadcaster class and starting this Thread, before each test.
     * Setting the client side socket's port to 50001, where the broadcast messages should be sent.
     * @throws SocketException
     */
    @BeforeEach
    public void init() throws SocketException {
        Broadcaster broadcaster = new Broadcaster();
        Thread broadcasterThread = new Thread(broadcaster);
        broadcasterThread.start();
        receiverSocket = new DatagramSocket(50001);
    }

    /**
     * This Test checks if server's ("localhost"'s)IP Address matches with the IP Address that is being broadcasted
     * @throws IOException
     */
    @Test
    void broadcasterValidIPTest() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[32], 32);
        receiverSocket.receive(packet);
        InetAddress myAddress = InetAddress.getLocalHost();
        InetAddress receivedAddress = packet.getAddress();

        assertEquals(myAddress, receivedAddress);
    }

    /**
     * It is important that the client receives the IP Address of the server, fast when starting the client application
     * so that the client does not have to wait long.
     * So this test checks if the server send and the client receives such a package in 100 ms
     * @throws IOException
     */
    @Test
    void broadcasterFastStartTest() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[32], 32);
        receiverSocket.setSoTimeout(100);
        try {
            receiverSocket.receive(packet);
        }catch (SocketTimeoutException e){
            assert false;
        }
        assertTrue(true);
    }
}