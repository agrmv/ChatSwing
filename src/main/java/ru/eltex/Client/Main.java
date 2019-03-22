package ru.eltex.Client;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Main{
    public static class EnterName extends JFrame {
        private JTextField jtfName;
        private String clientName = null;

        EnterName() {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setBounds(600, 300, 200, 70);
            setResizable(false);
            setTitle("Telegram");

            JPanel bottomPanel = new JPanel();
            add(bottomPanel);

            /**костыль*/
            JTextField jtfName2 = new JTextField();
            bottomPanel.add(jtfName2);

            jtfName = new JTextField("Enter your name...");
            bottomPanel.add(jtfName);
            jtfName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    jtfName.setText("");
                }
            });

            JButton btnOk = new JButton("Ok");
            bottomPanel.add(btnOk);
            btnOk.addActionListener(e -> {
                clientName = jtfName.getText();
                new Client(clientName);
                dispose();
            });
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        new EnterName();
    }
}