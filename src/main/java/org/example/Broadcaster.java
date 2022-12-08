package main.java.org.example;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

/**
 * This class is used  to broadcast the server's IP Address to everyone, on the same LAN, to the port 50001,
 * with the help of IPv4 Address and Subnet mask,
 * so the clients can contact the server later.
 */
public class Broadcaster implements Runnable{
    /**
     * Socket used broadcast the server's IP Address on the same LAN, on the port 50001.
     */
    private DatagramSocket broadcaster;

    /**
     *This thread responsible for sending the actual packets to the 50001 port.
     */
    @Override
    public void run(){
        try {
            broadcaster = new DatagramSocket();
            broadcaster.setBroadcast(true);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        /**
         * <code>ActionListener</code> triggered by a timer every 50 ms.
         * In every 50 ms the server IP Address is broadcasted in every 50 ms.
         * The broadcast Address is calculated from the IPv4 Address and the subnet mask.
         */
        ActionListener forTimer = new ActionListener() {
            /**
             * Actually sending the broadcast packet after a timer triggers this <code>ActionEvent.</code>
             * @param e Event triggered by a <code>Timer.</code>
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    /**
                     * Getting the IPv4 Address of this (server) computer.
                     */
                    byte[] serverIP = InetAddress.getLocalHost().getAddress();
                    broadcaster.setBroadcast(true);
                    InetAddress broadcastAddress = InetAddress.getByName("192.168.0.255");
                    /**
                     * Packet created and sent with the server IP Address.
                     */
                    DatagramPacket packet = new DatagramPacket(serverIP,serverIP.length, broadcastAddress, 50001);
                    broadcaster.send(packet);

                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                } catch (SocketException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        /**
         * Timer, that ticks in every 50 ms, triggering <code>actionPerformed</code>
         */
        Timer timer = new Timer(50, forTimer);
        timer.start();
    }
}
