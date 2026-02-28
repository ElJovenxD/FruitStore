async function login()
{
    let nombreUsuario = document.getElementById("txtUsuario").value;
    let contrasenia = document.getElementById("txtContrasenia").value;
    let url = "api/usuario/login";
    let params ={
                    nombre      : nombreUsuario,
                    contrasenia : contrasenia
                };
    let confServ = {
                    method: "POST",
                    headers : {"Content-Type":"application/x-www-form-urlencoded;charset=UTF-8"},
                    body : new URLSearchParams(params)
                   };
    let resp = await fetch(url, confServ);
    let data = await resp.json();
    
    // En web/js/fruitstore_sec.js
    if (data.error != null) {
        Swal.fire('Error', data.error, 'warning');
        return;
    } else if (data.exception != null) {
        Swal.fire('Error en el servidor', data.exception, 'error');
        return;
    } else {
        // NUEVO: Guardamos el objeto del usuario (que contiene el id) como texto
        localStorage.setItem("usuarioFruitStore", JSON.stringify(data)); 
        window.location.href = "modules/principal.html#";
    }

function logout() {
    localStorage.removeItem("usuario");
    // Salimos de 'modules' para volver a la raíz
    window.location.href = "../index.html";
    }
}