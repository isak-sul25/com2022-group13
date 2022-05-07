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
			ack = Integer.parseInt(text.substring(text.indexOf(":")+1, text.indexOf(";")));
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
				
			}
			else if (content.contains("pakistan")|| content.contains("today")) {
				response = Pakistan.toString();
			}
			else if (content.contains("moroccan")) {
				response = Moroccan.toString();
			}

			else {
				response = "unknown command";
			}

			CRC32 crc2 = new CRC32();
			String forCheck = ","+ ack+":"+ seq +";\n" + response;
			crc2.update(forCheck.getBytes());
			String str = 666666 + forCheck;
			

			byte[] buf = str.getBytes(charset);

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
