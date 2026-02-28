// ============================================================
//  RESERVATION ANIMATIONS — Animaciones de entrada de la sección
//  de reservas usando GSAP + ScrollTrigger.
//  Requiere: gsap.min.js y ScrollTrigger.min.js cargados antes
//  de este archivo en el HTML.
// ============================================================


// Espera a que el DOM esté completamente cargado antes de registrar
// el plugin y construir la timeline, garantizando que los elementos
// objetivo (.reservation-section, .reservation-header, etc.) existen
document.addEventListener("DOMContentLoaded", (event) => {

    // Registra ScrollTrigger como plugin de GSAP; necesario llamarlo
    // una sola vez antes de usarlo en cualquier timeline o tween
    gsap.registerPlugin(ScrollTrigger);

    // === Timeline principal de animaciones de la sección de reservas ===
    // La timeline se activa cuando .reservation-section entra en el viewport
    // al 85% desde la parte superior, dando margen suficiente para que
    // el usuario perciba las animaciones antes de llegar al borde inferior
    const tl = gsap.timeline({
        scrollTrigger: {
            trigger: ".reservation-section",
            start: "top 85%", 
        }
    });

    // PASO 1 — Cabecera principal: sube desde abajo con fade-in.
    // power3.out proporciona una desaceleración pronunciada para un
    // arranque con energía que se detiene de forma suave
    tl.from(".reservation-header", { 
        y: 40, 
        opacity: 0, 
        duration: 0.8, 
        ease: "power3.out" 
    })

    // PASO 2 — Nota de política: animación más corta y con menos recorrido
    // que la cabecera para transmitir menor jerarquía visual.
    // "-=0.4" solapa esta animación con los últimos 0.4s de la anterior,
    // creando un efecto de cascada fluido en lugar de secuencia rígida
    .from(".policy-note", { 
        y: 20, 
        opacity: 0, 
        duration: 0.5, 
        ease: "power2.out" 
    }, "-=0.4") 

    // PASO 3 — Grupos de políticas: stagger de 0.2s entre cada elemento
    // del grupo para que aparezcan uno tras otro de forma escalonada,
    // guiando la lectura del usuario de arriba hacia abajo
    .from(".policy-group", { 
        y: 30, 
        opacity: 0, 
        duration: 0.6, 
        stagger: 0.2, 
        ease: "power2.out" 
    }, "-=0.2")

    // PASO 4 — Botón de acción de reserva: la combinación de scale + y
    // simula un efecto de "aparición con rebote" reforzado por back.out(1.5),
    // que da un ligero overshoot para atraer la atención sobre el CTA principal
    .from(".reservation-action", { 
        scale: 0.9, 
        y: 20,
        opacity: 0, 
        duration: 0.6, 
        ease: "back.out(1.5)" 
    }, "-=0.2")

    // PASO 5 — Enlaces de consulta: entran desde la izquierda (x: -20)
    // con stagger para diferenciarlos visualmente del CTA principal
    // y sugerir que son opciones secundarias de contacto
    .from(".enquiry-link", { 
        x: -20, 
        opacity: 0, 
        duration: 0.5, 
        stagger: 0.15,
        ease: "power2.out" 
    }, "-=0.2");

});