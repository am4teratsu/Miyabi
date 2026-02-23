/**
 * @file index.js
 * @description Punto de entrada de animaciones e interacciones de la página index.
 *              Agrega y orquesta la lógica JS de cada módulo/fragmento que compone
 *              el index.html. A medida que se añadan nuevos módulos a la página,
 *              sus animaciones correspondientes deben incorporarse aquí, respetando
 *              la estructura de secciones definida a continuación.
 *
 * @requires gsap          - Biblioteca de animaciones (GreenSock Animation Platform)
 * @requires ScrollTrigger - Plugin oficial de GSAP (registrado en main.js)
 *
 * @modules
 *   [1] Intro  — Animación de entrada de la sección introductoria (.intro-section)
 *   [2] ...    — (Próximo módulo)
 */


// =============================================================================
// MÓDULO 1 — INTRO (intro.html)
// =============================================================================

/**
 * Timeline que anima la entrada de las dos columnas de .intro-section
 * cuando la sección entra en el viewport.
 *
 * La secuencia consta de dos pasos encadenados:
 *   1. La columna de imagen (.intro-right) sube desde 50px hasta su posición natural.
 *   2. La columna de texto (.intro-left) sigue el mismo recorrido con una duración
 *      ligeramente mayor, solapando el final del paso anterior (-=0.7) para crear
 *      un efecto de apertura fluida en lugar de dos movimientos independientes.
 *
 * Se usa .from() en lugar de .to() porque los elementos deben animarse hasta su
 * posición CSS original (y: 0, opacity: 1), sin necesidad de definir ese estado
 * final explícitamente en el JS.
 *
 * @trigger Se activa cuando el borde superior de .intro-section alcanza el 75%
 *          de la altura del viewport, asegurando que la animación comienza antes
 *          de que la sección sea completamente visible y no se corte a mitad.
 */
const introTimeline = gsap.timeline({
    scrollTrigger: {
        trigger: ".intro-section",
        start: "top 75%",
    }
});

// Paso 1: La imagen entra primero para establecer el contexto visual antes que el texto
introTimeline.from(".intro-right", {
    y: 50,
    opacity: 0,
    duration: 1,
    ease: "power3.out"
})
// Paso 2: El texto entra solapando los últimos 0.7s de la imagen (-=0.7).
// Su duración mayor (1.2s vs 1s) transmite un ritmo más pausado, coherente
// con el carácter filosófico y editorial del contenido.
.from(".intro-left", {
    y: 50,
    opacity: 0,
    duration: 1.2,
    ease: "power3.out"
}, "-=0.7");


// =============================================================================
// MÓDULO 2 — (Nombre del módulo) ((nombre-fragmento).html)
// Añadir aquí la lógica de animación del siguiente módulo del index
// =============================================================================