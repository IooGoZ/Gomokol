package fr.IooGoZ.GomokolClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.IooGoZ.GomokolClient.client.Client;
import fr.IooGoZ.GomokolClient.client.Orders;
import fr.IooGoZ.GomokolClient.interfaces.Board;
import fr.IooGoZ.GomokolClient.interfaces.Player;



/**
 * @author IooGoZ - Tom BOIREAU
 * Représente une partie de Gomoku
 */
public class Game {

	private final HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	private final int order;
	private final int id;
	private final GamesManager manager;

	private int player_id = Client.DEFAULT_VALUE;
	private final HashMap<int[], Player> board = new HashMap<>();
	private final List<Board> boards = new ArrayList<>();

	
	/**
	 * @param manager
	 * @param id
	 * @param order
	 * 
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public Game(GamesManager manager, int id, int order) {
		this.id = id;
		this.order = order;
		this.manager = manager;
	}

	
	/**
	 * Retourne l'ordre des coordonées utilisées par la parties.
	 * @return L'ordre des coordonées utilisées par la parties.
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Retourne l'identifiant de la parties.
	 * @return L'identifiant de la parties.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * @param board Plateau de jeu à enregistrer
	 */
	public void registerNewBoard(Board board) {
		boards.add(board);
	}
	
	/**
	 * @param pos Position souhaitée
	 * @return null si la position n'existe pas.
	 * Permet de récuperer une position du plateau.
	 */
	public Player getPlayerAtPosition(int[] pos) {
		return board.getOrDefault(pos, null);
	}
	
	/**
	 * @param player Nouveau joueur a enregistrer
	 * @throws Exception Les exceptions sont déclenchées lors de problème de communication avec le serveur. 
	 * Demande l'enregistrement d'un nouveau joueur auprès du serveur. 
	 * 
	 */
	public synchronized void registerNewPlayer(Player player) throws Exception {
		this.manager.getClient("registerNewPlayer").send(Orders.clientRegisterPlayer(id));

		player_id = Client.DEFAULT_VALUE;
		long time = System.currentTimeMillis();
		while (this.player_id == Client.DEFAULT_VALUE)
			if (System.currentTimeMillis() - time > Client.TIMEOUT_DURATION)
				throw new Exception("initNewGame : Timeout server");

		players.put(this.player_id, player);

		player_id = Client.DEFAULT_VALUE;
	}

	
	/**
	 * @param player_id
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public void serverSetPlayerId(int player_id) {
		this.player_id = player_id;
	}

	/**
	 * @param player_id
	 * @throws IOException
	 * @throws Exception
	 * Ne pas utiliser.
	 */
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

	/**
	 * @param player_id
	 * @param stroke
	 * @throws Exception
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public void serverSendStroke(int player_id, int[] stroke) throws Exception {
		Player player = players.getOrDefault(player_id, null);
		if (player != null) {
			board.put(stroke, player);
			for (int plId : players.keySet()) {
				players.get(plId).receiveNewStroke(player, stroke);
			}
			for (Board b : boards) {
				b.addStrokeToBoard(player, stroke);
			}
			return;
		}
		throw new Exception("serverRequestPlayerStroke : server request bad player.");
	}

}
