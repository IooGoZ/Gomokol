package fr.IooGoZ.GomokolServer.Server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Session implements Runnable {
	
	//Created by IooGoZ
	private static final char LF = 10;

	private Server server;
	private Socket client;
	private Scanner inSt;
	private BufferedOutputStream outSt;
	

	public Session(Server server, Socket client) {
		this.client = client;
		this.server = server;
		try {
			this.inSt = new Scanner(new InputStreamReader(client.getInputStream()));
			this.outSt = new BufferedOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!client.isClosed()) {
			String response = null;
			try {
				response = read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(toString() + "--->" + response);
			//Parser------------------------------------------------------------
			if (response != null) {
				try {
					Thread.sleep(1000l);
					server.sendToAll(response);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//this.parser.mainParser(response);
 				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else break;
		}

		synchronized (server) {
			server.removeSession(this);
		}
	}

	public void send(String str) throws IOException {

		StringBuilder sb = new StringBuilder(str);
		sb.append((char) LF);
		outSt.write(sb.toString().getBytes());
		outSt.flush();
		System.out.println(toString() + "<---" + str);
	}
	
	private String read() throws IOException {
		return inSt.nextLine();
	}


	public Server getServer() {
		return server;
	}
}
