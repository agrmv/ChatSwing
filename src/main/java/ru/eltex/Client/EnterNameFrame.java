package ru.eltex.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Класс, который описывает окно для ввода имени
 * @author Alekse Gromov
 * @version 1.0.2
 * */

class EnterNameFrame extends JFrame {
    /**Поле для ввода имени*/
    private JTextField jtfName;
    private String clientName;

    EnterNameFrame() {
        /**При нажатии на крестик окно закрывается*/
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        /**Получаем размер экрана*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        /**Устанавливаем положение окна по середине экрана*/
        setBounds(screenSize.width/ 2 - 100,screenSize.height / 2 - 35, 200, 70);
        setResizable(false);
        setTitle("Enter your name");

        JPanel bottomPanel = new JPanel();
        add(bottomPanel);
        jtfName = new JTextField("Enter your name...");
        bottomPanel.add(jtfName);
        /**
         * Обработчик фокуса для поля ввода имени.
         * При фокусе поле очищается.
         * */
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });

        /**Обработчик события нажатия на кнопку Ok.*/
        JButton btnOk = new JButton("Ok");
        bottomPanel.add(btnOk);
        btnOk.addActionListener(e -> {
            if (!jtfName.getText().trim().isEmpty()) {
                clientName = jtfName.getText();
                new Client(clientName);
                dispose();
            }
        });
        setVisible(true);
    }
}