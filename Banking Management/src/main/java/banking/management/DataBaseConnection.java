/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banking.management;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ramanan R
 * 
 * getConnection()
 * 
 */
public class DataBaseConnection {
    
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
 
        try (InputStream file = DataBaseConnection.class.getResourceAsStream("/config.properties")) {
 
            // load the properties file
            Properties props = new Properties();
            props.load(file);

            // assigning db parameters
            String driver = props.getProperty("driver");
            String url = props.getProperty("url");
            String user = props.getProperty("user");
            String password = props.getProperty("password");
           
            //loading jdbc driver
            try {             
                Class.forName(driver);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // creating connection
            conn = DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
