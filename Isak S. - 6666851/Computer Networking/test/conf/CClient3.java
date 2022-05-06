package conf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import main.Client;
import main.Message;
import main.Server;

public class CClient3 {
	public static void main(String[] args) {
		// 7
		try {

			DatagramSocket ds = new DatagramSocket();

			InetAddress ip = InetAddress.getLocalHost();
			String badCheck = "666666,0:0;extra\n" + "MENUS";

			byte[] bytes = badCheck.getBytes(Message.charset);

			// Sending a packet with a bad checksum.
			DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, ip, Server.port);
			ds.send(packetS);
			ds.close();
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
