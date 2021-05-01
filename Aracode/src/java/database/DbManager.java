/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author mecme
 */
public class DbManager {

    public static final String database = "jdbc:mysql://localhost:3306";
    public static final String schema = "arabase";
    private static final String username = "root";
    private static final String password = "redmineteam18";
    protected Connection con;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //volitelne, ale servlet to automaticky neinicializuje!
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public DbManager() {
        newConnection();
    }

    /**
     *
     * @return new Connection to the database specified by static variables
     */
    public Connection newConnection() {
        try {
            this.con = DriverManager.getConnection(database + "/" + schema + "?serverTimezone=UTC", username, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return con;
    }

    /**
     *
     * @return current connection
     */
    public Connection getConnection() {
        return con;
    }

    /**
     * closes current connection
     */
    public void closeConnection() {
        try {
            this.con.close();
        } catch (SQLException ex) {
        }
    }
}
