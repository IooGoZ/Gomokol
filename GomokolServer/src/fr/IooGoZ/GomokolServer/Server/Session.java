package fr.IooGoZ.GomokolServer.Server;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session implements Runnable {
	
	//Created by IooGoZ

	private Server server;
	private Socket client;
	private Parser parser;
	private DataOutputStream outSt;
	

	public Session(Server server, Socket client) {
		
		this.client = client;
		this.server = server;
		try {
			this.parser = new Parser(this, client.getInputStream());
			this.outSt = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connexion session : " + client.getInetAddress());
	}

	@Override
	public void run() {
		
		while (!client.isClosed()) {
			if (!this.parser.parse())
				break;
			
		}
			
		System.out.println("Déconnexion session : " + client.getInetAddress());
		try {
			client.close();
			synchronized (server) {
				server.removeSession(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void send(int[] msg) throws IOException {
		for (int letter : msg) {
			outSt.writeInt(letter);
		}
		outSt.flush();
	}


	public Server getServer() {
		return server;
	}
	
	public boolean isConnected() {
		return !client.isClosed() && client.isConnected() && !server.isClosed();
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
