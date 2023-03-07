package fr.IooGoZ.GomokolClient;

import java.io.IOException;
import java.util.HashMap;

import fr.IooGoZ.GomokolClient.client.Client;
import fr.IooGoZ.GomokolClient.client.Orders;
import fr.IooGoZ.GomokolClient.interfaces.Player;

public class Game {
	
	private final HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	private final int order;
	private final int id;
	private final GamesManager manager;
	
	private int player_id = Client.DEFAULT_VALUE;
	private HashMap<int[], Player> board = new HashMap<>();
	
	
	@DontUseOutsideAPI
	public Game(GamesManager manager, int id, int order) {
		this.id = id;
		this.order = order;
		this.manager = manager;
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public int getId() {
		return this.id;
	}
	
	public synchronized void registerNewPlayer(Player player) throws Exception {
		this.manager.getClient("registerNewPlayer").send(Orders.clientRegisterPlayer(id));
		
		player_id = Client.DEFAULT_VALUE;
		long time = System.currentTimeMillis();
		while (this.player_id == Client.DEFAULT_VALUE);
			if (System.currentTimeMillis() - time > Client.TIMEOUT_DURATION)
				throw new Exception("initNewGame : Timeout server");
		
		players.put(this.player_id, player);
		
		player_id = Client.DEFAULT_VALUE;
	}
	
	@DontUseOutsideAPI
	public synchronized void serverSetPlayerId(int player_id) {
		this.player_id = player_id;
	}
	
	@DontUseOutsideAPI
	public void serverRequestPlayerStroke(int player_id) throws IOException, Exception {
		Player player = players.getOrDefault(player_id, null);
		if (player != null) {
			int[] stroke = player.getStroke();
			this.manager.getClient("serverRequestPlayerStroke").send(Orders.clientEmitStroke(id, player_id, stroke));
			return;
		}
		throw new Exception("serverRequestPlayerStroke : server request bad player.");
	}
	
	@DontUseOutsideAPI
	public void serverSendStroke(int player_id, int[] stroke) throws Exception {
		Player player = players.getOrDefault(player_id, null);
		if (player != null) {
			board.put(stroke, player);
			for (int plId : players.keySet()) {
				players.get(plId).receiveNewStroke(player, stroke);
			}
			return;
		}
		throw new Exception("serverRequestPlayerStroke : server request bad player.");
	}
	
}
