package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A thread that handles the users input and redirects it to the server. It has
 * information on the target (recipient of the messages)
 * 
 * @Jaspervl
 */
public class ClientSession extends Thread {

	/*
	 * Declaring instance variables
	 */
	private Socket socket;
	private String nickname;
	private String target;

	/**
	 * 
	 * @param socket socket containing the input/output stream of the connected client
	 */
	public ClientSession(Socket socket) {
		/*
		 * Initializing instance variables
		 */
		this.socket = socket;
		nickname = null;
		target = null;
	}

	/**
	 * Standard run method,this'll keep running while the clientsession is active. Any input will be handled here.
	 */
	public void run() {
		notifySelf("Welcome,please begin by selecting a nickname");
		try {
			Scanner in = new Scanner(socket.getInputStream());
			while (true) {

				if (in.hasNextLine()) {
					String input = in.nextLine();
					if (nickname == null) {
						String tempNick = input.split(" ")[0];
						if (AplServer.targetExist(tempNick)) {
							notifySelf(1);
							continue;
						}
						nickname = tempNick;
						notifySelf("Welcome " + nickname);
						notifySelf(2);
					}
					if (input.startsWith("/w")) {
						String[] arr = input.split(" ");
						if (arr.length >= 2) {
							if (AplServer.targetExist(arr[1])) {
								target = arr[1];
							} else {
								notifySelf(3);
							}
						} else {
							notifySelf(4);
						}
					} else if (input.startsWith("!CHANGENICK")) {
						String[] arr = input.split(" ");
						if (arr.length >= 2) {
							if (AplServer.targetExist(arr[1])) {
								notifySelf(5);
							} else {
								nickname = arr[1];
								notifySelf("Name succesfully changed, Welcome " + arr[1]);
							}
						} else {
							notifySelf(6);
						}
					} else if (input.startsWith("!SHOWUSERS")) {
						notifySelf(AplServer.getNicks());
					} else if (target != null) {
						AplServer.sendMessage(nickname + " : " + input, target, socket.getOutputStream());
					} else if (input.startsWith("!HELP")) {
						notifySelf(7);
					} else if (input.startsWith("!BROADCAST")) {
						String[] arr = input.split(" ");
						if (arr.length >= 2) {
							AplServer.sendMessage(nickname + " : " + input.substring(11), null, null);
						} else {
							notifySelf(8);
						}
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Method that takes in a string and sends it to the client side
	 * @param message The message
	 */
	private void notifySelf(String message) {
		try {

			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.print(message + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**=============> OVERLOADED<=============
	 * Method that takes in an int and sends it to the client side for interpretation.
	 * @param message statuscode to be interpreted
	 */
	private void notifySelf(int message) {
		try {

			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.print(message + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Method to help getting the nickname of the client
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Method to get the socket (just in case)
	 * @return the socket instance
	 */
	public Socket getSocket() {
		return socket;
	}
}