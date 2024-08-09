package net.kronos.rkon.core;

import java.io.*;
import java.net.*;

import static net.kronos.rkon.core.RconPacket.*;

/** An RCON Protocol compatible server.
 * @see RconServer#RconServer(int, String, IRconHandler)
 * @see RconClient#RconClient(String, int, String, int)
 * @see RconClient#RconClient(String, int, String) */
public class RconServer {
    
    /** Creates an RCON Server with the specified port, password, and command handler.
     * Automatically starts the server on a separate thread.
     * @param port Server Port
     * @param password Server Password
     * @param handler Command Handler
     * @see IRconHandler
     * @throws IOException Throws is the socket is interrupted, disconnected, or destroyed.
     * */
    public RconServer(int port, String password, IRconHandler handler) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        if (clientSocket != null) new Thread(new RconAcceptor(clientSocket, password, handler)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },"rcon-thread").start();
        }
    }
    
    /** An internal class for handling incoming requests. */
    public static class RconAcceptor implements Runnable {
        private final Socket socket;
        private final String password;
        private final IRconHandler handler;
        
        /** Creates an acceptor with the incoming socket. */
        public RconAcceptor(Socket socket, String password, IRconHandler handler) {
            this.socket = socket;
            this.password = password;
            this.handler = handler;
        }
    
        /** handles all the authentication logic, then passes the message onto the given {@link IRconHandler}. */
        public void run() {
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            
                // This should stay running until either the socket closes, or a bad auth is sent
                while (true) {
                    int length = in.readInt();
                    int requestId = in.readInt();
                    int type = in.readInt();
                    byte[] bodyBytes = new byte[length - 10];
                    in.readFully(bodyBytes);
                    String body = new String(bodyBytes).trim();
                
                    if (type == SERVER_DATA_AUTH) {
                        if (body.equals(password)) {
                            RconPacket.write(out, requestId, 2, "Authentication Successful".getBytes());
                        } else {
                            RconPacket.write(out, requestId, 2, "Authentication Failed".getBytes());
                            socket.close();
                            break;
                        }
                    } else
                    if (type == SERVER_DATA_EXECUTE_COMMAND) {
                        RconPacket.write(out, requestId, 0, handler.handle(body).getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
