// ============================================================
//  CHECKOUT.JS — Lógica de la página de checkout de reservas
//  Responsabilidades:
//    · Validación de acceso (sesión y selección previa)
//    · Renderizado del carrito y la navbar en la vista de checkout
//    · Precarga de datos del huésped autenticado desde la API
//    · Autoguardado del formulario en sessionStorage (anti-F5)
//    · Dropdowns personalizados de país y método de pago
//    · Acordeón de peticiones especiales
//    · Validación de consentimientos y envío final de la reserva
// ============================================================


// ============================================================
//  SECCIÓN 1 — INICIALIZACIÓN AL CARGAR LA PÁGINA
// ============================================================

/**
 * Punto de entrada principal del checkout.
 * Verifica que el usuario llegó con una selección de fechas y habitación válida;
 * si no, lo redirige a la página de reservas para evitar un estado incoherente.
 * Si la sesión es válida, inicializa todos los componentes de la página en orden.
 */
document.addEventListener('DOMContentLoaded', async () => {
    const checkInDate = sessionStorage.getItem('booking_checkin');
    const roomId = sessionStorage.getItem('booking_roomId');
    
    // Guarda de acceso: redirige si el usuario llega al checkout sin haber
    // seleccionado fechas y habitación en el paso anterior
    if (!checkInDate || !roomId) {
        window.location.href = '/reservation/booking';
        return;
    }

    renderCheckoutCart();           // Rellena el carrito lateral con los datos de sessionStorage
    renderCheckoutNavbar();         // Muestra el badge de perfil en lugar del botón de login
    await fillUserData();           // Precarga los datos del huésped desde la API
    initAutosave();                 // Activa el guardado automático del formulario
    initAcknowledgementValidation();// Habilita el botón de confirmación solo con los checkboxes marcados
});


// ============================================================
//  SECCIÓN 2 — RENDERIZADO DEL CARRITO Y LA NAVBAR
// ============================================================

/**
 * Rellena el widget del carrito lateral con los datos de la reserva
 * almacenados en sessionStorage: fechas, huéspedes, habitación y total.
 * También actualiza el nombre de la habitación en el bloque de políticas.
 */
function renderCheckoutCart() {
    const checkInDate = new Date(sessionStorage.getItem('booking_checkin'));
    const checkOutDate = new Date(sessionStorage.getItem('booking_checkout'));
    const adults = parseInt(sessionStorage.getItem('booking_adults')) || 2;
    const roomName = sessionStorage.getItem('booking_roomName');
    const roomPrice = parseFloat(sessionStorage.getItem('booking_roomPrice'));
    const policyRoomNameEl = document.getElementById('policy-room-name');

    const options = { day: 'numeric', month: 'short' };
    document.getElementById('cart-dates').innerText = `${checkInDate.toLocaleDateString('es-ES', options)} - ${checkOutDate.toLocaleDateString('es-ES', options)}`;
    document.getElementById('cart-guests').innerText = `${adults} adultos`;
    
    // La habitación está oculta por defecto en el widget; se muestra aquí
    // porque en el checkout siempre hay una habitación seleccionada
    document.getElementById('cart-room-wrapper').classList.remove('hidden');
    document.getElementById('cart-room-name').innerText = roomName;

    // Total = precio base × número de adultos × número de noches
    const nights = Math.ceil((checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
    const total = roomPrice * adults * nights;
    document.getElementById('cart-total').innerText = `¥${total.toLocaleString('es-ES')}`;
    
    // Actualiza el nombre de la habitación en el bloque de políticas de cancelación
    if (policyRoomNameEl && roomName) {
        policyRoomNameEl.innerText = `HABITACION: ${roomName}`.toUpperCase();
    }
}

/**
 * Actualiza la navbar del checkout para mostrar el badge de perfil del huésped
 * en lugar del botón de login, dado que en esta vista el usuario siempre está autenticado.
 * Genera las iniciales del nombre directamente aquí en lugar de llamar a getInitials()
 * de globalBooking.js para mantener este módulo independiente.
 */
function renderCheckoutNavbar() {
    const guestName = sessionStorage.getItem('guestName') || '';
    const names = guestName.trim().split(' ');
    let initials = '';
    if (names.length > 0) initials += names[0].charAt(0).toUpperCase();
    if (names.length > 1) initials += names[names.length - 1].charAt(0).toUpperCase();

    // Oculta el enlace de login y muestra el wrapper del perfil con las iniciales
    document.getElementById('login-wrapper')?.classList.add('hidden');
    document.getElementById('profile-wrapper')?.classList.remove('hidden');
    if(document.getElementById('profile-badge')) {
        document.getElementById('profile-badge').innerText = initials;
    }
}


// ============================================================
//  SECCIÓN 3 — PRECARGA DE DATOS DEL HUÉSPED DESDE LA API
// ============================================================

/**
 * Consulta la API de huéspedes y rellena automáticamente los campos
 * del formulario de contacto con los datos del perfil del usuario autenticado.
 * Si el huésped no está logueado (sin guestId) o la petición falla,
 * el formulario queda vacío para que el usuario lo complete manualmente.
 *
 * @returns {Promise<void>}
 */
async function fillUserData() {
    const guestId = sessionStorage.getItem('guestId');
    // Sin guestId no hay sesión activa; se continúa sin precargar datos
    if(!guestId) return;

    try {
        // GET /api/guests/:id → espera { names, surnames, email, phone, mobilePhone }
        const response = await fetch(`/api/guests/${guestId}`);
        if (response.ok) {
            const guest = await response.json();
            
            // Precarga solo los campos de contacto; los de dirección se dejan vacíos
            // para que el usuario los confirme activamente (pueden haber cambiado)
            document.getElementById('chk-names').value = guest.names || '';
            document.getElementById('chk-surnames').value = guest.surnames || '';
            document.getElementById('chk-email').value = guest.email || '';
            document.getElementById('chk-phone').value = guest.phone || '';
            document.getElementById('chk-mobile').value = guest.mobilePhone || '';
        } else {
            showToast("No pudimos cargar tus datos de perfil. Intenta recargar la página.", true);
        }
    } catch (error) {
        console.error("Error cargando los datos del usuario:", error);
        showToast("Error de red al intentar cargar tus datos.", true);
    }
}


// ============================================================
//  SECCIÓN 4 — ENVÍO FINAL DE LA RESERVA
// ============================================================

/**
 * Valida el formulario, construye el payload de la reserva y lo envía a la API.
 * Aplica una estrategia de bloqueo del botón durante el envío para prevenir
 * dobles clics y el consecuente riesgo de reservas duplicadas.
 * En caso de éxito, limpia sessionStorage y redirige al historial de reservas.
 * En caso de error (validación, red o servidor), restaura el botón y muestra feedback.
 *
 * @returns {Promise<void>}
 */
async function processFinalReservation() {

    // === Paso 1: Validación frontend ===
    // Se validan los campos de dirección porque fillUserData() no los precarga;
    // el usuario debe completarlos activamente antes de confirmar
    const phone = document.getElementById('chk-phone').value.trim();
    const address = document.getElementById('chk-address').value.trim();
    const city = document.getElementById('chk-city').value.trim();
    const zip = document.getElementById('chk-zip').value.trim();
    const country = document.getElementById('chk-country').value.trim();

    if (!phone || !address || !city || !zip || !country) {
        showToast("Por favor, completa todos los campos obligatorios (Teléfono, Dirección, Ciudad, Código Postal, País).", true);
        // El return cancela la ejecución antes de llegar al fetch, evitando peticiones inválidas
        return;
    }

    // === Paso 2: Bloqueo del botón anti-doble clic ===
    // Se deshabilita y cambia el texto inmediatamente tras superar la validación,
    // antes del await, para cubrir el tiempo de espera de la respuesta del servidor
    const btn = document.getElementById('btn-confirm-booking');
    btn.disabled = true;
    btn.innerText = "PROCESSING...";

    // === Paso 3: Construcción del payload ===
    // Los datos del formulario se combinan con los de sessionStorage para
    // construir el objeto completo que espera el endpoint de confirmación
    const guestData = {
        idGuest: parseInt(sessionStorage.getItem('guestId')),
        phone: phone,
        mobilePhone: document.getElementById('chk-mobile').value.trim(),
        address: address,
        country: country,
        city: city,
        postalCode: zip
    };

    const reservationData = {
        guest: guestData,
        room: { idRoom: parseInt(sessionStorage.getItem('booking_roomId')) },
        // Las fechas se convierten a formato ISO "YYYY-MM-DD" eliminando la parte de hora
        entryDate: new Date(sessionStorage.getItem('booking_checkin')).toISOString().split('T')[0],
        departureDate: new Date(sessionStorage.getItem('booking_checkout')).toISOString().split('T')[0],
        pricePerNight: parseFloat(sessionStorage.getItem('booking_roomPrice')),
        numAdults: parseInt(sessionStorage.getItem('booking_adults')) || 2, 
        observations: document.getElementById('chk-observations').value.trim(),
        paymentMethod: document.getElementById('chk-payment-method').value
    };

    // === Paso 4: Envío a la API ===
    try {
        // POST /api/reservations/confirm → espera { reservationCode } si la reserva se crea correctamente
        const response = await fetch('/api/reservations/confirm', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reservationData)
        });

        if (response.ok) {
            const result = await response.json();
            
            showToast("¡Reserva exitosa! Tu código es: " + result.reservationCode);
            
            // Limpia los datos de la reserva activa del sessionStorage;
            // se conservan guestId y guestName para mantener la sesión del usuario
            sessionStorage.removeItem('booking_roomId');
            sessionStorage.removeItem('booking_checkin');
            sessionStorage.removeItem('booking_checkout');
            sessionStorage.removeItem('checkout_draft'); 
            
            // Redirige tras 5 segundos para que el usuario pueda leer el toast de confirmación
            setTimeout(() => {
                window.location.href = "/reservation/my-reservations"; 
            }, 5000);

        } else {
            // Error controlado del servidor (ej. habitación ya no disponible):
            // se muestra el mensaje de error y se restaura el botón para reintentar
            const error = await response.text();
            showToast("Error en la reserva: " + error, true); 
            btn.disabled = false;
            btn.innerText = "CONFIRM BOOKING";
        }
    } catch (error) {
        // Error de red (sin conexión, timeout, CORS): también restaura el botón
        console.error("Error en la conexión:", error);
        showToast("Error de conexión. Por favor, intenta de nuevo.", true);
        btn.disabled = false;
        btn.innerText = "CONFIRM BOOKING";
    }
}


// ============================================================
//  SECCIÓN 5 — AUTOGUARDADO DEL FORMULARIO (ANTI-F5)
// ============================================================

/**
 * Inicializa el sistema de autoguardado del formulario de checkout.
 * Persiste los valores de los campos en sessionStorage bajo la clave
 * 'checkout_draft' para que el usuario no pierda lo escrito si recarga la página.
 * Al inicializarse, restaura el borrador si existe.
 * Escucha eventos 'input' para campos de texto y 'click' para dropdowns personalizados.
 */
function initAutosave() {
    const inputIds = [
        'chk-phone', 'chk-mobile', 'chk-address', 'chk-address2', 
        'chk-city', 'chk-zip', 'chk-observations'
    ];

    // === Restauración del borrador ===
    // Se usa || {} para obtener un objeto vacío si no hay borrador previo,
    // evitando errores al iterar sobre él
    const savedData = JSON.parse(sessionStorage.getItem('checkout_draft')) || {};
    
    // Restaura los campos de texto estándar
    inputIds.forEach(id => {
        const el = document.getElementById(id);
        if (el && savedData[id]) { el.value = savedData[id]; }
    });

    // Los dropdowns personalizados requieren restaurar tanto el input visible
    // como el hidden, ya que son dos elementos distintos
    if (savedData['chk-country']) {
        document.getElementById('chk-country').value = savedData['chk-country'];
        document.getElementById('country-display').value = savedData['country-display'];
    }
    if (savedData['chk-payment-method']) {
        document.getElementById('chk-payment-method').value = savedData['chk-payment-method'];
        document.getElementById('payment-display').value = savedData['payment-display'];
    }

    // === Guardado en tiempo real: campos de texto ===
    // El listener de 'input' en el documento evita registrar N listeners individuales;
    // el filtro por inputIds garantiza que solo se persistan los campos del checkout
    document.addEventListener('input', (e) => {
        if (inputIds.includes(e.target.id)) {
            savedData[e.target.id] = e.target.value;
            sessionStorage.setItem('checkout_draft', JSON.stringify(savedData));
        }
    });

    // === Guardado en tiempo real: dropdowns personalizados ===
    // Los dropdowns no emiten eventos 'input', por lo que se escucha 'click'
    // en sus opciones. El setTimeout de 50ms asegura que populateCountries()
    // e initPaymentDropdown() ya hayan actualizado los inputs antes de leer sus valores
    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('custom-option')) {
            setTimeout(() => {
                savedData['chk-country'] = document.getElementById('chk-country').value;
                savedData['country-display'] = document.getElementById('country-display').value;
                savedData['chk-payment-method'] = document.getElementById('chk-payment-method').value;
                savedData['payment-display'] = document.getElementById('payment-display').value;
                sessionStorage.setItem('checkout_draft', JSON.stringify(savedData));
            }, 50);
        }
    });
}


// ============================================================
//  SECCIÓN 6 — SEGUNDO LISTENER DOMContentLoaded
//  (Dropdowns y acordeón: separados del listener principal
//   para aislar la lógica de UI del flujo de inicialización)
// ============================================================

document.addEventListener('DOMContentLoaded', async () => {
    // Construye el dropdown de países y el de métodos de pago
    populateCountries();
    initPaymentDropdown();

    // === Acordeón de peticiones especiales ===
    // Al hacer clic, alterna la visibilidad del textarea y rota el icono chevron
    // actualizando su SVG directamente para evitar transformaciones CSS adicionales
    const srToggle = document.getElementById('special-requests-toggle');
    const srContent = document.getElementById('special-requests-content');
    const srIcon = document.getElementById('sr-icon');

    if (srToggle && srContent) {
        srToggle.addEventListener('click', () => {
            srContent.classList.toggle('hidden');
            
            // Chevron hacia abajo (colapsado) o hacia arriba (expandido)
            if (srContent.classList.contains('hidden')) {
                srIcon.innerHTML = '<polyline points="6 9 12 15 18 9"></polyline>';
            } else {
                srIcon.innerHTML = '<polyline points="18 15 12 9 6 15"></polyline>';
            }
        });
    }
});


// ============================================================
//  SECCIÓN 7 — DROPDOWN DE PAÍS
// ============================================================

// Lista reducida de países; ampliar aquí si se requiere cobertura global
const countryList = [
    "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Argentina", "Australia",
    "Brazil", "Canada", "Chile", "China", "Colombia", "Ecuador", "France", "Germany",
    "Italy", "Mexico", "Spain", "United Kingdom", "Venezuela"
];

/**
 * Construye y activa el dropdown personalizado del selector de país.
 * La lista se divide en dos grupos: países favoritos en la parte superior
 * (separados por un divisor visual) y el resto de países en orden alfabético.
 * Sincroniza el input visible (#country-display) con el hidden (#chk-country)
 * que es el que se envía al servidor.
 */
function populateCountries() {
    const wrapper = document.getElementById('country-wrapper');
    const displayInput = document.getElementById('country-display');
    const hiddenInput = document.getElementById('chk-country');
    const listContainer = document.getElementById('country-list');

    if (!listContainer || !wrapper) return;

    listContainer.innerHTML = ''; 

    // Países con mayor volumen de reservas, mostrados en primer lugar
    // para reducir el tiempo de búsqueda del usuario más habitual
    const topCountries = ["Estados Unidos", "Japan"];
    topCountries.forEach(c => {
        listContainer.innerHTML += `<div class="custom-option" data-value="${c}">${c}</div>`;
    });

    // Separador visual entre favoritos y lista general
    listContainer.innerHTML += `<div class="custom-divider"></div>`;

    // Lista general en el orden del array (alfabético si se mantiene ordenado)
    countryList.forEach(c => {
        listContainer.innerHTML += `<div class="custom-option" data-value="${c}">${c}</div>`;
    });

    // === Apertura/cierre del dropdown ===
    // stopPropagation evita que el listener global de cierre (más abajo)
    // se ejecute inmediatamente y vuelva a cerrar el dropdown recién abierto
    wrapper.addEventListener('click', (e) => {
        e.stopPropagation();
        listContainer.classList.toggle('hidden');
        // Cambia el color del borde para indicar el estado activo del campo
        wrapper.style.borderColor = listContainer.classList.contains('hidden') ? '#000' : '#aeb3b7';
    });

    // === Selección de un país ===
    const options = listContainer.querySelectorAll('.custom-option');
    options.forEach(opt => {
        opt.addEventListener('click', (e) => {
            e.stopPropagation();
            const val = opt.getAttribute('data-value');
            // El input visible muestra el texto legible; el hidden almacena el valor enviado
            displayInput.value = val;
            hiddenInput.value = val;
            
            listContainer.classList.add('hidden');
            wrapper.style.borderColor = '#000';
        });
    });

    // Cierra el dropdown al hacer clic en cualquier parte fuera del wrapper
    document.addEventListener('click', (e) => {
        if (!wrapper.contains(e.target)) {
            listContainer.classList.add('hidden');
            wrapper.style.borderColor = '#000';
        }
    });
}


// ============================================================
//  SECCIÓN 8 — DROPDOWN DE MÉTODO DE PAGO
// ============================================================

/**
 * Inicializa el dropdown personalizado del selector de método de pago.
 * Las opciones están definidas estáticamente en el HTML (no requieren API).
 * Sincroniza el input visible (#payment-display) con el hidden (#chk-payment-method)
 * que almacena el valor de código (Card/Cash/Transfer) enviado al servidor.
 */
function initPaymentDropdown() {
    const wrapper = document.getElementById('payment-wrapper');
    const displayInput = document.getElementById('payment-display');
    const hiddenInput = document.getElementById('chk-payment-method');
    const listContainer = document.getElementById('payment-list');

    if (!wrapper || !listContainer) return;

    // stopPropagation por el mismo motivo que en populateCountries():
    // evitar que el listener global de cierre cancele la apertura inmediatamente
    wrapper.addEventListener('click', (e) => {
        e.stopPropagation(); 
        listContainer.classList.toggle('hidden');
        wrapper.style.borderColor = listContainer.classList.contains('hidden') ? '#000' : '#aeb3b7';
    });

    // === Selección de método de pago ===
    // A diferencia del selector de país, aquí se diferencia el texto visible (text)
    // del valor interno (val) porque el backend espera códigos cortos (Card, Cash, Transfer)
    const options = listContainer.querySelectorAll('.custom-option');
    options.forEach(opt => {
        opt.addEventListener('click', (e) => {
            e.stopPropagation();
            const val = opt.getAttribute('data-value'); // Valor de código para el servidor
            const text = opt.innerText;                  // Texto legible para el usuario
            
            displayInput.value = text;
            hiddenInput.value = val;
            
            listContainer.classList.add('hidden');
            wrapper.style.borderColor = '#000';
        });
    });

    // Cierra el dropdown al hacer clic fuera del wrapper
    document.addEventListener('click', (e) => {
        if (!wrapper.contains(e.target)) {
            listContainer.classList.add('hidden');
            wrapper.style.borderColor = '#000';
        }
    });
}


// ============================================================
//  SECCIÓN 9 — VALIDACIÓN DE CONSENTIMIENTOS
// ============================================================

/**
 * Activa la validación de los dos checkboxes obligatorios de consentimiento.
 * El botón de confirmación (#btn-confirm-booking) permanece disabled hasta que
 * ambos checkboxes estén marcados simultáneamente.
 * Se usa el evento 'change' en lugar de 'click' para manejar correctamente
 * la desactivación si el usuario desmarca alguno.
 */
function initAcknowledgementValidation() {
    const chkDetails = document.getElementById('chk-ack-details');
    const chkConditions = document.getElementById('chk-ack-conditions');
    const btnConfirm = document.getElementById('btn-confirm-booking');

    if (!chkDetails || !chkConditions || !btnConfirm) return;

    // Función de validación compartida por ambos listeners:
    // el botón solo se habilita cuando los dos checkboxes están marcados a la vez
    function validateCheckboxes() {
        if (chkDetails.checked && chkConditions.checked) {
            btnConfirm.disabled = false;
        } else {
            btnConfirm.disabled = true;
        }
    }

    // Ambos checkboxes disparan la misma función para recalcular el estado del botón
    chkDetails.addEventListener('change', validateCheckboxes);
    chkConditions.addEventListener('change', validateCheckboxes);
}