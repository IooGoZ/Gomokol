package fr.IooGoZ.GomokolServer.Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.IooGoZ.GomokolServer.Game.GamesManager;

//Parser du serveur
public class Parser {

	private final Session session;
	private final DataInputStream in;
	
	public Parser(Session session, InputStream inSt) {
		this.session = session;
		this.in = new DataInputStream(new BufferedInputStream(inSt));
	}
	
	public synchronized boolean parse() {
		try {
			int order = readInt();
			System.out.println("[Parser] - Ordre reçus : " + order);
			switch (order) {
				case 0 : return clientInitGame();
				case 5 : return clientStartGame();
				case 6 : return clientEmitStroke();
				case 7 : return clientAnswerValidation();
				case 8 : return clientRegisterPlayer();
				
				default : return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean clientRegisterPlayer() throws IOException {
		int game_id = readInt();
		if (GamesManager.MANAGER.addPlayer(session, game_id))
			session.send(Orders.serverErrorInRequest(Orders.C_REGISTER_PLAYER.getId()));
		
		return true;
	}

	private boolean clientAnswerValidation() throws IOException {
		int game_id = readInt();
		int validation = readInt();
		if (GamesManager.MANAGER.ownerValidation(session, game_id, validation))
			session.send(Orders.serverErrorInRequest(Orders.C_ANSWER_VALIDATION.getId()));
		
		return true;
	}

	private boolean clientEmitStroke() throws IOException {
		int game_id = readInt();
		int player_id = readInt();
		int[] stroke = readIntArray();
		if (GamesManager.MANAGER.playStroke(session, game_id, player_id, stroke))
			session.send(Orders.serverErrorInRequest(Orders.C_EMIT_STROKE.getId()));
		
		return true;
	}

	private boolean clientStartGame() throws IOException {
		int game_id = readInt();
		if (GamesManager.MANAGER.startGame(session, game_id))
			session.send(Orders.serverErrorInRequest(Orders.C_START_GAME.getId()));
		
		return true;
	}

	private boolean clientInitGame() throws IOException {
		int order = readInt();
		if (GamesManager.MANAGER.createGame(this.session, order))
			session.send(Orders.serverErrorInRequest(Orders.C_INIT_GAME.getId()));
		return true;
	}

	
	//Function ressource-----------------------------
	private int readInt() throws IOException {
		return in.readInt();
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
