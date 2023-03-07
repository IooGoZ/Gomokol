package fr.IooGoZ.GomokolClient.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.IooGoZ.GomokolClient.DontUseOutsideAPI;
import fr.IooGoZ.GomokolClient.GamesManager;

public class Client extends Socket implements Runnable {
	
	@DontUseOutsideAPI
	public static final long TIMEOUT_DURATION = 3000l;
	
	@DontUseOutsideAPI
	public static final int DEFAULT_VALUE = -1;

	private final BufferedOutputStream outSt;
	private final Parser parser;

	@DontUseOutsideAPI
	public Client(String address, int port, GamesManager manager) throws UnknownHostException, IOException {
		super(address, port);
		this.parser = new Parser(super.getInputStream(), manager);
		this.outSt = new BufferedOutputStream(super.getOutputStream());
	}

	@DontUseOutsideAPI
	@Override
	public void run() {
		while (super.isConnected())
			if (!this.parser.parse())
				break;
	}

	@DontUseOutsideAPI
	public void send(int[] msg) throws IOException {
		for (int letter : msg)
			outSt.write(letter);
		outSt.flush();
	}
}