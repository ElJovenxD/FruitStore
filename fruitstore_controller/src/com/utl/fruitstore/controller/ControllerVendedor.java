package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import java.util.List;
import com.utl.fruitstore.model.Vendedor;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ControllerVendedor completo para la gestión de vendedores.
 * @author LiveGrios
 */
public class ControllerVendedor
{
    // Método para insertar un nuevo vendedor
    public int insert(Vendedor v) throws Exception {
        // Se definen todos los campos de la tabla vendedor
        String sql = "INSERT INTO vendedor (nombre, genero, fechaNac, email, telefono, fechaAlta, " +
                     "calle, numExt, numInt, colonia, cp, ciudad, estado, pais, estatus) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        // Se solicita el retorno de las llaves generadas (ID)
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        pstmt.setString(1, v.getNombre());
        pstmt.setString(2, v.getGenero());
        pstmt.setString(3, v.getFechaNacimiento());
        pstmt.setString(4, v.getEmail());
        pstmt.setString(5, v.getTelefono());
        pstmt.setString(6, v.getFechaAlta());
        pstmt.setString(7, v.getCalle());
        pstmt.setString(8, v.getNumExt());
        pstmt.setString(9, v.getNumInt());
        pstmt.setString(10, v.getColonia());
        pstmt.setString(11, v.getCp());
        pstmt.setString(12, v.getCiudad());
        pstmt.setString(13, v.getEstado());
        pstmt.setString(14, v.getPais());
        
        pstmt.executeUpdate();
        
        // Recuperar el ID autogenerado
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            v.setId(rs.getInt(1));
        }
        
        rs.close();
        pstmt.close();
        conn.close();
        return v.getId();
    }

    // Método para actualizar los datos de un vendedor existente
    public void update(Vendedor v) throws Exception {
        String sql = "UPDATE vendedor SET nombre=?, genero=?, fechaNac=?, email=?, telefono=?, " +
                     "calle=?, numExt=?, numInt=?, colonia=?, cp=?, ciudad=?, estado=?, pais=? " +
                     "WHERE idVendedor=?";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, v.getNombre());
        pstmt.setString(2, v.getGenero());
        pstmt.setString(3, v.getFechaNacimiento());
        pstmt.setString(4, v.getEmail());
        pstmt.setString(5, v.getTelefono());
        pstmt.setString(6, v.getCalle());
        pstmt.setString(7, v.getNumExt());
        pstmt.setString(8, v.getNumInt());
        pstmt.setString(9, v.getColonia());
        pstmt.setString(10, v.getCp());
        pstmt.setString(11, v.getCiudad());
        pstmt.setString(12, v.getEstado());
        pstmt.setString(13, v.getPais());
        pstmt.setInt(14, v.getId());
        
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    // Método para obtener todos los vendedores (ya lo tenías)
    public List<Vendedor> getAll(String filtro) throws Exception
    {
        String sql = "SELECT * FROM v_vendedor ORDER BY nombre ASC";
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        List<Vendedor> vendedores = new ArrayList<>();
        
        while(rs.next())
            vendedores.add(fill(rs));
        
        rs.close();
        pstmt.close();
        conn.close();
        
        return vendedores;
    }
    
    // Método auxiliar para llenar el objeto Vendedor desde el ResultSet (ya lo tenías)
    private Vendedor fill(ResultSet rs) throws Exception
    {
        Vendedor v = new Vendedor();
        
        v.setCalle(rs.getString("calle"));
        v.setCiudad(rs.getString("ciudad"));
        v.setColonia(rs.getString("colonia"));
        v.setCp(rs.getString("cp"));
        v.setEmail(rs.getString("email"));
        v.setEstado(rs.getString("estado"));
        v.setEstatus(rs.getInt("estatus"));
        v.setFechaAlta(rs.getString("fechaAlta"));
        v.setFechaNacimiento(rs.getString("fechaNac"));
        v.setGenero(rs.getString("genero"));
        v.setId(rs.getInt("idVendedor"));
        v.setNombre(rs.getString("nombre"));
        v.setNumExt(rs.getString("numExt"));
        v.setNumInt(rs.getString("numInt"));
        v.setPais(rs.getString("pais"));
        v.setTelefono(rs.getString("telefono"));
        return v;
    }
    
    public void delete(int id) throws Exception {
    String sql = "UPDATE vendedor SET estatus = 0 WHERE idVendedor = ?";
    
    ConexionMySQL connMySQL = new ConexionMySQL();
    Connection conn = connMySQL.open();
    PreparedStatement pstmt = conn.prepareStatement(sql);
    
    pstmt.setInt(1, id);
    pstmt.executeUpdate();
    
    pstmt.close();
    conn.close();
    }
}