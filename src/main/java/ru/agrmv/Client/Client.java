package ru.agrmv.Client;

import ru.agrmv.DataBase.MessageDB;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;


/**
 * Класс, который описывает логику работы клиента
 * @author Алексей Громов
 * @version 1.0.5
 * */

class Client extends MessageDB {
    private static final String host = "localhost";
    private static final int port = 8080;
    private PrintWriter outMessage;
    private Scanner inMessage;

    private Socket clientSocket;

    /**Поле для ввода сообщений*/
    private JTextField jtfMessage;
    /**Поле для вывода сообщений*/
    private JTextArea jtaTextAreaMessage;

    private String clientName;

    Client(String clientName) {
        this.clientName = clientName;
        try  {
            /**Подключаемся к серверу*/
            clientSocket = new Socket(host, port);

            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());

            /**Отрисовываем окно чата*/
            initClientFrame();

            /**Начинаем работу с сервером в отдельном потоке*/
            new Thread(() -> {
                try {
                    while (true) {
                        /**Если сообщение есть*/
                        if (inMessage.hasNext()) {
                            /**Считываем его и выводим**/
                            jtaTextAreaMessage.append(inMessage.nextLine() + "\n");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Возвращает имя клиента(отправителя сообщения)
     * @return имя клиента
     * */
    public String getClientName() {
        return this.clientName;
    }

    /**
     * Возвращает текущее время
     * @return текущее время
     * */
    public String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    /**Отправляет сообщения*/
    private void sendMessage() {
        outMessage.println(getTime() + "  " + getClientName() + ": " + jtfMessage.getText());
        addToDB(getTime(), getClientName(), jtfMessage.getText());
        outMessage.flush();
        jtfMessage.setText("");
    }

    /**Отрисовка интерфеса чата*/
    private void initClientFrame() throws IOException {
        /**Создаем окно чата*/
        JFrame clientFrame = new JFrame("Telegram");

        /**При нажатии на крестик окно закрывается*/
        clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /**Получаем размер экрана*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        /**Устанавливаем положение окна по середине экрана*/
        clientFrame.setBounds(screenSize.width/ 2 - 400,screenSize.height / 2 - 250, 800, 500);

        /**Запрещаем изменение размера*/
        clientFrame.setResizable(false);

        /**Устанавливаем иконку окна*/
        clientFrame.setIconImage(ImageIO.read(new File("src/main/resources/telegramIcon.png")));

        /**Создаем панель для размещения кнопок и полей*/
        JPanel bottomPanel = new JPanel(new BorderLayout());
        clientFrame.add(bottomPanel, BorderLayout.SOUTH);

        /**Создаем поле для ввода сообщения*/
        jtfMessage = new JTextField("Enter your message...");

        /**Размещаем поле на панели*/
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);

        /**Создаем поле для вывода сообщения*/
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);

        /**Добавляем скрол для поля вывода сообщений*/
        JScrollPane jspAreaMessage = new JScrollPane(jtaTextAreaMessage);
        clientFrame.add(jspAreaMessage, BorderLayout.CENTER);

        /**Создаем кнопку "History" и добавляем ее на панель*/
        JButton btnHistory = new JButton("History");
        bottomPanel.add(btnHistory, BorderLayout.EAST);

        /**Обработчик события нажатия на кнопку отправить*/
        btnHistory.addActionListener(e -> showDB());

        /**Обработчик события нажатия на enter.*/
        jtfMessage.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    if (!jtfMessage.getText().trim().isEmpty()) {
                        sendMessage();
                        jtfMessage.grabFocus();
                    }
                }
            }
        });

        /**
         * Обработчик фокуса для поля ввода сообщения.
         * При фокусе поле очищается.
         * */
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });

        /**Обработчик события закрытия клиентского окна*/
        clientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    if (!getClientName().isEmpty()) {
                        outMessage.println(getClientName() + " left the chat!");
                    } else {
                        outMessage.println("Member left the chat without introducing himself!");
                    }
                    /**Отправляем служебное сосбщение, завершающие текущую сессию*/
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

        /**Отображаем форму*/
        clientFrame.setVisible(true);
    }
}