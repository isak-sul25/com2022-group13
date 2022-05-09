package main;

import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.CRC32;

public class RunServer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Menu Monday = new Menu("Monday", "Chinese", "Sliced Tofu Salad", "Kung Pao Chicken", "Fried Bananas",
				"Bubble Tea");
		Menu Tuesday = new Menu("Tuesday", "Indian", "Vegetable Samosas", "Butter Chicken", "Modak", "Masala Chai");
		Menu Wednesday = new Menu("Wednesday", "Middle Eastern", "Fattoush Salad", "Pomegranate Chicken", "Halva",
				"Limonana");
		Menu Thursday = new Menu("Thursday", "Mediterranean", "Baba Ganoush", "Spanakopita", "Baklava", "Iced Tea");
		Menu Friday = new Menu("Friday", "American", "Mini Burgers", "Buffalo Chicken", "Apple Pie", "Soda");

		Menu[] menus = { Monday, Tuesday, Wednesday, Thursday, Friday };
		ArrayList<Menu> menuList = new ArrayList<>(Arrays.asList(menus));
//
//		HashMap<String, String> DayToTheme = new HashMap<String, String>();
//		DayToTheme.put("Monday", Monday.getTheme());
//		DayToTheme.put("Tuesday", Tuesday.getTheme());
//		DayToTheme.put("Wednesday", Wednesday.getTheme());
//		DayToTheme.put("Thursday", Thursday.getTheme());
//		DayToTheme.put("Friday", Friday.getTheme());
//
//		HashMap<String, Menu> ThemeToMenu = new HashMap<String, Menu>();
//
//		for (Menu menu : menus) {
//			ThemeToMenu.put(menu.getTheme(), menu);
//		}

//		String test = "10000, 3";
//		Charset U = Charset.availableCharsets().get("UTF-8");
//		
//		String message = "1863792733,0:0;\n" + 
//				"Hello World!";
//		int end = message.indexOf(",");
////
//		String input = "\nHello World!";
//		CRC32 crc = new CRC32();
//		crc.update(input.getBytes(U));
//		System.out.println("input:" + input);
//		System.out.println("CRC32:" + crc.getValue());
//		
//		
//		
//		CRC32 CRCTest = new CRC32();
//		CRCTest.update(message.substring(end).getBytes(U));
//		long checksumTest = CRCTest.getValue();
//		
//		System.out.println(checksumTest);
////
//		System.out.println();
//
//		for (byte b : input.getBytes(U)) {
//			System.out.println(b);
//		}
//		
//		System.out.println(input.getBytes(U).length);

		// System.out.println(Monday.getMenu());
		// System.out.println(test.getBytes(U).length);

		// System.out.println(input.substring(0, input.indexOf(",")));

		// String teststr = input.substring(0, input.indexOf(","));
		// long test2 = Long.parseLong("1" + teststr);
		
		//String test = "2018365746,\n" + 
		//		"Test";
		//System.out.println(test);
		
		//for (byte b : test.getBytes(U)) {
		//System.out.println(b);
	//}
		Server server = new Server(menuList);
		Thread thread = new Thread(server);
	    thread.start();
	    
	    server.useBackUp(true);
	    server.useBackUp(false);
	    
	    
	    
		//Client client = new Client();
	    //Thread thread2 = new Thread(client);
	    //thread2.start();	
		//client.test("american");
		//client.test("american");
	    //Message mes = new Message("Hello World!", 0, 0);
		//System.out.println(mes.toString());
		
		//Message mes2 = new Message(mes.encode());
		//System.out.println(mes2.toString());

	}

}
