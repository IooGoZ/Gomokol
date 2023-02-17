package fr.IooGoZ.GomokolServer.Game;

import java.io.IOException;
import java.util.HashMap;

import fr.IooGoZ.GomokolServer.Server.Orders;
import fr.IooGoZ.GomokolServer.Server.Session;

public class GamesManager {
	
	public static final GamesManager MANAGER = new GamesManager();
	
	private final HashMap<Integer, Game> id2game;
	
	private int current_game_id = 0;
	
	private GamesManager() {
		this.id2game = new HashMap<Integer, Game>();
	}
	
	public boolean createGame(Session session, int order) {
		if (order > 0 && order < 10 && id2game.size() < 20) {
			Game game = new Game(this, this.current_game_id, order, session);
			id2game.put(current_game_id, game);
			
			try {
				session.send(Orders.serverGameCreated(this.current_game_id));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.current_game_id++;
			
			return true;
		}
		return false;
	}
	
	public boolean playStroke(Session session, int gameId, int player_id, int[] stroke) {
		if (id2game.containsKey(gameId)) {
			return id2game.get(gameId).playStroke(session, player_id, stroke);
		}
		return false;
	}
	
	public boolean startGame(Session session, int gameId) {
		if (id2game.containsKey(gameId)) {
			return id2game.get(gameId).start(session);
		}
		return false;
	}
	
	public boolean addPlayer(Session session, int gameId) {
		if (id2game.containsKey(gameId)) {
			try {
				Game game = id2game.get(gameId);
				int player_id = game.addPlayer(session);
				session.send(Orders.serverPlayerRegistered(gameId, player_id));
				return true;
			} catch (Exception e) {
				return false;
			}
			
		}
		return false;
	}
	
	public boolean ownerValidation(Session session, int gameId, int validation) {
		if (id2game.containsKey(gameId)) {
			return id2game.get(gameId).ownerValidation(session, validation);
		}
		return false;
	}
	
	public void destroyGame(Game game) {
		game.destroy();
		id2game.remove(game.getId());
		System.gc();
	}

}
