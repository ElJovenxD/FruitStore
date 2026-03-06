/*
Artifact:   ControllerProducto.java

Version:    1.0
Date:       2024-05-28 19:00:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   Esta clase contiene los metodos para gestionar la persistencia de
            productos.

Version:    1.1
Date:       2025-04-29 08:50:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   1.  Se cambio la declaracion del paquete para adaptarlo a un 
                proyecto didactico de la UTL.
            2.  Se agrego documentacion detallada a los metodos de la clase
                para que se comprenda su funcionamiento.
*/

package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import com.utl.fruitstore.model.Categoria;
import com.utl.fruitstore.model.Producto;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class ControllerProducto {

    public int insert(Producto p) throws Exception {
        // Validación extra: No permitir insertar si el nombre ya existe (activo o inactivo)
        String sqlCheck = "SELECT idProducto FROM producto WHERE nombre = ?";
        String sql = "INSERT INTO producto(nombre, idCategoria, precioCompra, precioVenta, existencia) VALUES(?, ?, ?, ?, ?)";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        
        // Check de duplicados
        PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck);
        pstmtCheck.setString(1, p.getNombre());
        ResultSet rsCheck = pstmtCheck.executeQuery();
        if (rsCheck.next()) {
            rsCheck.close();
            pstmtCheck.close();
            conn.close();
            throw new Exception("El nombre del producto ya existe en el sistema.");
        }

        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, p.getNombre());
        pstmt.setInt(2, p.getCategoria().getId());
        pstmt.setDouble(3, p.getPrecioCompra());
        pstmt.setDouble(4, p.getPrecioVenta());
        pstmt.setDouble(5, p.getExistencia());
        
        pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) p.setId(rs.getInt(1));
        
        rs.close();
        pstmt.close();
        conn.close();
        return p.getId();
    }

    public void update(Producto p) throws Exception {
        String sql = "UPDATE producto SET nombre=?, idCategoria=?, precioCompra=?, precioventa=?, existencia=? WHERE idProducto=?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, p.getNombre());
        pstmt.setInt(2, p.getCategoria().getId());
        pstmt.setDouble(3, p.getPrecioCompra());
        pstmt.setDouble(4, p.getPrecioVenta());
        pstmt.setDouble(5, p.getExistencia());
        pstmt.setInt(6, p.getId());
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    public void delete(int id) throws Exception {
        String sql = "UPDATE producto SET estatus=0 WHERE idProducto=?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    // NUEVO: Reactivar producto (poner estatus en 1)
    public void reactivar(int id) throws Exception {
        String sql = "UPDATE producto SET estatus=1 WHERE idProducto=?";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    public List<Producto> getAll(String filtro) throws Exception {
        String sql = "SELECT * FROM v_producto WHERE estatus=1 ORDER BY nombre ASC";
        return ejecutarConsulta(sql);
    }

    // NUEVO: Obtener solo inactivos
    public List<Producto> getAllInactivos() throws Exception {
        String sql = "SELECT * FROM v_producto WHERE estatus=0 ORDER BY nombre ASC";
        return ejecutarConsulta(sql);
    }

    // NUEVO: Obtener absolutamente todos (para validaciones de JS)
    public List<Producto> getAllInclusoInactivos() throws Exception {
        String sql = "SELECT * FROM v_producto ORDER BY nombre ASC";
        return ejecutarConsulta(sql);
    }

    private List<Producto> ejecutarConsulta(String sql) throws Exception {
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        List<Producto> productos = new ArrayList<>();
        while(rs.next()) productos.add(fill(rs));
        rs.close();
        pstmt.close();
        conn.close();
        return productos;
    }

    private Producto fill(ResultSet rs) throws Exception {
        Producto p = new Producto();
        Categoria c = new Categoria();
        p.setId(rs.getInt("idProducto"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecioCompra(rs.getDouble("precioCompra"));
        p.setPrecioVenta(rs.getDouble("precioVenta"));
        p.setExistencia(rs.getDouble("existencia"));
        p.setEstatus(rs.getInt("estatus"));
        c.setId(rs.getInt("idCategoria"));
        c.setNombre(rs.getString("nombreCategoria"));
        p.setCategoria(c);
        return p;
    }
}