let categorias = [];
let idCategoriaSeleccionada = 0;

export async function inicializarModulo() {
    if (window.validarSesion && !window.validarSesion()) return;
    await consultarCategorias();
    limpiar();
}

async function consultarCategorias() {
    try {
        let resp = await fetch("../api/categoria/getAll", { method: "POST" });
        categorias = await resp.json();
        fillTableCategorias();
    } catch (e) {
        Swal.fire("Error", "No se pudo conectar con el servidor", "error");
    }
}

function fillTableCategorias() {
    let contenido = '';
    categorias.forEach((c, i) => {
        contenido += `<tr>
            <td>${c.id}</td>
            <td>${c.nombre}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-primary" onclick="mostrarDetalleCategoria(${i})">Seleccionar</button>
            </td>
        </tr>`;
    });
    document.getElementById("tbodyCategorias").innerHTML = contenido;
}

window.mostrarDetalleCategoria = (index) => {
    let c = categorias[index];
    idCategoriaSeleccionada = c.id;
    document.getElementById("txtIdCategoria").value = c.id;
    document.getElementById("txtNombre").value = c.nombre;
};

window.limpiar = () => {
    idCategoriaSeleccionada = 0;
    document.getElementById("txtIdCategoria").value = "";
    document.getElementById("txtNombre").value = "";
};

window.guardar = async () => {
    let nombre = document.getElementById("txtNombre").value.trim();
    if (!nombre) return Swal.fire("Aviso", "Nombre obligatorio", "warning");

    let params = new URLSearchParams();
    params.append("id", idCategoriaSeleccionada);
    params.append("nombre", nombre);

    let resp = await fetch("../api/categoria/save", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params
    });
    let res = await resp.json();
    
    if (res.error) Swal.fire("Error", res.error, "error");
    else {
        Swal.fire("¡Éxito!", "Cambios aplicados", "success");
        consultarCategorias();
        limpiar();
    }
};

window.eliminar = async () => {
    if (idCategoriaSeleccionada === 0) return Swal.fire("Aviso", "Selecciona una categoría", "info");

    let params = new URLSearchParams();
    params.append("id", idCategoriaSeleccionada);

    let resp = await fetch("../api/categoria/delete", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params
    });
    await consultarCategorias();
    limpiar();
    Swal.fire("Eliminado", "Registro borrado", "success");
};