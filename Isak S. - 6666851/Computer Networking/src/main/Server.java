package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Server implements Runnable {
	public final static int port = 1400;
	private DatagramSocket ds = null;

	private ArrayList<Menu> menus = null;
	private HashMap<String, Menu> themeToMenu = null;
	private HashMap<String, Menu> dayToMenu = null;

	private DatagramSocket dsBackUp = null;
	private boolean backUp = false;
	private Thread thread = new Thread();

	private int seqNumber = 0;
	private int ackNumber = 0;

	public Server(ArrayList<Menu> menus) {
		super();
		this.menus = menus;
		try {
			this.ds = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.themeToMenu = new HashMap<String, Menu>();
		this.dayToMenu = new HashMap<String, Menu>();

		for (Menu menu : menus) {
			this.themeToMenu.put(menu.getTheme(), menu);
			this.dayToMenu.put(menu.getDay(), menu);
		}

	}

	public void run() {

		while (!ds.isClosed()) {
			this.runOnce();
		}
	}

	public void useBackUp(boolean bool) {

		if (this.backUp == false && bool == true) {

			try {
				this.dsBackUp = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Could not start backup Server");
				return;
			}
			this.backUp = bool;
			System.out.println("Running backup Server at: " + this.dsBackUp.getPort());
		} else if (this.backUp == true && bool == false) {

			this.dsBackUp.close();
			System.out.println("Backup server is closed");
		}

		this.backUp = bool;
	}

	public void runOnce() {
		DatagramPacket packetR = null;
		Message messageR = null;
		Message messageS = null;
		byte[] buffer = new byte[65535];

		packetR = new DatagramPacket(buffer, buffer.length);

		try {
			ds.receive(packetR);
			
		} catch (Exception e) {
			// Ignore when connection closed.
			// System.out.println(e);
			return;
		}

		try {
			messageR = new Message(buffer);
		} catch (Exception e) {
			System.out.println(e);
			return;
		}

		/////
		//System.out.println("Server receiving:\n" + messageR.toString());

		if (messageR.getSeqNumber() != -100) {
			this.ackNumber = messageR.getSeqNumber();
		} else if (messageR.getAckNumber() == -100 && messageR.getContent().contains("OK")) {
			
			return;
		}

		messageS = this.receiveResponse(messageR);

		try {
			this.send(messageS, packetR.getSocketAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		/////
		//System.out.println("\nServer sending:\n" + messageS.toString() + "\n");

	}

	public void send(Message message, SocketAddress address) throws IOException {
		byte[] bytes = message.encode();

		DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, address);

		try {
			this.ds.send(packetS);
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
		
		if (message.getAckNumber() != -100) {
			this.seqNumber = this.seqNumber + 1;
		}
	}

	public void testSend(Message message, SocketAddress address) throws IOException {
		this.send(message, address);
		System.out.println("\nServer sending:\n" + message.toString());
	}

	public Message receive() {

		byte[] buffer = new byte[65535];
		DatagramPacket packetR = null;
		Message message = null;

		packetR = new DatagramPacket(buffer, buffer.length);

		try {
			ds.receive(packetR);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}

		try {
			message = new Message(buffer);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}

		/////////////////////////////
		System.out.println("\nServer receiving:\n" + new String(message.toString()));

		return message;
	}

	public Message receiveResponse(Message message) {
		Message response = null;

		if (Objects.isNull(message)) {
			response = new Message("Unknown command", this.ackNumber, this.seqNumber);
		} else {
			response = this.response(message);
		}

		return response;

	}

	public Message testReceive() {
		Message response = this.receiveResponse(this.receive());
		/////////////////////////////
		System.out.println("\nServer responding:\n" + new String(response.toString()));

		return response;
	}

	public Message response(Message message) {
		String content = message.getContent().toLowerCase();
		// String extra = message.getExtra();
		String response = "";
		Menu menu = null;
		Message messageR = null;

		if (content.contains("menus")) {
			response = this.getMenuNames();
		} else if (content.contains("today") || content.contains("menu of the day")) {
			menu = this.getToday();
			response = menu.getMenu();
		} else if (content.contains("100")) {
			return new Message("100:OK", -100, -100);
		} else {
			menu = this.getMenu(content);

			if (!Objects.isNull(menu)) {
				response = menu.getMenu();
			} else {
				response = "Unknown command";
			}
		}

		messageR = new Message(response, this.ackNumber, this.seqNumber);
		if (!Objects.isNull(menu)) {
			messageR.addCache(menu);
		}

		return messageR;
	}

	public Menu getToday() {
		LocalDate today = LocalDate.now();
		DayOfWeek dayOfWeek = today.getDayOfWeek();

		if (dayOfWeek.equals(DayOfWeek.SUNDAY) || dayOfWeek.equals(DayOfWeek.SATURDAY)) {
			dayOfWeek = DayOfWeek.FRIDAY;
		}

		return this.dayToMenu.get(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).toString());
	}

	public ArrayList<Menu> getMenus() {
		return menus;
	}

	public void addMenus(ArrayList<Menu> menus) {
		for (Menu menu : menus) {
			this.addMenu(menu);
		}
	}

	public void addMenu(Menu menu) {
		if (Objects.isNull(menus) || this.menus.isEmpty()) {
			this.menus = new ArrayList<Menu>();
		}
		this.menus.add(menu);
	}

	public String getMenuNames() {
		String output = "";

		for (Menu menu : this.menus) {
			output += menu.getName() + ", ";
		}
		return output.substring(0, output.length() - 2);
	}

	public Menu getMenu(String input) {
		for (String day : this.dayToMenu.keySet()) {
			if (input.contains(day.toLowerCase())) {
				return this.dayToMenu.get(day);
			}
		}

		for (String theme : this.themeToMenu.keySet()) {
			if (input.contains(theme.toLowerCase())) {
				return this.themeToMenu.get(theme);
			}
		}

		return null;
	}

	public void close() {
		ds.close();
	}

	public void runBackup( ) {
		this.useBackUp(true);
		
		DatagramPacket packetR = null;
		Message messageR = null;
		Message messageS = null;
		byte[] buffer = new byte[65535];

		packetR = new DatagramPacket(buffer, buffer.length);

		try {
			this.dsBackUp.receive(packetR);
			
		} catch (Exception e) {
			// Ignore when connection closed.
			// System.out.println(e);
			return;
		}

		try {
			messageR = new Message(buffer);
		} catch (Exception e) {
			System.out.println(e);
			return;
		}

		/////
		System.out.println("Server receiving:\n" + messageR.toString());

		if (messageR.getSeqNumber() != -100) {
			this.ackNumber = messageR.getSeqNumber();
		} else if (messageR.getAckNumber() == -100 && messageR.getContent().contains("OK")) {
			
			return;
		}

		messageS = this.receiveResponse(messageR);

		try {
			this.send(messageS, packetR.getSocketAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		/////
		System.out.println("\nServer sending:\n" + messageS.toString() + "\n");
	}
}
