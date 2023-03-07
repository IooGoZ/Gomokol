package fr.IooGoZ.GomokolClient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import fr.IooGoZ.GomokolClient.client.Client;
import fr.IooGoZ.GomokolClient.client.Orders;
import fr.IooGoZ.GomokolClient.interfaces.GameOwner;
import fr.IooGoZ.GomokolClient.interfaces.Validation;

public class GamesManager {
	
	public static final GamesManager MANAGER = new GamesManager();
	
	private Client client;
	
	private int game_id;
	
	private HashMap<Integer, Game> id2game;
	private HashMap<Integer, GameOwner> id2owner;
	
	private GamesManager() {
		game_id = Client.DEFAULT_VALUE;
		id2game = new HashMap<>();
		id2owner = new HashMap<>();
	}
	
	public void connect(String adress, int port) throws UnknownHostException, IOException, Exception {
		if (this.client == null)
			this.client = new Client(adress, port, this);
		else throw new Exception("GameManager : Client is already connected.");
	}
	
	
	@DontUseOutsideAPI
	public Client getClient(String function) throws Exception {
		if (this.client != null)
			return this.client;
		throw new Exception(function + " : Client is not connected.");
	}
	
	//Call Outside API
	
		//GameOwner--------------------------------------------------------
	public synchronized Game initNewGame(GameOwner owner, int order) throws Exception {
		this.getClient("initNewGame").send(Orders.clientInitGame(order));
		
		this.game_id = Client.DEFAULT_VALUE;
		
		long time = System.currentTimeMillis();
		while (this.game_id == Client.DEFAULT_VALUE);
			if (System.currentTimeMillis() - time > Client.TIMEOUT_DURATION)
				throw new Exception("initNewGame : Timeout server");
		
		Game game = new Game(this, this.game_id, order);
		
		id2game.put(this.game_id, game);
		id2owner.put(this.game_id, owner);
		
		this.game_id = Client.DEFAULT_VALUE;
		
		return game;
	}
	
	public synchronized void startGame(GameOwner owner, int game_id) throws Exception {
		if (id2owner.getOrDefault(game_id, null) == owner) {
			this.getClient("startGame").send(Orders.clientStartGame(game_id));
			return;
		}
		throw new Exception("startGame : owner and id must match.");
	}
	
	//DontUseOutsideAPI---------------------------------------------------
	
	@DontUseOutsideAPI
	public boolean serverPlayerRegistered(int game_id, int player_id) {
		Game game = id2game.getOrDefault(game_id, null);
		if (game != null) {
			game.serverSetPlayerId(player_id);
			return true;
		}
		System.err.println("serverPlayerRegistered : server request bad game.");
		return false;
	}
	
	@DontUseOutsideAPI
	public boolean serverRequestStroke(int game_id, int player_id) {
		Game game = id2game.getOrDefault(game_id, null);
		if (game != null)
			try {
				game.serverRequestPlayerStroke(player_id);
				return true;
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		return false;
	}
	
	@DontUseOutsideAPI
	public boolean serverRequestValidation(int game_id, int player_id, int[] stroke) {
		GameOwner owner = id2owner.getOrDefault(game_id, null);
		if (owner != null)
			try {
				Validation valid = owner.getValidation(player_id, stroke);
				getClient("serverRequestValidation").send(Orders.clientAnswerValidation(game_id, valid.getValue()));
				return true;
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		return false;
	}
	
	@DontUseOutsideAPI
	public boolean serverSendStroke(int game_id, int player_id, int[] stroke) {
		Game game = id2game.getOrDefault(game_id, null);
		if (game != null)
			try {
				game.serverSendStroke(player_id, stroke);
				return true;
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		return false;
	}
	
	@DontUseOutsideAPI
	public void serverSetGameId(int game_id) {
		this.game_id = game_id;
	}
}
