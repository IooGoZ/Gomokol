package fr.IooGoZ.GomokolClient.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Socket implements Runnable {
	
	private static final int PORT = 80;
	private static final String HOST = "localhost";

	private final BufferedOutputStream outSt;
	private final Parser parser;

	public Client() throws UnknownHostException, IOException {
		super(HOST, PORT);
		this.parser = new Parser(this, super.getInputStream());
		this.outSt = new BufferedOutputStream(super.getOutputStream());
	}

	@Override
	public void run() {
		while (super.isConnected())
			if (!this.parser.parse())
				break;
	}

	public void send(int[] msg) throws IOException {
		for (int letter : msg)
			outSt.write(letter);
		outSt.flush();
	}


    public static void main(String[] args) throws UnknownHostException, IOException{
        Client cl = new Client();
		Thread t = new Thread(cl);
		t.start();
    }
}