// ============================================================
//  PROFILE.JS — Lógica de la página "Tu Perfil"
//  Responsabilidades:
//    · Validación de sesión activa antes de mostrar el formulario
//    · Precarga de los datos actuales del huésped desde la API
//    · Validación frontend de campos obligatorios y coincidencias
//    · Actualización parcial del perfil vía PUT (contraseña opcional)
//    · Sincronización del nombre en sessionStorage tras el guardado
// ============================================================


// ============================================================
//  SECCIÓN 1 — INICIALIZACIÓN Y PRECARGA DEL FORMULARIO
// ============================================================

/**
 * Punto de entrada de la página de perfil.
 * Verifica que el huésped tenga sesión activa y, si es así,
 * precarga el formulario con sus datos actuales desde la API.
 * Si la sesión no existe, redirige al inicio para forzar el login.
 *
 * @returns {Promise<void>}
 */
document.addEventListener('DOMContentLoaded', async () => {
    const guestId = sessionStorage.getItem('guestId');

    // Sin guestId no hay sesión activa; se redirige al inicio
    // en lugar de mostrar un formulario vacío o inaccesible
    if (!guestId) {
        window.location.href = "/";
        return;
    }

    // === Precarga de datos del perfil ===
    try {
        // GET /api/guests/:id → espera { names, surnames, phone, email }
        const response = await fetch(`/api/guests/${guestId}`);
        if (response.ok) {
            const guest = await response.json();

            // El campo confirm-email se rellena con el mismo valor que email
            // para que el usuario solo tenga que modificarlo si quiere cambiarlo,
            // evitando que deba escribirlo dos veces si no hay cambios
            document.getElementById('pro-firstname').value = guest.names || '';
            document.getElementById('pro-lastname').value = guest.surnames || '';
            document.getElementById('pro-phone').value = guest.phone || '';
            document.getElementById('pro-email').value = guest.email || '';
            document.getElementById('pro-confirm-email').value = guest.email || '';
        } else {
            showToast("No pudimos cargar tus datos de perfil.", true);
        }
    } catch (error) {
        console.error("Error cargando perfil:", error);
        showToast("Error de conexión al cargar el perfil.", true);
    }
});


// ============================================================
//  SECCIÓN 2 — ACTUALIZACIÓN DEL PERFIL
// ============================================================

/**
 * Valida el formulario y envía los datos actualizados del perfil a la API.
 * La contraseña es opcional: si se deja en blanco, el backend la ignora
 * y conserva la contraseña existente del huésped.
 * Bloquea el botón durante el envío para prevenir peticiones duplicadas.
 * Tras un guardado exitoso, sincroniza el nombre en sessionStorage y recarga
 * la página para reflejar los cambios en el badge de la navbar.
 *
 * @returns {Promise<void>}
 */
async function processUpdate() {
    const btnUpdate = document.getElementById('btn-update-account');
    const guestId = sessionStorage.getItem('guestId');

    // === Lectura de campos del formulario ===
    const firstName = document.getElementById('pro-firstname').value.trim();
    const lastName = document.getElementById('pro-lastname').value.trim();
    const phone = document.getElementById('pro-phone').value.trim();
    const email = document.getElementById('pro-email').value.trim();
    const confirmEmail = document.getElementById('pro-confirm-email').value.trim();
    const password = document.getElementById('pro-password').value;
    const confirmPassword = document.getElementById('pro-confirm-password').value;
    const chkPrivacy = document.getElementById('chk-privacy').checked;

    // === Validación frontend ===
    // Se usan toasts en lugar de alert() para mantener la coherencia visual del sitio
    // y no bloquear el hilo de renderizado con diálogos nativos del navegador
    if (!firstName || !lastName || !email) {
        showToast("Por favor, completa los campos obligatorios (*).", true);
        return;
    }
    if (email !== confirmEmail) {
        showToast("Los correos no coinciden.", true);
        return;
    }
    // La contraseña solo se valida si el usuario ha escrito algo;
    // campo vacío = sin cambio de contraseña, lo gestiona el backend
    if (password && password !== confirmPassword) {
        showToast("Las nuevas contraseñas no coinciden.", true);
        return;
    }
    if (!chkPrivacy) {
        showToast("Debes aceptar los términos de privacidad.", true);
        return;
    }

    // === Bloqueo del botón anti-doble clic ===
    // Se deshabilita antes del await para cubrir todo el tiempo de espera de la petición
    btnUpdate.disabled = true;
    btnUpdate.innerText = "UPDATING...";

    // === Construcción del payload ===
    // Si password está vacío, Spring Boot lo ignora en el backend
    // para no sobreescribir la contraseña existente con un valor nulo
    const updatedGuest = {
        names: firstName,
        surnames: lastName,
        email: email,
        phone: phone,
        password: password
    };

    // === Envío a la API ===
    try {
        // PUT /api/guests/:id → actualiza el perfil del huésped y espera 200 OK
        const response = await fetch(`/api/guests/${guestId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedGuest)
        });

        if (response.ok) {
            // Actualiza el nombre en sessionStorage para que el badge de la navbar
            // refleje el nuevo nombre sin necesidad de un nuevo login
            sessionStorage.setItem('guestName', firstName + " " + lastName);

            showToast("¡Tu perfil ha sido actualizado correctamente!");

            // La recarga fuerza a booking-logic.js a releer sessionStorage y regenerar
            // las iniciales del badge con el nombre actualizado
            setTimeout(() => {
                window.location.reload();
            }, 3000);

        } else {
            // Error controlado del servidor (ej. email ya registrado, violación de constraints)
            const error = await response.text();
            showToast("Error: " + error, true);
        }
    } catch (e) {
        console.error(e);
        // Error de red (sin conexión, timeout, CORS): el finally restaura el botón
        showToast("Error de conexión con el servidor.", true);
    } finally {
        // Se restaura el botón tanto en éxito como en error para permitir reintentos
        // En el caso de éxito la recarga lo hace irrelevante, pero es una buena práctica
        btnUpdate.disabled = false;
        btnUpdate.innerText = "UPDATE";
    }
}