package net.kronos.rkon.core;

import java.io.*;
import java.net.*;

import static net.kronos.rkon.core.RconPacket.SERVER_DATA_AUTH;
import static net.kronos.rkon.core.RconPacket.SERVER_DATA_EXECUTE_COMMAND;

public class RconServer implements Runnable {
    
    private Socket socket;
    private String password;
    private IRconHandler handler;
    
    public RconServer(int port, String password, IRconHandler handler) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            new Thread(new RconServer(clientSocket, password, handler)).start();
                        } catch (IOException ignored) {}
                    }
                }
            }.run();
        }
    }
    
    private RconServer(Socket socket, String password, IRconHandler handler) {
        this.socket = socket;
        this.password = password;
        this.handler = handler;
    }
    
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
                        RconPacket.write(out, requestId, 2, "Authentication successful".getBytes());
                    } else {
                        RconPacket.write(out, requestId, 2, "Authentication failed".getBytes());
                        socket.close();
                        break;
                    }
                } else
                if (type == SERVER_DATA_EXECUTE_COMMAND) {
                    RconPacket.write(out, requestId, 0, "Placeholder Return Value".getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
