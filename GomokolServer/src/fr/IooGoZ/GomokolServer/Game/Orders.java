package fr.IooGoZ.GomokolServer.Game;

public enum Orders {
	//ClientOrders (C2S)
	
	//ServerOrder (S2C)
	S_REQUEST_STROKE(1)
	;
	
	private static final char ARGS_SEPERATOR = '^';
	
	private final int id;
	
	private Orders(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static String serverRequestStroke(int gameId, int playerId) {
		StringBuilder sb = new StringBuilder();
		sb.append(S_REQUEST_STROKE.getId());
		sb.append(ARGS_SEPERATOR);
		sb.append(gameId);
		sb.append(ARGS_SEPERATOR);
		sb.append(playerId);
		return sb.toString();
	}

}
