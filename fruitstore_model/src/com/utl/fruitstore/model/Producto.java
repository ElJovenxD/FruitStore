/*
Artifact:   Producto.java

Version:    1.0
Date:       2024-05-21 19:00:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   Version inicial del codigo.

Version:    1.1
Date:       2025-04-29 08:15:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   1. Se cambio la declaracion del paquete para adaptarlo a un proyecto
                didactico de la UTL.
            2. Se agrego el atributo existencia.
*/
package com.utl.fruitstore.model;
/**
 *
 * @author LiveGrios
 */
public class Producto
{
    int id;
    String nombre;
    double precioCompra;
    double precioVenta;
    double existencia;
    Categoria categoria;
    int estatus;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public double getPrecioCompra()
    {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra)
    {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta()
    {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta)
    {
        this.precioVenta = precioVenta;
    }

    public double getExistencia()
    {
        return existencia;
    }

    public void setExistencia(double existencia)
    {
        this.existencia = existencia;
    }

    public Categoria getCategoria()
    {
        return categoria;
    }

    public void setCategoria(Categoria categoria)
    {
        this.categoria = categoria;
    }

    public int getEstatus()
    {
        return estatus;
    }

    public void setEstatus(int estatus)
    {
        this.estatus = estatus;
    }    
}
