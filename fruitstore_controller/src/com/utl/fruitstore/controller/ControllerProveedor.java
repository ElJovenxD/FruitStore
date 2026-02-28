package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import com.utl.fruitstore.model.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerProveedor {

    // Método para insertar un nuevo proveedor
    public int insert(Proveedor p) throws Exception {
        String sql = "INSERT INTO proveedor (nombre, razonSocial, rfc, direccion, email, " +
                     "telefonoFijo, telefonoMovil, estatus) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        pstmt.setString(1, p.getNombre());
        pstmt.setString(2, p.getRazonSocial());
        pstmt.setString(3, p.getRfc());
        pstmt.setString(4, p.getDireccion());
        pstmt.setString(5, p.getEmail());
        pstmt.setString(6, p.getTelefonoFijo());
        pstmt.setString(7, p.getTelefonoMovil());
        
        pstmt.executeUpdate();
        
        // Recuperar el ID generado automáticamente
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            p.setIdProveedor(rs.getInt(1));
        }
        
        rs.close();
        pstmt.close();
        conn.close();
        return p.getIdProveedor();
    }

    // Método para actualizar un proveedor existente
    public void update(Proveedor p) throws Exception {
        String sql = "UPDATE proveedor SET nombre=?, razonSocial=?, rfc=?, direccion=?, " +
                     "email=?, telefonoFijo=?, telefonoMovil=? WHERE idProveedor=?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, p.getNombre());
        pstmt.setString(2, p.getRazonSocial());
        pstmt.setString(3, p.getRfc());
        pstmt.setString(4, p.getDireccion());
        pstmt.setString(5, p.getEmail());
        pstmt.setString(6, p.getTelefonoFijo());
        pstmt.setString(7, p.getTelefonoMovil());
        pstmt.setInt(8, p.getIdProveedor());
        
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    // Método para eliminar (desactivar) un proveedor
    public void delete(int id) throws Exception {
        String sql = "UPDATE proveedor SET estatus = 0 WHERE idProveedor = ?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    // Método para obtener todos los proveedores
    public List<Proveedor> getAll(String filtro) throws Exception {
        String sql = "SELECT * FROM proveedor WHERE estatus = 1 ORDER BY nombre ASC";
        List<Proveedor> lista = new ArrayList<>();
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Proveedor p = new Proveedor();
            p.setIdProveedor(rs.getInt("idProveedor"));
            p.setNombre(rs.getString("nombre"));
            p.setRazonSocial(rs.getString("razonSocial"));
            p.setRfc(rs.getString("rfc"));
            p.setDireccion(rs.getString("direccion"));
            p.setEmail(rs.getString("email"));
            p.setTelefonoFijo(rs.getString("telefonoFijo"));
            p.setTelefonoMovil(rs.getString("telefonoMovil"));
            p.setEstatus(rs.getInt("estatus"));
            lista.add(p);
        }
        
        rs.close();
        pstmt.close();
        conn.close();
        return lista;
    }
}