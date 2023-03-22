package fr.IooGoZ.GomokolServer.Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.IooGoZ.GomokolServer.Server.Orders;
import fr.IooGoZ.GomokolServer.Server.Session;

public class GamesManager {
	
	//Seule instance du manager
	public static final GamesManager MANAGER = new GamesManager();
	
	public static final boolean DEBUG = true;
	
	//Map game_id -> game
	private final HashMap<Integer, Game> id2game;
	
	private final HashMap<Integer, Group> id2group = new HashMap<>();
	
	private int current_group_id = 0;
	
	//id de la prochaine partie
	private int current_game_id = 0;
	
	//Constructeur privé
	private GamesManager() {
		this.id2game = new HashMap<Integer, Game>();
	}
	
	//Fonction du parser------------------------------------------------------------------
	
	//Création d'une partie
	public boolean createGame(Session session, int group, int order, boolean withAnswer) {
		if (order > 0 && order < 10 && id2game.size() < 20 && group >= -1) {
			
			Game game = new Game(this, id2group.getOrDefault(group, null), this.current_game_id, order, session);
			id2game.put(current_game_id, game);
			
			if (withAnswer)
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
	
	public boolean registerClientToGroup(Session session, int group_id) {
		if (id2group.containsKey(group_id)) {
			Group g = id2group.get(group_id);
			if (!g.isReady()) {
				g.registerSessions(session);
				return true;
			}
		}
		return false;
	}
	
	public boolean createGroup(Session session, int nb_player_per_game, int nb_games) {
		List<Session> sessions = new ArrayList<>();
		sessions.add(session);

		Group group = new Group(current_group_id, session, nb_player_per_game, nb_games);
		id2group.put(current_group_id, group);
		
		try {
			session.send(Orders.serverGroupRegistered(current_group_id));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return false;
		}
		current_group_id++;
		return true;
	}
	
	public boolean freeDataTransmitter(int gameId, int[] data) {
		if (id2game.containsKey(gameId)) {
			Game game = id2game.get(gameId);
			for (Session session : game.getSessions()) {
				try {
					session.send(Orders.serverFreeData(gameId, data));
				} catch (IOException e) {
					System.err.println(e.getMessage());
					return false;
				}
			}
			return true;
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
