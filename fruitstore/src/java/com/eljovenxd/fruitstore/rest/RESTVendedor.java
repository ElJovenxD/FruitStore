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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("vendedor")
public class RESTVendedor {

    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        String out = "";
        try {
            ControllerVendedor cv = new ControllerVendedor();
            List<Vendedor> vendedores = cv.getAll("");
            out = new Gson().toJson(vendedores);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al obtener datos.\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    // ESTE ES EL MÉTODO QUE DEBES AGREGAR:
    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("datosVendedor") @DefaultValue("") String datosVendedor) {
        String out = "";
        Gson gson = new Gson();
        try {
            // Convierte el JSON que viene del JS a un objeto Vendedor
            Vendedor v = gson.fromJson(datosVendedor, Vendedor.class);
            ControllerVendedor cv = new ControllerVendedor();

            if (v.getId() == 0) {
                cv.insert(v); // Crea el nuevo registro
            } else {
                cv.update(v); // Actualiza el existente
            }
            out = gson.toJson(v);
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error interno en el servidor al guardar.\"}";
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
            cv.delete(id);
            out = "{\"result\":\"Vendedor eliminado correctamente\"}";
        } catch (Exception e) {
            e.printStackTrace();
            out = "{\"exception\":\"Error al eliminar vendedor\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}
