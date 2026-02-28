package com.utl.fruitstore.model;

public class Usuario
{
    int id;
    String nombre;
    String contrasenia;
    Vendedor vendedor;
    
    public Usuario(){}

    public Usuario(int id, String nombre, String contrasenia, Vendedor vendedor)
    {
        this.id = id;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.vendedor = vendedor;
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

    public String getContrasenia()
    {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia)
    {
        this.contrasenia = contrasenia;
    }

    public Vendedor getVendedor()
    {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor)
    {
        this.vendedor = vendedor;
    }
}