let vendedores = [];
let idVendedorSeleccionado = 0;

/**
 * Arranca el módulo y configura los eventos.
 */
export async function inicializarModulo() {
    // Configura el switch de inactivos
    const chkInactivos = document.getElementById("chkMostrarInactivos");
    if (chkInactivos) {
        chkInactivos.onclick = () => {
            consultarVendedores();
            limpiar();
        };
    }
    await consultarVendedores();
    limpiar();
}

/**
 * Consulta vendedores según el estatus del switch.
 */
async function consultarVendedores() {
    const mostrarInactivos = document.getElementById("chkMostrarInactivos").checked;
    const estatusBusqueda = mostrarInactivos ? 0 : 1;

    let url = `../api/vendedor/getAll?estatus=${estatusBusqueda}`;

    try {
        let resp = await fetch(url);
        let datos = await resp.json();

        if (datos.exception) {
            Swal.fire("Error", datos.exception, "error");
            return;
        }

        vendedores = datos;
        fillTableVendedores();
    } catch (e) {
        console.error("Error al consultar:", e);
    }
}

function fillTableVendedores() {
    let contenido = '';
    vendedores.forEach((v, i) => {
        contenido += `
            <tr>
                <td>${v.nombre}</td>
                <td>${v.email}</td>
                <td>${v.ciudad}</td>
                <td>${v.telefono}</td>
                <td class="text-center">
                    <button class="btn btn-sm btn-outline-info" onclick="mostrarDetalleVendedor(${i})">
                        <i class="fas fa-eye"></i> Ver Detalle
                    </button>
                </td>
            </tr>`;
    });
    document.getElementById("tbodyVendedores").innerHTML = contenido;
}

window.mostrarDetalleVendedor = function (index) {
    let v = vendedores[index];
    idVendedorSeleccionado = v.id;

    // Llenado de campos (Asegúrate de que los IDs coincidan con tu HTML)
    document.getElementById("txtId").value = v.id;
    document.getElementById("txtNombre").value = v.nombre;
    document.getElementById("txtEstatus").value = v.estatus === 1 ? "Activo" : "Inactivo";
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

    if (v.usuario) {
        document.getElementById("txtUsuario").value = v.usuario.nombre || "";
        document.getElementById("txtPassword").value = v.usuario.contrasenia || "";
    } else {
        document.getElementById("txtUsuario").value = "";
        document.getElementById("txtPassword").value = "";
    }

    // Lógica de botones
    const btnReactivar = document.getElementById("btnReactivar");
    const btnEliminar = document.getElementById("btnEliminar");
    const badge = document.getElementById("badgeEstatus");

    if (v.estatus === 0) {
        btnReactivar.classList.remove("d-none");
        btnEliminar.classList.add("d-none");
        if (badge) { badge.textContent = "Inactivo"; badge.className = "badge bg-danger"; }
    } else {
        btnReactivar.classList.add("d-none");
        btnEliminar.classList.remove("d-none");
        if (badge) { badge.textContent = "Activo"; badge.className = "badge bg-success"; }
    }
};

window.limpiar = function () {
    idVendedorSeleccionado = 0;
    const ids = ["txtId", "txtNombre", "txtFechaNac", "txtEmail", "txtTelefono", "txtCalle", "txtNumExt", "txtNumInt", "txtColonia", "txtCp", "txtCiudad", "txtEstado", "txtPais", "txtUsuario", "txtPassword"];
    ids.forEach(id => {
        let el = document.getElementById(id);
        if (el) el.value = "";
    });

    document.getElementById("cmbGenero").value = "M";
    document.getElementById("txtEstatus").value = "Activo";
    document.getElementById("txtFechaAlta").value = new Date().toISOString().split('T')[0];

    document.getElementById("btnReactivar").classList.add("d-none");
    document.getElementById("btnEliminar").classList.remove("d-none");
};

window.guardar = function () {
    if (document.getElementById("txtNombre").value.trim() === "") {
        Swal.fire("Aviso", "El nombre es obligatorio", "warning");
        return;
    }
    Swal.fire({
        title: '¿Guardar cambios?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí'
    }).then((r) => { if (r.isConfirmed) ejecutarGuardar(); });
};

async function ejecutarGuardar() {
    let vendedor = {
        id: idVendedorSeleccionado,
        nombre: document.getElementById("txtNombre").value,
        genero: document.getElementById("cmbGenero").value,
        fechaNacimiento: document.getElementById("txtFechaNac").value,
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

    let params = new URLSearchParams();
    params.append("datosVendedor", JSON.stringify(vendedor));
    params.append("usuario", document.getElementById("txtUsuario").value);
    params.append("password", document.getElementById("txtPassword").value);

    try {
        let resp = await fetch("../api/vendedor/save", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });
        let res = await resp.json();
        if (res.exception) Swal.fire("Error", res.exception, "error");
        else {
            Swal.fire("Éxito", "Guardado correctamente", "success");
            consultarVendedores();
            limpiar();
        }
    } catch (e) { Swal.fire("Error", "No hay conexión", "error"); }
}

window.eliminar = function () {
    if (idVendedorSeleccionado === 0) return;
    Swal.fire({
        title: '¿Dar de baja?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Eliminar'
    }).then((r) => { if (r.isConfirmed) ejecutarAccion("../api/vendedor/delete"); });
};

window.reactivar = function () {
    Swal.fire({
        title: '¿Reactivar?',
        icon: 'info',
        showCancelButton: true,
        confirmButtonText: 'Reactivar'
    }).then((r) => { if (r.isConfirmed) ejecutarAccion("../api/vendedor/reactivate"); });
};

async function ejecutarAccion(url) {
    let params = new URLSearchParams();
    params.append("id", idVendedorSeleccionado);
    try {
        let resp = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });
        let res = await resp.json();
        if (res.exception) Swal.fire("Error", res.exception, "error");
        else {
            Swal.fire("Listo", "Operación exitosa", "success");
            consultarVendedores();
            limpiar();
        }
    } catch (e) { Swal.fire("Error", "Error de red", "error"); }
}   