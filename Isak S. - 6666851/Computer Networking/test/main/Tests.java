package main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Tests {

	static private Server server = null;
	static private Client client = null;
	static private Thread thread = new Thread();

	static private String testString = "Hello World!";
	static private Menu testMenu = new Menu("Friday", "American", "Mini Burgers", "Buffalo Chicken", "Apple Pie",
			"Soda");
	static private StringHolder string = null;
	static ArrayList<Menu> menus = null;

	@BeforeAll
	static void setUp() throws Exception {
		Menu Monday = new Menu("Monday", "Chinese", "Sliced Tofu Salad", "Kung Pao Chicken", "Fried Bananas",
				"Bubble Tea");
		Menu Tuesday = new Menu("Tuesday", "Indian", "Vegetable Samosas", "Butter Chicken", "Modak", "Masala Chai");
		Menu Wednesday = new Menu("Wednesday", "Middle Eastern", "Fattoush Salad", "Pomegranate Chicken", "Halva",
				"Limonana");
		Menu Thursday = new Menu("Thursday", "Mediterranean", "Baba Ganoush", "Spanakopita", "Baklava", "Iced Tea");
		Menu Friday = testMenu;

		Menu[] menusAr = { Monday, Tuesday, Wednesday, Thursday, Friday };
		ArrayList<Menu> menuList = new ArrayList<>(Arrays.asList(menusAr));
		menus = menuList;

		server = null;
		client = null;

		System.out.println("Due to multithreading, the requests/responses might be printed out in an incorrect order.");
	}

	@BeforeEach
	void pre() {
		server = new Server(menus);
		client = new Client();
		System.out.println("\n---------------------------------------------------------\n");
	}

	@AfterEach
	void post() {
		server.close();
		client.close();
		Thread.yield();
		string = new StringHolder();
	}

	// Test 1 - client sends message to server at the default port, message is
	// received.
	@Test
	void test1() {
		System.out.println("><><><><Test 1><><><><\n");
		System.out.println("Server port: " + Server.port + "\n");
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				string.setValue(server.receive().getContent());
			}
		});
		thread.start();

		client.testSend(testString);

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertNotNull(string);

	}

	// Test 2 & 12 - client sends a request for the American menu, the server
	// responds with the menu
	// which the client receives.
	@Test
	void test2_12() throws Exception {
		System.out.println("><><><><Test 2 & 12><><><><");

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				string.setValue(server.testReceive().getContent());
				server.runOnce();
			}
		});
		thread.start();

		client.test("MENU " + testMenu.getTheme());

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(testMenu.getMenu(), string.toString());
	}

	// Test 3 & 4 - when the client sends a message to the server and vice versa,
	// the
	// message is send encoded, received and decoded. These actions are implemented
	// in the
	// Message class - this test is meant to verify its validity.
	@Test
	void test3_4() {
		System.out.println("><><><><Test 3 & 4><><><><\n");

		// Encode the testString.
		Message message = new Message(testString, 0, 0);

		System.out.println("Decoded:\n" + message.toString());

		// getBytes() returns a String representation of the encoded message.
		System.out.println("\nEncoded:\n" + message.getBytes());

		// charset used for decoding is UTF-8
		assertEquals(message.toString(), new String(message.encode(), Message.charset));
	}

	// Test 5 & 6 - when the client sends a message to the the server and vice
	// versa, a checksum is calculated in the Message class - this test is meant to
	// verify its validity.
	@Test
	void test5_6() {
		System.out.println("><><><><Test 5 & 6><><><><\n");

		Message message = new Message(testString, 0, 0);

		System.out.println("Message:\n" + message.toString());
		System.out.println("\nChecksum (applied on ',0:0;\\n<message content>':\n" + message.getChecksum());

		// verify the checksum
		CRC32 crc = new CRC32();
		String s = "," + message.getAckSeq() + "\n" + message.getContent();
		crc.update(s.getBytes(Message.charset));
		assertEquals(message.getChecksum(), crc.getValue());
	}

	// Test 7 & 8 - when the client sends a message to the server and vice versa,
	// and the checksum included is incorrect - the message is dropped. This is
	// implemented in the Message class and this test is meant to verify it's
	// validity.
	@Test
	void test7_8() {
		System.out.println("><><><><Test 7 & 8><><><><\n");

		String badMessage = "66666666,11:12;\n" + "Hello World!";

		System.out.println(
				"Bad Message (throws an IllegalArgumentException when received by server/client)\n" + badMessage);

		assertThrows(IllegalArgumentException.class, () -> {
			new Message(badMessage.getBytes(Message.charset));
		});
	}

	// Test 9 - the client resends a message if no response is received (timeout).
	@Test
	void test9() throws Exception {
		System.out.println("><><><><Test 9><><><><\n");

		System.out.println("Client sends requests, Server receives the requests without responding.");

		// Receive the request without responding
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int i = 3;
				while (i > 0) {
					string.setValue(server.receive().getContent());
					i--;
				}
			}
		});
		thread.start();

		client.test("MENU " + testMenu.getTheme());

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Requests send match the ones received
		assertEquals("MENU " + testMenu.getTheme(), string.toString());
	}

	// Test 10 & 11 - client requests the list of all menus and today's menu.
	@Test
	void test10_11() throws IOException {
		System.out.println("><><><><Test 10 & 11><><><><\n");
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				server.run();
			}
		});
		thread.start();

		// request list of menus.
		String menusList = client.test("MENUS");

		// request today's menu.
		String today = client.test("MENU TODAY");

		assertEquals(menusList, server.getMenuNames());
		assertEquals(today, server.getToday().getMenu());

	}

	// Test 13 & 14 - client/server sends a message with a non-empty <extra> field.
	@Test
	void test13_14() throws IOException {
		System.out.println("><><><><Test 13 & 14><><><><\n");
		Message message = new Message(testString, 0, 0);
		message.setExtra("this is the extra field");

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				server.runOnce();
			}
		});
		thread.start();

		client.test("MENUS", "this is the extra field");

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				client.testReceive(0);
			}
		});
		thread.start();

		server.testSend(message, client.getAddress());
	}

	// Test 15 & 16 - client/server attempts to send a message with a size larger
	// than the specified limit - the package is not sent. This is
	// implemented in the Message class and this test is meant to verify it's
	// validity.
	@Test
	void test15_16() throws IOException {
		System.out.println("><><><><Test 15 & 16><><><><\n");
		
	    byte[] array = new byte[577];
	    new Random().nextBytes(array);
	    String longString = new String(array, Message.charset);
	    
		assertThrows(IllegalArgumentException.class, () -> {
			Message message = new Message(longString, 0, 0);
		});
		
	}

	// Test 17 - client requests the list of all menus and today's menu.
	@Test
	void test17() throws IOException {
		System.out.println("><><><><Test 17><><><><\n");
		
		System.out.println("Expected pattern:\n"
				+ "C - 0:0\n"
				+ "S - 0:0\n\n"
				+ "C - 0:1\n"
				+ "S - 1:1\n\n"
				+ "C - 1:2\n"
				+ "S - 2:2\n\n"
				+ "C - 2:3\n"
				+ "S - 3:3");
		
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				server.run();
			}
		});
		thread.start();

		client.test("Message 1");
		client.test("Message 2");
		client.test("Message 3");
		client.test("Message 4");
		
		
	}
}
