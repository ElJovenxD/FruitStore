let vendedores = [];
let idVendedorSeleccionado = 0;

/**
 * Función principal que arranca el módulo.
 */
export async function inicializarModulo() {
    await consultarVendedores();
    limpiar(); 
}

/**
 * Obtiene todos los vendedores del servidor.
 */
async function consultarVendedores() {
    let url = "../api/vendedor/getAll";
    try {
        let resp = await fetch(url);
        let datos = await resp.json();

        if (datos.error != null) {
            Swal.fire("", "Error al consultar vendedores", "warning");
            return;
        }

        if (datos.exception != null) {
            Swal.fire("", datos.exception, "danger");
            return;
        }

        vendedores = datos;
        fillTableVendedores();
    } catch (e) {
        console.error("Error al consultar:", e);
    }
}

/**
 * Llena la tabla de vendedores.
 */
function fillTableVendedores() {
    let contenido = '';
    for (let i = 0; i < vendedores.length; i++) {
        let v = vendedores[i];
        contenido += `<tr>
                        <td>${v.nombre}</td>
                        <td>${v.email}</td>
                        <td>${v.ciudad}</td>
                        <td>${v.telefono}</td>
                        <td class="text-center">
                            <a href="#" onclick="mostrarDetalleVendedor(${i});">
                                <i class="fas fa-eye text-info"></i> Modificar
                            </a>
                        </td>
                     </tr>`;
    }
    document.getElementById("tbodyVendedores").innerHTML = contenido;
}

/**
 * Carga todos los datos del vendedor al formulario.
 */
window.mostrarDetalleVendedor = function(index) {
    let v = vendedores[index];
    idVendedorSeleccionado = v.id;

    document.getElementById("txtId").value = v.id;
    document.getElementById("txtNombre").value = v.nombre;
    document.getElementById("txtFechaNac").value = v.fechaNacimiento;
    document.getElementById("cmbGenero").value = v.genero;
    document.getElementById("txtEmail").value = v.email;
    document.getElementById("txtTelefono").value = v.telefono;
    document.getElementById("txtFechaAlta").value = v.fechaAlta;
    document.getElementById("txtCalle").value = v.calle;
    document.getElementById("txtNumExt").value = v.numExt;
    document.getElementById("txtNumInt").value = v.numInt;
    document.getElementById("txtColonia").value = v.colonia;
    document.getElementById("txtCp").value = v.cp;
    document.getElementById("txtCiudad").value = v.ciudad;
    document.getElementById("txtEstado").value = v.estado;
    document.getElementById("txtPais").value = v.pais;
};

/**
 * Limpia el formulario y prepara para un nuevo registro.
 */
window.limpiar = function() {
    idVendedorSeleccionado = 0;
    const campos = ["txtId", "txtNombre", "txtFechaNac", "txtEmail", "txtTelefono", 
                    "txtCalle", "txtNumExt", "txtNumInt", "txtColonia", 
                    "txtCp", "txtCiudad", "txtEstado", "txtPais"];
    
    campos.forEach(id => {
        let el = document.getElementById(id);
        if(el) el.value = "";
    });
    
    document.getElementById("cmbGenero").value = "M";
    
    // Fecha de alta automática para el día de hoy
    const hoy = new Date().toISOString().split('T')[0];
    document.getElementById("txtFechaAlta").value = hoy;
};

/**
 * Confirmación de Guardado.
 */
window.guardar = function() {
    const nombre = document.getElementById("txtNombre").value.trim();
    if (nombre === "") {
        Swal.fire("Aviso", "El nombre es obligatorio", "warning");
        return;
    }

    Swal.fire({
        title: '¿Deseas guardar al vendedor?',
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

async function ejecutarGuardar() {
    // Construcción del objeto siguiendo la estructura del Modelo Vendedor.java
    let vendedor = {
        id: idVendedorSeleccionado,
        nombre: document.getElementById("txtNombre").value,
        genero: document.getElementById("cmbGenero").value,
        fechaNacimiento: document.getElementById("txtFechaNac").value, // Debe ser exactamente fechaNacimiento
        email: document.getElementById("txtEmail").value,
        telefono: document.getElementById("txtTelefono").value,
        fechaAlta: document.getElementById("txtFechaAlta").value,
        calle: document.getElementById("txtCalle").value,
        numExt: document.getElementById("txtNumExt").value,
        numInt: document.getElementById("txtNumInt").value,
        colonia: document.getElementById("txtColonia").value,
        cp: document.getElementById("txtCp").value,
        ciudad: document.getElementById("txtCiudad").value,
        estado: document.getElementById("txtEstado").value,
        pais: document.getElementById("txtPais").value,
        estatus: 1
    };

    // Formateo de parámetros para @FormParam en el REST
    let params = new URLSearchParams();
    params.append("datosVendedor", JSON.stringify(vendedor));

    try {
        let resp = await fetch("../api/vendedor/save", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        let res = await resp.json();
        
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("¡Éxito!", "Vendedor procesado correctamente.", "success");
            consultarVendedores();
            limpiar();
        }
    } catch (error) {
        console.error("Error en la conexión:", error);
        Swal.fire("Error", "No se pudo conectar con el servidor.", "error");
    }
}

/**
 * Eliminación lógica del vendedor.
 */
window.eliminar = function() {
    if (idVendedorSeleccionado === 0) {
        Swal.fire("Aviso", "Selecciona un vendedor de la tabla.", "info");
        return;
    }

    Swal.fire({
        title: '¿Eliminar vendedor?',
        text: "El registro cambiará a estatus inactivo.",
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
    params.append("id", idVendedorSeleccionado);

    try {
        let resp = await fetch("../api/vendedor/delete", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        let res = await resp.json();
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("Eliminado", "Vendedor desactivado con éxito.", "success");
            consultarVendedores();
            limpiar();
        }
    } catch (error) {
        Swal.fire("Error", "No se pudo realizar la eliminación.", "error");
    }
}