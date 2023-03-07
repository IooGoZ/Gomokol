package fr.IooGoZ.GomokolClient.interfaces;

public enum Validation {
	CAVOK(0),
	ENDGAME(1),
	CHEATING(2);

	private final int value;
	
	Validation(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
