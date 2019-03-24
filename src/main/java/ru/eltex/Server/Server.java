package ru.eltex.Server;

import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Класс, который описывает логику работы сервера
 * @author Алексей Громов
 * @version 1.0
 * */

class Server {
    private static final int PORT = 8080;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    Server() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start!");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                /** создаём обработчик клиента, который подключился к серверу this - это наш сервер*/
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                /**Каждое подключение обрабатывается в новом потоке*/
                new Thread(client).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**Отправляем сообщения всем клиентам*/
    void sendMessageToAllClients(String message) {
        for (ClientHandler o : clients) {
            o.sendMessage(message);
        }
        System.out.println(message);

    }

    /**Удаляем клиента из списка киентов*/
    void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}