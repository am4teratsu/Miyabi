// ============================================================
//  BOOKING LOGIC — Lógica principal del flujo de reservas
//  Responsabilidades:
//    · Autenticación de usuario (login / logout / sesión)
//    · Selector de huéspedes
//    · Calendario doble con selección de rango de fechas
//    · Consulta y renderizado de habitaciones disponibles
//    · Carrito de reserva y navegación al checkout
// ============================================================


// ============================================================
//  SECCIÓN 1 — AUTENTICACIÓN Y ESTADO DE SESIÓN
// ============================================================

/**
 * Genera las iniciales de un nombre completo para mostrarlas
 * en el badge del perfil de la navbar (ej. "Kenji Tanaka" → "KT").
 *
 * @param {string} fullName - Nombre completo del huésped.
 * @returns {string} Una o dos letras en mayúscula.
 */
function getInitials(fullName) {
    const names = fullName.trim().split(' ');
    if (names.length === 0) return '';
    if (names.length === 1) return names[0].charAt(0).toUpperCase();
    // Combina la inicial del primer y último token para nombres compuestos
    return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
}


/**
 * Actualiza la navbar según el estado de sesión almacenado en sessionStorage.
 * Muestra el botón de login si no hay sesión activa, o el badge de perfil si la hay.
 */
function updateNavbarState() {
    const isLoggedIn = sessionStorage.getItem('isLoggedIn') === 'true';
    const loginWrapper = document.getElementById('login-wrapper');
    const profileWrapper = document.getElementById('profile-wrapper');
    const profileBadge = document.getElementById('profile-badge'); 
    
    if (isLoggedIn) {
        const guestName = sessionStorage.getItem('guestName') || 'User';
        if (loginWrapper) loginWrapper.classList.add('hidden'); 
        if (profileWrapper) profileWrapper.classList.remove('hidden'); 
        // Reemplaza la foto de perfil con las iniciales del nombre del huésped
        if (profileBadge) profileBadge.innerText = getInitials(guestName); 
    } else {
        if (loginWrapper) loginWrapper.classList.remove('hidden');
        if (profileWrapper) profileWrapper.classList.add('hidden');
    }
}

/**
 * Abre o cierra el modal de login.
 * Al abrirlo, fuerza el cierre de cualquier otro modal activo
 * para evitar superposiciones en la interfaz.
 *
 * @param {Event} [event] - Evento del DOM (opcional). Se previene su comportamiento por defecto.
 */
function toggleLoginModal(event) {
    if(event) event.preventDefault();
    const modal = document.getElementById('login-modal');
    
    if (modal) {
        modal.classList.toggle('hidden');
    }

    // Cierra otros modales que pudieran estar abiertos simultáneamente
    if (modal && !modal.classList.contains('hidden')) {
        document.getElementById('profile-modal')?.classList.add('hidden');
        document.getElementById('calendar-modal')?.classList.add('hidden');
        document.getElementById('guest-modal')?.classList.add('hidden');
    }
}


/**
 * Maneja el envío del formulario de login.
 * Realiza una petición POST a la API con las credenciales del usuario.
 * Si la autenticación es exitosa, persiste los datos de sesión en sessionStorage
 * y actualiza la UI sin recargar la página.
 *
 * @param {Event} event - Submit event del formulario de login.
 * @returns {Promise<void>}
 */
async function handleLogin(event) {
    event.preventDefault(); 
    
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const errorMsg = document.getElementById('login-error-msg');
    
    // Oculta el mensaje de error previo antes de cada intento
    errorMsg.classList.add('hidden'); 

    try {
        // POST /api/auth/login → espera { guestName, guestId } si las credenciales son válidas
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email, password: password })
        });

        if (response.ok) {
            const data = await response.json();
            
            // Persiste la sesión en sessionStorage para que sobreviva
            // a las navegaciones dentro del flujo de reservas sin re-autenticar
            sessionStorage.setItem('guestName', data.guestName);
            sessionStorage.setItem('guestId', data.guestId);
            sessionStorage.setItem('isLoggedIn', 'true');
            
            toggleLoginModal(); 
            updateNavbarState(); 
            showToast(`¡Bienvenido de nuevo, ${data.guestName}!`);
            
        } else {
            // Credenciales incorrectas: muestra error inline y toast
            errorMsg.classList.remove('hidden');
            showToast("Correo o contraseña incorrectos.", true);
        }
    } catch (error) {
        // Error de red o servidor no disponible
        console.error("Error al conectar con el servidor:", error);
        showToast("Error de conexión con el servidor. Intenta nuevamente.", true);
    }
}

/**
 * Abre o cierra el modal de perfil del huésped autenticado.
 * Al abrirlo, cierra el resto de modales activos.
 *
 * @param {Event} [event] - Evento del DOM (opcional).
 */
function toggleProfileModal(event) {
    if(event) event.preventDefault();
    const modal = document.getElementById('profile-modal');
    modal.classList.toggle('hidden');

    // Cierra otros modales que pudieran estar abiertos simultáneamente
    if (!modal.classList.contains('hidden')) {
        document.getElementById('login-modal')?.classList.add('hidden');
        document.getElementById('calendar-modal')?.classList.add('hidden');
        document.getElementById('guest-modal')?.classList.add('hidden');
    }
}

/**
 * Cierra la sesión del huésped.
 * Notifica al servidor (fire-and-forget) y limpia sessionStorage
 * antes de redirigir a la página de reservas.
 *
 * @param {Event} [event] - Evento del DOM (opcional).
 * @returns {Promise<void>}
 */
async function handleLogout(event) {
    if(event) event.preventDefault();
    
    try {
        // Notifica al servidor para invalidar la cookie/sesión server-side.
        // Si falla, se continúa con el logout local igualmente.
        await fetch('/api/auth/logout', { method: 'POST' });
    } catch (e) { console.error(e); }
    
    // Limpia el estado de sesión local independientemente de la respuesta del servidor
    sessionStorage.removeItem('guestName');
    sessionStorage.removeItem('guestId'); 
    sessionStorage.setItem('isLoggedIn', 'false');
    
    window.location.href = '/reservation/booking';
}


// ============================================================
//  SECCIÓN 2 — INICIALIZACIÓN AL CARGAR LA PÁGINA
//  Verifica la sesión con el servidor, sincroniza sessionStorage
//  y arranca todos los componentes de la UI en el orden correcto.
// ============================================================

document.addEventListener('DOMContentLoaded', async () => {
    try {
        // GET /api/auth/check → verifica si la cookie de sesión del servidor
        // sigue siendo válida (útil tras recargar página o abrir nueva pestaña)
        const response = await fetch('/api/auth/check');
        const data = await response.json();

        if (data.isLoggedIn) {
            // Sincroniza sessionStorage con los datos frescos del servidor
            sessionStorage.setItem('isLoggedIn', 'true');
            sessionStorage.setItem('guestName', data.guestName);
            sessionStorage.setItem('guestId', data.guestId);
        } else {
            // Limpia cualquier dato de sesión residual si el servidor la considera expirada
            sessionStorage.clear();
        }
    } catch (e) { console.error("Error verificando sesión"); }
        
    // Inicializa todos los componentes con el estado persistido en sessionStorage
    updateNavbarState();   // Navbar: login vs. badge de perfil
    updateGuestUI();       // Selector de huéspedes
    updateDisplay();       // Fechas de check-in / check-out en la barra del widget
    updateCartUI();        // Resumen del carrito lateral
    
    // Si ya hay fechas seleccionadas (sesión previa), lanza la búsqueda directamente
    if (checkInDate && checkOutDate) {
        fetchAndRenderRooms();
    }
});


// ============================================================
//  SECCIÓN 3 — DISPONIBILIDAD Y RENDERIZADO DE HABITACIONES
// ============================================================

/**
 * Consulta la API de tipos de habitación y filtra los resultados
 * según la capacidad mínima requerida para los huéspedes seleccionados.
 * Actualiza el contenedor de habitaciones con un estado de carga y,
 * al recibir la respuesta, delega el renderizado a `renderRoomList`.
 *
 * @returns {Promise<void>}
 */
async function fetchAndRenderRooms() {
    const listContainer = document.getElementById('room-list-container');
    if(!listContainer) return;

    // Muestra estado de carga mientras se espera la respuesta de la API
    listContainer.innerHTML = '<p style="padding: 20px;">Buscando habitaciones disponibles...</p>';
    
    try {
        // GET /api/room-types → devuelve array de { idTipo, nameType, capacityPeople, basePrice, ... }
        const response = await fetch('/api/room-types'); 
        const roomTypes = await response.json();
        // Filtra en cliente: solo muestra habitaciones que admitan el número de adultos seleccionados
        const availableRooms = roomTypes.filter(rt => rt.capacityPeople >= selectedAdults);
        renderRoomList(availableRooms);
    } catch (error) {
        console.error("Error buscando habitaciones:", error);
        listContainer.innerHTML = '<p style="color:red; padding: 20px;">Hubo un error al conectar con la base de datos.</p>';
    }
}


// ============================================================
//  SECCIÓN 4 — SELECTOR DE HUÉSPEDES
// ============================================================

// Recupera el número de adultos de la sesión anterior o usa 2 como valor por defecto
let selectedAdults = parseInt(sessionStorage.getItem('booking_adults')) || 2;
const MAX_ALLOWED = 6; // Límite máximo de huéspedes por política del hotel

// Asegura que el valor inicial quede persistido desde el primer momento
sessionStorage.setItem('booking_adults', selectedAdults);

/**
 * Abre o cierra el modal de selección de huéspedes.
 * Usa stopPropagation para evitar que el click se propague al listener
 * global del documento que cierra modales al hacer clic fuera.
 *
 * @param {Event} [event] - Evento del DOM (opcional).
 */
function toggleGuestModal(event) {
    if (event) event.stopPropagation();
    const modal = document.getElementById('guest-modal');
    modal.classList.toggle('hidden');
}

/**
 * Incrementa o decrementa el contador de adultos respetando el rango [1, MAX_ALLOWED].
 * Persiste el nuevo valor y actualiza la UI y los resultados de habitaciones.
 *
 * @param {number} change - Valor a sumar al contador actual (+1 o -1).
 */
function updateGuestCount(change) {
    let newCount = selectedAdults + change;
    if (newCount >= 1 && newCount <= MAX_ALLOWED) {
        selectedAdults = newCount;
        sessionStorage.setItem('booking_adults', selectedAdults); 
        updateGuestUI();
        // Relanza la búsqueda de habitaciones para reflejar la nueva capacidad requerida
        fetchAndRenderRooms(); 
    }
}

/**
 * Sincroniza los elementos del DOM del selector de huéspedes
 * con el valor actual de `selectedAdults`, tanto en el modal
 * como en la barra del widget.
 */
function updateGuestUI() {
    const guestNumberEl = document.getElementById('modal-guest-number');
    const criteriaValEl = document.getElementById('criteria-guests-val');

    if (guestNumberEl) {
        guestNumberEl.innerText = selectedAdults;
    }
    
    if (criteriaValEl) {
        // Singulariza el texto correctamente según el número de adultos
        const text = selectedAdults === 1 ? "1 adulto" : `${selectedAdults} adultos`;
        criteriaValEl.innerText = text;
    }
}

// Cierra el modal de huéspedes al hacer clic en cualquier lugar fuera de él o su trigger.
// La comprobación de contains() evita falsos cierres al interactuar con el propio modal.
document.addEventListener('click', function(e) {
    const modal = document.getElementById('guest-modal');
    const trigger = document.getElementById('guests-trigger');

    if (modal && !modal.classList.contains('hidden')) {
        if (!modal.contains(e.target) && !trigger.contains(e.target)) {
            modal.classList.add('hidden');
        }
    }
});


// ============================================================
//  SECCIÓN 5 — CALENDARIO Y SELECCIÓN DE FECHAS
// ============================================================

// Recupera las fechas de la sesión anterior para mantener la selección entre recargas
let checkInDate = sessionStorage.getItem('booking_checkin') ? new Date(sessionStorage.getItem('booking_checkin')) : null;
let checkOutDate = sessionStorage.getItem('booking_checkout') ? new Date(sessionStorage.getItem('booking_checkout')) : null;

// Array de fechas no disponibles obtenidas de la API (formato "YYYY-MM-DD")
let unavailableDates = [];

// Índices del mes que se muestra en la columna izquierda del calendario doble (0 = Enero)
let currentViewYear = 2026;
let currentViewMonth = 1; // Febrero 2026 como vista inicial

const monthNames = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];

/**
 * Abre o cierra el modal del calendario doble.
 * Al abrirlo, cierra otros modales activos y lanza la petición de fechas
 * no disponibles para pintar el estado correcto del calendario.
 *
 * @param {Event} [event] - Evento del DOM (opcional).
 */
function toggleCalendarModal(event) {
    if (event) event.stopPropagation();
    const modal = document.getElementById('calendar-modal');
    modal.classList.toggle('hidden');

    // Cierra otros modales que pudieran estar abiertos
    document.getElementById('guest-modal')?.classList.add('hidden');

    if (!modal.classList.contains('hidden')) {
        // Solo consulta las fechas bloqueadas cuando el calendario se abre
        fetchUnavailableDates();
    }
    
    renderDualCalendar();
}

/**
 * Obtiene del servidor el listado de fechas completamente ocupadas
 * y re-renderiza el calendario con esa información actualizada.
 *
 * @returns {Promise<void>}
 */
async function fetchUnavailableDates() {
    try {
        // GET /api/reservations/unavailable-dates → devuelve array de strings "YYYY-MM-DD"
        const response = await fetch('/api/reservations/unavailable-dates');
        if (response.ok) {
            unavailableDates = await response.json(); 
            // Re-renderiza para aplicar el estilo .day-unavailable a las fechas bloqueadas
            renderDualCalendar(); 
        }
    } catch (error) {
        console.error("Error al cargar fechas sin disponibilidad:", error);
    }
}


/**
 * Avanza la vista del calendario al mes siguiente
 * y re-renderiza ambas columnas.
 */
function nextMonth() {
    currentViewMonth++;
    // Maneja el desbordamiento de año al pasar de diciembre a enero
    if (currentViewMonth > 11) {
        currentViewMonth = 0;
        currentViewYear++;
    }
    renderDualCalendar();
}

/**
 * Retrocede la vista del calendario al mes anterior.
 * Impide navegar hacia meses anteriores al mes actual para
 * evitar la selección de fechas pasadas.
 */
function prevMonth() {
    const today = new Date(2026, 1, 26); 
    // Bloquea la navegación si ya estamos en el mes actual o anterior
    if (currentViewYear === today.getFullYear() && currentViewMonth <= today.getMonth()) {
        return; 
    }
    
    currentViewMonth--;
    // Maneja el desbordamiento de año al retroceder de enero a diciembre
    if (currentViewMonth < 0) {
        currentViewMonth = 11;
        currentViewYear--;
    }
    renderDualCalendar();
}

/**
 * Orquesta el renderizado del calendario doble calculando el mes
 * siguiente al mes izquierdo visible y actualizando los títulos y grids.
 */
function renderDualCalendar() {
    // Calcula el mes derecho (siguiente al actual) manejando el desbordamiento de año
    let nextMonthIndex = currentViewMonth + 1;
    let nextYearIndex = currentViewYear;
    if (nextMonthIndex > 11) {
        nextMonthIndex = 0;
        nextYearIndex++;
    }

    document.getElementById('month1-name').innerText = `${monthNames[currentViewMonth]} ${currentViewYear}`;
    document.getElementById('month2-name').innerText = `${monthNames[nextMonthIndex]} ${nextYearIndex}`;

    renderMonth(currentViewYear, currentViewMonth, 'grid-month-1');
    renderMonth(nextYearIndex, nextMonthIndex, 'grid-month-2');
}

/**
 * Genera y pinta el grid de días de un mes específico en el contenedor indicado.
 * Aplica clases de estado (pasado, no disponible, hoy, seleccionado, en-rango)
 * y asigna el listener de clic únicamente a los días seleccionables.
 *
 * @param {number} year   - Año del mes a renderizar.
 * @param {number} month  - Mes a renderizar (0 = enero, 11 = diciembre).
 * @param {string} gridId - ID del contenedor DOM donde se inyectarán los días.
 */
function renderMonth(year, month, gridId) {
    const grid = document.getElementById(gridId);
    grid.innerHTML = '';
    
    // Calcula el día de la semana del primer día para añadir celdas vacías de relleno
    const firstDay = new Date(year, month, 1).getDay();
    // Obtiene el número de días del mes pasando 0 como día del mes siguiente
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const today = new Date(2026, 1, 26); 

    // Relleno inicial: celdas vacías para alinear el día 1 con su columna de día de semana
    for (let i = 0; i < firstDay; i++) {
        grid.innerHTML += `<div class="calendar-day empty"></div>`;
    }

    for (let day = 1; day <= daysInMonth; day++) {
        const fullDate = new Date(year, month, day);
        const isPast = fullDate < today; 
        const isToday = fullDate.getTime() === today.getTime(); 

        // Formato ISO parcial para comparar contra el array de fechas no disponibles de la API
        const dateString = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const isUnavailable = unavailableDates.includes(dateString);
        
        let dayClass = "calendar-day";
        let content = `<span>${day}</span>`;

        // Prioridad de estados: pasado > no disponible > hoy
        if (isPast) {
            dayClass += " day-past";
        } else if (isUnavailable) {
            dayClass += " day-unavailable";
        } else {
            if (isToday) dayClass += " day-today";
        }

        // Los estados de selección se añaden independientemente del estado base
        if (checkInDate && fullDate.getTime() === checkInDate.getTime()) dayClass += " day-selected";
        if (checkOutDate && fullDate.getTime() === checkOutDate.getTime()) dayClass += " day-selected";
        // Rango entre check-in y check-out (excluye los días extremos que ya son .day-selected)
        if (checkInDate && checkOutDate && fullDate > checkInDate && fullDate < checkOutDate) dayClass += " day-in-range";

        const dayEl = document.createElement('div');
        dayEl.className = dayClass;
        dayEl.innerHTML = content;
        
        // Solo los días disponibles y futuros reciben el listener de selección
        if (!dayClass.includes('day-past') && !dayClass.includes('day-unavailable')) {
            dayEl.onclick = () => selectDate(fullDate);
        }
        
        dayEl.style.position = 'relative'; 
        
        grid.appendChild(dayEl);
    }
}

/**
 * Implementa la lógica de selección de rango de fechas en dos pasos:
 *  1er clic → establece check-in y limpia check-out.
 *  2do clic (fecha posterior) → establece check-out.
 *  Si se hace clic en una fecha anterior al check-in, reinicia la selección.
 *
 * Persiste el estado en sessionStorage y actualiza el calendario y el carrito.
 *
 * @param {Date} date - Fecha del día clicado.
 */
function selectDate(date) {
    if (!checkInDate || (checkInDate && checkOutDate)) {
        // No hay selección previa o ya había un rango completo → reinicia con nuevo check-in
        checkInDate = date;
        checkOutDate = null;
    } else if (date > checkInDate) {
        // Segunda fecha seleccionada y es posterior → la asigna como check-out
        checkOutDate = date;
    } else {
        // La segunda fecha es anterior o igual al check-in → la reemplaza como nuevo check-in
        checkInDate = date;
    }
    
    if (checkInDate) sessionStorage.setItem('booking_checkin', checkInDate.toISOString());
    // Solo persiste check-out si existe; en caso contrario lo elimina de sessionStorage
    if (checkOutDate) sessionStorage.setItem('booking_checkout', checkOutDate.toISOString());
    else sessionStorage.removeItem('booking_checkout');
    
    updateDisplay();
    renderDualCalendar();
    updateCartUI(); 
}

/**
 * Actualiza los textos de check-in y check-out en la barra del widget
 * formateando las fechas en español con día de la semana completo.
 */
function updateDisplay() {
    const checkinEl = document.getElementById('display-checkin');
    const checkoutEl = document.getElementById('display-checkout');

    if (checkInDate && checkinEl) {
        checkinEl.innerText = checkInDate.toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' });
    }
    if (checkOutDate && checkoutEl) {
        checkoutEl.innerText = checkOutDate.toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' });
    }
}

/**
 * Valida que ambas fechas estén seleccionadas, cierra el modal del calendario
 * y lanza la búsqueda de habitaciones disponibles para el rango confirmado.
 *
 * @param {Event} [event] - Evento del DOM (opcional).
 * @returns {Promise<void>}
 */
async function confirmDates(event) {
    if (event) event.stopPropagation();
    
    if (!checkInDate || !checkOutDate) {
        alert("Por favor selecciona una fecha de entrada y salida.");
        return;
    }

    document.getElementById('calendar-modal').classList.add('hidden');
    await fetchAndRenderRooms(); 
}


// ============================================================
//  SECCIÓN 6 — RENDERIZADO DE TARJETAS DE HABITACIONES
// ============================================================

/**
 * Genera y pinta las tarjetas de habitaciones disponibles.
 * Si no hay habitaciones que cumplan los criterios, muestra un mensaje informativo.
 * El botón de cada tarjeta actúa como toggle: "RESERVAR AHORA" / "REMOVER RESERVA"
 * dependiendo de si esa habitación ya está seleccionada en el carrito.
 *
 * @param {Array<Object>} rooms - Array de objetos de tipo de habitación filtrados por capacidad.
 */
function renderRoomList(rooms) {
    const listContainer = document.getElementById('room-list-container');
    listContainer.innerHTML = '';

    if (rooms.length === 0) {
        listContainer.innerHTML = '<p style="padding: 20px;">No hay habitaciones con capacidad para esa cantidad de huéspedes.</p>';
        return;
    }

    rooms.forEach(room => {
        // Construye la lista de características solo con los campos que el tipo de habitación tenga definidos
        let featuresHtml = '';
        if (room.roomSize) featuresHtml += `<li>${room.roomSize}</li>`;
        if (room.bedType) featuresHtml += `<li>${room.bedType}</li>`;
        if (room.shortDescription) featuresHtml += `<li>${room.shortDescription}</li>`;

        // Genera el HTML de la tarjeta usando template literal para mantener el
        // binding de datos inline (data-room-id) necesario para updateRoomButtonsUI()
        const card = `
            <div class="room-card">
                <div class="room-card-img-wrapper">
                    <img src="${room.imageUrl}" alt="${room.nameType}" class="room-card-img">
                </div>
                
                <div class="room-card-info">
                    <h3 class="room-card-title">${room.nameType}</h3>
                    <ul class="room-features">
                        ${featuresHtml}
                    </ul>
                    
                    <div class="room-card-footer">
                        <div class="room-benefits">
                            <p class="rate-type">Tarifa Estándar Media Pensión</p>
                            <p class="black">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#1a1a1a" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
                                    <line x1="1" y1="10" x2="23" y2="10"></line>
                                </svg>
                                    Garantizado con Tarjeta de Crédito
                            </p>
                                            
                            <p class="black">
                                <svg width="18" height="18" viewBox="0 0 50 60">
                                    <g transform="translate(0, 2)">
                                    <line x1="10" y1="0" x2="10" y2="55" stroke="#1a1a1a" stroke-width="4" stroke-linecap="round"/>
                                    <line x1="5" y1="0" x2="5" y2="18" stroke="#1a1a1a" stroke-width="3" stroke-linecap="round"/>
                                    <line x1="10" y1="0" x2="10" y2="18" stroke="#1a1a1a" stroke-width="3" stroke-linecap="round"/>
                                    <line x1="15" y1="0" x2="15" y2="18" stroke="#1a1a1a" stroke-width="3" stroke-linecap="round"/>
                                    <path d="M5,18 Q10,26 15,18" fill="none" stroke="#1a1a1a" stroke-width="3"/>
                                    <line x1="35" y1="0" x2="35" y2="55" stroke="#1a1a1a" stroke-width="4" stroke-linecap="round"/>
                                    <path d="M35,0 Q45,8 35,22" fill="#1a1a1a"/>
                                    </g>
                                </svg>
                                    Desayuno y Cena Incluidos
                            </p>
                        </div>
                        <div class="room-price-box">
                            <p class="room-price">¥${room.basePrice.toLocaleString('es-ES')}</p>
                            <p class="room-price-sub">Por noche<br>Incluye impuestos y tasas</p>
                            <button class="btn-room-action btn-book-now" 
                                    data-room-id="${room.idTipo}" 
                                    onclick="selectRoom(${room.idTipo}, '${room.nameType}', ${room.basePrice})">
                                ${room.idTipo === selectedRoomId ? 'REMOVER RESERVA' : 'RESERVAR AHORA'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        listContainer.innerHTML += card;
    });
}


// ============================================================
//  SECCIÓN 7 — CARRITO DE RESERVA
// ============================================================

// Estado del carrito: recuperado de sessionStorage para persistir entre páginas del flujo
let selectedRoomId = sessionStorage.getItem('booking_roomId') ? parseInt(sessionStorage.getItem('booking_roomId')) : null;
let selectedRoomPrice = sessionStorage.getItem('booking_roomPrice') ? parseFloat(sessionStorage.getItem('booking_roomPrice')) : 0;
let selectedRoomName = sessionStorage.getItem('booking_roomName') || "";


/**
 * Recalcula y actualiza el resumen del carrito lateral.
 * El precio total se calcula como: precio base × nº adultos × nº noches.
 * El botón "Continuar" permanece deshabilitado hasta que hay habitación
 * y rango de fechas completos seleccionados.
 */
function updateCartUI() {
    const datesEl = document.getElementById('cart-dates');
    const guestsEl = document.getElementById('cart-guests');
    const totalEl = document.getElementById('cart-total');
    const btnContinue = document.getElementById('btn-continue');
    const roomWrapper = document.getElementById('cart-room-wrapper');
    const roomNameEl = document.getElementById('cart-room-name');

    if (checkInDate && checkOutDate && datesEl) {
        const options = { day: 'numeric', month: 'short' };
        datesEl.innerText = `${checkInDate.toLocaleDateString('es-ES', options)} - ${checkOutDate.toLocaleDateString('es-ES', options)}`;
    }

    if (guestsEl) guestsEl.innerText = `${selectedAdults} adultos`;

    if (checkInDate && checkOutDate && selectedRoomId) {
        // Calcula noches convirtiendo la diferencia de milisegundos a días enteros
        const nights = Math.ceil((checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
        const total = selectedRoomPrice * selectedAdults * nights;
        
        if (totalEl) totalEl.innerText = `¥${total.toLocaleString('es-ES')}`;
        if (btnContinue) btnContinue.disabled = false;
        if (roomWrapper) roomWrapper.classList.remove('hidden');
        if (roomNameEl) roomNameEl.innerText = selectedRoomName;
    } else {
        // Carrito incompleto: resetea total y bloquea el botón de continuar
        if (totalEl) totalEl.innerText = `¥0`;
        if (btnContinue) btnContinue.disabled = true;
        if (roomWrapper) roomWrapper.classList.add('hidden');
    }
}


/**
 * Selecciona una habitación o la deselecciona si ya estaba elegida (toggle).
 * Persiste la selección en sessionStorage, actualiza el carrito y hace
 * scroll automático hacia la card del carrito para confirmar la selección.
 *
 * @param {number} typeId - ID del tipo de habitación seleccionado.
 * @param {string} name   - Nombre del tipo de habitación.
 * @param {number} price  - Precio base por noche de la habitación.
 */
function selectRoom(typeId, name, price) {
    // Si se vuelve a clicar la misma habitación, actúa como "remover"
    if (selectedRoomId === typeId) {
        removeRoom();
        return;
    }

    selectedRoomId = typeId;
    selectedRoomName = name;
    selectedRoomPrice = price;

    sessionStorage.setItem('booking_roomId', selectedRoomId);
    sessionStorage.setItem('booking_roomName', selectedRoomName);
    sessionStorage.setItem('booking_roomPrice', selectedRoomPrice);
    
    updateCartUI();
    updateRoomButtonsUI();
    
    // Scroll suave hacia el carrito para que el usuario vea la confirmación de su selección
    document.querySelector('.cart-card').scrollIntoView({ behavior: 'smooth', block: 'center' });
}

/**
 * Elimina la habitación seleccionada del carrito y limpia
 * su persistencia en sessionStorage.
 */
function removeRoom() {
    selectedRoomId = null;
    selectedRoomName = "";
    selectedRoomPrice = 0;

    sessionStorage.removeItem('booking_roomId');
    sessionStorage.removeItem('booking_roomName');
    sessionStorage.removeItem('booking_roomPrice');

    updateCartUI();
    updateRoomButtonsUI();
}


/**
 * Recorre todos los botones de habitación del DOM y actualiza
 * su texto según si su ID coincide con la habitación actualmente seleccionada.
 * Necesario porque las tarjetas se generan dinámicamente con innerHTML
 * y sus botones no mantienen referencia al estado en memoria.
 */
function updateRoomButtonsUI() {
    const buttons = document.querySelectorAll('.btn-room-action');
    buttons.forEach(btn => {
        const roomId = parseInt(btn.getAttribute('data-room-id'));
        
        // Resetea las clases del botón antes de aplicar el nuevo estado
        btn.className = 'btn-room-action btn-book-now';

        if (roomId === selectedRoomId) {
            btn.innerText = 'REMOVER RESERVA';
        } else {
            btn.innerText = 'RESERVAR AHORA';
        }
    });
}


// ============================================================
//  SECCIÓN 8 — NAVEGACIÓN Y ACCIONES FINALES
// ============================================================

/**
 * Gestiona el clic en el botón de acción del carrito.
 * Si el usuario no ha iniciado sesión, abre el modal de login antes de continuar.
 * Si está autenticado, redirige al checkout.
 * La detección del contexto (página de booking vs. checkout) permite
 * reutilizar esta función en diferentes vistas del flujo.
 */
function handleCartAction() {
    if (document.getElementById('search-widget-container') || document.getElementById('room-results-section')) {
        
        const isLoggedIn = sessionStorage.getItem('isLoggedIn') === 'true';
        if (!isLoggedIn) {
            // Interrumpe el flujo y solicita login antes de proceder al checkout
            showToast("Por favor, inicie sesión para continuar con su reserva.", true);
            toggleLoginModal();
            return;
        }

        window.location.href = '/reservation/checkout'; 

    } else {
        // Placeholder para la lógica de pago final en la página de checkout
        console.log("¡Procesando reserva final y pago!");
    }
}


/**
 * Maneja el clic en el enlace "Mis Reservas" de la navbar.
 * Si no hay sesión activa, abre el modal de login en lugar de redirigir.
 *
 * @param {Event} [event] - Evento del DOM (opcional).
 */
function handleMyReservationsClick(event) {
    if (event) event.preventDefault(); 
    
    const isLoggedIn = sessionStorage.getItem('isLoggedIn') === 'true';

    if (!isLoggedIn) {
        toggleLoginModal();
    } else {
        window.location.href = '/reservation/my-reservations'; 
    }
}