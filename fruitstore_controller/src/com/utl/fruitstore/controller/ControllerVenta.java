package com.utl.fruitstore.controller;

import com.utl.fruitstore.db.ConexionMySQL;
import com.utl.fruitstore.model.ProductoDetalle;
import com.utl.fruitstore.model.Vendedor;
import com.utl.fruitstore.model.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerVenta {

    public int insert(Venta v) throws Exception {
        String sqlVenta = "INSERT INTO venta (idVendedor, fechaVenta) VALUES (?, ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (idVenta, idProducto, kilos, precioCompra, precioVenta, descuento) VALUES (?, ?, ?, ?, ?, ?)";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = (Connection) connMySQL.open();
        conn.setAutoCommit(false);

        try {
            PreparedStatement pstmtV = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtV.setInt(1, v.getVendedor().getId());
            pstmtV.setString(2, v.getFecha());
            pstmtV.executeUpdate();

            ResultSet rs = pstmtV.getGeneratedKeys();
            if (rs.next()) v.setId(rs.getInt(1));

            PreparedStatement pstmtD = conn.prepareStatement(sqlDetalle);
            for (ProductoDetalle pd : v.getDetallesVenta()) {
                pstmtD.setInt(1, v.getId());
                pstmtD.setInt(2, pd.getId());
                pstmtD.setDouble(3, pd.getCantidad());
                pstmtD.setDouble(4, pd.getPrecioCompra());
                pstmtD.setDouble(5, pd.getPrecioVenta());
                pstmtD.setDouble(6, pd.getDescuento());
                pstmtD.addBatch();
            }
            pstmtD.executeBatch();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        return v.getId();
    }

    public List<Venta> getAll() throws Exception {
        // 1. Corregimos el nombre de la columna en el SELECT: de 'fecha' a 'fechaVenta'
        String sql = "SELECT V.idVenta, V.fechaVenta, V.idVendedor, VE.nombre AS nombreVendedor " +
                     "FROM venta V INNER JOIN vendedor VE ON V.idVendedor = VE.idVendedor " +
                     "ORDER BY V.idVenta DESC";

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = (Connection) connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        List<Venta> ventas = new ArrayList<>();

        while(rs.next()) {
            Venta v = new Venta();
            v.setId(rs.getInt("idVenta"));

            // 2. Corregimos aquí también: debe coincidir con el nombre en la base de datos
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