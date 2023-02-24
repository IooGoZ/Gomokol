package fr.IooGoZ.GomokolClient.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Parser {

	private final Client client;
	private final InputStream in;
	
	public Parser(Client client, InputStream in) {
		this.client = client;
		this.in = in;
	}
	
	public boolean parse() {
		try {
			int order = readInt();
			switch (order) {
				
				
				default : return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private int readInt() throws IOException {
		byte[] b = new byte[Integer.BYTES];
		in.read(b);
		return ByteBuffer.wrap(b).getInt();
	}
	
	private int[] readIntArray() throws IOException {
		int len = readInt();
		int[] res = new int[len];
		for (int i = 0; i < len; i++) {
			res[i] = readInt();
		}
		return res;
	}
	
}
