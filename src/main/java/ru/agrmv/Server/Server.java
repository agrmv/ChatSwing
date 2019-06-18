package ru.agrmv.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Класс, который описывает логику работы сервера
 * @author Алексей Громов
 */

class Server {

    /** Порт, который будет прослушивать наш сервер */
    private static final int port = 8080;

    /** Список клиентов подкюченных к серверу */
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    Server() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server start!");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                /*Cоздаём обработчик клиента, который подключился к серверу this - это наш сервер*/
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                /*Каждое подключение обрабатывается в новом потоке*/
                new Thread(client).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Отправляем сообщения всем клиентам
     */
    void sendMessageToAllClients(String message) {
        for (ClientHandler o : clients) {
            o.sendMessage(message);
        }
        System.out.println(message);

    }

    /**
     * Удаляем клиента из списка киентов
     */
    void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}