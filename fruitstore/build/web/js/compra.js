let compras = [];
let productosAgregados = []; // Lista temporal para el detalle de la compra

/**
 * Inicializa el módulo cargando proveedores, productos e historial.
 */
export async function inicializarModulo() {
    await cargarProveedores();
    await cargarProductos();
    await consultarCompras();
    limpiarFormulario();
}

/**
 * Llena el combo de proveedores desde el servidor.
 */
async function cargarProveedores() {
    let url = "../api/proveedor/getAll";
    let resp = await fetch(url);
    let proveedores = await resp.json();
    
    let html = '<option value="0" selected disabled>Seleccione un proveedor...</option>';
    proveedores.forEach(p => {
        html += `<option value="${p.idProveedor}">${p.nombre}</option>`;
    });
    document.getElementById("cmbProveedor").innerHTML = html;
}

/**
 * Llena el combo de productos desde el servidor.
 */
async function cargarProductos() {
    let url = "../api/producto/getAll";
    let resp = await fetch(url);
    let productos = await resp.json();
    
    let html = '<option value="0" selected disabled>Seleccione un producto...</option>';
    productos.forEach(p => {
        html += `<option value="${p.id}">${p.nombre}</option>`;
    });
    document.getElementById("cmbProducto").innerHTML = html;
}

/**
 * Agrega un producto a la tabla temporal de detalles.
 */
window.agregarProductoALista = function() {
    let idProd = document.getElementById("cmbProducto").value;
    let combo = document.getElementById("cmbProducto");
    let nombreProd = combo.options[combo.selectedIndex].text;
    let cant = document.getElementById("txtCantidad").value;
    let precio = document.getElementById("txtPrecioCompra").value;

    if (idProd == 0 || cant <= 0 || precio <= 0) {
        Swal.fire("Aviso", "Capture producto, cantidad y precio válidos.", "warning");
        return;
    }

    // Agregar al arreglo temporal
    productosAgregados.push({
        producto: { id: parseInt(idProd), nombre: nombreProd },
        kilos: parseInt(cant),
        precioCompra: parseFloat(precio),
        descuento: 0
    });

    actualizarTablaTemporal();
    
    // Limpiar campos de captura de producto
    document.getElementById("cmbProducto").value = "0";
    document.getElementById("txtCantidad").value = "";
    document.getElementById("txtPrecioCompra").value = "";
}

/**
 * Dibuja la tabla visual de los productos que se van a comprar.
 */
function actualizarTablaTemporal() {
    let html = "";
    let total = 0;
    productosAgregados.forEach((p, index) => {
        let subtotal = p.kilos * p.precioCompra;
        total += subtotal;
        html += `<tr>
                    <td>${p.producto.nombre}</td>
                    <td>${p.kilos} Kg</td>
                    <td>$${p.precioCompra.toFixed(2)}</td>
                    <td>$${subtotal.toFixed(2)}</td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-danger" onclick="quitarProducto(${index})">X</button>
                    </td>
                 </tr>`;
    });
    document.getElementById("tbodyDetalleTemporal").innerHTML = html;
    document.getElementById("spnTotal").innerText = total.toFixed(2);
}

window.quitarProducto = function(index) {
    productosAgregados.splice(index, 1);
    actualizarTablaTemporal();
}

/**
 * Envía la compra maestra y sus detalles al servidor.
 */
window.guardar = async function() {
    let idProv = document.getElementById("cmbProveedor").value;

    if (idProv == 0 || productosAgregados.length === 0) {
        Swal.fire("Aviso", "Seleccione un proveedor y agregue al menos un producto.", "warning");
        return;
    }

    let compra = {
        proveedor: { idProveedor: parseInt(idProv) },
        fechaCompra: new Date().toISOString().split('T')[0],
        detalles: productosAgregados
    };

    let params = new URLSearchParams();
    params.append("datosCompra", JSON.stringify(compra));

    try {
        let resp = await fetch("../api/compra/save", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        let res = await resp.json();
        if (res.exception) {
            Swal.fire("Error", res.exception, "error");
        } else {
            Swal.fire("¡Éxito!", "La compra se ha registrado correctamente.", "success");
            productosAgregados = [];
            actualizarTablaTemporal();
            consultarCompras();
        }
    } catch (error) {
        Swal.fire("Error", "No se pudo comunicar con el servidor.", "error");
    }
}

async function consultarCompras() {
    let resp = await fetch("../api/compra/getAll");
    let datos = await resp.json();
    let html = "";
    datos.forEach(c => {
        html += `<tr>
                    <td>${c.idCompra}</td>
                    <td>${c.fechaCompra}</td>
                    <td>${c.proveedor.nombre}</td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-info" onclick="verDetalleCompra(${c.idCompra})">
                            👁️ Ver Productos
                        </button>
                    </td>
                </tr>`;
    });
    document.getElementById("tbodyCompras").innerHTML = html;
}

window.verDetalleCompra = async function(id) {
    let resp = await fetch("../api/compra/getDetalle?idCompra=" + id);
    let detalles = await resp.json();
    
    let tablaHtml = `
        <table class="table table-sm">
            <thead>
                <tr><th>Producto</th><th>Cantidad</th><th>Precio</th><th>Subtotal</th></tr>
            </thead>
            <tbody>`;
    
    let total = 0;
    detalles.forEach(d => {
        let sub = d.kilos * d.precioCompra;
        total += sub;
        tablaHtml += `<tr>
                        <td>${d.producto.nombre}</td>
                        <td>${d.kilos} Kg</td>
                        <td>$${d.precioCompra.toFixed(2)}</td>
                        <td>$${sub.toFixed(2)}</td>
                      </tr>`;
    });
    
    tablaHtml += `</tbody></table><h4 class="text-end">Total: $${total.toFixed(2)}</h4>`;

    Swal.fire({
        title: 'Detalle de Compra #' + id,
        html: tablaHtml,
        width: '600px',
        confirmButtonText: 'Cerrar'
    });
}