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
     * Registra una compra maestra, sus detalles e INCREMENTA el stock.
     * Además, actualiza el precioCompra en la tabla Producto con el valor de esta compra.
     */
    public int insert(Compra c) throws Exception {
        String sqlCompra = "INSERT INTO compra (idProveedor, fechaCompra) VALUES (?, ?)";
        String sqlDetalle = "INSERT INTO detalle_compra (idCompra, idProducto, kilos, precioCompra, descuento) VALUES (?, ?, ?, ?, ?)";
        
        // Esta sentencia actualiza la existencia sumando la nueva cantidad 
        // y actualiza el costo base del producto.
        String sqlActualizarStock = "UPDATE producto SET existencia = existencia + ?, precioCompra = ? WHERE idProducto = ?";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        
        // Iniciamos transacción manual
        conn.setAutoCommit(false);

        try {
            // 1. Insertar la Compra (Cabecera)
            PreparedStatement pstmtC = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS);
            pstmtC.setInt(1, c.getProveedor().getIdProveedor());
            pstmtC.setString(2, c.getFechaCompra());
            pstmtC.executeUpdate();

            // Recuperar ID de la compra
            ResultSet rs = pstmtC.getGeneratedKeys();
            if (rs.next()) {
                c.setIdCompra(rs.getInt(1));
            }

            // 2. Preparar los Statements para Detalle y Actualización de Producto
            PreparedStatement pstmtD = conn.prepareStatement(sqlDetalle);
            PreparedStatement pstmtS = conn.prepareStatement(sqlActualizarStock);

            for (DetalleCompra dc : c.getDetalles()) {
                // Registro del detalle de compra
                pstmtD.setInt(1, c.getIdCompra());
                pstmtD.setInt(2, dc.getProducto().getId());
                pstmtD.setDouble(3, dc.getKilos());
                pstmtD.setFloat(4, dc.getPrecioCompra());
                pstmtD.setFloat(5, dc.getDescuento());
                pstmtD.addBatch();

                // Actualización de inventario y precio oficial
                pstmtS.setDouble(1, dc.getKilos()); // Cantidad que entra
                pstmtS.setDouble(2, dc.getPrecioCompra()); // Nuevo precio de costo
                pstmtS.setInt(3, dc.getProducto().getId()); // ID del producto
                pstmtS.addBatch();
            }

            // Ejecución masiva
            pstmtD.executeBatch();
            pstmtS.executeBatch();
            
            // Confirmación de la transacción
            conn.commit();
            
            rs.close();
            pstmtC.close();
            pstmtD.close();
            pstmtS.close();
        } catch (Exception e) {
            // En caso de error, no se guarda nada
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        return c.getIdCompra();
    }

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