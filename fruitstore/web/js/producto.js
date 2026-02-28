let productos = [];
let categorias = [];
let idProductoSeleccionado = 0; // Se declara una sola vez al inicio

export async function inicializarModulo() {
    await consultarProductos();
}

async function consultarProductos() {
    let url = "../api/producto/getAll";
    let resp = await fetch(url);
    let datos = await resp.json();
    
    if (datos.error != null) {
        Swal.fire("", "Error al consultar productos", "warning");
        return;
    }
    
    if (datos.exception != null) {
        Swal.fire("", datos.exception, "danger");
        return;
    }
    
    productos = datos; 
    fillTableProductos(); 
}

function fillTableProductos() {
    let contenido = '';
    for (let i = 0; i < productos.length; i++) {
        contenido += '<tr>' +
                        '<td>' + productos[i].nombre + '</td>' +
                        '<td>' + productos[i].categoria.nombre + '</td>' +
                        '<td>$' + productos[i].precioVenta + '</td>' +
                        '<td>' + (productos[i].estatus == 1 ? "Activo" : "Inactivo") + '</td>' +
                        '<td>' + 
                            '<a href="#" onclick="mostrarDetalleProducto(' + i + ');">' +
                                '<i class="fas fa-eye text-info"></i> Modificar' +
                            '</a>' +
                        '</td>' +
                     '</tr>';
    }
    document.getElementById("tbodyProductos").innerHTML = contenido;
}

// ÚNICA VERSIÓN de mostrarDetalleProducto
window.mostrarDetalleProducto = function(index) {
    let p = productos[index];
    // Guardamos el ID para saber que estamos EDITANDO y no CREANDO
    idProductoSeleccionado = p.idProducto || p.id; 

    document.getElementById("txtNombre").value = p.nombre;
    document.getElementById("txtPrecio").value = p.precioVenta;
    // Asignamos el ID de la categoría al select
    document.getElementById("cmbCategoria").value = p.categoria.id;
    
    console.log("Editando ID:", idProductoSeleccionado);
};

window.limpiar = function() {
    idProductoSeleccionado = 0; // IMPORTANTE: resetear a 0 para permitir nuevos
    document.getElementById("txtNombre").value = "";
    document.getElementById("txtPrecio").value = "";
    document.getElementById("cmbCategoria").value = "1";
}

window.guardar = function() {
    Swal.fire({
        title: '¿Deseas guardar los cambios?',
        text: "El producto se registrará en el sistema.",
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
    let nombreInput = document.getElementById("txtNombre").value.trim();
    let precio = document.getElementById("txtPrecio").value;
    let idCat = document.getElementById("cmbCategoria").value;

    if (nombreInput === "" || precio === "") {
        Swal.fire("Campos incompletos", "Por favor, llena todos los campos.", "warning");
        return;
    }

    // LÓGICA ANTIDUPLICADOS: Solo si es nuevo (id === 0)
    if (idProductoSeleccionado === 0) {
        let existe = productos.some(p => p.nombre.toLowerCase() === nombreInput.toLowerCase());
        if (existe) {
            Swal.fire("Producto duplicado", `El producto "${nombreInput}" ya existe en el catálogo.`, "warning");
            return; 
        }
    }

    let producto = {
        id: idProductoSeleccionado,
        nombre: nombreInput,
        precioVenta: parseFloat(precio),
        categoria: { id: parseInt(idCat) },
        estatus: 1
    };

    let params = { datosProducto: JSON.stringify(producto) };

    try {
        let resp = await fetch("../api/producto/save", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams(params)
        });

        let res = await resp.json();
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("¡Éxito!", "El producto se ha guardado correctamente.", "success");
            consultarProductos();
            limpiar();
        }
    } catch (error) {
        Swal.fire("Error de conexión", "No se pudo comunicar con el servidor.", "error");
    }
}

window.eliminar = function() {
    if (idProductoSeleccionado === 0) {
        Swal.fire("Aviso", "Primero selecciona un producto de la tabla.", "info");
        return;
    }

    Swal.fire({
        title: '¿Estás seguro?',
        text: "El producto será desactivado del catálogo.",
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
    let params = { id: idProductoSeleccionado };
    let resp = await fetch("../api/producto/delete", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams(params)
    });

    let res = await resp.json();
    if (res.exception) {
        Swal.fire("Error", res.exception, "error");
    } else {
        Swal.fire("Eliminado", "El producto se ha desactivado.", "success");
        consultarProductos();
        limpiar();
    }
}