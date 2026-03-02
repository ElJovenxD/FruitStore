package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import com.utl.fruitstore.model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerCategoria {

    public int insertar(Categoria c) throws Exception {
        String query = "INSERT INTO categoria (nombre) VALUES (?);";
        int idGenerado = -1;
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, c.getNombre());
        pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            idGenerado = rs.getInt(1);
            c.setId(idGenerado);
        }
        rs.close(); pstmt.close(); conn.close(); connMySQL.close();
        return idGenerado;
    }

    public void eliminar(int id) throws Exception {
        String query = "DELETE FROM categoria WHERE idCategoria = ?;";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        pstmt.close(); conn.close(); connMySQL.close();
    }

    public List<Categoria> getAll(String filtro) throws Exception {
        String query = "SELECT * FROM categoria;";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        List<Categoria> categorias = new ArrayList<>();
        while (rs.next()) {
            Categoria c = new Categoria();
            c.setId(rs.getInt("idCategoria"));
            c.setNombre(rs.getString("nombre"));
            categorias.add(c);
        }
        rs.close(); pstmt.close(); conn.close(); connMySQL.close();
        return categorias;
    }
}