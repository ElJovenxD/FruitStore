package com.eljovenxd.fruitstore.rest;

import com.google.gson.Gson;
import com.utl.fruitstore.controller.ControllerCompra;
import com.utl.fruitstore.model.Compra;
import com.utl.fruitstore.model.DetalleCompra;
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

@Path("compra")
public class RESTCompra {

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("datosCompra") @DefaultValue("") String datosCompra) {
        String out = "";
        Gson gson = new Gson();
        try {
            Compra c = gson.fromJson(datosCompra, Compra.class);
            ControllerCompra cc = new ControllerCompra();
            cc.insert(c);
            out = gson.toJson(c);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al registrar la compra.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        String out = "";
        try {
            ControllerCompra cc = new ControllerCompra();
            List<Compra> compras = cc.getAll();
            out = new Gson().toJson(compras);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al consultar compras.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("getDetalle")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetalle(@QueryParam("idCompra") @DefaultValue("0") int idCompra) {
        try {
            List<DetalleCompra> detalles = new ControllerCompra().getDetalle(idCompra);
            return Response.ok(new Gson().toJson(detalles)).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}