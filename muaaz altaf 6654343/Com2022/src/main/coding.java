package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.zip.CRC32;

public class coding {
	public static void main(String[] args) throws IOException {

	DatagramSocket ds = new DatagramSocket();
	

	InetAddress ip = InetAddress.getLocalHost();
	

	// loop while user not enters "bye"
	
		String str = "12345,0:0;\n hello";

		// convert the String input into the byte array.
		
		Charset charset = Charset.availableCharsets().get("UTF-8");
		

		
		byte[] buf = str.getBytes(charset);

		DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 1400);
		ds.send(DpSend);
}
	}
