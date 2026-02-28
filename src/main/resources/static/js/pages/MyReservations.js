// ============================================================
//  MYRESERVATIONS.JS — Lógica de la página "Mis Reservas"
//  Responsabilidades:
//    · Validación de sesión activa antes de mostrar contenido
//    · Consulta a la API de reservas por huésped autenticado
//    · Normalización y renderizado de tarjetas de reserva
//    · Mapeo de estados de reserva a clases CSS y textos localizados
//    · Manejo defensivo de datos de habitación potencialmente nulos
// ============================================================


// ============================================================
//  SECCIÓN 1 — INICIALIZACIÓN AL CARGAR LA PÁGINA
// ============================================================

// Dispara la carga de reservas en cuanto el DOM está listo.
// No se usa 'load' para no esperar a que carguen imágenes y recursos externos.
document.addEventListener('DOMContentLoaded', () => {
    loadMyReservations();
});


// ============================================================
//  SECCIÓN 2 — CARGA Y RENDERIZADO DE RESERVAS
// ============================================================

/**
 * Punto de entrada principal de la página.
 * Valida que el huésped tenga sesión activa, consulta sus reservas a la API
 * y renderiza una tarjeta por cada resultado dentro de #reservations-container.
 * Cubre tres estados de UI: carga vacía, lista de tarjetas y error de red.
 *
 * @returns {Promise<void>}
 */
async function loadMyReservations() {
    const container = document.getElementById('reservations-container');
    const guestId = sessionStorage.getItem('guestId');

    // Sin guestId no hay sesión activa: se redirige al inicio en lugar de mostrar
    // un error, para que el usuario pueda autenticarse y volver al flujo normal
    if (!guestId) {
        window.location.href = "/";
        return;
    }

    try {
        // GET /api/reservations/guest/:guestId → espera un array de objetos reserva.
        // El endpoint está implementado en el controlador de Spring Boot.
        const response = await fetch(`/api/reservations/guest/${guestId}`);

        if (!response.ok) throw new Error("Error fetching reservations");

        const reservations = await response.json();

        // Estado vacío: el huésped existe pero aún no tiene reservas.
        // Se ofrece un CTA directo al motor de reservas en lugar de un mensaje muerto.
        if (reservations.length === 0) {
            container.innerHTML = `
                <div style="text-align: center; padding: 50px 0;">
                    <p style="font-size: 18px; margin-bottom: 20px;">Aún no tienes reservas con nosotros.</p>
                    <button class="btn-auth-dark" onclick="window.location.href='/reservation/booking'" style="background:#000; color:#fff; padding: 15px 30px; border:none; cursor:pointer;">RESERVAR AHORA</button>
                </div>
            `;
            return;
        }

        // Limpia el mensaje de "Cargando..." antes de inyectar las tarjetas
        container.innerHTML = '';

        // Se invierte el array para mostrar la reserva más reciente en primer lugar,
        // ya que la API devuelve los registros en orden de creación ascendente
        reservations.reverse().forEach(res => {

            // === Formateo de fechas ===
            // Se usa toLocaleDateString con 'es-ES' para obtener fechas en formato legible
            // en español (ej. "15 de marzo de 2025") coherente con el idioma de la UI
            const checkIn = new Date(res.entryDate).toLocaleDateString('es-ES', { day: 'numeric', month: 'long', year: 'numeric' });
            const checkOut = new Date(res.departureDate).toLocaleDateString('es-ES', { day: 'numeric', month: 'long', year: 'numeric' });

            // === Mapeo de estado a clase CSS y texto localizado ===
            // El backend devuelve estados en inglés (Paid, Reserved, Cancelled);
            // aquí se traducen al español y se asigna la clase visual correspondiente
            let statusClass = "status-pending";
            let statusText = "Pendiente";

            if (res.state === "Paid" || res.state === "Reserved" || res.state === "Confirmed") {
                statusClass = "status-paid";
                statusText = "Confirmada";
            } else if (res.state === "Check-out") {
                statusClass = "status-paid";
                statusText = "Completada";
            } else if (res.state === "Cancelled") {
                statusClass = "status-cancelled";
                statusText = "Cancelada";
            }

            // === Resolución defensiva del nombre de habitación ===
            // La relación room → roomType puede venir parcialmente nula desde el backend;
            // se aplica una cascada de fallbacks para garantizar que siempre haya un texto visible
            let roomName = "Habitación Estándar";

            if (res.room && res.room.roomType && res.room.roomType.nameType) {
                roomName = res.room.roomType.nameType;
            } else if (res.room && res.room.roomNumber) {
                // Si el tipo viene nulo, se muestra al menos el número de cuarto
                // para que el huésped pueda identificar su reserva
                roomName = "Habitación " + res.room.roomNumber;
            }

            // === Normalización de valores financieros ===
            // Se usa || 0 como fallback para evitar que NaN o null rompan el formateo
            // con toLocaleString() en el template HTML de la tarjeta
            const roomSubtotal = res.roomSubtotal || 0;
            const consumption = res.totalConsumption || 0;
            const totalPay = res.totalPay || 0;

            // === Construcción del HTML de la tarjeta ===
            // Los consumos extra se renderizan condicionalmente: solo aparece la fila
            // si el huésped tiene cargos adicionales (consumption > 0), manteniendo
            // la tarjeta limpia para reservas sin extras
            const cardHtml = `
			                <div class="reservation-card">
			                    <div class="res-card-header">
			                        <span class="res-code">Reserva: ${res.reservationCode}</span>
			                        <span class="res-status ${statusClass}">${statusText}</span>
			                    </div>
			                    
			                    <div class="res-card-body">
			                        <div class="res-detail-group">
			                            <span class="res-label">Check-in</span>
			                            <span class="res-value">${checkIn}</span>
			                        </div>
			                        <div class="res-detail-group">
			                            <span class="res-label">Check-out</span>
			                            <span class="res-value">${checkOut}</span>
			                        </div>
			                        <div class="res-detail-group">
			                            <span class="res-label">Habitación</span>
			                            <span class="res-value">${roomName}</span>
			                        </div>
			                        <div class="res-detail-group">
			                            <span class="res-label">Huéspedes</span>
			                            <span class="res-value">${res.numAdults} Adultos</span>
			                        </div>
			                    </div>

			                    <div class="res-card-footer">
			                        <div class="res-breakdown-row">
			                            <span>Costo de Habitación</span>
			                            <span>¥${roomSubtotal.toLocaleString('es-ES')}</span>
			                        </div>
			                        
			                        ${consumption > 0 ? `
			                        <div class="res-breakdown-row">
			                            <span>Consumos Extra</span>
			                            <span>¥${consumption.toLocaleString('es-ES')}</span>
			                        </div>
			                        ` : ''}

			                        <div class="res-total-row">
			                            <span class="res-label">TOTAL A PAGAR</span>
			                            <span class="res-total">¥${totalPay.toLocaleString('es-ES')}</span>
			                        </div>
			                    </div>
			                </div>
			            `;

            container.innerHTML += cardHtml;
        });

    } catch (error) {
        // Error de red o respuesta inesperada del servidor:
        // se muestra un mensaje en el contenedor Y un toast para mayor visibilidad,
        // ya que el usuario podría no estar mirando esa zona de la página
        console.error(error);
        container.innerHTML = '<p style="color:#666; text-align:center; padding: 50px 0;">No pudimos cargar tus reservas en este momento.</p>';
        
        showToast("Error de conexión. No se pudieron cargar las reservas.", true);
    }
}