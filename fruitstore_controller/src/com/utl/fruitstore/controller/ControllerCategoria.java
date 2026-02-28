/*
Artifact:   ControllerCategoria.java

Version:    1.0
Date:       2024-05-28 19:00:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   Esta clase contiene los metodos para gestionar la persistencia de
            grupos (clases de frutas y verduras).

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
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author LiveGrios
 */
public class ControllerCategoria
{
    /**
     * Inserta un registro en la tabla [grupo].
     * 
     * @param g Es un objeto de tipo Grupo con los valores en los atributos
     *          correspondientes.
     * @return  Devuelve el ID del grupo que se genero al realizar 
     *          la insercion.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public int insert(Categoria g) throws Exception
    {
        return 0;
    }
    
    /**
     * Actualiza un registro en la tabla [grupo].
     * 
     * @param g Es un objeto de tipo Grupo con todos los datos que van a 
     *          actualizarse.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public void update(Categoria g) throws Exception
    {
        
    }
    
    /**
     * Elimina de forma logica el registro de la tabla [grupo] 
     * correspondiente al valor del identificador (ID) pasado como parametro.
     * 
     * @param id    El valor del ID del grupo que desea eliminarse.
     * @throws Exception    Se lanza una excepcion cuando ocurre un fallo en la
     *                      comunicacion con la Base de Datos o se altero de
     *                      forma erronea una sentencia SQL.
     */
    public void delete (int id) throws Exception
    {
        
    }
    
    /**
     * Devuelve todos los registros de la tabla grupo.
     * 
     * @param filtro    Un valor que sera buscado por coincidencia parcial
     *                  en todos los campos de la vista que contiene los
     *                  registros de grupos.
     * @return          Devuelve una lista <code>List&lt;Grupo&gt;</code>
     *                  que contiene todos los registros encontrados en la BD.
     * @throws Exception 
     */
    public List<Categoria> getAll(String filtro) throws Exception
    {
        String sql = "SELECT * FROM categoria ORDER BY nombre ASC";
        List<Categoria> grupos = new ArrayList<>();
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        while(rs.next())
            grupos.add(fill(rs));
        
        rs.close();
        pstmt.close();
        conn.close();
        
        return grupos;
    }
    
    /**
     * Este metodo genera un objeto de tipo <code>Grupo<code> extrayendo 
     * los datos de la posicion en la que se encuentra el <i>cursor</i> del
     * <code>ResultSet<code> pasado como parametro.
     * @param rs
     * @return
     * @throws Exception 
     */
    private Categoria fill(ResultSet rs) throws Exception
    {
        Categoria c = new Categoria();
        c.setId(rs.getInt("idCategoria"));
        c.setNombre(rs.getString("nombre"));
        return c;
    }
}
