package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.zip.CRC32;

public class server {

	public static void main(String[] args) throws IOException {
		Menu American = new Menu("American", "Mini Burgers", "Buffalo Chicken", "Apple Pie", "Soda");
		Menu Mediterranean = new Menu("Mediterranean", "Baba Ganoush", "Spanakopita", "Baklava", "Iced Tea");
		Menu Pakistan = new Menu("Pakistan", "Mince kebab", "Chicken Karahi", "Kheer", "Pink Tea");
		Menu Moroccan = new Menu("Moroccan", "Green Hareesa Olives", "Chicken Tagine", "Seasonal Fruit Platter",
				"Mint Tea");
		int seq = 0;
		int ack = 0;

		DatagramSocket ds = new DatagramSocket(1400);
		byte[] receive = new byte[65535];
		
		byte[] by = new byte[576];
		DatagramPacket hands = new DatagramPacket(by, by.length);
		
		ds.receive(hands);
		
		
		String handsh = "1320470661,\n" + 
				"100:OK";
		by = handsh.getBytes();
		DatagramPacket hands2 = new DatagramPacket(by, by.length, hands.getSocketAddress());
		ds.send(hands2);
		
		ds.receive(hands);
		
		
		DatagramPacket DpReceive = null;
		while (true) {

			// Step 2 : create a DatgramPacket to receive the data.
			DpReceive = new DatagramPacket(receive, receive.length);

			// Step 3 : revieve the data in byte buffer.
			ds.receive(DpReceive);
			// System.out.println(new String(receive));

			CRC32 crc = new CRC32();
			Charset charset = Charset.availableCharsets().get("UTF-8");
			String message = data(receive).toString();
			long check = Long.parseLong(message.substring(0, message.indexOf(",")));

			String text = message.substring(message.indexOf(","));

			if (text.indexOf(":") == -1) {
				ack = Integer.parseInt(text.substring(text.indexOf(",") + 1, text.indexOf(";")));
			} else {
				ack = Integer.parseInt(text.substring(text.indexOf(":") + 1, text.indexOf(";")));
			}
			crc.update(text.getBytes(charset));
			System.out.print(crc.getValue());
			System.out.println(text);

			long check2 = crc.getValue();
			if (check != check2) {
				System.out.println("wrong checksum" + check + "other" + check2);
				continue;
			}

			String content = data(receive).toString().toLowerCase();
			String response = "";

			if (content.contains("menus")) {
				response = "American, Mediterranean, Pakistan, Moroccan";

				System.out.println(response);
			} else if (content.contains("american")) {
				response = American.toString();
			} else if (content.contains("mediterranean")) {
				response = Mediterranean.toString();

			} else if (content.contains("pakistan") || content.contains("today")) {
				response = Pakistan.toString();
			} else if (content.contains("moroccan")) {
				response = Moroccan.toString();
			} else if (content.contains("bigtext")) {
				response = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.";
			}

			else {
				response = "unknown command";
			}

			CRC32 crc2 = new CRC32();
			String forCheck = "," + ack + ":" + seq + ";\n" + response;
			crc2.update(forCheck.getBytes());
			
			String str =  crc2.getValue() + forCheck;
			
			
			byte[] buf = str.getBytes(charset);
			
			if (buf.length > 754) {
				System.out.println("Input is too large");
				continue;
			}

			DatagramPacket Dpresponse = new DatagramPacket(buf, buf.length, DpReceive.getSocketAddress());
			ds.send(Dpresponse);
			seq = seq + 1;

			// Clear the buffer after every message.
			receive = new byte[65535];
		}
	}

// A utility method to convert the byte array
// data into a string representation.
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
