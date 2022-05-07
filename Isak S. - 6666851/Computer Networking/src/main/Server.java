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
		//System.out.println(messageR.toString());
	
		this.ackNumber = messageR.getSeqNumber();

		messageS = this.receiveResponse(messageR);

		try {
			this.send(messageS, packetR.getSocketAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
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
		this.seqNumber = this.seqNumber + 1;
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

}
