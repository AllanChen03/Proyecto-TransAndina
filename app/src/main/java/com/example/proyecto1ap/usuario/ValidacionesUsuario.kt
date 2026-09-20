package com.example.proyecto1ap.usuario

const val LARGO_CEDULA_USUARIO = 9
const val LARGO_TELEFONO_USUARIO = 8

fun soloLetrasEspacios(texto: String): String {
    return texto.filter { it.isLetter() || it.isWhitespace() }
}

fun soloNumeros(texto: String, maximo: Int): String {
    return texto.filter { it.isDigit() }.take(maximo)
}

fun validarDatosUsuario(
    nombreCompleto: String,
    cedula: String,
    correo: String,
    telefono: String,
    rol: String,
    numeroLicencia: String?
): String? {
    val nombre = nombreCompleto.trim()
    val cedulaLimpia = cedula.trim()
    val correoLimpio = correo.trim()
    val telefonoLimpio = telefono.trim()
    val licenciaLimpia = numeroLicencia?.trim().orEmpty()

    if (nombre.isBlank()) return "Debe ingresar el nombre completo"
    if (!nombre.all { it.isLetter() || it.isWhitespace() }) {
        return "El nombre solo debe contener letras"
    }

    if (cedulaLimpia.length != LARGO_CEDULA_USUARIO || !cedulaLimpia.all { it.isDigit() }) {
        return "La cédula debe tener $LARGO_CEDULA_USUARIO dígitos numéricos"
    }

    if (correoLimpio.isBlank()) return "Debe ingresar el correo"
    if (!correoLimpio.contains("@") || !correoLimpio.contains(".")) {
        return "Debe ingresar un correo válido"
    }

    if (telefonoLimpio.length != LARGO_TELEFONO_USUARIO || !telefonoLimpio.all { it.isDigit() }) {
        return "El teléfono debe tener $LARGO_TELEFONO_USUARIO dígitos numéricos"
    }

    if (rol == "CONDUCTOR") {
        if (licenciaLimpia.isBlank()) return "Debe ingresar el número de licencia"
    }

    return null
}

fun validarContrasenaRegistro(contrasena: String): String? {
    if (contrasena.isBlank()) return "Debe ingresar la contraseña"
    if (contrasena.length < 6) return "La contraseña debe tener al menos 6 caracteres"
    return null
}
