package net.kronos.rkon.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import net.kronos.rkon.core.ex.AuthenticationException;

/** An RCON Protocol compatible client.
 * @see RconClient#RconClient(String, int, byte[], int)
 * @see RconClient#RconClient(String, int, byte[])
 * @see RconServer#RconServer(int, String, IRconHandler) */
public class RconClient {
	
	private final Object sync = new Object();
	private final Random rand = new Random();
	
	private int requestId;
	private Socket socket;

	/**
	 * Create, connect and authenticate a new RconClient object
	 * 
	 * @param host RconClient server address
	 * @param port RconClient server port
	 * @param password RconClient server password
	 * 
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public RconClient(String host, int port, byte[] password) throws IOException, AuthenticationException {
		this(host, port, password, 0);
	}

	/**
	 * Create, connect and authenticate a new RconClient object
	 *
	 * @param host RconClient server address
	 * @param port RconClient server port
	 * @param password RconClient server password
	 * @param timeout RconClient socket connection timeout
	 *
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public RconClient(String host, int port, byte[] password, int timeout) throws IOException, AuthenticationException {
		// Connect to host
		this.connect(host, port, password, timeout);
	}

	/**
	 * Connect to a rcon server
	 *
	 * @param host RconClient server address
	 * @param port RconClient server port
	 * @param password RconClient server password
	 *
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public void connect(String host, int port, byte[] password) throws IOException, AuthenticationException {
		connect(host, port, password, 0);
	}
	
	/**
	 * Connect to a rcon server
	 * 
	 * @param host RconClient server address
	 * @param port RconClient server port
	 * @param password RconClient server password
	 * @param timeout RconClient socket connection timeout
	 *
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public void connect(String host, int port, byte[] password, int timeout) throws IOException, AuthenticationException {
		if(host == null || host.trim().isEmpty()) {
			throw new IllegalArgumentException("Host can't be null or empty");
		}
		
		if(port < 1 || port > 65535) {
			throw new IllegalArgumentException("Port is out of range");
		}
		
		// Connect to the rcon server
		synchronized(sync) {
			// New random request id
			this.requestId = rand.nextInt();
			
			// We can't reuse a socket, so we need a new one
			this.socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), timeout);
		}
		
		// Send the auth packet
		RconPacket res = this.send(RconPacket.SERVER_DATA_AUTH, password);
		
		// Auth failed
		if(res.getRequestId() == -1) {
			throw new AuthenticationException("Password rejected by server");
		}
	}
	
	/**
	 * Disconnect from the current server
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		synchronized(sync) {
			this.socket.close();
		}
	}
	
	/**
	 * Send a command to the server
	 * 
	 * @param payload The command to send
	 * @return The payload of the response
	 * 
	 * @throws IOException
	 */
	public String command(String payload) throws IOException {
		if(payload == null || payload.trim().isEmpty()) {
			throw new IllegalArgumentException("Payload can't be null or empty");
		}
		
		RconPacket response = this.send(RconPacket.SERVER_DATA_EXECUTE_COMMAND, payload.getBytes());
		
		return new String(response.getPayload(), StandardCharsets.UTF_8);
	}
	
	private RconPacket send(int type, byte[] payload) throws IOException {
		synchronized(sync) {
			return RconPacket.send(this, type, payload);
		}
	}

	public int getRequestId() {
		return requestId;
	}
	
	public Socket getSocket() {
		return socket;
	}

}
