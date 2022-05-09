package conf;

import java.io.IOException;

import main.Client;

public class CClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client c = new Client();
		try {
			// sends and receives a message
			c.test("");
			
			// 10& 1 & 17
			c.test("MENUS");
			
			//11
			c.test("MENU TODAY");
			
			//12
			c.test("MENU AMERICAN");
			
			//c.run();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
