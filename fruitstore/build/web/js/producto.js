// Arreglos globales para almacenar la información
let productos = [];
let categorias = [];
let categoriasCargadas = []; // Se declara para evitar el error de "undeclared variable"
let idProductoSeleccionado = 0;

/**
 * Función principal que arranca el módulo.
 */
export async function inicializarModulo() {
    await cargarCategorias(); 
    await consultarProductos();
}

/**
 * Obtiene la lista de productos desde el servidor.
 */
async function consultarProductos() {
    let url = "../api/producto/getAll";
    let resp = await fetch(url);
    let datos = await resp.json();
    
    // Validaciones de error del servidor
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
 * Dibuja la tabla de productos en el HTML.
 */
function fillTableProductos() {
    let contenido = '';
    for (let i = 0; i < productos.length; i++) {
        contenido += '<tr>' +
                        '<td>' + productos[i].nombre + '</td>' +
                        '<td>' + productos[i].categoria.nombre + '</td>' +
                        '<td>$' + productos[i].precioCompra + '</td>' +
                        '<td>$' + productos[i].precioVenta + '</td>' +
                        // Se agrega la columna de existencia con la unidad "KG"
                        '<td>' + productos[i].existencia + ' KG</td>' + 
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

/**
 * Carga los datos de un producto seleccionado en el formulario superior.
 */
window.mostrarDetalleProducto = function(index) {
    let p = productos[index];
    idProductoSeleccionado = p.idProducto || p.id; 

    // Llenado de campos
    document.getElementById("txtNombre").value = p.nombre;
    document.getElementById("txtPrecioCompra").value = p.precioCompra;
    document.getElementById("txtPrecioVenta").value = p.precioVenta;
    document.getElementById("txtExistencia").value = p.existencia;
    document.getElementById("cmbCategoria").value = p.categoria.id;
    
    console.log("Editando ID:", idProductoSeleccionado);
};

/**
 * Limpia el formulario para un nuevo registro.
 */
window.limpiar = function() {
    idProductoSeleccionado = 0;
    document.getElementById("txtNombre").value = "";
    document.getElementById("txtPrecioCompra").value = "";
    document.getElementById("txtPrecioCompra").value = "";
    document.getElementById("txtPrecioVenta").value = "";
    document.getElementById("txtExistencia").value = "";
    document.getElementById("cmbCategoria").value = "0";
}

/**
 * Muestra la alerta de confirmación antes de guardar.
 */
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

/**
 * Procesa el envío de datos al servidor.
 */
async function ejecutarGuardar() {
    let nombreInput = document.getElementById("txtNombre").value.trim();
    let pCompra = document.getElementById("txtPrecioCompra").value;
    let pVenta = document.getElementById("txtPrecioVenta").value;
    let idCat = document.getElementById("cmbCategoria").value;
    
    // CORRECCIÓN: Se extrae la existencia del DOM para evitar el ReferenceError
    let existencia = document.getElementById("txtExistencia").value; 

    // Validación de campos obligatorios
    if (nombreInput === "" || pVenta === "" || existencia === "" || idCat === "0") {
        Swal.fire("Campos incompletos", "Por favor, llena todos los campos.", "warning");
        return;
    }

    // LÓGICA ANTIDUPLICADOS: Solo para registros nuevos
    if (idProductoSeleccionado === 0) {
        let existe = productos.some(p => p.nombre.toLowerCase() === nombreInput.toLowerCase());
        if (existe) {
            Swal.fire("Producto duplicado", `El producto "${nombreInput}" ya existe en el catálogo.`, "warning");
            return; 
        }
    }

    // Construcción del objeto producto según el modelo Java
    let producto = {
        id: idProductoSeleccionado,
        nombre: nombreInput,
        precioCompra: parseFloat(pCompra) || 0,
        precioVenta: parseFloat(pVenta),
        existencia: parseFloat(existencia),
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

/**
 * Lógica para eliminación lógica (cambio de estatus a 0).
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
        // CORRECCIÓN: Asignación a la variable global declarada al inicio
        categoriasCargadas = datos; 
    } catch (e) {
        console.error("Error al cargar categorías:", e);
    }
}