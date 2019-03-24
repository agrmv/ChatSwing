package ru.eltex.Client;

import ru.eltex.DataBase.MessageDB;
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
 * @version 1.0.4
 * */

class Client extends JFrame {
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

    private MessageDB db = new MessageDB();

    Client(String clientName) {
        this.clientName = clientName;
        try  {
            /**Подключаемся к серверу*/
            clientSocket = new Socket(host, port);

            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());

            /**Отрисовываем окно чата*/
            drawClientFrame();

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
                }
            }).start();

            /**Обработчик события закрытия клиентского окна*/
            addWindowListener(new WindowAdapter() {
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
            setVisible(true);

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
        db.addToDB(getTime(), getClientName(), jtfMessage.getText());
        outMessage.flush();
        jtfMessage.setText("");
    }

    /**Отрисовка интерфеса чата*/
    private void drawClientFrame() throws IOException {
        /**При нажатии на крестик окно закрывается*/
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        /**Получаем размер экрана*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        /**Устанавливаем положение окна по середине экрана*/
        setBounds(screenSize.width/ 2 - 400,screenSize.height / 2 - 250, 800, 500);
        setResizable(false);
        setTitle("Telegram");
        setIconImage(ImageIO.read(new File("src/main/resources/telegramIcon.png")));

        jtfMessage = new JTextField();
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);

        JScrollPane jspAreaMessage = new JScrollPane(jtaTextAreaMessage);
        add(jspAreaMessage, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        jtfMessage = new JTextField("Enter your message...");
        bottomPanel.add(jtfMessage);

        JButton btnSend = new JButton("Send");
        bottomPanel.add(btnSend);

        JButton btnHistory = new JButton("History");
        bottomPanel.add(btnHistory);

        /**Обработчик события нажатия на кнопку отправить*/
        btnSend.addActionListener(e -> {
            if (!jtfMessage.getText().trim().isEmpty()) {
                sendMessage();
                jtfMessage.grabFocus();
            }
        });

        /**Обработчик события нажатия на кнопку отправить*/
        btnHistory.addActionListener(e -> {
            db.showDB();
        });

        /**Обработчик события нажатия на enter.*/
        jtfMessage.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    btnSend.doClick();
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
    }
}