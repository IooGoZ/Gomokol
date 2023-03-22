package fr.IooGoZ.GomokolServer.Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

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
				switch (order) {
				case 0 : return clientInitGame();
				case 5 : return clientStartGame();
				case 6 : return clientEmitStroke();
				case 7 : return clientAnswerValidation();
				case 8 : return clientRegisterPlayer();
				case 12 : return clientSubscribeGroup();
				case 14 : return clientInitGroup();
				case 17 : return clientFreeData();
				
				
				default : {
					System.err.println("[Parser] - Invalid order.");
					return false;
				}
			}
			} catch (SocketException e) {
				System.err.println("[Parser] - " + e.getMessage());
				return false;
			} catch (IOException e) {
				System.err.println("[Parser] - Reading socket error");
				return false;
			}
	}
	
	private boolean clientRegisterPlayer() throws IOException {
		int game_id = readInt();
		if (GamesManager.DEBUG) 
			System.out.println("[Parser] - Register Player (8) : game_id=" + game_id);
		if (!GamesManager.MANAGER.addPlayer(session, game_id))
			session.send(Orders.serverErrorInRequest(Orders.C_REGISTER_PLAYER.getId()));
		
		return true;
	}

	private boolean clientAnswerValidation() throws IOException {
		int game_id = readInt();
		int validation = readInt();
		if (GamesManager.DEBUG) 
			System.out.println("[Parser] - Answer Validation (7) : game_id=" + game_id + ", validation=" + validation);
		if (!GamesManager.MANAGER.ownerValidation(session, game_id, validation))
			session.send(Orders.serverErrorInRequest(Orders.C_ANSWER_VALIDATION.getId()));
		
		return true;
	}

	private boolean clientEmitStroke() throws IOException {
		int game_id = readInt();
		int player_id = readInt();
		int[] stroke = readIntArray();
		if (GamesManager.DEBUG) {
			System.out.print("[Parser] - Emit Stroke (6) : game_id=" + game_id + ", player_id=" + player_id + ", stroke=");
			for (int i : stroke) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		if (!GamesManager.MANAGER.playStroke(session, game_id, player_id, stroke))
			session.send(Orders.serverErrorInRequest(Orders.C_EMIT_STROKE.getId()));
		
		return true;
	}

	private boolean clientStartGame() throws IOException {
		int game_id = readInt();
		
		if (GamesManager.DEBUG) 
			System.out.println("[Parser] - Start Game (5) : game_id=" + game_id);
		if (!GamesManager.MANAGER.startGame(session, game_id))
			session.send(Orders.serverErrorInRequest(Orders.C_START_GAME.getId()));
		
		return true;
	}

	private boolean clientInitGame() throws IOException {
		int groupId = readInt();
		int order = readInt();
		if (GamesManager.DEBUG) 
			System.out.println("[Parser] - Init Game (0) : order=" + order);
		if (!GamesManager.MANAGER.createGame(this.session, groupId, order, true))
			session.send(Orders.serverErrorInRequest(Orders.C_INIT_GAME.getId()));
		return true;
	}
	
	private boolean clientSubscribeGroup() throws IOException {
		int groupId = readInt();
		if (GamesManager.DEBUG) 
			System.out.println("[Parser] - Subscribe Group (12) : group_id=" + groupId);
		return GamesManager.MANAGER.registerClientToGroup(this.session, groupId);
	}
	
	private boolean clientInitGroup() throws IOException {
		int nb_player_per_game = readInt();
		int nb_games = readInt();
		if (GamesManager.DEBUG) 
			System.out.println("[Parser] - Init Group (14) : nb_player_per_game=" + nb_player_per_game + ", nb_games=" + nb_games);
		return GamesManager.MANAGER.createGroup(session, nb_player_per_game, nb_games);
	}
	
	private boolean clientFreeData() throws IOException {
		int gameId = readInt();
		int[] data = readIntArray(); 
		if (GamesManager.DEBUG) {
			System.out.print("[Parser] - Emit Stroke (6) : game_id=" + gameId + ", data=");
			for (int i : data) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		return GamesManager.MANAGER.freeDataTransmitter(gameId, data);
	}

	
	//Function ressource-----------------------------
	private int readInt() throws IOException, SocketException {
		return in.readInt();
	}
	
	private int[] readIntArray() throws IOException, SocketException {
		int len = readInt();
		int[] res = new int[len];
		for (int i = 0; i < len; i++) {
			res[i] = readInt();
		}
		return res;
	}
	
	
}
