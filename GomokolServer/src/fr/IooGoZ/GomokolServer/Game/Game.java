package fr.IooGoZ.GomokolServer.Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import fr.IooGoZ.GomokolServer.Server.Orders;
import fr.IooGoZ.GomokolServer.Server.Session;

public class Game implements Runnable {

	//Minimum de joueur qu'une partie doit contenir avant son lancement
	private static final int MINIMUM_PLAYER = 2;
	
	//Temps sans réponse avant kick
	private static final long TIMEOUT_DURATION = 3000l;
	
	//Liste des joueurs présents
	private final LinkedList<Player> players = new LinkedList<>();
	
	//Liste des sessions actives
	private final List<Session> sessions = new ArrayList<>();
	
	//List des coups joués
	private final List<int[]> strokes = new ArrayList<>();
	
	//Id de la partie
	private final int id;
	
	//Ordre de la partie
	private final int order;
	
	
	//Propriétaire de la partie
	private final Session owner;
	
	//Manager gérant la partie
	private final GamesManager manager;
	
	//Thread de la partie courante
	private final Thread thread;
	
	
	//Id du prochain joueur enregistrable 
	private int current_player_id = 0;
	
	//Partie lancée ?
	private boolean is_running = false;
	
	//Attente d'un coup d'un joueur
	private Player waiting_player = null;
	private int[] last_stroke = null;
	
	//Validation du coup joué
	private Integer validation = -1;
	
	private final Group group;
	
	
	//Appelé par le Manager, ne pas faire d'appel en dehors de la partie
	public Game(GamesManager manager, Group group, int id, int order, Session owner) {
		this.owner = owner;
		this.group = group;
		this.id = id;
		this.order = order;
		this.manager = manager;
		this.thread = new Thread(this);
		if (group != null)
			group.registerGame(this);
	}
	
	//Retourne l'id de la partie courante
	public int getId() {
		return this.id;
	}
	
	
	//Fonctions appelée par le Parser-----------------------------------------------------------------------------
	
	//Demande l'ajout d'un joueur
	public synchronized int addPlayer(Session session) throws Exception {
		if (!is_running) {
			if (group != null) {
				if (group.isValidSession(session)) {
					
					if (!this.owner.equals(session) && !sessions.contains(session))
						sessions.add(session);
					this.current_player_id++;
					players.add(new Player(this, this.current_player_id, session));
					if (group.isReady() && group.getNbPlayers() == players.size())
						start(owner);
				} else throw new Exception("Game->addPlayer : session is not registered to group.");
			} else {
				if (!this.owner.equals(session) && !sessions.contains(session))
					sessions.add(session);
				this.current_player_id++;
				players.add(new Player(this, this.current_player_id, session));
			}
			
			return this.current_player_id;
		}
		throw new Exception("Game->addPlayer : game is already runned.");
	}
	
	//Tentative de jouer un coup
	public boolean playStroke(Session session, int player_id, int[] stroke) {
		if (this.waiting_player != null || stroke.length != this.order) {
			if (this.waiting_player.getId() == player_id && this.waiting_player.isValidSession(session)) {
				if (!this.strokes.contains(stroke)) {
					this.last_stroke = stroke;
					return true;
				}
			}
		}
		return false;
	}
	
	//Validation par l'owner d'un coup
	public boolean ownerValidation(Session session, int validation) {
		if (this.owner.equals(session) && this.validation == -1 && validation >= 0 && validation <= 3) {
			this.validation = validation;
			return true;
		}
		return false;
	}

	
	//Lancement de la partie
	public synchronized boolean start(Session session) {
		if (this.owner.equals(session) && !this.is_running && this.players.size() >= MINIMUM_PLAYER) {
			Collections.shuffle(players, new Random(System.currentTimeMillis()));
			
			this.is_running = true;
			this.thread.start();
			return true;
		}
		return false;
	}
	
	public List<Session> getSessions() {
		return sessions;
	}
	
	public int getNbPlayers() {
		return players.size();
	}
	
	public Session getOwner() {
		return owner;
	}

	//Fonction run du Runnable, ne pas utiliser en dehors
	@Deprecated
	@Override
	public void run() {
		//On lance la boucle
		while (this.is_running) {
			
			//A chaque tour, on interroge l'ensemble des joeurs
			for (Player player : players) {
				try {
					this.waiting_player = player;
					
					//On reset les variables.
					this.last_stroke = null;
					this.validation = -1;
					
					
					//On envoie la requete au joueurs concernés
					player.getSession().send(Orders.serverRequestStroke(this.id, player.getId()));
					
					long time = System.currentTimeMillis();
					
					//On attend sa réponse, attention delay
					while (this.last_stroke == null) {
						if (System.currentTimeMillis() - time > TIMEOUT_DURATION) {
							System.err.println("Player " + this.id + " " + player.getId() + " : time out !" );
							this.manager.destroyGame(this);
							return;
						}
						Thread.yield();
					}
					
					//Une fois le coup récupéré, on l'envoie pour la vérification
					this.owner.send(Orders.serverRequestValidation(this.id, player.getId(), this.last_stroke));
					time = System.currentTimeMillis();
					
					//On attend la réponse de l'owner, attention delay
					while (this.validation == -1) {
						if (System.currentTimeMillis() - time > TIMEOUT_DURATION) {
							System.err.println("Owner : time out !" );
							this.manager.destroyGame(this);
							return;
						}
						Thread.yield();
					}
					
					//En fonction de la validation
					switch (this.validation) {
						//Case 0 : On continue 
					
						//Fin de partie
						case 1 : 
							System.out.println("Player  " + player.getId() + " from the game " + this.id + " : won the game !" );
							
							this.sendToAll(Orders.serverEndGame(id, player.getId()));
							
							if (group!=null) group.addWinner(player.getSession());
							
							this.is_running = false; break;

						//Triche
						case 2 : 
							System.err.println("Player " + this.id + " " + player.getId() + " : cheat detected !" ); 
							
							this.sendToAll(Orders.serverEndGame(id, -1));
							
							this.manager.destroyGame(this);
							return;
							
						case 3 :
							System.out.println("The game " + this.id + " is draw.");
							
							this.sendToAll(Orders.serverEndGame(id, -2));
							
							if (group!=null) group.addWinner(null);
							
							this.is_running = false; break;

					}
					
					this.waiting_player = null;
					this.validation = -1;
					
					for (int i = 0; i < this.last_stroke.length; i++)
						this.last_stroke[i] = Math.abs(this.last_stroke[i]);
					
					//On envoie le résultat de la partie
					this.sendToAll(Orders.serverSendStroke(this.id, player.getId(), this.last_stroke));
				
					//Vérification de la fin de partie
					if (!is_running) break;
					
					this.is_running = this.is_running && player.getSession().isConnected();
					
				} catch (IOException e) {
					System.err.println(e.getMessage());
					break;
				}
			}
		}
		if (group!=null) group.restartIfPossible();
		this.manager.destroyGame(this);
	}
	
	//Envoie un message à toutes les sessionss de la partie
	private void sendToAll(int[] msg) throws IOException {
		this.owner.send(msg);
		for (Session sess : sessions)
			sess.send(msg);
	}
	
	//Détruit la partie courante
	public void destroy() {
		players.clear();
		sessions.clear();
		strokes.clear();
	}

	public Group getGroup() {
		return group;
	}
	
	public int getOrder() {
		return order;
	}

	
}
