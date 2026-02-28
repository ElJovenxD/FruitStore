/*
Artifact:   Vendedor.java

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
public class Vendedor
{
    int id;
    String nombre;
    String fechaNacimiento;
    String genero;
    String calle;
    String numExt;
    String numInt;
    String colonia;
    String cp;
    String ciudad;
    String estado;
    String pais;
    String telefono;
    String email;
    String fechaAlta;
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

    public String getFechaNacimiento()
    {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento)
    {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero()
    {
        return genero;
    }

    public void setGenero(String genero)
    {
        this.genero = genero;
    }

    public String getCalle()
    {
        return calle;
    }

    public void setCalle(String calle)
    {
        this.calle = calle;
    }

    public String getNumExt()
    {
        return numExt;
    }

    public void setNumExt(String numExt)
    {
        this.numExt = numExt;
    }

    public String getNumInt()
    {
        return numInt;
    }

    public void setNumInt(String numInt)
    {
        this.numInt = numInt;
    }

    public String getColonia()
    {
        return colonia;
    }

    public void setColonia(String colonia)
    {
        this.colonia = colonia;
    }

    public String getCp()
    {
        return cp;
    }

    public void setCp(String cp)
    {
        this.cp = cp;
    }

    public String getCiudad()
    {
        return ciudad;
    }

    public void setCiudad(String ciudad)
    {
        this.ciudad = ciudad;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public String getPais()
    {
        return pais;
    }

    public void setPais(String pais)
    {
        this.pais = pais;
    }

    public String getTelefono()
    {
        return telefono;
    }

    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFechaAlta()
    {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta)
    {
        this.fechaAlta = fechaAlta;
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
