package com.eljovenxd.fruitstore.rest;

import com.google.gson.Gson;
import com.utl.fruitstore.controller.ControllerVenta;
import com.utl.fruitstore.model.ProductoDetalle;
import com.utl.fruitstore.model.Venta;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("venta")
public class RESTVenta {

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("datosVenta") @DefaultValue("") String datosVenta) {
        String out = "";
        try {
            Venta v = new Gson().fromJson(datosVenta, Venta.class);
            ControllerVenta cv = new ControllerVenta();
            cv.insert(v);
            out = new Gson().toJson(v);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al registrar la venta en el servidor.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        String out = "";
        try {
            ControllerVenta cv = new ControllerVenta();
            // Llama al método que ahora ya debe estar en el Controller
            List<Venta> ventas = cv.getAll(); 
            out = new Gson().toJson(ventas);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al consultar el historial de ventas.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("getDetalle")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetalle(@QueryParam("idVenta") @DefaultValue("0") int idVenta) {
        String out = "";
        try {
            ControllerVenta cv = new ControllerVenta();
            List<ProductoDetalle> detalles = cv.getDetalle(idVenta);
            out = new Gson().toJson(detalles);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al recuperar los productos de la venta.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}