package com.eljovenxd.fruitstore.rest;

import com.google.gson.Gson;
import com.utl.fruitstore.controller.ControllerProducto;
import com.utl.fruitstore.model.Producto;
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

@Path("producto")
public class RESTProducto {

    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("filtro") @DefaultValue("") String filtro) {
        String out = "";
        try {
            ControllerProducto cp = new ControllerProducto();
            List<Producto> productos = cp.getAll(filtro); 
            out = new Gson().toJson(productos);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error interno del servidor.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("datosProducto") @DefaultValue("") String datosProducto) {
        String out = "";
        try {
            Gson gson = new Gson();
            Producto p = gson.fromJson(datosProducto, Producto.class);
            ControllerProducto cp = new ControllerProducto();
            
            if (p.getId() == 0) {
                cp.insert(p);
            } else {
                cp.update(p); 
            }
            out = gson.toJson(p);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al guardar el producto.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@FormParam("id") @DefaultValue("0") int id) {
        String out = "";
        try {
            ControllerProducto cp = new ControllerProducto();
            cp.delete(id); // Llama al delete lógico (estatus=0) del profesor
            out = "{\"result\":\"OK\"}";
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al eliminar el producto.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}