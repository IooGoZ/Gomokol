package fr.IooGoZ.GomokolClient.interfaces;

public interface Player {
	
	public int[] getStroke();
	
	public void receiveNewStroke(Player player, int[] stroke);

}
