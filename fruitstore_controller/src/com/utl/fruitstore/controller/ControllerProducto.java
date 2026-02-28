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

public class ControllerProducto
{
    /**
     * Inserta un registro en la tabla [producto].
     * 
     * @param p Es un objeto de tipo Producto con los valores en los atributos
     *          correspondientes.
     * @return  Devuelve el ID de producto que se genero al realizar 
     *          la insercion.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public int insert(Producto p) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "INSERT INTO producto(nombre, idCategoria, precioCompra, precioVenta, existencia) VALUES(?, ?, ?, ?, ?)";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL y se indica que
        // se devolveran los ID's que se generen despues de ejecutarla:
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        
        // En este objeto se almacenaran los resultados de la consulta que,
        // en este caso, sera el ID que se genera al realizar la insercion
        // del registro:
        ResultSet rs = null;
        
        // Se llenan los valores de la sentencia SQL. 
        // Es importante recordar que el estandar JDBC es el unico caso especial
        // en el que cuyos indices comienzan en 1, en lugar de 0:
        pstmt.setString(1, p.getNombre());
        pstmt.setInt(2, p.getCategoria().getId());
        pstmt.setDouble(3, p.getPrecioCompra());
        pstmt.setDouble(4, p.getPrecioVenta());
        pstmt.setDouble(5, p.getExistencia());
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se recupera el ID del Producto que se inserto:
        rs = pstmt.getGeneratedKeys();
        if (rs.next())
            p.setId(rs.getInt(1)); //Se asigna el ID al objeto de tipo Producto
        
        // Se cierran los objetos de BD:
        rs.close();
        pstmt.close();
        conn.close();
        
        // Se devuelve el ID que se genero:
        return p.getId();
    }
    
    /**
     * Actualiza un registro en la tabla [producto].
     * 
     * @param p Es un objeto de tipo Producto con todos los datos que van a 
     *          actualizarse.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public void update(Producto p) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "UPDATE producto SET nombre=?, idCategoria=?, precioCompra=?, precioventa=?, existencia=? WHERE idProducto=?";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se llenan los valores de la sentencia SQL:
        pstmt.setString(1, p.getNombre());
        pstmt.setInt(2, p.getCategoria().getId());
        pstmt.setDouble(3, p.getPrecioCompra());
        pstmt.setDouble(4, p.getPrecioVenta());
        pstmt.setDouble(5, p.getExistencia());
        pstmt.setInt(6, p.getId());
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se cierran los objetos de BD:
        pstmt.close();
        conn.close();
    }
    
    /**
     * Elimina de forma logica el registro de la tabla [producto] 
     * correspondiente al valor del identificador (ID) pasado como parametro.
     * 
     * @param id    El valor del ID del producto que desea eliminarse.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public void delete(int id) throws Exception
    {
        // Se define la consulta SQL:
        String sql = "UPDATE producto SET estatus=0 WHERE idProducto=?";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se llenan los valores de la sentencia SQL:
        pstmt.setInt(1, id);
        
        // Se ejecuta la sentencia:
        pstmt.executeUpdate();
        
        // Se cierran los objetos de BD:
        pstmt.close();
        conn.close();
    }
    
    /**
     * Devuelve todos los registros de la tabla producto.
     * 
     * @param filtro    Un valor que sera buscado por coincidencia parcial
     *                  en todos los campos de la vista que contiene los
     *                  registros de productos.
     * @return          Devuelve una lista <code>List&lt;Producto&gt;</code>
     *                  que contiene todos los registros encontrados en la BD.
     * @throws Exception 
     */
    public List<Producto> getAll(String filtro) throws Exception
    {
        // Se define la consulta SQL que devuelve todos los productos
        // ordenados por nombre de manera ascendente:
        String sql = "SELECT * FROM v_producto WHERE estatus=1 ORDER BY nombre ASC";
        
        // Se crea un objeto de conexion con MySQL:
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        // Se abre la conexion con MySQL:
        Connection conn = connMySQL.open();
        
        // Se genera un objeto para definir la consulta SQL:
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // Se ejecuta la consulta SQL y se almacena el resultado:
        ResultSet rs = pstmt.executeQuery();
        
        // En este objeto de tipo lista se agregara cada registro recuperado
        // de la BD:
        List<Producto> productos = new ArrayList<>();
        
        // Se itera sobre cada renglon (Row) del ResultSet:
        while(rs.next())
            productos.add(fill(rs)); //Por cada registro, se genera un nuevo objeto
        
        // Se cierran los objetos de BD:
        rs.close();
        pstmt.close();
        conn.close();
        
        // Se devuelve la lista con los productos recuperados de la BD.
        return productos;
    }
    
    /**
     * Este metodo genera un objeto de tipo <code>Producto<code> extrayendo 
     * los datos de la posicion en la que se encuentra el <i>cursor</i> del
     * <code>ResultSet<code> pasado como parametro.
     * @param rs
     * @return
     * @throws Exception 
     */
    private Producto fill(ResultSet rs) throws Exception
    {
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
