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
 *   [1] Intro    — Animación de entrada de la sección introductoria (.intro-section)
 *   [2] Features — Animación escalonada por bloque de servicio (.feature-block)
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
// MÓDULO 2 — FEATURES (features.html)
// =============================================================================

/**
 * Aplica una Timeline de animación independiente a cada bloque de servicio
 * (.feature-block) de la sección features conforme van entrando en el viewport.
 *
 * Cada bloque recibe su propio ScrollTrigger en lugar de uno compartido para
 * toda la sección, garantizando que la animación se active en el momento exacto
 * en que ESE bloque concreto se vuelve visible, sin depender de la posición de
 * los bloques anteriores o posteriores.
 *
 * La secuencia de aparición dentro de cada bloque sigue una jerarquía visual
 * de tres pasos que guía la atención del usuario de mayor a menor protagonismo:
 *   1. Imagen principal  (.feature-main-img) — impacto visual inmediato
 *   2. Título en japonés (.feature-jp-img)   — identidad tipográfica del servicio
 *   3. Título en español (.feature-title)    — lectura y comprensión del contenido
 *
 * Los offsets negativos (-=0.8 y -=0.6) solapan el inicio de cada paso con el
 * final del anterior, creando una cascada fluida en lugar de tres animaciones
 * secuenciales con pausas perceptibles entre ellas.
 *
 * toggleActions: "play none none reverse" permite que la animación se revierta
 * si el usuario vuelve a subir, manteniendo la coherencia visual en ambas
 * direcciones de scroll.
 *
 * @param {NodeList} featureBlocks - Colección de todos los .feature-block del DOM.
 */
const featureBlocks = document.querySelectorAll(".feature-block");

// Cada bloque gestiona su propia Timeline para que el trigger sea independiente
featureBlocks.forEach((block) => {
        
    const tl = gsap.timeline({
        scrollTrigger: {
            trigger: block,           // Trigger individual por bloque, no por sección completa
            start: "top 80%",         // Margen anticipado para que la animación no se corte al entrar
            toggleActions: "play none none reverse"
        }
    });

    // Paso 1: La imagen entra primero — es el elemento de mayor peso visual del bloque
    tl.from(block.querySelector(".feature-main-img"), {
        y: 50,
        opacity: 0,
        duration: 1.2,
        ease: "power3.out"
    })
    
    // Paso 2: El SVG japonés aparece solapando los últimos 0.8s de la imagen,
    // añadiendo la identidad tipográfica mientras la fotografía aún está animándose
    .from(block.querySelector(".feature-jp-img"), {
        y: 40,
        opacity: 0,
        duration: 1,
        ease: "power3.out"
    }, "-=0.8")
    
    // Paso 3: El título en español cierra la secuencia, invitando a la lectura
    // una vez que imagen e identidad japonesa ya han captado la atención
    .from(block.querySelector(".feature-title"), {
        y: 30,
        opacity: 0,
        duration: 1,
        ease: "power3.out"
    }, "-=0.6");
});