package com.utl.fruitstore.model;

public class DetalleCompra {
    private Producto producto;
    private double kilos; // CAMBIADO de int a double
    private float precioCompra;
    private float descuento;

    // Getters y Setters
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public double getKilos() { return kilos; } // Actualizar getter
    public void setKilos(double kilos) { this.kilos = kilos; }
    public float getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(float precioCompra) { this.precioCompra = precioCompra; }
    public float getDescuento() { return descuento; }
    public void setDescuento(float descuento) { this.descuento = descuento; }
}