package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A chat server. Clients can connect and send messages to the server, the
 * server will send the message to all clients connected.
 * 
 * @Jaspervl
 */
public class AplServer {
	private static final int PORT = 7777;
	private static ArrayList<ClientSession> sessions = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		try (ServerSocket serverSocket = new ServerSocket(PORT);) {
			System.out.println("Server initiated");

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("Client connected");

				ClientSession session = new ClientSession(socket);
				sessions.add(session);
				session.start();
			}
		}
	}
	
	/**
	 * Gets all the current logged in users
	 * @return a formated string with all the current logged in users (login order / not alphabetical)
	 */
	public static String getNicks(){
		String str = "Currently logged in users : ";
		for(ClientSession a : sessions){
			if(a.getNickname() != null){
				str += "\n" + a.getNickname();
			}
		}
		return str;
		
	}

	/**
	 * Sends a message to all logged in users or a private message to the target. Depending whether the second paramter is null or not
	 * @param input the message
	 * @param target null for all,a legit value to message the recipient
	 * @param sender if finding the appropiate target fails,the system will notify the user
	 */
	public static void sendMessage(String input, String target, OutputStream sender) {
		if (target == null) {
			// iterate over the sessions
			for (ClientSession a : sessions) {
				try {
					// Message the user
					PrintWriter writer = new PrintWriter(a.getSocket().getOutputStream());
					writer.println(input);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			ClientSession session = getTarget(target);
			if (session != null) {
				try {
					PrintWriter writer = new PrintWriter(session.getSocket().getOutputStream());
					writer.println(input);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				PrintWriter writer = new PrintWriter(sender);
				writer.println("Sending failed");
				writer.flush();
			}
		}
	}

	/**
	 * Gets the corresponding clientsession belonging to the person with the parameter name
	 * @param target Nickname of the person
	 * @return Clientsession that has the parameter name or null if no one was found
	 */
	private static ClientSession getTarget(String target) {
		for (ClientSession a : sessions) {
			if (a.getNickname() != null && a.getNickname().equals(target)) {
				return a;
			}
		}
		return null;

	}
	
	/**
	 * Verifying method to look whether someone exists in the list of clientsessions (See getTarget)
	 * @param target Name of the recipient
	 * @return TRUE = target exists  ||| FALSE = target doesnt exist
	 */
	public static boolean targetExist(String target) {
		return getTarget(target) != null;
	}

}
