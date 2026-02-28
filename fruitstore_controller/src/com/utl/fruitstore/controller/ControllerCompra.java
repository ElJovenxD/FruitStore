package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import com.utl.fruitstore.model.Compra;
import com.utl.fruitstore.model.DetalleCompra;
import com.utl.fruitstore.model.Producto;
import com.utl.fruitstore.model.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ControllerCompra {

    /**
     * Registra una compra maestra y todos sus productos asociados (detalles).
     * Utiliza una transacción para asegurar la integridad de los datos.
     */
    public int insert(Compra c) throws Exception {
        // Consultas para la tabla maestra y la tabla de detalle
        String sqlCompra = "INSERT INTO compra (idProveedor, fechaCompra) VALUES (?, ?)";
        String sqlDetalle = "INSERT INTO detalle_compra (idCompra, idProducto, kilos, precioCompra, descuento) VALUES (?, ?, ?, ?, ?)";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        
        // Desactivamos el autoCommit para manejar la transacción manualmente
        conn.setAutoCommit(false);

        try {
            // 1. Insertar la Compra (Maestra)
            PreparedStatement pstmtC = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS);
            pstmtC.setInt(1, c.getProveedor().getIdProveedor());
            pstmtC.setString(2, c.getFechaCompra());
            pstmtC.executeUpdate();

            // Recuperar el ID generado para la compra
            ResultSet rs = pstmtC.getGeneratedKeys();
            if (rs.next()) {
                c.setIdCompra(rs.getInt(1));
            }

            // 2. Insertar los Detalles (Productos comprados)
            PreparedStatement pstmtD = conn.prepareStatement(sqlDetalle);
            for (DetalleCompra dc : c.getDetalles()) {
                pstmtD.setInt(1, c.getIdCompra()); // ID de la compra recién creada
                pstmtD.setInt(2, dc.getProducto().getId());
                pstmtD.setInt(3, dc.getKilos());
                pstmtD.setFloat(4, dc.getPrecioCompra());
                pstmtD.setFloat(5, dc.getDescuento());
                pstmtD.addBatch(); // Agregamos al lote para ejecución eficiente
            }
            pstmtD.executeBatch();
            
            // Si todo salió bien, confirmamos los cambios en la DB
            conn.commit();
            
            rs.close();
            pstmtC.close();
            pstmtD.close();
        } catch (Exception e) {
            // Si hubo un error, revertimos cualquier cambio hecho durante la transacción
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        return c.getIdCompra();
    }

    /**
     * Obtiene el historial de compras con el nombre del proveedor.
     */
    public List<Compra> getAll() throws Exception {
        String sql = "SELECT C.*, P.nombre AS nombreProveedor " +
                     "FROM compra C " +
                     "INNER JOIN proveedor P ON C.idProveedor = P.idProveedor " +
                     "ORDER BY C.idCompra DESC";
        
        List<Compra> compras = new ArrayList<>();
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Compra c = new Compra();
            c.setIdCompra(rs.getInt("idCompra"));
            c.setFechaCompra(rs.getString("fechaCompra"));

            Proveedor p = new Proveedor();
            p.setIdProveedor(rs.getInt("idProveedor"));
            p.setNombre(rs.getString("nombreProveedor"));
            
            c.setProveedor(p);
            compras.add(c);
        }

        rs.close();
        pstmt.close();
        conn.close();
        return compras;
    }
    
    public List<DetalleCompra> getDetalle(int idCompra) throws Exception {
    String sql = "SELECT dc.*, p.nombre " +
                 "FROM detalle_compra dc " +
                 "INNER JOIN producto p ON dc.idProducto = p.idProducto " +
                 "WHERE dc.idCompra = ?";
    
    List<DetalleCompra> detalles = new ArrayList<>();
    ConexionMySQL connMySQL = new ConexionMySQL();
    Connection conn = connMySQL.open();
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, idCompra);
    ResultSet rs = pstmt.executeQuery();

    while (rs.next()) {
        DetalleCompra dc = new DetalleCompra();
        dc.setKilos(rs.getInt("kilos"));
        dc.setPrecioCompra(rs.getFloat("precioCompra"));
        
        Producto p = new Producto();
        p.setNombre(rs.getString("nombre"));
        dc.setProducto(p);
        
        detalles.add(dc);
    }
    rs.close();
    pstmt.close();
    conn.close();
    return detalles;
}
}