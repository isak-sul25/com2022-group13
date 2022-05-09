package main;

import java.nio.charset.Charset;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.CRC32;

public class Message {

	private CRC32 checksum;
	private int ackNumber = 0;
	private int seqNumber = 0;

	private String extra = "";

	private ArrayList<String> cache = null;
	private String content;
	public final static Charset charset = Charset.availableCharsets().get("UTF-8");
	public final static int packetSize = 576;
	public final static int headerSize = 100;

	public Message(String content, int ack, int seq) {
		super();
		String message = "";

		if (ack == -1 && this.seqNumber == 0) {
			message = "," + seq + ";\n" + content;
		} else if (seq == -100) {
			message = "," + "\n" + content;
		} else {
			message = "," + ack + ":" + seq + ";\n" + content;
		}

		byte[] bytes = message.getBytes(charset);

		// comma
		if (bytes.length + 1 < packetSize - 5) {
			this.content = content;
			this.ackNumber = ack;
			this.seqNumber = seq;

			this.checksum = new CRC32();
			this.checksum.update(bytes);
		} else {
			throw new IllegalArgumentException("Message is too long");
		}

		this.cache = new ArrayList<String>();
	}

	public Message(byte[] bytes) throws IllegalArgumentException {
		super();
		String message = this.clean(bytes);
		int end = message.indexOf(",");
		int end2 = message.indexOf(";");

		long checksumR = 0;

		if (end != -1) {
			checksumR = Long.parseLong(message.substring(0, end));
		} else {
			throw new IllegalArgumentException("Missing ,");
		}

		if (end2 != -1 && message.indexOf(":") != -1) {
			String ackSeq = message.substring(end + 1, end2);
			int seperator = ackSeq.indexOf(":");

			this.ackNumber = Integer.parseInt(ackSeq.substring(0, seperator));
			this.seqNumber = Integer.parseInt(ackSeq.substring(seperator + 1));
		} else if (end2 != -1) {
			String ackSeq = message.substring(end + 1, end2);

			// System.out.println(ackSeq);
			this.ackNumber = -1;
			this.seqNumber = Integer.parseInt(ackSeq);
		} else if ((end2 == -1 && message.contains("100")) || message.contains("OK")) {
			this.ackNumber = -100;
			this.seqNumber = -100;
		} else {
			throw new IllegalArgumentException("Missing proper seq/ack");
		}

		CRC32 CRCTest = new CRC32();
		CRCTest.update(message.substring(end).getBytes(charset));
		long checksumTest = CRCTest.getValue();

		if (checksumTest != checksumR) {
			throw new IllegalArgumentException(
					"Checksum Mismatch: R: " + checksumR + " T: " + checksumTest + "\nDropping package...");
		} else {
			this.checksum = CRCTest;
		}

		Scanner scanner = new Scanner(message);
		String extra = "";
		
		// ?
		if (this.ackNumber != -100) {
			extra = scanner.nextLine().substring(end2 + 1);
		} else {
			scanner.nextLine();
		}

		if (extra.indexOf("CA:") != -1) {
			String cacheBuf = extra.substring(extra.indexOf("CA:") + 3);
			cacheBuf = cacheBuf.substring(0, cacheBuf.indexOf(";"));

			this.cache = new ArrayList<String>();
			this.cache.addAll(Arrays.asList(cacheBuf.split(",")));

			if (!(extra.length() == extra.indexOf(";") + 1)) {
				this.extra = extra.substring(extra.indexOf(";" + 1));
			} else {
				this.extra = "";
			}

		} else {
			this.extra = extra;
		}

		this.content = "";

		while (scanner.hasNextLine()) {
			this.content += scanner.nextLine();

			if (scanner.hasNextLine() == true) {
				this.content += "\n";
			}
		}

		scanner.close();
	}

	public int getAckNumber() {
		return ackNumber;
	}

	public void setAckNumber(int ackNumber) {
		this.ackNumber = ackNumber;
	}

	public int getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(int seqNumber) {
		this.seqNumber = seqNumber;
	}

	public long getChecksum() {
		CRC32 cs = new CRC32();
		cs.update(new String("," + this.getAckSeq() + this.getExtra() + "\n" + this.getContent()).getBytes(charset));
		this.checksum = cs;
		return this.checksum.getValue();
	}

	public String getExtra() {
		// return this.getCache() + this.extra;
		return this.extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getContent() {
		return content;
	}

	public String getAckSeq() {
		if (this.ackNumber == -1 && this.seqNumber == 0) {
			return this.seqNumber + ";";
		} else if (this.seqNumber == -100) {
			return "";
		}

		return this.ackNumber + ":" + this.seqNumber + ";";
	}

	public byte[] encode() {

		String buf = this.getChecksum() + "," + this.getAckSeq() + this.getExtra() + "\n" + this.getContent();
		return buf.getBytes(charset);
	}

	public String getBytes() {
		return Arrays.toString(this.encode());
	}

	public String clean(byte[] buffer) {
		if (Objects.isNull(buffer)) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (byte b : buffer) {
			if (b == 0) {
				break;
			}
			builder.append((char) b);
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return new String(this.encode(), charset);
	}

	public void addCache(Menu menu) {

		LocalDate today = LocalDate.now();
		DayOfWeek dayOfWeek = today.getDayOfWeek();
		String day = menu.getDay().toLowerCase();

		if (day.equals(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).toString().toLowerCase())) {
			this.cache.add("today");
		}

		this.cache.add(day);
		this.cache.add(menu.getTheme().toLowerCase());

	}

	public String getCache() {
		String output = "";

		if (!Objects.isNull(this.cache) && !this.cache.isEmpty()) {
			output += "CA:";

			for (String cache : this.cache) {
				output += cache + ",";
			}

			output = output.substring(0, output.length() - 1);
			output += ";";
		}

		return output;
	}

	public HashMap<String, String> getCacheMap() {
		HashMap<String, String> map = null;

		if (!Objects.isNull(this.cache) && !this.cache.isEmpty()) {
			map = new HashMap<String, String>();

			for (String str : this.cache) {
				map.put(str, this.getContent());
			}
		}

		return map;
	}

}
