package fr.IooGoZ.GomokolServer.Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.IooGoZ.GomokolServer.Server.Orders;
import fr.IooGoZ.GomokolServer.Server.Session;

public class Group {

	private final Session owner;
	private final List<Session> sessions;
	private final List<Game> games;
	private final HashMap<Session, Integer> scores;
	private int score_draw = 0;
	private final int id;
	private final int nbPlayers;
	private final int nbGames;
	private int order;
	private boolean isReady;
	private int countGame = 0;
	
	public Group(int id, Session owner, int nbPlayers, int nbGames) {
		this.id = id;
		this.sessions = new ArrayList<>();
		this.games = new ArrayList<>();
		this.scores = new HashMap<>();
		this.owner = owner;
		this.nbGames = nbGames;
		this.nbPlayers = nbPlayers;
		
		this.isReady = false;
		
		registerSessions(owner);
	}
	
	public void registerGame(Game game) {
		games.add(game);
		this.order = game.getOrder();
		
		
		
			try {
				if (countGame != 0) owner.send(Orders.serverNewGroupGame(id, game.getId()));
				for (Session session : sessions)
					if (!(session == owner)) {
						session.send(Orders.serverNewGroupGame(id, game.getId()));
					}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		
		if (countGame == 0)
			this.isReady = true;
		countGame++;
		
	}
	
	public boolean isValidSession(Session sess) {
		return sessions.contains(sess);
	}
	
	public void registerSessions(Session session) {
		sessions.add(session);
		scores.put(session, 0);
	}

	public int getNbPlayers() {
		return nbPlayers;
	}

	public int getNbGames() {
		return nbGames;
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void addWinner(Session sess) {
		if (sess == null) {
			this.score_draw++;
		} else scores.replace(sess, scores.get(sess)+1);
		
	}
	
	public void restartIfPossible() {
		if (countGame < nbGames) {
			GamesManager.MANAGER.createGame(owner, id, order, false);
			this.isReady = true;
		} else {
			printScores();
			this.isReady = false;
		}
			
	}
	
	private void printScores() {
		System.out.println("=============Score du groupe : " + this.id + "=============");
		System.out.println("Nombre de parties : " + this.countGame);
		System.out.println("Nombre de nulles : " + this.score_draw);
		for (Session sess : sessions) {
			System.out.println("Score de la sesion " + sess.getServer().getInetAddress() + " : " + this.scores.getOrDefault(sess, null));
		}
			
	}
	
}
