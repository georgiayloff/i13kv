package de.tum.i13.server.singlethreadedandblocking;

import de.tum.i13.server.kv.KVStore;
import de.tum.i13.shared.Constants;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	/**
	 * The most boring server you could ever think of. Disconnects after each
	 * request.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final ServerSocket serverSocket = new ServerSocket();
		final KVStore kv = new KVStore();
		Integer port = 5558;

		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Closing single threaded and blocking kv server");
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// bind the socketserver only to localhost
		serverSocket.bind(new InetSocketAddress("127.0.0.1", port));

		while (true) {
			Socket clientSocket = serverSocket.accept();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), Constants.TELNET_ENCODING));
			PrintWriter out = new PrintWriter(
					new OutputStreamWriter(clientSocket.getOutputStream(), Constants.TELNET_ENCODING));

			String firstLine;
			while ((firstLine = in.readLine()) != null) {
				String res = kv.process(firstLine);
				out.write(res);
				out.flush();
			}
			clientSocket.close();
		}

	}
}
