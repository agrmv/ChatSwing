package ru.eltex.DataBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

/**
 * Класс, который описывает логику работы базы данных
 * @author Алексей Громов
 * @version 1.0.5
 */
public class MessageDB {

    private static final String url = "jdbc:mysql://localhost:3306/messagehistory?autoReconnect=true&useSSL=false";
    private static final String user = "agrmv";
    private static final String password = "linux";

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private JTable messageHistoryTable;
    private JFrame dbFrame;
    private int messageCount;

    private void closeConnection() {
        try { connection.close(); } catch(SQLException sqlEx) { sqlEx.printStackTrace(); }
        try { statement.close(); } catch(SQLException sqlEx) { sqlEx.printStackTrace(); }
        try { resultSet.close(); } catch(SQLException sqlEx) { sqlEx.printStackTrace(); }
    }

    private int getMessageCount() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select MAX(Count) from messagehistory");
            while (resultSet.next()) {
                messageCount = resultSet.getInt(1);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
        return messageCount;
    }

    private void deleteDB() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeUpdate("TRUNCATE messagehistory");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    protected void addToDB(String time, String clientName, String message) {
        String insert = " insert into messagehistory (Count, Time, Name, Message)" + " values (?, ?, ?, ?)";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();

            /*Добавляем данные в таблицу*/
            PreparedStatement preparedStmt = connection.prepareStatement(insert);
            preparedStmt.setInt(1, getMessageCount() + 1);
            preparedStmt.setString(2, time);
            preparedStmt.setString(3, clientName);
            preparedStmt.setString(4, message);
            preparedStmt.execute();

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void initFrame() {
        dbFrame = new JFrame("Message History");
        dbFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dbFrame.setSize(500, 500);
        dbFrame.setResizable(false);
        dbFrame.setVisible(true);

        DefaultTableModel tableModel = new DefaultTableModel() {
            String[] columnName = {"Time", "Name", "Message"};

            @Override
            public int getColumnCount() {
                return columnName.length;
            }

            @Override
            public String getColumnName(int index) {
                return columnName[index];
            }

            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tableModel.setRowCount(getMessageCount());
        messageHistoryTable = new JTable(tableModel);
        messageHistoryTable.setBounds(20, 10, 500, 500);
        dbFrame.add(messageHistoryTable);

        dbFrame.add(new JScrollPane(messageHistoryTable));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        dbFrame.add(bottomPanel, BorderLayout.SOUTH);

        JButton btnDelete = new JButton("Delete");
        bottomPanel.add(btnDelete);

        /*Обработчик события нажатия на кнопку Delete.*/
        btnDelete.addActionListener(e -> {
            dbFrame.dispose();
            deleteDB();
            initFrame();
        });

        dbFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dbFrame = null;
            }
        });
    }

    protected void showDB() {
        if (dbFrame == null) {
            try {
                initFrame();
                connection = DriverManager.getConnection(url, user, password);
                statement = connection.createStatement();
                resultSet = statement.executeQuery("select  Time, Name, Message from messagehistory");
                while (resultSet.next()) {
                    messageHistoryTable.setValueAt(resultSet.getString("Time"), resultSet.getRow() - 1, 0);
                    messageHistoryTable.setValueAt(resultSet.getString("Name"), resultSet.getRow() - 1, 1);
                    messageHistoryTable.setValueAt(resultSet.getString("Message"), resultSet.getRow() - 1, 2);
                }
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            } finally {
                closeConnection();
            }
        }
    }
}