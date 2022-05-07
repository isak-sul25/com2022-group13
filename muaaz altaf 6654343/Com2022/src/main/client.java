package main;

//Java program to illustrate Client side
//Implementation using DatagramSocket
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.zip.CRC32;

public class client {
	public static void main(String args[]) throws IOException {
		byte[] receive = new byte[65535];
	   int seq = 0;
	   int ack = 0; 

		Scanner sc = new Scanner(System.in);

		// Step 1:Create the socket object for
		// carrying the data.
		DatagramSocket ds = new DatagramSocket();
		ds.setSoTimeout(1000);

		InetAddress ip = InetAddress.getLocalHost();
		byte buf[] = null;

		// loop while user not enters "bye"
		while (true) {
			String inp = sc.nextLine();

			// convert the String input into the byte array.
			buf = inp.getBytes();

			CRC32 crc = new CRC32();
			String forCheck = ","+ ack+":"+ seq +";\n" + inp;
			crc.update(forCheck.getBytes());

			Charset charset = Charset.availableCharsets().get("UTF-8");
			
			String str = crc.getValue() + forCheck;
			buf = str.getBytes(charset);

			DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 1400);

			ds.send(DpSend);
			

			int i = 0;
			DatagramPacket DpReceive = new DatagramPacket(receive, receive.length);
			boolean b = false;
			
			while (i<3) {
			try {

			ds.receive(DpReceive);
			} catch (Exception e) {
				if (i==2) {
					System.out.println("no response from server after several tries");
					b=true;
					break;
				}
				
				ds.send(DpSend);
				i++;
				continue;
			} 
			break;
			}
			
			if (b==true) {
				continue;
			}
			
			CRC32 crc2 = new CRC32();
			String message = data(receive).toString();
			long check = Long.parseLong(message.substring(0, message.indexOf(",")));
			// System.out.println(message);

			String text = message.substring(message.indexOf(","));
			crc2.update(text.getBytes(charset));
			System.out.println(message);
			seq = seq + 1;
			ack = Integer.parseInt(text.substring(text.indexOf(":")+1, text.indexOf(";")));
			

			long check2 = crc2.getValue();
			if (check != check2) {
				System.out.println("wrong checksum" + check + "other" + check2);
				continue;
			}
			
			receive = new byte[65535];

		}
	}

	public static StringBuilder data(byte[] a) {
		if (a == null)
			return null;
		StringBuilder ret = new StringBuilder();
		int i = 0;
		while (a[i] != 0) {
			ret.append((char) a[i]);
			i++;
		}
		return ret;

	}

}
