// ============================================================
//  REGISTER.JS — Lógica de la página de registro de nuevos huéspedes
//  Responsabilidades:
//    · Validación frontend de campos obligatorios, coincidencias y seguridad
//    · Generación de un DNI provisional hasta que el huésped complete su perfil
//    · Alta del huésped vía POST a la API de autenticación
//    · Inicialización de la sesión en sessionStorage tras el registro exitoso
//    · Redirección automática al motor de reservas una vez creada la cuenta
// ============================================================


// ============================================================
//  SECCIÓN 1 — REGISTRO DEL HUÉSPED
// ============================================================

/**
 * Valida el formulario de registro, construye el payload del nuevo huésped
 * y lo envía a la API. Si el alta es exitosa, inicializa la sesión en
 * sessionStorage y redirige al motor de reservas para que el usuario
 * pueda reservar de inmediato sin necesidad de hacer login por separado.
 * Bloquea el botón durante el envío para prevenir registros duplicados.
 *
 * @returns {Promise<void>}
 */
async function processRegistration() {
    const btnCreate = document.getElementById('btn-create-account');

    // === Lectura de campos del formulario ===
    const firstName = document.getElementById('reg-firstname').value.trim();
    const lastName = document.getElementById('reg-lastname').value.trim();
    const phone = document.getElementById('reg-phone').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const confirmEmail = document.getElementById('reg-confirm-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const confirmPassword = document.getElementById('reg-confirm-password').value;

    const chkPrivacy = document.getElementById('chk-privacy').checked;
    const chkCreate = document.getElementById('chk-create-acc').checked;

    // === Validación frontend ===
    // Se usan toasts en lugar de alert() para mantener la coherencia visual del sitio.
    // Cada return corta la ejecución antes de llegar al fetch, evitando peticiones inválidas.

    // Campos mínimos para crear una cuenta funcional en el sistema
    if (!firstName || !lastName || !email || !password) {
        showToast("Por favor, completa todos los campos obligatorios (*).", true);
        return;
    }
    // Doble campo de email para prevenir errores tipográficos en un dato crítico de la cuenta
    if (email !== confirmEmail) {
        showToast("Los correos electrónicos no coinciden.", true);
        return;
    }
    // A diferencia de Profile.js, la contraseña es siempre obligatoria en el registro
    if (password !== confirmPassword) {
        showToast("Las contraseñas no coinciden.", true);
        return;
    }
    // Validación de longitud mínima alineada con la política de seguridad del backend
    if (password.length < 8) {
        showToast("La contraseña debe tener al menos 8 caracteres.", true);
        return;
    }
    // Ambos checkboxes son obligatorios: privacidad y confirmación explícita de crear cuenta
    if (!chkPrivacy || !chkCreate) {
        showToast("Debes aceptar los términos y marcar la casilla de creación de cuenta.", true);
        return;
    }

    // === Bloqueo del botón anti-doble clic ===
    // Se deshabilita antes del await para cubrir todo el tiempo de espera de la petición
    btnCreate.disabled = true;
    btnCreate.innerText = "CREATING...";

    // === Construcción del payload ===
    // Los campos de dirección (country, city) se inicializan con valores provisionales
    // porque el backend los requiere como no nulos. El huésped los completará en el checkout.
    // El DNI se genera con un prefijo "REG" + timestamp truncado para garantizar unicidad
    // sin depender de un documento de identidad real en el momento del registro.
    const newGuest = {
        names: firstName,
        surnames: lastName,
        email: email,
        password: password,
        phone: phone,
        dni: "REG" + Date.now().toString().slice(-10), // DNI provisional único basado en timestamp
        country: "Por definir",
        city: "Por definir",
        state: 1 // Estado activo por defecto; el backend lo mapea al enum correspondiente
    };

    // === Envío a la API ===
    try {
        // POST /api/auth/register → espera { guestName, guestId } si el alta es correcta
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newGuest)
        });

        if (response.ok) {
            const data = await response.json();

            // Inicializa la sesión directamente tras el registro para que el usuario
            // no tenga que hacer login por separado antes de reservar
            sessionStorage.setItem('guestName', data.guestName);
            sessionStorage.setItem('guestId', data.guestId);
            sessionStorage.setItem('isLoggedIn', 'true');

            showToast("Tu cuenta ha sido creada correctamente.");

            // El retardo de 4.5s permite que el usuario lea el toast de confirmación
            // antes de ser redirigido al motor de reservas
            setTimeout(() => {
                window.location.href = "/reservation/booking";
            }, 4500);

        } else {
            // Error controlado del servidor (ej. email ya registrado, datos inválidos):
            // se restaura el botón manualmente porque el finally no existe en este flujo
            const error = await response.text();
            showToast("Error: " + error, true);
            btnCreate.disabled = false;
            btnCreate.innerText = "CREATE";
        }
    } catch (e) {
        // Error de red (sin conexión, timeout, CORS): también restaura el botón para reintentar
        console.error(e);
        showToast("Error de conexión. Intenta nuevamente.", true);
        btnCreate.disabled = false;
        btnCreate.innerText = "CREATE";
    }
}