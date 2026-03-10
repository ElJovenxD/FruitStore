package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import com.utl.fruitstore.model.Usuario;
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
    public int insert(Vendedor v, String nombreUsuario, String contrasenia) throws Exception {
        // Consultas SQL
        String sqlVendedor = "INSERT INTO vendedor (nombre, genero, fechaNac, email, telefono, fechaAlta, " +
                             "calle, numExt, numInt, colonia, cp, ciudad, estado, pais, estatus) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";

        String sqlUsuario = "INSERT INTO usuario (nombre, contrasenia, idVendedor) VALUES (?, ?, ?)";

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        // Desactivamos el autocommit para manejar la transacción manualmente
        conn.setAutoCommit(false); 

        try {
            // 1. Insertar el Vendedor
            PreparedStatement pstmtV = conn.prepareStatement(sqlVendedor, Statement.RETURN_GENERATED_KEYS);
            pstmtV.setString(1, v.getNombre());
            pstmtV.setString(2, v.getGenero());
            pstmtV.setString(3, v.getFechaNacimiento());
            pstmtV.setString(4, v.getEmail());
            pstmtV.setString(5, v.getTelefono());
            pstmtV.setString(6, v.getFechaAlta());
            pstmtV.setString(7, v.getCalle());
            pstmtV.setString(8, v.getNumExt());
            pstmtV.setString(9, v.getNumInt());
            pstmtV.setString(10, v.getColonia());
            pstmtV.setString(11, v.getCp());
            pstmtV.setString(12, v.getCiudad());
            pstmtV.setString(13, v.getEstado());
            pstmtV.setString(14, v.getPais());

            pstmtV.executeUpdate();

            // Recuperar el ID generado para el vendedor
            ResultSet rs = pstmtV.getGeneratedKeys();
            if (rs.next()) {
                v.setId(rs.getInt(1));
            }
            rs.close();
            pstmtV.close();

            // 2. Insertar el Usuario asociado al ID del vendedor recién creado
            PreparedStatement pstmtU = conn.prepareStatement(sqlUsuario);
            pstmtU.setString(1, nombreUsuario);
            pstmtU.setString(2, contrasenia);
            pstmtU.setInt(3, v.getId());

            pstmtU.executeUpdate();
            pstmtU.close();

            // Si todo salió bien, guardamos los cambios
            conn.commit();

        } catch (Exception e) {
            // Si hubo un error (ej. el nombre de usuario ya existe), deshacemos todo
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }

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

    public List<Vendedor> getAll(String filtro, int estatus) throws Exception {
        // Usamos la vista v_usuario que ya tiene los JOINs necesarios
        String sql = "SELECT * FROM v_usuario WHERE estatus = ? ORDER BY nombre ASC";

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, estatus);

        ResultSet rs = pstmt.executeQuery();
        List<Vendedor> vendedores = new ArrayList<>();

        while(rs.next()) {
            vendedores.add(fill(rs));
        }

        rs.close();
        pstmt.close();
        conn.close();

        return vendedores;
    }

    private Vendedor fill(ResultSet rs) throws Exception {
        Vendedor v = new Vendedor();
        v.setId(rs.getInt("idVendedor"));
        v.setNombre(rs.getString("nombre"));
        v.setGenero(rs.getString("genero"));
        v.setFechaNacimiento(rs.getString("fechaNac"));
        v.setEmail(rs.getString("email"));
        v.setTelefono(rs.getString("telefono"));
        v.setFechaAlta(rs.getString("fechaAlta"));
        v.setCalle(rs.getString("calle"));
        v.setNumExt(rs.getString("numExt"));
        v.setNumInt(rs.getString("numInt"));
        v.setColonia(rs.getString("colonia"));
        v.setCp(rs.getString("cp"));
        v.setCiudad(rs.getString("ciudad"));
        v.setEstado(rs.getString("estado"));
        v.setPais(rs.getString("pais"));
        v.setEstatus(rs.getInt("estatus"));

        // Creamos el objeto usuario
        Usuario u = new Usuario();

        // IMPORTANTE: Revisar si el idUsuario es NULL antes de asignar
        int idU = rs.getInt("idUsuario");
        if (!rs.wasNull()) {
            u.setId(idU);
            u.setNombre(rs.getString("nombreUsuario"));
            u.setContrasenia(rs.getString("contrasenia"));
        } else {
            // Valores por defecto si no hay usuario asociado
            u.setId(0);
            u.setNombre("");
            u.setContrasenia("");
        }

        v.setUsuario(u);
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
    
    public void reactivar(int id) throws Exception {
        // Cambiamos el estatus a 1 (Activo)
        String sql = "UPDATE vendedor SET estatus = 1 WHERE idVendedor = ?";

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, id);
        pstmt.executeUpdate();

        pstmt.close();
        conn.close();
}
}