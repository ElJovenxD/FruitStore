/*
Artifact:   Categoria.java

Version:    1.0
Date:       2024-05-21 19:00:00
Author:     Miguel Angel Gil Rios
Email:      angel.grios@gmail.com - mgil@utleon.edu.mx
Comments:   Version inicial del codigo.

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
public class Categoria
{
    int id;
    String nombre;

    public Categoria()
    {
    }

    public Categoria(int id, String nombre)
    {
        this.id = id;
        this.nombre = nombre;
    }
    
    

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
    
    
}
