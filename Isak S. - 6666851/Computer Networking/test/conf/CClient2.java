package conf;

import java.io.IOException;
import java.util.zip.CRC32;

import main.Client;
import main.Message;

public class CClient2 {

	public static void main(String[] args) {
		//3,4,5,6,11,13,14
		Client c = new Client();
		try {
			CRC32 crc = new CRC32();
			Message send = new Message("MENUS",0,0);
			send.setExtra("extra");
			String mes = "," + send.getAckSeq() + send.getExtra() + "\n" + send.getContent();
			crc.update(mes.getBytes(Message.charset));
			System.out.println("\nSending the 'MENUS' request\nCorrect sent checksum:" + crc.getValue()+ "\n");
			
			crc = new CRC32();
			Message received = c.sendAndReceive("MENUS", "extra");
			
			System.out.println(received.toString());
			
			mes = "," + received.getAckSeq() + received.getExtra()+  "\n" + received.getContent();
			crc.update(mes.getBytes(Message.charset));
			
			System.out.println("\nCorrect receive checksum:" + crc.getValue());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
