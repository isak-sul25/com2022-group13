package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Objects;
import java.util.Scanner;

public class Client implements Runnable {

	private DatagramSocket ds = null;
	private int seqNumber = 0;
	private int ackNumber = 0;

	public Client() {
		super();
		try {
			this.ds = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.ds.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public void run() {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			String input = scanner.nextLine();
			String output = null;
			try {
				output = this.sendAndReceive(input).getContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!Objects.isNull(output)) {
				System.out.println(output);
			}
		}

	}

	public String test(String message) throws IOException {
		int a = this.ackNumber;
		int s = this.seqNumber;
		Message response = this.sendAndReceive(message);
		/////////////////////////////
		System.out.println("\nClient sending:\n" + new Message(message, a, s).toString());
		
		if (Objects.isNull(response)) {
			return null;
		}
		System.out.println("\nClient receiving:\n" + response.toString());
		return response.getContent();
		
	}
	
	public String test(String message, String extra) throws IOException {
		int a = this.ackNumber;
		int s = this.seqNumber;
		Message response = this.sendAndReceive(message, extra);
		/////////////////////////////
		Message test = new Message(message, a, s);
		test.setExtra(extra);
		System.out.println("\nClient sending:\n" + test.toString());
		
		if (Objects.isNull(response)) {
			return null;
		}
		System.out.println("\nClient receiving:\n" + response.toString());
		return response.getContent();
		
	}

	public Message sendAndReceive(String input) throws IOException {
		Message messageS = new Message(input, this.ackNumber, this.seqNumber);
		Message messageR = null;
		byte[] buffer = null;
		DatagramPacket packetR = null;

		int i = 0;
		this.send(messageS);

		while (i < 3) {
			buffer = new byte[65535];
			packetR = new DatagramPacket(buffer, buffer.length);
			try {
				ds.receive(packetR);
			} catch (Exception e) {
				System.out.println("Request timed-out, retrying");
				this.send(messageS);
				this.seqNumber = this.seqNumber - 1;
				i++;

				if (i == 3) {
					return null;
				}

				continue;
			}
			break;
		}

		try {
			messageR = new Message(buffer);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
		
		this.ackNumber = messageR.getSeqNumber();
		return messageR;

	}
	
	public Message sendAndReceive(String input, String extra) throws IOException {
		Message messageS = new Message(input, this.ackNumber, this.seqNumber);
		messageS.setExtra(extra);
		Message messageR = null;
		byte[] buffer = null;
		DatagramPacket packetR = null;

		int i = 0;
		this.send(messageS);

		while (i < 3) {
			buffer = new byte[65535];
			packetR = new DatagramPacket(buffer, buffer.length);
			try {
				ds.receive(packetR);
			} catch (Exception e) {
				System.out.println("Request timed-out, retrying");
				this.send(messageS);
				this.seqNumber = this.seqNumber - 1;
				i++;

				if (i == 3) {
					return null;
				}

				continue;
			}
			break;
		}

		try {
			messageR = new Message(buffer);
		} catch (Exception e) {
			return null;
		}
		
		this.ackNumber = messageR.getSeqNumber();
		return messageR;

	}
	
	public Message testReceive(int timeout) {
		
		byte[] buffer = new byte[65535];
		DatagramPacket packetR = null;
		Message message = null;
		try {
			this.ds.setSoTimeout(timeout);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
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
		System.out.println("\nClient receiving:\n" + new String(message.toString()));
		
		this.ackNumber = message.getSeqNumber();
		return message;
	}
	

	public void send(Message message) throws IOException {
		InetAddress ip = InetAddress.getLocalHost();
		byte[] bytes = message.encode();

		DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, ip, Server.port);

		try {
			this.ds.send(packetS);
		} catch (Exception e) {
			System.out.println("Can't send message");
			return;
		}
		this.seqNumber = this.seqNumber + 1;
	}
	
	public void testSend(String string) {
		int a = this.ackNumber;
		int s = this.seqNumber;
		this.send(string);
		/////////////////////////////
		System.out.println("Client sending:\n" + new String(new Message(string, a, s).encode(), Message.charset));

	}

	public void send(String string) {
		try {
			this.send(new Message(string, this.ackNumber, this.seqNumber));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		ds.close();
	}
	
	public SocketAddress getAddress() {
		return this.ds.getLocalSocketAddress();
	}

}
