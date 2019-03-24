package ru.eltex.DataBase;

import javax.swing.*;
import java.sql.*;

/**
 * TODO LIST: refracroring !!!!
 * @author Алексей Громов
 */
public class MessageDB {

    private static final String url = "jdbc:mysql://localhost:3306/messagehistory?autoReconnect=true&useSSL=false";
    private static final String user = "agrmv";
    private static final String password = "linux";

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private JTable messageHistoryTable;
    private int messageCount = 1;

    public MessageDB() {}
    public void addToDB(String time, String clientName, String message) {
        String insert = " insert into messagehistory (Count, Time, Name, Message)" + " values (?, ?, ?, ?)";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();

            /**Добавляем данные в таблицу*/
            PreparedStatement preparedStmt = connection.prepareStatement(insert);
            preparedStmt.setInt(1, getMessageCount() + 1);
            preparedStmt.setString(2, time);
            preparedStmt.setString(3, clientName);
            preparedStmt.setString(4, message);
            preparedStmt.execute();

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    private int getMessageCount() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select MAX(Count) from messagehistory");
            while (resultSet.next()) {
                messageCount = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return messageCount;
    }

    private void initFrame() {
        messageHistoryTable = new JTable(getMessageCount(), 3);
        messageHistoryTable.setBounds(20, 10, 500, 500);

        JFrame frame = new JFrame("Message History");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.add(messageHistoryTable);
    }

    public void showDB() {
        try {
            initFrame();
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select  Time, Name, Message from messagehistory");
            int li_row = 0;
            while (resultSet.next()) {
                messageHistoryTable.setValueAt(resultSet.getString("Time"), li_row, 0);
                messageHistoryTable.setValueAt(resultSet.getString("Name"), li_row, 1);
                messageHistoryTable.setValueAt(resultSet.getString("Message"), li_row, 2);
                li_row++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
    }
}