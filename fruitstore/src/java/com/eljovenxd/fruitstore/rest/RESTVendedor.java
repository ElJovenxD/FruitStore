package com.eljovenxd.fruitstore.rest;

import com.google.gson.Gson;
import com.utl.fruitstore.controller.ControllerVendedor;
import com.utl.fruitstore.model.Vendedor;
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

@Path("vendedor")
public class RESTVendedor {

    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("estatus") @DefaultValue("1") int estatus) {
        String out = "";
        try {
            ControllerVendedor cv = new ControllerVendedor();
            // Se asume que el getAll ya recibe el filtro de estatus en el Controller
            List<Vendedor> vendedores = cv.getAll("", estatus); 
            out = new Gson().toJson(vendedores);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al obtener datos.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("datosVendedor") @DefaultValue("") String datosVendedor,
                         @FormParam("usuario") @DefaultValue("") String usuario,
                         @FormParam("password") @DefaultValue("") String password) {
        String out = "";
        Gson gson = new Gson();
        try {
            Vendedor v = gson.fromJson(datosVendedor, Vendedor.class);
            ControllerVendedor cv = new ControllerVendedor();

            if (v.getId() == 0) {
                // Inserta vendedor y crea su usuario simultáneamente
                cv.insert(v, usuario, password); 
            } else {
                // Actualiza datos del vendedor
                cv.update(v);
            }
            out = gson.toJson(v);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al guardar: " + e.getMessage() + "\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@FormParam("id") int id) {
        String out = "";
        try {
            ControllerVendedor cv = new ControllerVendedor();
            cv.delete(id); // Baja lógica (estatus = 0)
            out = "{\"result\":\"Vendedor eliminado correctamente\"}";
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al eliminar vendedor\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    /**
     * Endpoint para reactivar un vendedor (estatus = 1).
     */
    @Path("reactivate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response reactivate(@FormParam("id") int id) {
        String out = "";
        try {
            ControllerVendedor cv = new ControllerVendedor();
            cv.reactivar(id); // Llama al método del Controller
            out = "{\"result\":\"Vendedor reactivado correctamente\"}";
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al reactivar vendedor: " + e.getMessage() + "\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}