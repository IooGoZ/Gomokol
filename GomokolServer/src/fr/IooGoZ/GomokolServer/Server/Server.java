package fr.IooGoZ.GomokolServer.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server extends ServerSocket {

	private static final int PORT = 8080;
	private boolean isRunning = true;
	private List<Session> wsSessions = new ArrayList<>();
	
	private final Server wsServer;
	
	
	public Server() throws IOException {
		super(PORT);
		System.out.println("Démarrage du serveur sur 127.0.0.1:" + PORT);
		this.wsServer = this;
	}

	public void open() {
		System.out.println("Attente d'une connexion...");
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (isRunning) {
					try {
						Socket client = accept();
						System.out.println("Connexion cliente reçue.");
						Session wsSession = new Session(wsServer, client);
						Thread t = new Thread(wsSession);
						t.start();
						synchronized (wsSessions) {
							wsSessions.add(wsSession);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				close();
			}
		});
		t.start();
		
	}

	public void removeSession(Session session) {
		synchronized (wsSessions) {
			wsSessions.remove(session);
		}
	}
	
	public void close() {
		isRunning = false;
		try {
			super.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
