let proveedores = [];
let idProveedorSeleccionado = 0;

export async function inicializarModulo() {
    await consultarProveedores();
}

// 1. Corregir la carga inicial de datos
async function consultarProveedores() {
    let url = "../api/proveedor/getAll"; // Eliminamos el localhost:8080
    try {
        let resp = await fetch(url);
        let datos = await resp.json();

        // Validaciones igual que en producto.js
        if (datos.error != null) {
            Swal.fire("Aviso", "Error al consultar proveedores", "warning");
            return;
        }

        if (datos.exception != null) {
            Swal.fire("Error", datos.exception, "error");
            return;
        }

        proveedores = datos;
        fillTableProveedores();
    } catch (error) {
        console.error("Error en la petición:", error);
    }
}

// 2. Corregir el envío de datos al servidor
async function ejecutarGuardar() {
    // Construir el objeto con los nombres exactos del Modelo Java
    let proveedor = {
        idProveedor: idProveedorSeleccionado,
        nombre: document.getElementById("txtNombre").value,
        razonSocial: document.getElementById("txtRazonSocial").value,
        rfc: document.getElementById("txtRfc").value,
        direccion: document.getElementById("txtDireccion").value,
        email: document.getElementById("txtEmail").value,
        telefonoFijo: "", // CAMBIO: Se agrega porque el Controller lo pide
        telefonoMovil: document.getElementById("txtTelMovil").value,
        estatus: 1
    };

    let params = new URLSearchParams();
    params.append("datosProveedor", JSON.stringify(proveedor));

    try {
        let resp = await fetch("../api/proveedor/save", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        let res = await resp.json();
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("Éxito", "Proveedor procesado correctamente.", "success");
            consultarProveedores();
            limpiar();
        }
    } catch (error) {
        Swal.fire("Error de conexión", "No se pudo contactar al servidor.", "error");
    }
}

function fillTableProveedores() {
    let contenido = '';
    for (let i = 0; i < proveedores.length; i++) {
        let p = proveedores[i];
        contenido += `
            <tr>
                <td>${p.nombre}</td>
                <td>${p.razonSocial}</td>
                <td>${p.rfc}</td>
                <td>${p.telefonoMovil}</td>
                <td>${p.estatus == 1 ? '<span class="badge bg-success">Activo</span>' : '<span class="badge bg-danger">Inactivo</span>'}</td>
                <td class="text-center">
                    <a href="#" onclick="mostrarDetalleProveedor(${i})">
                        <i class="fas fa-eye text-info"></i> Modificar
                    </a>
                </td>
            </tr>`;
    }
    document.getElementById("tbodyProveedores").innerHTML = contenido;
}

/**
 * Carga los datos del proveedor seleccionado al formulario.
 */
window.mostrarDetalleProveedor = function(index) {
    let p = proveedores[index];
    idProveedorSeleccionado = p.idProveedor;

    document.getElementById("txtNombre").value = p.nombre;
    document.getElementById("txtRazonSocial").value = p.razonSocial;
    document.getElementById("txtRfc").value = p.rfc;
    document.getElementById("txtTelMovil").value = p.telefonoMovil;
    document.getElementById("txtEmail").value = p.email;
    document.getElementById("txtDireccion").value = p.direccion;

    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'info',
        title: 'Modo edición: ' + p.nombre,
        showConfirmButton: false,
        timer: 2000
    });
};

/**
 * Limpia los campos del formulario.
 */
window.limpiar = function() {
    idProveedorSeleccionado = 0;
    document.getElementById("txtNombre").value = "";
    document.getElementById("txtRazonSocial").value = "";
    document.getElementById("txtRfc").value = "";
    document.getElementById("txtTelMovil").value = "";
    document.getElementById("txtEmail").value = "";
    document.getElementById("txtDireccion").value = "";
};

/**
 * Lógica de confirmación para guardar.
 */
window.guardar = function() {
    let nombre = document.getElementById("txtNombre").value.trim();
    if (nombre === "") {
        Swal.fire("Campo obligatorio", "El nombre es necesario para el registro.", "warning");
        return;
    }

    Swal.fire({
        title: '¿Guardar proveedor?',
        text: "Los datos se actualizarán en la base de datos.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí, guardar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            ejecutarGuardar();
        }
    });
};

window.eliminar = function() {
    if (idProveedorSeleccionado === 0) {
        Swal.fire("Aviso", "Selecciona un proveedor de la tabla.", "info");
        return;
    }

    Swal.fire({
        title: '¿Eliminar proveedor?',
        text: "El proveedor cambiará a estatus inactivo.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            ejecutarEliminacion();
        }
    });
};

async function ejecutarEliminacion() {
    let params = new URLSearchParams();
    params.append("id", idProveedorSeleccionado);

    try {
        let resp = await fetch("../api/proveedor/delete", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        let res = await resp.json();
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("Eliminado", "Proveedor desactivado con éxito.", "success");
            consultarProveedores();
            limpiar();
        }
    } catch (error) {
        Swal.fire("Error", "No se pudo realizar la eliminación.", "error");
    }
}