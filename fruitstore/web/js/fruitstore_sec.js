// 1. Función de Login (Asegúrate de que las llaves encierren todo el proceso)
async function login() {
    let nombreUsuario = document.getElementById("txtUsuario").value;
    let contrasenia = document.getElementById("txtContrasenia").value;
    
    // Aquí es donde marcaba el error: la variable url debe estar dentro de login
    let url = "api/usuario/login"; 
    
    let params = {
        nombre: nombreUsuario,
        contrasenia: contrasenia
    };

    let confServ = {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
        body: new URLSearchParams(params)
    };

    try {
        let resp = await fetch(url, confServ);
        let data = await resp.json();

        if (data.error != null) {
            Swal.fire('Error', data.error, 'warning');
        } else if (data.exception != null) {
            Swal.fire('Error en el servidor', data.exception, 'error');
        } else {
            // Guardamos la sesión
            localStorage.setItem("usuarioFruitStore", JSON.stringify(data)); 
            window.location.href = "modules/principal.html";
        }
    } catch (error) {
        console.error("Error en login:", error);
        Swal.fire('Error', 'No se pudo conectar con el servidor', 'error');
    }
} // <--- AQUÍ TERMINA LOGIN

// 2. Funciones Globales (FUERA de login)
window.logout = function() {
    localStorage.removeItem("usuarioFruitStore");
    localStorage.clear();
    sessionStorage.clear();
    // Ajusta la ruta según desde dónde llames al logout
    // Si estás en un módulo: "../index.html", si estás en la raíz: "index.html"
    window.location.replace("../index.html"); 
};

window.validarSesion = function() {
    let usuario = localStorage.getItem("usuarioFruitStore");
    if (usuario == null) {
        // Redirige al index si no hay sesión
        window.location.replace("../index.html");
        return false;
    }
    return true;
};