package main;

import java.io.IOException;

public class RunClient {

	public static void main(String[] args) throws IOException {
		Client client = new Client();
		
		//client.run();
		
		try {
			client.handshake();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//client.run();
	}

}
