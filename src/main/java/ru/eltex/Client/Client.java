package ru.eltex.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class Client extends JFrame {
    private static final String host = "localhost";
    private static final int port = 3443;
    private PrintWriter outMessage;
    private Scanner inMessage;

    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;

    private String clientName = "";

    Client() {
        try (Socket clientSocket = new Socket(host, port)) {
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
            guiDraw();
            new Thread(() -> {
                try {
                    while (true) {
                        if (inMessage.hasNext()) {
                            jtaTextAreaMessage.append(inMessage.nextLine() + "\n");
                        }
                    }
                } catch (Exception ex) {
                }
            }).start();

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    try {
                        if (!getClientName().isEmpty() && !getClientName().equals("Enter your name... ")) {
                            outMessage.println(getClientName() + " left the chat!");
                        } else {
                            outMessage.println("Member left the chat without introducing himself!");
                        }
                        outMessage.println("##session##end##");
                        outMessage.flush();
                        outMessage.close();
                        inMessage.close();
                        clientSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            setVisible(true);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public String getClientName() {
        return this.clientName;
    }

    private void sendMsg() {
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }

    private void guiDraw() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(600, 300, 600, 500);
        setResizable(false);
        setTitle("Client");

        jtfMessage = new JTextField();
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);

        JScrollPane jspAreaMessage = new JScrollPane(jtaTextAreaMessage);
        add(jspAreaMessage, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton btnSend = new JButton("Send");
        btnSend.setToolTipText("Broadcast a message");
        bottomPanel.add(btnSend, BorderLayout.EAST);

        jtfMessage = new JTextField("Enter your message...");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Enter your name...");
        bottomPanel.add(jtfName, BorderLayout.WEST);

        btnSend.addActionListener(e -> {
            if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                clientName = jtfName.getText();
                sendMsg();
                jtfMessage.grabFocus();
            }
        });

        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });
    }
}