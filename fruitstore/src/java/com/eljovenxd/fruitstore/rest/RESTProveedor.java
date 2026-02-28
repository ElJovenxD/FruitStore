package com.eljovenxd.fruitstore.rest;

import com.google.gson.Gson;
import com.utl.fruitstore.controller.ControllerProveedor;
import com.utl.fruitstore.model.Proveedor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("proveedor")
public class RESTProveedor {

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("datosProveedor") @DefaultValue("") String datosProveedor) {
        String out = "";
        Gson gson = new Gson();
        try {
            Proveedor p = gson.fromJson(datosProveedor, Proveedor.class);
            ControllerProveedor cp = new ControllerProveedor();
            
            if (p.getIdProveedor() == 0) {
                cp.insert(p);
            } else {
                cp.update(p);
            }
            out = gson.toJson(p);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al guardar el proveedor.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("getAll")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getAll(@QueryParam("filtro") @DefaultValue("") String filtro) {
    try {
        ControllerProveedor cp = new ControllerProveedor();
        List<Proveedor> proveedores = cp.getAll(filtro);

        if (proveedores == null) {
            proveedores = new ArrayList<>();
        }

        return Response.ok(proveedores).build();

    } catch (Exception e) {
        e.printStackTrace();
        return Response.serverError()
                .entity("{\"exception\":\"Error real en servidor\"}")
                .build();
    }
}
    
    @Path("delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@FormParam("id") @DefaultValue("0") int id) {
        String out = "";
        try {
            ControllerProveedor cp = new ControllerProveedor();
            cp.delete(id);
            out = "{\"result\":\"OK\"}";
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al eliminar el proveedor.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}

