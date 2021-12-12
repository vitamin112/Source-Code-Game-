package server;

/**
 *
 * @author Dũng Trần
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager {

    protected static Connection conn;
    public static Statement stat;

    protected static synchronized void create(String host, String database, String user, String pass) {
        try {
            Class.forName("com.mysql.jdbc.Driver");  // Kiểm tra driver
        } catch (ClassNotFoundException e) {
            System.out.println("driver mysql not found!");
            System.exit(0);
        }
        String url = "jdbc:mysql://" + host + "/" + database + "?autoReconnect=true";
        System.out.println("Ket noi voi MySQL: " + url);
        try {
            conn = DriverManager.getConnection(url, user, pass);
            stat = conn.createStatement();
            System.out.println("... da ket noi thanh cong!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected static synchronized boolean close() {
        System.out.println("Dong, chuyen sang ket noi voi MySQL luu tru nguoi dung");
        try {
            if (stat != null) {
                stat.close();
            }
            if (conn != null) {
                conn.close();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
