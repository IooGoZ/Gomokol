package fr.IooGoZ.GomokolServer.Server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session implements Runnable {
	
	//Created by IooGoZ

	private Server server;
	private Socket client;
	private Parser parser;
	private BufferedOutputStream outSt;
	

	public Session(Server server, Socket client) {
		this.client = client;
		this.server = server;
		try {
			this.parser = new Parser(this, client.getInputStream());
			this.outSt = new BufferedOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!client.isClosed())
			if (!this.parser.parse())
				break;
		
		synchronized (server) {
			server.removeSession(this);
		}
	}

	public void send(int[] msg) throws IOException {
		for (int letter : msg)
			outSt.write(letter);
		outSt.flush();
	}


	public Server getServer() {
		return server;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Session) {
			Session session = (Session) obj;
			return session.client == this.client && session.parser == this.parser;
		}
		return false;
	}
}
