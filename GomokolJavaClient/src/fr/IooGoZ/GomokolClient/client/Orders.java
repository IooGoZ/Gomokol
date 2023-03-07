package fr.IooGoZ.GomokolClient.client;

import fr.IooGoZ.GomokolClient.DontUseOutsideAPI;

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
	
	@DontUseOutsideAPI
	public int getId() {
		return this.id;
	}
	
	@DontUseOutsideAPI
	public static int[] clientInitGame(int order) {
		return new int[] {C_INIT_GAME.getId(), order};
	}
	
	@DontUseOutsideAPI
	public static int[] clientStartGame(int gameId) {
		return new int[] {C_START_GAME.getId(), gameId};
	}
	
	@DontUseOutsideAPI
	public static int[] clientEmitStroke(int gameId, int playerId, int[] stroke) {
		int[] msg = new int[4+stroke.length];
		msg[0] = C_EMIT_STROKE.getId();
		msg[1] = gameId;
		msg[2] = playerId;
		msg[3] = stroke.length;
		for (int i = 0; i < stroke.length; i++)
			msg[4 + i] = stroke[i];
		return msg;
	}
	
	@DontUseOutsideAPI
	public static int[] clientAnswerValidation(int gameId, int validation) {
		return new int[] {C_ANSWER_VALIDATION.getId(), gameId, validation};
	}
	
	@DontUseOutsideAPI
	public static int[] clientRegisterPlayer(int gameId) {
		return new int[] {C_REGISTER_PLAYER.getId(), gameId};
	}
	
}
