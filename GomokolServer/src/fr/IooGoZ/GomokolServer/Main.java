package fr.IooGoZ.GomokolServer;

import java.io.IOException;

import fr.IooGoZ.GomokolServer.Server.Server;

public class Main {

	private Server server;
	
	public Main() throws IOException {
		this.server  = new Server();
		server.open();
		
	}
	
	public static void main(String[] args) {
		try {
			new Main();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
