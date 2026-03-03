package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import com.utl.fruitstore.model.ProductoDetalle;
import com.utl.fruitstore.model.Vendedor;
import com.utl.fruitstore.model.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la persistencia de las ventas y el control de inventarios.
 */
public class ControllerVenta {

    /**
     * Registra una venta completa, sus detalles y actualiza el stock de productos.
     * Utiliza una transacción (commit/rollback) para asegurar la integridad de los datos.
     */
    public int insert(Venta v) throws Exception {
        // SQL para la cabecera de la venta
        String sqlVenta = "INSERT INTO venta (idVendedor, fechaVenta) VALUES (?, ?)";
        
        // SQL para el detalle de la venta
        String sqlDetalle = "INSERT INTO detalle_venta (idVenta, idProducto, kilos, precioCompra, precioVenta, descuento) VALUES (?, ?, ?, ?, ?, ?)";
        
        // SQL PARA ACTUALIZAR EL STOCK (RESTA DE EXISTENCIAS)
        String sqlActualizarStock = "UPDATE producto SET existencia = existencia - ? WHERE idProducto = ?";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        
        // Desactivamos el autocommit para manejar la transacción manualmente
        conn.setAutoCommit(false);

        try {
            // 1. Insertar la Venta (Cabecera)
            PreparedStatement pstmtV = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtV.setInt(1, v.getVendedor().getId());
            pstmtV.setString(2, v.getFecha());
            pstmtV.executeUpdate();

            // Recuperar el ID generado para la venta
            ResultSet rs = pstmtV.getGeneratedKeys();
            if (rs.next()) {
                v.setId(rs.getInt(1));
            }

            // 2. Preparar los Statements para Detalle y Actualización de Stock
            PreparedStatement pstmtD = conn.prepareStatement(sqlDetalle);
            PreparedStatement pstmtS = conn.prepareStatement(sqlActualizarStock);

            for (ProductoDetalle pd : v.getDetallesVenta()) {
                // Llenamos el detalle de la venta
                pstmtD.setInt(1, v.getId());
                pstmtD.setInt(2, pd.getId());
                pstmtD.setDouble(3, pd.getCantidad());
                pstmtD.setDouble(4, pd.getPrecioCompra());
                pstmtD.setDouble(5, pd.getPrecioVenta());
                pstmtD.setDouble(6, pd.getDescuento());
                pstmtD.addBatch();

                // Llenamos la actualización de inventario (restamos la cantidad vendida)
                pstmtS.setDouble(1, pd.getCantidad());
                pstmtS.setInt(2, pd.getId());
                pstmtS.addBatch();
            }

            // Ejecutamos ambos lotes (Batches)
            pstmtD.executeBatch();
            pstmtS.executeBatch();

            // Si todo salió bien, confirmamos los cambios en la BD
            conn.commit();
            
        } catch (Exception e) {
            // Si hubo cualquier error, deshacemos todos los cambios para no dejar datos inconsistentes
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        return v.getId();
    }

    /**
     * Consulta el historial de ventas realizadas.
     */
    public List<Venta> getAll() throws Exception {
        String sql = "SELECT V.idVenta, V.fechaVenta, V.idVendedor, VE.nombre AS nombreVendedor " +
                     "FROM venta V INNER JOIN vendedor VE ON V.idVendedor = VE.idVendedor " +
                     "ORDER BY V.idVenta DESC";

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        List<Venta> ventas = new ArrayList<>();

        while(rs.next()) {
            Venta v = new Venta();
            v.setId(rs.getInt("idVenta"));
            v.setFecha(rs.getString("fechaVenta"));

            Vendedor ven = new Vendedor();
            ven.setId(rs.getInt("idVendedor"));
            ven.setNombre(rs.getString("nombreVendedor"));
            v.setVendedor(ven);

            ventas.add(v);
        }
        conn.close();
        return ventas;
    }

    /**
     * Recupera los productos que pertenecen a una venta específica.
     */
    public List<ProductoDetalle> getDetalle(int idVenta) throws Exception {
        String sql = "SELECT DV.*, P.nombre FROM detalle_venta DV " +
                     "INNER JOIN producto P ON DV.idProducto = P.idProducto WHERE DV.idVenta = ?";
        
        List<ProductoDetalle> detalles = new ArrayList<>();
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idVenta);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            ProductoDetalle pd = new ProductoDetalle();
            pd.setId(rs.getInt("idProducto"));
            pd.setNombre(rs.getString("nombre"));
            pd.setCantidad(rs.getDouble("kilos"));
            pd.setPrecioVenta(rs.getFloat("precioVenta"));
            pd.setDescuento(rs.getDouble("descuento"));
            detalles.add(pd);
        }
        conn.close();
        return detalles;
    }
}