let categorias = [];
let idCategoriaSeleccionada = 0;

export async function inicializarModulo() {
    await consultarCategorias();
    if (!window.validarSesion()) {
        return; 
    }

    // Si pasó la validación, cargamos el módulo normalmente
    await consultarVendedores();
    limpiar();
}

async function consultarCategorias() {
    let url = "../api/categoria/getAll"; //
    let resp = await fetch(url);
    let datos = await resp.json();
    
    if (datos.error != null) {
        Swal.fire("", "Error al consultar categorías", "warning");
        return;
    }
    
    if (datos.exception != null) {
        Swal.fire("", datos.exception, "danger");
        return;
    }
    
    categorias = datos; 
    fillTableCategorias(); 
}

function fillTableCategorias() {
    let contenido = '';
    for (let i = 0; i < categorias.length; i++) {
        contenido += '<tr>' +
                        '<td>' + categorias[i].id + '</td>' + //
                        '<td>' + categorias[i].nombre + '</td>' + //
                        '<td class="text-center">' + 
                            '<a href="#" onclick="mostrarDetalleCategoria(' + i + ');">' +
                                '<i class="fas fa-eye text-info"></i> Modificar (No hay controller ni REST)' +
                            '</a>' +
                        '</td>' +
                     '</tr>';
    }
    document.getElementById("tbodyCategorias").innerHTML = contenido;
}

window.mostrarDetalleCategoria = function(index) {
    let c = categorias[index];
    idCategoriaSeleccionada = c.id; 

    document.getElementById("txtIdCategoria").value = c.id;
    document.getElementById("txtNombre").value = c.nombre;
};


window.limpiar = function() {
    idCategoriaSeleccionada = 0;
    document.getElementById("txtIdCategoria").value = "";
    document.getElementById("txtNombre").value = "";
}


window.guardar = function() {
    let nombre = document.getElementById("txtNombre").value.trim();
    
    if (nombre === "") {
        Swal.fire("Aviso", "El nombre de la categoría es obligatorio.", "warning");
        return;
    }

    Swal.fire({
        title: '¿Deseas guardar la categoría?',
        text: "Se registrará en el catálogo.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, guardar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            ejecutarGuardar();
        }
    });
}


async function ejecutarGuardar() {
    let nombre = document.getElementById("txtNombre").value.trim();
    
    // Crear el objeto exactamente como lo espera el modelo Categoria en Java
    let categoria = {
        id: parseInt(idCategoriaSeleccionada),
        nombre: nombre
    };

    // Asegúrate de que el nombre de la propiedad coincida con @FormParam("datosCategoria")
    let params = new URLSearchParams();
    params.append("datosCategoria", JSON.stringify(categoria)); 

    try {
        let resp = await fetch("../api/categoria/save", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params // Enviamos el URLSearchParams directamente
        });

        let res = await resp.json();
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("¡Éxito!", "Cambios aplicados correctamente.", "success");
            consultarCategorias(); 
            limpiar(); 
        }
    } catch (error) {
        console.error(error);
        Swal.fire("Error", "No se pudo conectar con el servidor.", "error");
    }
}

window.eliminar = function() {
    if (idCategoriaSeleccionada === 0) {
        Swal.fire("Aviso", "Selecciona una categoría para eliminar.", "info");
        return;
    }

    Swal.fire({
        title: '¿Estás seguro?',
        text: "Esta acción no se puede deshacer.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            ejecutarEliminacion();
        }
    });
}


async function ejecutarEliminacion() {
    let params = { id: idCategoriaSeleccionada };
    
    try {
        let resp = await fetch("../api/categoria/delete", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams(params)
        });

        let res = await resp.json();
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("Eliminado", "La categoría ha sido eliminada.", "success");
            consultarCategorias();
            limpiar();
        }
    } catch (error) {
        Swal.fire("Error", "No se pudo conectar con el servidor.", "error");
    }
}
