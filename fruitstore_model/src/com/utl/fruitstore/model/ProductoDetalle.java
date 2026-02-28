/*
Artifact:   ProductoVenta.java

Version:    1.0
Date:       2024-05-21 19:00:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   Esta clase contiene informacion detallada de la venta de un
            producto.

Version:    1.1
Date:       2025-04-29 08:15:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   Se cambio la declaracion del paquete para adaptarlo a un proyecto
            didactico de la UTL.
*/
package com.utl.fruitstore.model;

/**
 *
 * @author LiveGrios
 */
public class ProductoDetalle extends Producto
{
    double cantidad;
    double subtotal;
    double descuento;
    double total;
    
    public double getCantidad()
    {
        return cantidad;
    }

    public void setCantidad(double cantidad)
    {
        this.cantidad = cantidad;
    }

    public double getDescuento()
    {
        return descuento;
    }

    public void setDescuento(double descuento)
    {
        this.descuento = descuento;
    }

    public double getTotal()
    {
        return total;
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public double getSubtotal()
    {
        return subtotal;
    }

    public void setSubtotal(double subtotal)
    {
        this.subtotal = subtotal;
    }
    
    
    
}
