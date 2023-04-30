package fr.IooGoZ.GomokolServer;

import java.io.IOException;

import fr.IooGoZ.GomokolServer.Server.Server;

public class Main {

	private Server server;
	
	public Main(String[] args) throws IOException {
		if (args.length != 0) {
			int port = Integer.parseInt(args[0]);
			this.server  = new Server(port);
			server.open();
		} else {
			this.server  = new Server();
			server.open();
		}
	}
	
	public static void main(String[] args) {
		try {
			new Main(args);
		} catch (IOException e) {
			System.err.println("Le serveur n'a pas pu démarrer, le port est-il correct ?");
			e.printStackTrace();
		}
		
	}

}
