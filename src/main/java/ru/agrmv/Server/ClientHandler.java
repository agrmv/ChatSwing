package ru.agrmv.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Класс, в котором обрабатывается подключение клиента к серверу
 * @author Алексей Громов
 * @version 1.0.1
 * */

class ClientHandler implements Runnable {

    private PrintWriter outMessage;
    private Scanner inMessage;
    private Server server;

    ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Переопределяем метод run(), который вызывается когда
     * мы вызываем new Thread(client).start();
     * */
    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToAllClients("New member has entered!");
                break;
            }
            while (true) {
                /**Если от клиента пришло сообщение*/
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    /**Отправляем сообщение всем клиентам*/
                    server.sendMessageToAllClients(clientMessage);
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            this.close();
        }
    }
    void sendMessage(String message) {
        try {
            outMessage.println(message);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void close() {
        server.removeClient(this);
    }
}
