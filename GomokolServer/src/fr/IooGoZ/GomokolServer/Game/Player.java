package fr.IooGoZ.GomokolServer.Game;

import fr.IooGoZ.GomokolServer.Server.Session;

public class Player {
	
	private final Game game;
	private final int id;
	private final Session session;
	
	public Player(Game game, int id, Session session) {
		this.game = game;
		this.id = id;
		this.session = session;
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isValidSession(Session sess) {
		return sess.equals(this.session);
	}
	
	public Session getSession() {
		return this.session;
	}
	
	//Verification de la validité d'un joueur (Sécurité réseau et Anticheat)
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			Player pl = (Player) obj;
			return this.session.equals(pl.session) && this.id == pl.id && this.game.equals(pl.game);
		}
		return false;
	}
	
}
