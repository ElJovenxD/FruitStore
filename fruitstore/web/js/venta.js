let listaProductosTemporal = []; 

export async function inicializarModulo() {
    console.log("Módulo de ventas inicializado");
    await cargarProductos();
    await refrescarTablaVentas();
}

async function cargarProductos() {
    let resp = await fetch("../api/producto/getAll");
    let productos = await resp.json();
    let html = '<option value="0" disabled selected>Seleccione un producto...</option>';
    productos.forEach(p => {
        // Se usa p.id de acuerdo a la estructura de tu API
        html += `<option value="${p.id}">${p.nombre}</option>`;
    });
    document.getElementById("cmbProducto").innerHTML = html;
}

window.agregarProductoALista = async function() {
    let idProd = document.getElementById("cmbProducto").value;
    let combo = document.getElementById("cmbProducto");
    let nombreProd = combo.options[combo.selectedIndex].text;
    let cant = parseFloat(document.getElementById("txtCantidad").value);
    let desc = parseFloat(document.getElementById("txtDescuento").value) || 0;

    if (idProd == "0" || cant <= 0) {
        Swal.fire("Aviso", "Debe seleccionar un producto y una cantidad válida.", "warning");
        return;
    }

    // Consulta para obtener el precio de venta actual del producto
    let resp = await fetch("../api/producto/getAll");
    let productos = await resp.json();
    let productoEncontrado = productos.find(p => p.id == idProd);

    let precioVenta = productoEncontrado.precioVenta;
    let subtotal = (cant * precioVenta) - desc;

    // Agregar al arreglo global listaProductosTemporal
    listaProductosTemporal.push({
        id: parseInt(idProd),
        nombre: nombreProd,
        cantidad: cant,
        precioVenta: precioVenta,
        precioCompra: productoEncontrado.precioCompra, 
        descuento: desc,
        subtotal: subtotal
    });

    actualizarTablaTemporal();
};
function actualizarTablaTemporal() {
    let html = "";
    let total = 0;
    listaProductosTemporal.forEach((p, index) => {
        total += p.subtotal;
        html += `<tr>
                    <td>${p.nombre}</td>
                    <td class="text-center">${p.cantidad} Kg</td>
                    <td class="text-end">$${p.precioVenta.toFixed(2)}</td>
                    <td class="text-end">$${p.descuento.toFixed(2)}</td>
                    <td class="text-end">$${p.subtotal.toFixed(2)}</td>
                    <td class="text-center">
                        <button class="btn btn-danger btn-sm" onclick="quitarProducto(${index})">X</button>
                    </td>
                </tr>`;
    });
    document.getElementById("tbodyDetalleVentaTemporal").innerHTML = html;
    document.getElementById("spnTotalVenta").innerText = total.toFixed(2);
}

window.quitarProducto = function(index) {
    listaProductosTemporal.splice(index, 1);
    actualizarTablaTemporal();
};

/**
 * Envía la venta completa al servidor.
 */
window.guardarVenta = async function() {
    if (listaProductosTemporal.length === 0) {
        Swal.fire("Error", "No hay productos en la venta actual.", "warning");
        return;
    }

    // NUEVO: Recuperamos los datos del usuario que guardamos en el login
    let datosUsuario = JSON.parse(localStorage.getItem("usuarioFruitStore"));

    if (!datosUsuario || !datosUsuario.id) {
        Swal.fire("Error", "No se detectó una sesión activa. Reingrese al sistema.", "error");
        return;
    }

    let venta = {
        // CAMBIO: Usamos el ID real del vendedor logueado
        vendedor: { id: datosUsuario.id }, 
        fecha: new Date().toISOString().split('T')[0],
        detallesVenta: listaProductosTemporal
    };

    let params = new URLSearchParams();
    params.append("datosVenta", JSON.stringify(venta));

    let resp = await fetch("../api/venta/save", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params
    });

    let res = await resp.json();
    if (res.exception) {
        Swal.fire("Error", res.exception, "error");
    } else {
        Swal.fire("¡Venta Realizada!", "El registro se guardó con éxito.", "success");
        listaProductosTemporal = [];
        actualizarTablaTemporal();
        refrescarTablaVentas();
    }
};

/**
 * Consulta el historial de ventas.
 */
export async function refrescarTablaVentas() {
    let url = "../api/venta/getAll"; 
    try {
        let resp = await fetch(url);
        let ventas = await resp.json();
        let html = "";

        // Si el servidor mandó el JSON de excepción que pusiste en el Java
        if (ventas.exception) {
            console.error("Error del servidor:", ventas.exception);
            return;
        }

        // Validar que sea un arreglo antes de usar forEach
        if (Array.isArray(ventas)) {
            ventas.forEach(v => {
                html += `<tr>
                    <td>${v.id}</td>
                    <td>${v.fecha}</td>
                    <td>${v.vendedor ? v.vendedor.nombre : 'N/A'}</td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-outline-primary" onclick="verDetalleVenta(${v.id})">
                            👁️ Ver Detalle
                        </button>
                    </td>
                </tr>`;
            });
        }
        document.getElementById("tbodyVentas").innerHTML = html;
    } catch (e) {
        console.error("Error al procesar JSON de ventas:", e);
    }
}

/**
 * Muestra el detalle de una venta en un modal.
 */
window.verDetalleVenta = async function(id) {
    let url = "../api/venta/getDetalle?idVenta=" + id;
    try {
        let resp = await fetch(url);
        let detalles = await resp.json();

        let tablaHtml = `<table class="table table-sm table-striped">
                <thead>
                    <tr><th>Producto</th><th>Cantidad</th><th>Precio</th><th>Descuento</th><th>Subtotal</th></tr>
                </thead>
                <tbody>`;
        
        let totalVenta = 0;
        detalles.forEach(d => {
            let sub = d.subtotal || ((d.cantidad * d.precioVenta) - d.descuento);
            totalVenta += sub;
            tablaHtml += `<tr>
                <td>${d.nombre}</td>
                <td>${d.cantidad} Kg</td>
                <td>$${d.precioVenta.toFixed(2)}</td>
                <td>$${d.descuento.toFixed(2)}</td>
                <td>$${sub.toFixed(2)}</td>
            </tr>`;
        });

        tablaHtml += `</tbody></table><hr><h4 class="text-end">Total: $${totalVenta.toFixed(2)}</h4>`;

        Swal.fire({
            title: 'Detalle Venta #' + id,
            html: tablaHtml,
            width: '700px',
            confirmButtonText: 'Cerrar'
        });
    } catch (e) {
        Swal.fire("Error", "No se pudo recuperar el detalle.", "error");
    }
};

// Carga inicial del historial al entrar al módulo
document.addEventListener("DOMContentLoaded", refrescarTablaVentas);