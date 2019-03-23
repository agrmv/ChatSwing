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

    private static Connection connection;
    private static Statement statement;

    public MessageDB() {}

    public void addToDB(String time, String clientName, String message) {
        String query = " insert into messagehistory (Time, Name, Message)" + " values ?, ?, ?)";

        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();

            /**Добавляем данные в таблицу*/
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(2, time);
            preparedStmt.setString(3, clientName);
            preparedStmt.setString(4, message);
            preparedStmt.execute();

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try {
                connection.close();
                //statement.executeUpdate("TRUNCATE messagehistory");
            } catch (SQLException se) { /*can't do anything */ }
            try {
                statement.close();
            } catch (SQLException se) { /*can't do anything */ }
        }
    }

    public void showDB() {
        JTable mysTable;
        mysTable = new JTable(10, 4);
        mysTable.setBounds(20, 10, 300, 300);

        JFrame frame = new JFrame("Message History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.add(mysTable);

        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement stmts = con.createStatement();
            String query = "select  Time, Name, Message from messagehistory.messagehistory ";
            ResultSet rs = stmts.executeQuery(query);
            int li_row = 0;
            while (rs.next()) {
                mysTable.setValueAt(rs.getString("Time"), li_row, 0);
                mysTable.setValueAt(rs.getString("Name"), li_row, 1);
                mysTable.setValueAt(rs.getString("Message"), li_row, 2);
                li_row++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}