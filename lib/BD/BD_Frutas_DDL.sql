-- **********************************************
--  Artifact:   BD_Frutas_DDL.sql
--  Version:    1.0
--  Date:       2023-01-01 00:00:00
--  Email:       
--  Author:     Daniel Ceballos
--
--  Version:    1.1
--  Date:       2024-01-29 19:27:00
--  Author:     Miguel Angel Gil Rios
--  Comments:   Code changed to SQL naming 
--              standards.
--              Code was also adpated to 
--              MySQL coding standard.
-- **********************************************

DROP DATABASE IF EXISTS fruit_store;
CREATE DATABASE fruit_store;

USE fruit_store;
SELECT * FROM  usuario;

CREATE TABLE categoria
(
    idCategoria INTEGER PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(50),
    estatus	 	INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE producto
(
    idProducto   INTEGER PRIMARY KEY AUTO_INCREMENT,
    nombre       VARCHAR(128),
    idCategoria  INTEGER,
    precioCompra FLOAT NOT NULL DEFAULT 0.0, -- Precio de Compra por Kg
    precioVenta  FLOAT NOT NULL DEFAULT 0.0, -- Precio de Venta por Kg
    existencia   FLOAT NOT NULL DEFAULT 0.0, -- La cantidad de producto 
											-- disponible en stock,
                                            -- medida en kilogramos.
    estatus	 	INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT fk_producto_categoria
    FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria)
);

CREATE TABLE vendedor
(
    idVendedor      INTEGER PRIMARY KEY AUTO_INCREMENT,
    nombre          VARCHAR(50),    
    fechaNac        DATE,
    genero          VARCHAR(3) NOT NULL DEFAULT 'O',
    calle           VARCHAR(255) NOT NULL DEFAULT '',
    numExt          VARCHAR(65)  NOT NULL DEFAULT '',
    numInt          VARCHAR(65)  NOT NULL DEFAULT '',
    colonia         VARCHAR(127) NOT NULL DEFAULT '',
    cp              VARCHAR(25)  NOT NULL DEFAULT '',
    ciudad          VARCHAR(127) NOT NULL DEFAULT '',
    estado          VARCHAR(127) NOT NULL DEFAULT '',
    pais            VARCHAR(127) NOT NULL DEFAULT '',
    telefono        VARCHAR(20),
    fechaAlta       DATE,
    email			VARCHAR(255) NOT NULL DEFAULT '',
    estatus	 		INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE proveedor
(
    idProveedor     INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre          VARCHAR(129) NOT NULL,
    razonSocial     VARCHAR(129) NOT NULL,
    rfc             VARCHAR(15)  NOT NULL DEFAULT '',
    direccion       VARCHAR(255) NOT NULL DEFAULT '',
    email           VARCHAR(129) NOT NULL  DEFAULT '',
    telefonoFijo    VARCHAR(15)  NOT NULL DEFAULT '',
    telefonoMovil   VARCHAR(15)  NOT NULL DEFAULT '',
    estatus	 		INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE venta
(
    idVenta     INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    idVendedor  INTEGER REFERENCES vendedor(idVendedor),
    fechaVenta  DATETIME,    
    CONSTRAINT  fk_venta_vendedor 
    FOREIGN KEY (idVendedor) REFERENCES vendedor(idVendedor)
);

CREATE TABLE detalle_venta
(
    idVenta      INTEGER NOT NULL,
    idProducto   INTEGER NOT NULL,
    kilos        INTEGER NOT NULL DEFAULT 0,
    precioCompra FLOAT NOT NULL DEFAULT 0.0,
    precioVenta  FLOAT NOT NULL DEFAULT 0.0,
    descuento    FLOAT NOT NULL DEFAULT 0.0,
    CONSTRAINT  fk_dv_venta
    FOREIGN KEY (idVenta) REFERENCES venta(idVenta),
    CONSTRAINT  fk_dv_producto
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
);

CREATE TABLE compra
(
    idCompra    INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    idProveedor INTEGER NOT NULL,
    fechaCompra DATE,    
    CONSTRAINT  fk_compra_proveedor 
    FOREIGN KEY (idProveedor) REFERENCES proveedor(idProveedor)
);

CREATE TABLE detalle_compra
(
    idCompra        INTEGER NOT NULL,
    idProducto      INTEGER NOT NULL,
    kilos           INTEGER NOT NULL DEFAULT 0,
    precioCompra    FLOAT NOT NULL DEFAULT 0,
    descuento       FLOAT NOT NULL DEFAULT 0,
    CONSTRAINT      fk_dc_compra
    FOREIGN KEY     (idCompra) REFERENCES compra(idCompra),
    CONSTRAINT      fk_dc_producto
    FOREIGN KEY     (idProducto) REFERENCES producto(idProducto)
);

CREATE TABLE usuario
(
    idUsuario       INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    idVendedor      INTEGER NOT NULL,
    nombre   		VARCHAR(129) NOT NULL,
    contrasenia     VARCHAR(255) NOT NULL,
    CONSTRAINT  fk_usuario_vendedor
    FOREIGN KEY (idVendedor) REFERENCES vendedor(idVendedor)
);