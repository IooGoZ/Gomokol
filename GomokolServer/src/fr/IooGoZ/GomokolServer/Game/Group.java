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
		
		
		for (Session session : sessions)
			try {
				
				if (!(countGame == 0 && session == owner)) {
					session.send(Orders.serverNewGroupGame(id, game.getId()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
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
		scores.replace(sess, scores.get(sess)+1);
		
	}
	
	public void restartIfPossible() {
		if (countGame < nbGames) {
			GamesManager.MANAGER.createGame(owner, id, order, false);
			this.isReady = true;
		} else this.isReady = false;
			
	}
	
}
