package ru.eltex.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class ClientHandler implements Runnable {

    private PrintWriter outMessage;
    private Scanner inMessage;
    private Server server;

    ClientHandler(Socket socket, Server server) throws IOException {
        try {
            this.server = server;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToAllClients("New member has entered!");
                break;
            }
            while (true) {
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    System.out.println(clientMessage);
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
