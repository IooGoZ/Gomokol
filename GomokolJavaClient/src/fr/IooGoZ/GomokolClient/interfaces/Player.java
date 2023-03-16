package fr.IooGoZ.GomokolClient.interfaces;

/**
 * @author IooGoZ - Tom BOIREAU
 * Représente un joueur d'une partie
 * Les joueurs doivent implémentés cette interface pour être reconnus par une partie.
 * Une partie par joueur.
 * On enregistre un joueur avec la méthode 'registerNewPlayer' de la classe Game.
 */
public interface Player {

	public void setId(int id);
	public int getId();
	/**
	 * @return Le coup joué par le joueur.
	 * Est appelé pour demander au joueur le coup qu'il souhaite joué.
	 */
	public int[] getStroke();

	/**
	 @param player Identifiant du joueur courant
	 * @param stroke Coup joué par ledit joueur
	 * 
	 * Permet d'enregistrer un coup joué par un autre joueur. 
	 */
	public void receiveNewStroke(int player_id, int[] stroke);

}
