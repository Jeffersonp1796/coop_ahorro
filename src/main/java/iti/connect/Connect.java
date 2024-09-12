package iti.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    public static Connection getConnection(){
        Connection connection = null;
        // definir datos para crear conexion
        var database = "coopahorro";
        var url = "jdbc:mysql://localhost:3306/" + database;
        var username = "root";
        var password = "";

        // Cargar clase del driver mysql en memoria
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        }
        //catch(Exception e){
        catch(ClassNotFoundException | SQLException e){
            System.out.println("Error en conexion a BD: " + e.getMessage());
        }

        return connection;
    }

    public static void main(String[] args){
        var conexion = Connect.getConnection();
        if(conexion != null)
            System.out.println("Conexion a BD coopahorro satisfactoria!!! " + conexion);
        else
            System.out.println("Error en conexion a BD coopahorro!!!");
    }
}
