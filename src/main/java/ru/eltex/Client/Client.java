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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

class Client extends JFrame {
    private static final String host = "localhost";
    private static final int port = 3443;
    private PrintWriter outMessage;
    private Scanner inMessage;

    private Socket clientSocket;

    private JTextField jtfMessage;
    private JTextArea jtaTextAreaMessage;

    private String clientName;

    Client(String clientName) {
        this.clientName = clientName;
        try  {
            clientSocket = new Socket(host, port);
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

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    private void sendMessage() {
        String messageStr = getTime() + "  " + getClientName() + ": " + jtfMessage.getText();
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }

    private void guiDraw() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(600, 300, 800, 500);
        setResizable(false);
        setTitle("Telegram");

        jtfMessage = new JTextField();
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);

        JScrollPane jspAreaMessage = new JScrollPane(jtaTextAreaMessage);
        add(jspAreaMessage, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3, 5, 5));
        add(bottomPanel, BorderLayout.SOUTH);

        jtfMessage = new JTextField("Enter your message...");
        bottomPanel.add(jtfMessage);

        JButton btnSend = new JButton("Send");
        bottomPanel.add(btnSend);

        JButton btnFile = new JButton("File");
        bottomPanel.add(btnFile);

        JButton btnHistory = new JButton("Chat History");
        bottomPanel.add(btnHistory);


        btnSend.addActionListener(e -> {
            if (!jtfMessage.getText().trim().isEmpty()) {
                sendMessage();
                jtfMessage.grabFocus();
            }
        });

        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
    }
}