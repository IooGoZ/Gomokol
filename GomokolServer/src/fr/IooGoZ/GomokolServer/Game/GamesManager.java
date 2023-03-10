package fr.IooGoZ.GomokolServer.Game;

import java.io.IOException;
import java.util.HashMap;

import fr.IooGoZ.GomokolServer.Server.Orders;
import fr.IooGoZ.GomokolServer.Server.Session;

public class GamesManager {
	
	//Seule instance du manager
	public static final GamesManager MANAGER = new GamesManager();
	
	//Map game_id -> game
	private final HashMap<Integer, Game> id2game;
	
	//id de la prochaine partie
	private int current_game_id = 0;
	
	//Constructeur privé
	private GamesManager() {
		this.id2game = new HashMap<Integer, Game>();
	}
	
	//Fonction du parser------------------------------------------------------------------
	
	//Création d'une partie
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
	
	//Redirection des coups joués
	public boolean playStroke(Session session, int gameId, int player_id, int[] stroke) {
		if (id2game.containsKey(gameId)) {
			return id2game.get(gameId).playStroke(session, player_id, stroke);
		}
		return false;
	}
	
	//Redirection du lancement de la partie
	public boolean startGame(Session session, int gameId) {
		if (id2game.containsKey(gameId)) {
			return id2game.get(gameId).start(session);
		}
		return false;
	}
	
	//Redirection ajout d'un joueur à la partie
	public boolean addPlayer(Session session, int gameId) {
		if (id2game.containsKey(gameId)) {
			try {
				Game game = id2game.get(gameId);
				int player_id = game.addPlayer(session);
				session.send(Orders.serverPlayerRegistered(gameId, player_id));
				return true;
			} catch (Exception e) {
				System.err.println(e.getMessage());
				return false;
			}
			
		}
		return false;
	}
	
	//Redirection de la validation par l'owner
	public boolean ownerValidation(Session session, int gameId, int validation) {
		if (id2game.containsKey(gameId)) {
			return id2game.get(gameId).ownerValidation(session, validation);
		}
		return false;
	}
	
	//Destruction d'une game
	public void destroyGame(Game game) {
		game.destroy();
		id2game.remove(game.getId());
		System.gc();
	}

}
