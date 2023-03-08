package fr.IooGoZ.GomokolServer.Server;


//Générateur des messsages
public enum Orders {
	//ClientOrders (C2S)
	C_INIT_GAME(0),
	C_START_GAME(5),
	C_EMIT_STROKE(6),
	C_ANSWER_VALIDATION(7),
	C_REGISTER_PLAYER(8),
	//ServerOrder (S2C)
	
	S_REQUEST_STROKE(1),
	S_SEND_STROKE(2),
	S_REQUEST_VALIDATION(3),
	S_GAME_CREATED(4),
	S_PLAYER_REGISTERED(9),
	S_ERROR_REQUEST(10)
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
		int[] msg = {S_REQUEST_STROKE.getId(), gameId, playerId};
		return msg;
	}
	
	public static int[] serverSendStroke(int gameId, int playerId, int[] stroke) {
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
		int[] msg = {S_GAME_CREATED.getId(), gameId};
		return msg;
	}
	
	public static int[] serverPlayerRegistered(int gameId, int playerId) {
		int[] msg = {S_PLAYER_REGISTERED.getId(), gameId, playerId};
		return msg;
	}
	
	public static int[] serverErrorInRequest(int cmd) {
		int[] msg = {S_ERROR_REQUEST.getId(), cmd};
		return msg;
	}

}
