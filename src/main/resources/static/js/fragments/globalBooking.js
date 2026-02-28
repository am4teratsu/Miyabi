// ============================================================
//  GLOBAL BOOKING — Utilidades globales del ecosistema de reservas
//  Este archivo se carga en todas las páginas del flujo de reservas
//  y expone funciones compartidas que pueden ser invocadas desde
//  cualquier otro módulo JS (booking-logic.js, checkout, etc.).
// ============================================================


// ============================================================
//  SISTEMA DE NOTIFICACIONES (TOAST)
// ============================================================

/**
 * Muestra una notificación tipo toast que desciende desde la parte
 * superior de la pantalla y desaparece automáticamente tras 4 segundos.
 *
 * El elemento DOM del toast se crea de forma lazy la primera vez que
 * se invoca la función y se reutiliza en llamadas posteriores,
 * evitando acumular nodos en el DOM con cada notificación.
 *
 * @param {string}  message          - Texto a mostrar en la notificación.
 * @param {boolean} [isError=false]  - Si es true, aplica fondo rojo de error;
 *                                     si es false (por defecto), fondo negro informativo.
 */
function showToast(message, isError = false) {

    // Reutiliza el nodo si ya existe en el DOM; lo crea solo en la primera invocación
    let snackbar = document.getElementById('miyabi-global-toast');
    
    if (!snackbar) {
        snackbar = document.createElement('div');
        snackbar.id = 'miyabi-global-toast';
        document.body.appendChild(snackbar);
        
        // Los estilos se aplican por JS para mantener este componente autocontenido:
        // no requiere una hoja de estilos externa y funciona en cualquier página del flujo
        snackbar.style.visibility = 'hidden';
        snackbar.style.minWidth = '300px';
        // Desplazamiento negativo de la mitad del ancho mínimo para centrarlo
        // horizontalmente junto con left: 50% (técnica clásica de centrado absoluto)
        snackbar.style.marginLeft = '-150px';
        snackbar.style.color = '#fff';
        snackbar.style.textAlign = 'center';
        // Bordes redondeados solo en la parte inferior para simular que
        // el toast "emerge" desde el borde superior de la pantalla
        snackbar.style.borderRadius = '0 0 4px 4px';
        snackbar.style.padding = '16px 24px';
        snackbar.style.position = 'fixed';
        // z-index extremadamente alto para garantizar que el toast se superponga
        // a cualquier modal, overlay o componente sticky de la página
        snackbar.style.zIndex = '99999';
        snackbar.style.left = '50%';
        snackbar.style.top = '0';
        snackbar.style.fontSize = '15px';
        snackbar.style.fontWeight = '500';
        snackbar.style.boxShadow = '0 4px 15px rgba(0,0,0,0.3)';
        // Estado inicial invisible y fuera del viewport (desplazado hacia arriba)
        // listo para la animación de entrada
        snackbar.style.opacity = '0';
        snackbar.style.transform = 'translateY(-100%)';
        // Transición aplicada tanto a la entrada como a la salida del toast
        snackbar.style.transition = 'opacity 0.4s ease, transform 0.4s ease, visibility 0.4s';
    }
    
    // Actualiza el mensaje en cada llamada, permitiendo reutilizar el mismo nodo
    snackbar.innerText = message;
    
    // Diferencia visualmente errores (rojo) de notificaciones informativas (negro)
    snackbar.style.backgroundColor = isError ? '#b71c1c' : '#000';
    
    // === Animación de entrada ===
    // Hace visible el nodo y lo desliza hacia abajo desde fuera del viewport
    snackbar.style.visibility = 'visible';
    snackbar.style.opacity = '1';
    snackbar.style.transform = 'translateY(20px)'; 
    
    // === Animación de salida (auto-dismiss tras 4 segundos) ===
    // El primer setTimeout inicia el fade-out y el deslizamiento hacia arriba.
    // El segundo setTimeout oculta el nodo con visibility: hidden solo después
    // de que la transición CSS de 400ms haya terminado, evitando un corte brusco.
    setTimeout(() => { 
        snackbar.style.opacity = '0';
        snackbar.style.transform = 'translateY(-100%)'; 
        setTimeout(() => { snackbar.style.visibility = 'hidden'; }, 400);
    }, 4000); 
}