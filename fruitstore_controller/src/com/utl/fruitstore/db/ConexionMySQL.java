package com.utl.fruitstore.db;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionMySQL
{
    Connection conn;
    
    public Connection open() throws Exception
    {
        //Definimos la ruta de conexion a MySQL:
        String url = "jdbc:mysql://127.0.0.1:3306/fruit_store";
        String usuario = "root";
        String password = "1234"; 
        
        
        // Comenzamos registrando el Driver de MySQL:
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        //Abrimos la conexion con MySQL:
        conn = DriverManager.getConnection(url, usuario, password);
        
        //Devolvemos la conexion:
        return conn;
    }
    
    public void close() throws Exception
    {
        //Revisamos si hay una conexion activa:
        if (conn != null)
            conn.close();
    }
}
