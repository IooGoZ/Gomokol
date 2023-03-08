package fr.IooGoZ.GomokolClient.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.IooGoZ.GomokolClient.DontUseOutsideAPI;
import fr.IooGoZ.GomokolClient.GamesManager;


/**
 * @author IooGoZ - Tom BOIREAU
 * Ne pas utiliser.
 */
@DontUseOutsideAPI
public class Client extends Socket implements Runnable {

	/**
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public static final long TIMEOUT_DURATION = 3000l;

	/**
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public static final int DEFAULT_VALUE = -1;

	private final DataOutputStream outSt;
	private final Parser parser;

	/**
	 * @param address
	 * @param port
	 * @param manager
	 * @throws UnknownHostException
	 * @throws IOException
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public Client(String address, int port, GamesManager manager) throws UnknownHostException, IOException {
		super(address, port);
		this.parser = new Parser(super.getInputStream(), manager);
		this.outSt = new DataOutputStream(new BufferedOutputStream(super.getOutputStream()));
		System.out.println("[Client] - Connecté au serveur : " + getInetAddress() + ":" + getPort());
	}

	/**
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	@Override
	public void run() {
		while (super.isConnected())
			if (!this.parser.parse())
				break;
		System.out.println("[Client] - Déconnecté du serveur.");
	}

	/**
	 * @param msg
	 * @throws IOException
	 * Ne pas utiliser.
	 */
	@DontUseOutsideAPI
	public synchronized void send(int[] msg) throws IOException {
		StringBuilder build = new StringBuilder("[Client] - Message envoyé : ");
		for (int letter : msg) {
			outSt.writeInt(letter);
			build.append(letter).append("; ");
		}
		outSt.flush();
		System.out.println(build.toString());
	}
}