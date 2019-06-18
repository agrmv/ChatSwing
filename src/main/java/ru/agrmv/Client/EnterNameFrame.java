package ru.agrmv.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Класс, который описывает окно для ввода имени
 * @author Alekse Gromov
 */

class EnterNameFrame {
    /**
     * Поле для ввода имени
     */
    private JTextField jtfName;
    private String clientName;

    EnterNameFrame() {
        initEnterNameFrame();
    }

    private void initEnterNameFrame() {
        JFrame enterNameFrame = new JFrame("Enter your name");
        enterNameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        /*Получаем размер экрана*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        enterNameFrame.setBounds(screenSize.width / 2 - 100, screenSize.height / 2 - 35, 200, 70);
        enterNameFrame.setResizable(false);

        JPanel bottomPanel = new JPanel();
        enterNameFrame.add(bottomPanel);
        jtfName = new JTextField(10);
        bottomPanel.add(jtfName);

        JButton btnOk = new JButton("Ok");
        bottomPanel.add(btnOk);

        /*Обработчик события нажатия на enter.*/
        jtfName.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!jtfName.getText().trim().isEmpty()) {
                        btnOk.doClick();
                    }
                }
            }
        });

        /*Обработчик фокуса для поля ввода имени.*/
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });

        /*Обработчик события нажатия на кнопку Ok.*/
        btnOk.addActionListener(e -> {
            if (!jtfName.getText().trim().isEmpty()) {
                clientName = jtfName.getText();
                new Client(clientName);
                enterNameFrame.dispose();
            }
        });
        enterNameFrame.setVisible(true);
    }
}