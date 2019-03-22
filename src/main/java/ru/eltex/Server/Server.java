package ru.eltex.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Server {
    private static final int PORT = 3443;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    Server() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start!");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }

    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}