/**
 * Módulo de Gestión de Productos
 * Maneja el CRUD, validaciones y recuperación de registros inactivos.
 */

// --- Variables Globales ---
let productos = [];
let categoriasCargadas = []; 
let idProductoSeleccionado = 0;
let mostrandoInactivos = false;

/**
 * Función principal que arranca el módulo.
 */
export async function inicializarModulo() {
    await cargarCategorias(); 
    await consultarProductos();
}

// --- Sección de Consultas (Fetch) ---

/**
 * Obtiene la lista de productos activos desde el servidor.
 */
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

/**
 * Obtiene la lista de productos dados de baja (estatus 0).
 */
async function consultarInactivos() {
    let url = "../api/producto/getAllInactivos";
    let resp = await fetch(url);
    let datos = await resp.json();
    
    productos = datos;
    fillTableProductosInactivos();
}

// --- Sección de Interfaz (Tablas y DOM) ---

/**
 * Dibuja la tabla de productos activos.
 */
function fillTableProductos() {
    let contenido = '';
    for (let i = 0; i < productos.length; i++) {
        contenido += '<tr>' +
                        '<td>' + productos[i].nombre + '</td>' +
                        '<td>' + productos[i].categoria.nombre + '</td>' +
                        '<td>$' + productos[i].precioCompra + '</td>' +
                        '<td>$' + productos[i].precioVenta + '</td>' +
                        '<td>' + productos[i].existencia + ' KG</td>' + 
                        '<td><span class="badge bg-success">Activo</span></td>' +
                        '<td>' + 
                            '<a href="#" onclick="mostrarDetalleProducto(' + i + ');">' +
                                '<i class="fas fa-edit text-info"></i> Modificar' +
                            '</a>' +
                        '</td>' +
                     '</tr>';
    }
    document.getElementById("tbodyProductos").innerHTML = contenido;
}

/**
 * Dibuja la tabla de productos inactivos con opción de reactivación.
 */
function fillTableProductosInactivos() {
    let contenido = '';
    for (let i = 0; i < productos.length; i++) {
        contenido += `<tr>
            <td>${productos[i].nombre}</td>
            <td>${productos[i].categoria.nombre}</td>
            <td>$${productos[i].precioCompra}</td>
            <td>$${productos[i].precioVenta}</td>
            <td>${productos[i].existencia} KG</td>
            <td><span class="badge bg-danger">Inactivo</span></td>
            <td>
                <button class="btn btn-sm btn-success" onclick="reactivarProducto(${productos[i].id})">
                    <i class="fas fa-undo"></i> Reactivar
                </button>
            </td>
        </tr>`;
    }
    document.getElementById("tbodyProductos").innerHTML = contenido;
}

/**
 * Alterna la vista entre el catálogo activo y el de inactivos.
 */
window.toggleInactivos = function() {
    mostrandoInactivos = !mostrandoInactivos;
    const btn = document.getElementById("btnToggleInactivos");
    
    if (mostrandoInactivos) {
        btn.innerHTML = '<i class="fas fa-eye"></i> Ver Activos';
        btn.className = "btn btn-info btn-sm text-white"; 
        consultarInactivos();
    } else {
        btn.innerHTML = '<i class="fas fa-eye-slash"></i> Ver Inactivos';
        btn.className = "btn btn-outline-secondary btn-sm";
        consultarProductos(); 
    }
}

/**
 * Carga los datos de un producto seleccionado en el formulario.
 */
window.mostrarDetalleProducto = function(index) {
    let p = productos[index];
    idProductoSeleccionado = p.idProducto || p.id; 

    document.getElementById("txtNombre").value = p.nombre;
    document.getElementById("txtPrecioCompra").value = p.precioCompra;
    document.getElementById("txtPrecioVenta").value = p.precioVenta;
    document.getElementById("txtExistencia").value = p.existencia;
    document.getElementById("cmbCategoria").value = p.categoria.id;
};

// --- Sección de Operaciones (Guardar, Eliminar, Reactivar) ---

/**
 * Pre-validación antes de guardar.
 */
window.guardar = async function() {
    let nombreInput = document.getElementById("txtNombre").value.trim();
    let pVenta = document.getElementById("txtPrecioVenta").value;
    let existencia = document.getElementById("txtExistencia").value;
    let idCat = document.getElementById("cmbCategoria").value;

    // 1. Campos obligatorios
    if (nombreInput === "" || pVenta === "" || existencia === "" || idCat === "0") {
        Swal.fire("Campos incompletos", "Por favor, llena todos los campos.", "warning");
        return;
    }

    // 2. Lógica Antiduplicados Global (Incluso en inactivos)
    if (idProductoSeleccionado === 0) {
        let respValidar = await fetch("../api/producto/getAllInclusoInactivos");
        let todosLosProductos = await respValidar.json();

        let productoExistente = todosLosProductos.find(p => 
            p.nombre.toLowerCase() === nombreInput.toLowerCase()
        );

        if (productoExistente) {
            if (productoExistente.estatus === 0) {
                Swal.fire({
                    title: "Producto en Inactivos",
                    text: `"${nombreInput}" ya existe pero está desactivado. ¿Deseas reactivarlo?`,
                    icon: "info",
                    showCancelButton: true,
                    confirmButtonText: "Sí, reactivar",
                    cancelButtonText: "Cancelar"
                }).then((result) => {
                    if (result.isConfirmed) reactivarProducto(productoExistente.id);
                });
            } else {
                Swal.fire("Producto duplicado", `El producto "${nombreInput}" ya está activo.`, "warning");
            }
            return; 
        }
    }

    // 3. Confirmación de guardado
    Swal.fire({
        title: '¿Deseas guardar los cambios?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí, guardar'
    }).then((result) => {
        if (result.isConfirmed) ejecutarGuardar();
    });
}

/**
 * Envío de datos al servidor.
 */
async function ejecutarGuardar() {
    let producto = {
        id: idProductoSeleccionado,
        nombre: document.getElementById("txtNombre").value.trim(),
        precioCompra: parseFloat(document.getElementById("txtPrecioCompra").value) || 0,
        precioVenta: parseFloat(document.getElementById("txtPrecioVenta").value),
        existencia: parseFloat(document.getElementById("txtExistencia").value),
        categoria: { id: parseInt(document.getElementById("cmbCategoria").value) },
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
            Swal.fire("¡Éxito!", "Operación realizada correctamente.", "success");
            limpiar();
            mostrandoInactivos ? consultarInactivos() : consultarProductos();
        }
    } catch (error) {
        Swal.fire("Error de conexión", "No se pudo comunicar con el servidor.", "error");
    }
}

/**
 * Eliminación lógica (cambio de estatus a 0).
 */
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
        confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
        if (result.isConfirmed) ejecutarEliminacion();
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

/**
 * Reactivación de un producto (cambio de estatus a 1).
 */
window.reactivarProducto = async function(id) {
    let params = { id: id };
    try {
        await fetch("../api/producto/activate", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams(params)
        });
        Swal.fire("Reactivado", "El producto vuelve a estar disponible.", "success");
        
        // Resetear vista a activos después de reactivar
        mostrandoInactivos = false;
        const btn = document.getElementById("btnToggleInactivos");
        btn.innerHTML = '<i class="fas fa-eye-slash"></i> Ver Inactivos';
        btn.className = "btn btn-outline-secondary btn-sm";
        
        consultarProductos();
        limpiar();
    } catch (e) {
        Swal.fire("Error", "No se pudo reactivar el producto.", "error");
    }
}

// --- Sección de Funciones Auxiliares ---

/**
 * Limpia el formulario para un nuevo registro.
 */
window.limpiar = function() {
    idProductoSeleccionado = 0;
    document.getElementById("txtNombre").value = "";
    document.getElementById("txtPrecioCompra").value = "";
    document.getElementById("txtPrecioVenta").value = "";
    document.getElementById("txtExistencia").value = "";
    document.getElementById("cmbCategoria").value = "0";
}

/**
 * Carga las categorías existentes para el selector (combo).
 */
export async function cargarCategorias() {
    const url = "../api/categoria/getAll";
    const select = document.getElementById("cmbCategoria");

    try {
        let resp = await fetch(url, { method: "POST" });
        let datos = await resp.json();

        select.innerHTML = '<option value="0">-- Seleccione una categoría --</option>';

        datos.forEach(c => {
            let opt = document.createElement("option");
            opt.value = c.id; 
            opt.innerHTML = c.nombre; 
            select.appendChild(opt);
        });
        categoriasCargadas = datos; 
    } catch (e) {
        console.error("Error al cargar categorías:", e);
    }
}