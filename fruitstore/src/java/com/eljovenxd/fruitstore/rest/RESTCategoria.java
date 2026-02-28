package com.eljovenxd.fruitstore.rest;

import com.google.gson.Gson;
import com.utl.fruitstore.controller.ControllerCategoria;
import com.utl.fruitstore.model.Categoria;
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

@Path("categoria")
public class RESTCategoria {

    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("filtro") @DefaultValue("") String filtro) {
        String out = "";
        try {
            ControllerCategoria cc = new ControllerCategoria();
            List<Categoria> categorias = cc.getAll(filtro);
            out = new Gson().toJson(categorias);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al obtener categorías.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("datosCategoria") @DefaultValue("") String datosCategoria) {
        String out = "";
        try {
            Gson gson = new Gson();
            Categoria c = gson.fromJson(datosCategoria, Categoria.class);
            ControllerCategoria cc = new ControllerCategoria();
            
            if (c.getId() == 0) {
                cc.insert(c);
            } else {
                cc.update(c);
            }
            out = gson.toJson(c);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al guardar categoría.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}