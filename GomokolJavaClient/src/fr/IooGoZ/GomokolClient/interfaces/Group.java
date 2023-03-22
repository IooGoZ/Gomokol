package fr.IooGoZ.GomokolClient.interfaces;

public abstract class Group {

	private final int order; 
	
	public Group(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	public abstract void autoGameSubscriber(int game_id);
	
}
