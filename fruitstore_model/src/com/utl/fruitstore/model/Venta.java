package com.utl.fruitstore.model;

import java.util.List;

public class Venta {
    private int id;
    private Vendedor vendedor;
    private String fecha;
    // Esta es la clave: la lista de productos detallados
    private List<ProductoDetalle> detallesVenta; 

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public List<ProductoDetalle> getDetallesVenta() { return detallesVenta; }
    public void setDetallesVenta(List<ProductoDetalle> detallesVenta) { this.detallesVenta = detallesVenta; }
}