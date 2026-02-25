/**
 * @file scroll-indicator.js
 * @description Controla un indicador de scroll lateral que muestra al usuario
 *              en qué sección de la página se encuentra actualmente.
 *
 *              El indicador es un panel vertical compuesto por:
 *                - Un "thumb" (pastilla) que se desplaza verticalmente a medida
 *                  que el usuario hace scroll entre las secciones rastreadas.
 *                - Una línea activa cuya altura se calcula dinámicamente en función
 *                  del número total de secciones.
 *                - Un texto que muestra el nombre de la sección activa, con una
 *                  transición suave de entrada/salida al cambiar de sección.
 *
 *              El panel se oculta automáticamente antes de entrar en la primera
 *              sección rastreada y al llegar al final de la página (.facilities-page).
 *
 * @requires gsap         - Librería de animaciones (debe estar cargada antes que este script)
 * @requires ScrollTrigger - Plugin de GSAP para animaciones basadas en scroll
 */

// ScrollTrigger se registra fuera del DOMContentLoaded porque GSAP lo permite
// en cualquier momento antes de su primer uso, y aquí se hace a nivel de módulo
// para garantizar que esté disponible desde el primer ciclo de render.
gsap.registerPlugin(ScrollTrigger);

document.addEventListener("DOMContentLoaded", () => {


    // === Variables de Estado ===

    /** @type {Element} Elemento de texto que muestra el nombre de la sección activa */
    const indicatorText   = document.querySelector(".indicator-text");

    /** @type {Element} Contenedor principal del indicador de scroll (panel lateral) */
    const indicatorPanel  = document.querySelector(".scroll-indicator");

    /** @type {Element} Pastilla deslizante que se mueve verticalmente entre secciones */
    const indicatorThumb  = document.querySelector(".indicator-thumb");

    /** @type {Element} Línea activa cuya altura representa una sección dentro del track */
    const activeLine      = document.querySelector(".indicator-active-line");

    /** @type {NodeList} Todas las secciones de la página que el indicador debe rastrear */
    const allTrackedSections = document.querySelectorAll(".fac-section");

    /** @type {number} Número total de secciones rastreadas; determina la geometría del indicador */
    const totalSections = allTrackedSections.length;


    // === Inicialización y Geometría ===

    // Si no hay secciones rastreables en el DOM, ocultamos el panel
    // y salimos para no registrar ScrollTriggers innecesarios.
    if (totalSections === 0) {
        if(indicatorPanel) indicatorPanel.style.display = "none";
        return;
    }

    // El panel arranca invisible e ininteractivo hasta que el usuario
    // alcanza la primera sección rastreada (ver trigger de visibilidad más abajo).
    gsap.set(indicatorPanel, { opacity: 0, pointerEvents: "none" });

    // Calculamos la altura del thumb proporcionalmente al número de secciones,
    // de modo que el track siempre quede dividido en partes iguales sin importar
    // cuántas secciones existan en la página.
    const trackHeight = indicatorPanel.offsetHeight;
    const dynamicThumbHeight = trackHeight / totalSections;
    activeLine.style.height = `${dynamicThumbHeight}px`;

    // stepSize es la distancia en píxeles que el thumb debe recorrer entre
    // una sección y la siguiente. Si solo hay una sección, no necesita moverse.
    const maxTravel = trackHeight - dynamicThumbHeight;
    const stepSize  = totalSections > 1 ? maxTravel / (totalSections - 1) : 0;


    // === Funciones Utilitarias ===

    /**
     * Actualiza el texto del indicador con una transición suave de fade + desplazamiento vertical.
     *
     * Para evitar un parpadeo innecesario, la actualización solo se ejecuta si el
     * nuevo texto es diferente al que ya se está mostrando. La animación consta de
     * dos fases encadenadas:
     *   1. Fade-out + subida del texto actual.
     *   2. Actualización del contenido y fade-in + bajada del nuevo texto.
     *
     * @param {string} newText - Nombre de la nueva sección activa, leído desde
     *                           el atributo `data-name` del elemento rastreado.
     * @returns {void}
     */
    const updateTextSmoothly = (newText) => {
        if (indicatorText.textContent !== newText) {
            gsap.to(indicatorText, {
                opacity: 0,
                y: -5,
                duration: 0.4,
                ease: "power1.inOut",
                onComplete: () => {
                    // Cambiamos el texto solo cuando el fade-out ha terminado,
                    // para que el usuario nunca vea el contenido antiguo y el nuevo a la vez.
                    indicatorText.textContent = newText;
                    gsap.fromTo(indicatorText,
                        { y: 5 },
                        { opacity: 1, y: 0, duration: 0.4, ease: "power1.out" }
                    );
                }
            });
        }
    };


    // === ScrollTriggers ===

    // Trigger de visibilidad del panel: lo muestra al entrar en la primera sección
    // rastreada y lo oculta al hacer scroll hacia arriba por encima de ella.
    // Usar la primera sección como trigger garantiza que el indicador no aparezca
    // en ninguna sección anterior de la página (hero, intro, etc.).
    ScrollTrigger.create({
        trigger: allTrackedSections[0],
        start: "top bottom", 
        onEnter: () => gsap.set(indicatorPanel, { opacity: 1, pointerEvents: "auto" }),
        onLeaveBack: () => gsap.set(indicatorPanel, { opacity: 0, pointerEvents: "none" })
    });

    // Trigger individual por sección: desplaza el thumb a su posición correspondiente
    // y actualiza el texto tanto al entrar en scroll descendente (onEnter)
    // como al volver en scroll ascendente (onEnterBack).
    allTrackedSections.forEach((sec, index) => {
        ScrollTrigger.create({
            trigger: sec,
            start: "top center",
            end: "bottom center",
            onEnter: () => {
                updateTextSmoothly(sec.dataset.name);
                gsap.to(indicatorThumb, { y: index * stepSize, duration: 0.5, ease: "power2.out" });
            },
            onEnterBack: () => {
                updateTextSmoothly(sec.dataset.name);
                gsap.to(indicatorThumb, { y: index * stepSize, duration: 0.5, ease: "power2.out" });
            },
        });
    });

    // Trigger de ocultación al pie de página: esconde el indicador cuando el usuario
    // llega al 80% del final de .facilities-page para que no solape con el footer,
    // y lo vuelve a mostrar si el usuario sube de nuevo.
    ScrollTrigger.create({
        trigger: ".facilities-page", 
        start: "bottom 80%",         
        onEnter: () => gsap.set(indicatorPanel, { opacity: 0, pointerEvents: "none" }),
        onLeaveBack: () => gsap.set(indicatorPanel, { opacity: 1, pointerEvents: "auto" })
    });

});