USE fruit_store;

DROP VIEW IF EXISTS v_usuario;
CREATE VIEW v_usuario AS
    SELECT  V.idVendedor,
			V.nombre,
            DATE_FORMAT(V.fechaNac, '%Y-%m-%d') AS fechaNac,
            V.genero,
            V.calle,
            V.numExt,
            V.numInt,
            V.colonia,
            V.cp,
            V.ciudad,
            V.estado,
            V.pais,
            V.telefono,
            DATE_FORMAT(V.fechaAlta, '%Y-%m-%d') AS fechaAlta,
            V.email, 
            V.estatus,
            U.idUsuario,
            U.nombre AS nombreUsuario,
            U.contrasenia
    FROM	vendedor V
			INNER JOIN usuario U ON V.idVendedor = U.idVendedor;

DROP VIEW IF EXISTS v_producto;
CREATE VIEW v_producto AS
    SELECT  P.idProducto,
            P.nombre,
            P.precioCompra,
            P.precioVenta,
            p.existencia,
            P.estatus,
            C.idCategoria,
            C.nombre AS nombreCategoria
    FROM	producto P 
            INNER JOIN categoria C ON P.idCategoria = C.idCategoria;
            
DROP VIEW IF EXISTS v_vendedor;

CREATE VIEW v_vendedor AS
SELECT  V.idVendedor,
        V.nombre,
        DATE_FORMAT(V.fechaNac, '%Y-%m-%d') AS fechaNac,
        V.genero,
        V.calle,
        V.numExt,
        V.numInt,
        V.colonia,
        V.cp,
        V.ciudad,
        V.estado,
        V.pais,
        V.telefono,
        DATE_FORMAT(V.fechaAlta, '%Y-%m-%d') AS fechaAlta,
        V.email, 
        V.estatus
FROM vendedor V
WHERE V.estatus = 1;
			
DROP VIEW IF EXISTS v_ventas_resumen;
CREATE VIEW v_ventas_resumen AS
    SELECT  VT.idVenta,
            DATE_FORMAT(VT.fechaVenta, '%Y-%m-%d %H:%i:%s') AS fechaVenta,
            VD.idVendedor,
            VD.nombre,
            DV.precioCompra,
            DV.precioVenta,
            SUM(DV.precioVenta * DV.kilos) AS subtotal,
            SUM(DV.descuento) AS descuento,
            (SUM(DV.precioVenta * DV.kilos) - SUM(DV.descuento)) AS total
    FROM    venta VT
            INNER JOIN detalle_venta DV ON DV.idVenta = VT.idVenta
            INNER JOIN vendedor VD ON VD.idVendedor = VT.idVendedor
    GROUP BY VT.idVenta, DV.precioCompra, DV.precioVenta
    ORDER BY idVenta DESC;
    
DROP VIEW IF EXISTS v_venta;
CREATE VIEW v_venta AS
    SELECT  VT.idVenta,
            DATE_FORMAT(VT.fechaVenta, '%Y-%m-%d %H:%i:%s') AS fechaVenta,
            VD.idVendedor,
            VD.nombre AS nombreVendedor,
            VD.email,
            VD.telefono,
            P.idProducto,
            P.nombre AS nombreProducto,
            P.precioCompra,
            P.precioVenta,
            P.estatus,
            DV.kilos,
            DV.precioVenta AS precioUnitario,
            DV.descuento,
            (DV.kilos * DV.precioVenta) AS subtotal,
            ((DV.kilos * DV.precioVenta) - descuento) AS total,
            C.idCategoria,
            C.nombre AS nombreCategoria
    FROM    venta VT
            INNER JOIN detalle_venta DV ON DV.idVenta = VT.idVenta
            INNER JOIN vendedor VD ON VD.idVendedor = VT.idVendedor
            INNER JOIN producto P ON DV.idProducto = P.idProducto
            INNER JOIN categoria C ON P.idCategoria = C.idCategoria;
        