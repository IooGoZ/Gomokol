package fr.IooGoZ.GomokolServer.Server;

import fr.IooGoZ.GomokolServer.Game.GamesManager;

//Générateur des messsages
public enum Orders {
	//ClientOrders (C2S)
	C_INIT_GAME(0),
	C_START_GAME(5),
	C_EMIT_STROKE(6),
	C_ANSWER_VALIDATION(7),
	C_REGISTER_PLAYER(8),
	C_SUBSCRIBE_GROUP(12),
	C_INIT_GROUP(14),
	C_FREE_DATA(16),
	
	//ServerOrder (S2C)
	
	S_REQUEST_STROKE(1),
	S_SEND_STROKE(2),
	S_REQUEST_VALIDATION(3),
	S_GAME_CREATED(4),
	S_PLAYER_REGISTERED(9),
	S_ERROR_REQUEST(10),
	
	S_END_GAME(11),
	S_NEW_GROUPGAME(13),
	S_GROUP_REGISTERED(15),
	S_FREE_DATA(17)
	;
	
	private final int id;
	
	private Orders(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	//----------------------------------------------------------------------------------------------
	
	public static int[] serverRequestStroke(int gameId, int playerId) {
		if (GamesManager.DEBUG) 
			System.out.println("[Orders] - Request Stroke (1) : game_id=" + gameId + ", player_id=" + playerId);
		int[] msg = {S_REQUEST_STROKE.getId(), gameId, playerId};
		return msg;
	}
	
	public static int[] serverSendStroke(int gameId, int playerId, int[] stroke) {
		
		if (GamesManager.DEBUG) {
			System.out.print("[Orders] - Send Stroke (2) : game_id=" + gameId + ", player_id=" + playerId + ", stroke=");
			for (int i : stroke) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		
		int[] msg = new int[4+stroke.length];
		msg[0] = S_SEND_STROKE.getId();
		msg[1] = gameId;
		msg[2] = playerId;
		msg[3] = stroke.length;
		for (int i = 0; i < stroke.length; i++)
			msg[4 + i] = stroke[i];
		return msg;
	}

	public static int[] serverRequestValidation(int gameId, int playerId, int[] stroke) {
		
		if (GamesManager.DEBUG) {
			System.out.print("[Orders] - Request Validation (3) : game_id=" + gameId + ", player_id=" + playerId + ", stroke=");
			for (int i : stroke) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		
		int[] msg = new int[4+stroke.length];
		msg[0] = S_REQUEST_VALIDATION.getId();
		msg[1] = gameId;
		msg[2] = playerId;
		msg[3] = stroke.length;
		for (int i = 0; i < stroke.length; i++)
			msg[4 + i] = stroke[i];
		return msg;
	}
	
	public static int[] serverGameCreated(int gameId) {
		if (GamesManager.DEBUG) 
			System.out.println("[Orders] - Game Created (4) : game_id=" + gameId);
		
		int[] msg = {S_GAME_CREATED.getId(), gameId};
		return msg;
	}
	
	public static int[] serverPlayerRegistered(int gameId, int playerId) {
		
		if (GamesManager.DEBUG) 
			System.out.println("[Orders] - Player Registered (9) : game_id=" + gameId + ", player_id=" + playerId);
		
		int[] msg = {S_PLAYER_REGISTERED.getId(), gameId, playerId};
		return msg;
	}
	
	public static int[] serverErrorInRequest(int cmd) {
		
		System.err.println("[Orders] - Error In Request (10) : command=" + cmd);
		
		int[] msg = {S_ERROR_REQUEST.getId(), cmd};
		return msg;
	}
	
	public static int[] serverEndGame(int gameId, int playerId) {
		
		if (GamesManager.DEBUG) 
			System.out.println("[Orders] - End Game (11) : game_id=" + gameId + ", player_id=" + playerId);
		
		return new int[] {S_END_GAME.getId(), gameId, playerId};
	}
	
	public static int[] serverNewGroupGame(int group_id, int gameId) {
		
		if (GamesManager.DEBUG) 
			System.out.println("[Orders] - New Group's Game (13) : group_id=" + group_id + ", game_id=" + gameId);
		
		return new int[] {S_NEW_GROUPGAME.getId(), group_id, gameId};
	}
	
	public static int[] serverGroupRegistered(int groupId) {
		
		if (GamesManager.DEBUG) 
			System.out.println("[Orders] - Group Registered (15) : group_id=" + groupId);
		
		return new int[] {S_GROUP_REGISTERED.getId(), groupId};
	}
	
	public static int[] serverFreeData(int gameId, int[] data) {
		
		if (GamesManager.DEBUG) {
			System.out.print("[Orders] - Free Data (17) : game_id=" + gameId + ", free_data=");
			for (int i : data) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		
		int[] msg = new int[3+data.length];
		msg[0] = S_FREE_DATA.getId();
		msg[1] = gameId;
		msg[2] = data.length;
		for (int i = 0; i < data.length; i++)
			msg[2 + i] = data[i];
		
		return msg;
	}

}
