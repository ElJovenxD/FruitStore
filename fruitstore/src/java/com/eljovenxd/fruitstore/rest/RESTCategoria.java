package com.eljovenxd.fruitstore.rest;

import com.google.gson.Gson;
import com.utl.fruitstore.controller.ControllerCategoria;
import com.utl.fruitstore.model.Categoria;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("categoria")
public class RESTCategoria {

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("id") @DefaultValue("0") int id,
                         @FormParam("nombre") @DefaultValue("") String nombre) {
        Categoria c = new Categoria(id, nombre);
        ControllerCategoria cc = new ControllerCategoria();
        String out = "";
        try {
            if (c.getId() == 0) cc.insertar(c);
            // else cc.actualizar(c); // Si tuvieras update
            out = new Gson().toJson(c);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"error\":\"Error en el servidor\"}";
        }
        return Response.ok(out).build();
    }

    @Path("delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@FormParam("id") @DefaultValue("0") int id) {
        ControllerCategoria cc = new ControllerCategoria();
        try {
            cc.eliminar(id);
            return Response.ok("{\"result\":\"OK\"}").build();
        } catch (Exception e) {
            return Response.ok("{\"error\":\"Error al eliminar\"}").build();
        }
    }

    @Path("getAll")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        ControllerCategoria cc = new ControllerCategoria();
        try {
            return Response.ok(new Gson().toJson(cc.getAll(""))).build();
        } catch (Exception e) {
            return Response.ok("{\"error\":\"Error al consultar\"}").build();
        }
    }
}