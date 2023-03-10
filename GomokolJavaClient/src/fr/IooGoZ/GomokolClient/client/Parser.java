package fr.IooGoZ.GomokolClient.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.IooGoZ.GomokolClient.DontUseOutsideAPI;
import fr.IooGoZ.GomokolClient.GamesManager;

/**
 * @author IooGoZ - Tom BOIREAU
 * Ne pas utiliser.
 */
@DontUseOutsideAPI
public class Parser {
	private final DataInputStream in;
	private final GamesManager manager;

	/**
	 * @param in
	 * @param manager
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public Parser(InputStream inSt, GamesManager manager) {
		this.in = new DataInputStream(new BufferedInputStream(inSt));
		this.manager = manager;
	}

	/**
	 * @return
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public synchronized boolean parse() {
		try {
			int order = readInt();
			System.out.println("[Parser] - Ordre  reçus : " + order);
			switch (order) {
			case 1:
				return serverRequestStroke();
			case 2:
				return serverSendStroke();
			case 3:
				return serverRequestValidation();
			case 4:
				return serverGameCreated();
			case 9:
				return serverPlayerRegistered();
			case 10:
				return serverErrorRequest();

			default:
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean serverRequestStroke() throws IOException {
		int game_id = readInt();
		int player_id = readInt();
		return manager.serverRequestStroke(game_id, player_id);
	}

	private boolean serverSendStroke() throws IOException {
		int game_id = readInt();
		int player_id = readInt();
		int[] stroke = readIntArray();

		return manager.serverSendStroke(game_id, player_id, stroke);
	}

	private boolean serverRequestValidation() throws IOException {
		int game_id = readInt();
		int player_id = readInt();
		int[] stroke = readIntArray();

		return manager.serverRequestValidation(game_id, player_id, stroke);
	}

	private boolean serverGameCreated() throws IOException {
		int game_id = readInt();
		manager.serverSetGameId(game_id);
		return true;
	}

	private boolean serverPlayerRegistered() throws IOException {
		int game_id = readInt();
		int player_id = readInt();
		return manager.serverPlayerRegistered(game_id, player_id);
	}

	private boolean serverErrorRequest() throws IOException {
		int order = readInt();
		System.err.println("Server Error : code=" + order);
		return false;
	}

	// Fonction ressource-----------------------------------------------------

	private int readInt() throws IOException {
		return in.readInt();
	}

	private int[] readIntArray() throws IOException {
		int len = readInt();
		int[] res = new int[len];
		for (int i = 0; i < len; i++) {
			res[i] = readInt();
		}
		return res;
	}

}
