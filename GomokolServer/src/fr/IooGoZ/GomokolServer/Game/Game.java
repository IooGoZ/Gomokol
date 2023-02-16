package fr.IooGoZ.GomokolServer.Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.IooGoZ.GomokolServer.Server.Session;

public class Game implements Runnable {

	private static final int MINIMUM_PLAYER = 2;
	private static final long TIMEOUT_DURATION = 3000l;
	
	private final LinkedList<Player> players = new LinkedList<>();
	private final List<Session> sessions = new ArrayList<>();
	private final List<int[]> strokes = new ArrayList<>();
	private final int id;
	private final int order;
	private final Session owner;
	private final GamesManager manager;
	
	private int current_player_id = 0;
	private boolean is_running = false;
	private Player waiting_player = null;
	private int[] last_stroke = null;
	
	
	public Game(GamesManager manager, int id, int order, Session owner) {
		this.owner = owner;
		this.id = id;
		this.order = order;
		this.manager = manager;
	}
	
	public int getId() {
		return this.id;
	}
	
	public synchronized int addPlayer(Session session) throws Exception {
		if (!is_running) {
			if (!this.owner.equals(session) && !sessions.contains(session))
				sessions.add(session);
			this.current_player_id++;
			players.add(new Player(this, this.current_player_id, session));
			return this.current_player_id;
		}
		throw new Exception("Game->addPlayer : game is already runned.");
	}
	
	public boolean playStroke(Session session, int player_id, int[] stroke) {
		if (this.waiting_player != null || stroke.length != this.order) {
			if (this.waiting_player.getId() == player_id && this.waiting_player.isValidSession(session)) {
				if (!this.strokes.contains(stroke)) {
					this.strokes.add(stroke);
					this.waiting_player = null;
					this.last_stroke = stroke;
					return true;
				}
			}
		}
		return false;
	}

	
	public synchronized boolean start() {
		if (!this.is_running && this.players.size() >= MINIMUM_PLAYER) {
			this.is_running = true;
			Thread t = new Thread(this);
			t.start();
		}
		return false;
	}

	@Deprecated
	@Override
	public void run() {
		while (this.is_running) {
			for (Player player : players) {
				this.last_stroke = null;
				this.waiting_player = player;
				try {
					player.getSession().send(Orders.serverRequestStroke(this.id, player.getId()));
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				long time = System.currentTimeMillis();
				while (this.last_stroke == null) {
					if (System.currentTimeMillis() - time > TIMEOUT_DURATION) {
						System.err.println("Player " + this.id + " " + player.getId() + " : time out !" );
						this.manager.destroyGame(this);
						return;
					}
				}
				
			}
			
		}
		
		this.manager.destroyGame(this);
	}
	
	
	
}
