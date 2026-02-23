/**
 * @file main.js
 * @description Lógica de animaciones e interacciones globales del sitio Miyabi Ryokan.
 *              Depende de GSAP (v3) y su plugin ScrollTrigger para las animaciones
 *              basadas en scroll. Debe cargarse después de que el DOM esté disponible.
 *
 * @requires gsap        - Biblioteca de animaciones (GreenSock Animation Platform)
 * @requires ScrollTrigger - Plugin oficial de GSAP para animaciones en scroll
 */


// === Inicialización =========================================================
// Registrar el plugin antes de cualquier uso. Sin este paso, las propiedades
// scrollTrigger dentro de gsap.to() serían ignoradas silenciosamente.
gsap.registerPlugin(ScrollTrigger);


// === 1. LÓGICA DE APARICIÓN DEL NAVBAR =====================================

const navBar = document.getElementById("main-nav");

/**
 * Controla la visibilidad y el estilo de la barra de navegación según la ruta
 * actual y la posición del scroll del usuario.
 *
 * @behavior Página de inicio ("/"):
 *   La navbar arranca invisible (fuera del viewport hacia arriba) y aparece con
 *   una animación suave una vez el usuario ha bajado 100px. Si el usuario sube
 *   de vuelta al inicio, la animación se revierte y la navbar desaparece.
 *
 * @behavior Resto de páginas:
 *   La navbar se muestra de forma estática desde el principio, con el fondo
 *   visible (.scrolled), ya que no hay una sección hero sobre la que flotar.
 */
if (window.location.pathname === '/' || window.location.pathname === '') {

    // Estado inicial: navbar desplazada fuera del viewport hacia arriba para
    // que la primera animación de entrada tenga recorrido visible.
    gsap.set(navBar, { y: -100, opacity: 0 }); 

    gsap.to(navBar, {
        y: 0,
        opacity: 1,
        duration: 0.8,
        ease: "power2.out",
        scrollTrigger: {
            trigger: "body",
            start: "top -100px",   // Se activa cuando el usuario ha bajado 100px desde el top
            // play: aparece al bajar | reverse: desaparece al volver arriba
            toggleActions: "play none none reverse",
            // Clases CSS que controlan el fondo semitransparente de la navbar
            onEnter:     () => navBar.classList.add("scrolled"),
            onLeaveBack: () => navBar.classList.remove("scrolled")
        }
    });

} else {
    // En páginas interiores (/reservas, /habitaciones, etc.) la navbar parte
    // ya visible y con fondo opaco, sin necesidad de animación de entrada.
    navBar.classList.add("scrolled");
}


// === 2. MENÚ LATERAL — ANIMACIÓN ESCALONADA =================================

// === Variables de Estado ====================================================
const menuToggle = document.getElementById("menu-toggle");

/** @type {boolean} Rastrea si el panel lateral está abierto o cerrado. */
let isMenuOpen = false;


// === Timeline de Animación ==================================================
/**
 * Timeline de GSAP para la apertura y cierre del menú lateral.
 * Se define en estado pausado para controlarlo manualmente (play/reverse)
 * desde el event listener del botón hamburguesa, en lugar de ejecutarse al cargarse.
 *
 * La secuencia consta de dos pasos encadenados:
 *   1. El panel (#side-menu) se desliza desde la derecha hasta su posición final.
 *   2. Los enlaces aparecen de forma escalonada (stagger) desde abajo, solapando
 *      el final del paso anterior (-=0.2) para que la transición se sienta fluida.
 */
const menuTimeline = gsap.timeline({ paused: true });

// Paso 1: El panel desliza desde fuera del viewport (translateX: 100% en CSS)
// hasta su posición natural (x: 0%).
menuTimeline.to("#side-menu", {
    x: "0%", 
    duration: 0.6, 
    ease: "power3.inOut"
})
// Paso 2: Cada enlace aparece 0.1s después del anterior (stagger), creando un
// efecto de cascada que guía la vista del usuario de arriba a abajo por el menú.
// El offset "-=0.2" solapa este paso con el final del anterior para mayor fluidez.
.to(".menu-links a", {
    y: 0,
    opacity: 1,
    duration: 0.4,
    stagger: 0.1,
    ease: "power2.out"
}, "-=0.2");


// === Eventos del DOM ========================================================
/**
 * Alterna la apertura/cierre del menú lateral al pulsar el botón hamburguesa.
 *
 * - Al abrir: reproduce el timeline a velocidad normal (1x).
 * - Al cerrar: invierte el timeline al doble de velocidad (2x) para que el
 *   cierre sea más ágil y no resulte tedioso para el usuario.
 * - La clase "active" en el botón activa la animación CSS hamburguesa → "X".
 */
menuToggle.addEventListener("click", () => {
    isMenuOpen = !isMenuOpen;
    menuToggle.classList.toggle("active");

    if (isMenuOpen) {
        menuTimeline.timeScale(1).play(); 
    } else {
        // Cierre más rápido: 2x evita que la animación de salida se perciba lenta
        menuTimeline.timeScale(2).reverse(); 
    }
});


// === 3. ANIMACIONES DE REVELADO POR SCROLL ==================================
/**
 * Aplica una animación de entrada (fade-up) a todos los elementos marcados
 * con la clase CSS ".reveal-text" a medida que entran en el viewport.
 *
 * @convention Los elementos deben tener en su CSS de partida:
 *   opacity: 0 y transform: translateY(Xpx), de forma que GSAP los anime
 *   hasta opacity: 1 e y: 0 al activarse el trigger.
 *
 * @trigger Cuando el borde superior del elemento alcanza el 85% de la altura
 *   de la ventana (start: "top 85%"), dejando un margen antes de que el
 *   elemento sea completamente visible para que la animación no se corte.
 */
const revealElements = document.querySelectorAll(".reveal-text");

revealElements.forEach((element) => {
    gsap.to(element, {
        scrollTrigger: {
            trigger: element,
            start: "top 85%",
            // play al entrar | reverse al salir hacia arriba (permite re-animación)
            toggleActions: "play none none reverse"
        },
        duration: 1.5,
        opacity: 1,
        y: 0,
        ease: "power2.out"
    });
});