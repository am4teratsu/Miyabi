/**
 * @file sections.js
 * @description Lógica de animaciones e interacciones compartida para páginas
 *              que utilizan los fragmentos de sections.html (fac-intro y fac-section).
 *              Versión simplificada de facilities.js sin el indicador lateral,
 *              pensada para páginas que reutilizan los fragmentos pero no necesitan
 *              ese componente de navegación.
 *
 *              Orquesta tres sistemas independientes:
 *                [1] Animación de la sección introductoria al cargar el DOM.
 *                [2] Animación escalonada por sección activada por scroll.
 *                [3] Carrusel automático con control manual por clic.
 *
 * @requires gsap          - Biblioteca de animaciones (GreenSock Animation Platform)
 * @requires ScrollTrigger - Plugin oficial de GSAP (registrado al inicio del archivo)
 */

gsap.registerPlugin(ScrollTrigger);

document.addEventListener("DOMContentLoaded", () => {

    // =========================================================================
    // SISTEMA 1 — ANIMACIÓN DE LA SECCIÓN INTRODUCTORIA
    // =========================================================================

    /**
     * Timeline de entrada del bloque introductorio (.fac-intro).
     * Se ejecuta al cargar el DOM sin ScrollTrigger porque la intro es el
     * primer elemento visible de la página y debe animarse inmediatamente,
     * sin esperar ningún evento de scroll del usuario.
     *
     * Secuencia de dos pasos solapados:
     *   1. Bloque izquierdo (.fac-intro-left): kanji + título — ancla la identidad visual
     *   2. Bloque derecho (.fac-intro-right): párrafo descriptivo — entra 0.8s después
     * El offset -=0.8 crea una apertura simultánea desde ambos lados sin pausas perceptibles.
     */
    const introTl = gsap.timeline();

    // El bloque de identidad visual entra primero para establecer la marca antes que el texto
    introTl.from(".fac-intro-left", {
        y: 40,
        opacity: 0,
        duration: 1.2,
        ease: "power3.out"
    })
        // El párrafo solapa los últimos 0.8s del bloque izquierdo para una apertura fluida
        .from(".fac-intro-right", {
            y: 40,
            opacity: 0,
            duration: 1.2,
            ease: "power3.out"
        }, "-=0.8");


    // =========================================================================
    // SISTEMA 2 — ANIMACIÓN DE LAS SECCIONES REPETIBLES
    // =========================================================================

    /**
     * Aplica una Timeline independiente a cada .fac-section conforme entra
     * en el viewport. Cada sección tiene su propio ScrollTrigger (trigger: sec)
     * en lugar de uno global para toda la lista, garantizando que la animación
     * se active exactamente cuando ese bloque concreto se vuelve visible,
     * independientemente de los bloques anteriores o posteriores.
     *
     * La secuencia de tres pasos sigue una jerarquía visual deliberada:
     *   1. Carrusel (.fac-carousel)   — mayor impacto visual, mayor desplazamiento (y: 50)
     *   2. Título + divisor           — contexto de la sección, entrada lateral (x: -20, stagger)
     *   3. Párrafo (p)                — lectura tras captar la atención, movimiento sutil (y: 20)
     *
     * Los desplazamientos decrecientes (50 → 20 → 20) dan energía a los elementos
     * de mayor peso visual y suavizan progresivamente la entrada del contenido textual.
     */
    const sections = document.querySelectorAll(".fac-section");

    sections.forEach((sec) => {
        const tl = gsap.timeline({
            scrollTrigger: {
                trigger: sec,
                start: "top 75%",           // Anticipa la animación antes de que la sección sea visible por completo
                toggleActions: "play none none reverse"  // Permite re-animación al volver a subir
            }
        });

        // Paso 1: El carrusel entra desde abajo con el mayor desplazamiento — protagonismo visual inmediato
        tl.from(sec.querySelector(".fac-carousel"), {
            y: 50,
            opacity: 0,
            duration: 1.2,
            ease: "power3.out"
        })
            // Paso 2: Título y divisor entran desde la izquierda en cascada.
            // El array pasa ambos elementos a GSAP para animarlos como unidad con stagger: 0.2
            .from([sec.querySelector(".fac-section-title"), sec.querySelector(".fac-divider")], {
                x: -20,
                opacity: 0,
                duration: 0.8,
                stagger: 0.2,   // El divisor aparece 0.2s después del título, reforzando la secuencia visual
                ease: "power2.out"
            }, "-=0.6")

            // Paso 3: El párrafo cierra la secuencia con el movimiento más suave, invitando a la lectura
            .from(sec.querySelector("p"), {
                y: 20,
                opacity: 0,
                duration: 1,
                ease: "power2.out"
            }, "-=0.4");
    });


    // =========================================================================
    // SISTEMA 3 — CARRUSEL DE IMÁGENES (Automático + Control manual por clic)
    // =========================================================================

    /**
     * Inicializa un carrusel independiente para cada .fac-carousel de la página.
     * Cada instancia gestiona su propio estado (currentIndex e intervalo) de forma
     * aislada, por lo que múltiples carruseles en la misma página no interfieren
     * entre sí.
     *
     * @behavior Automático: avanza al siguiente slide cada 4000ms vía setInterval.
     * @behavior Manual:     un clic sobre el carrusel adelanta el slide de inmediato
     *                       y reinicia el contador para evitar un doble salto involuntario.
     *
     * La visibilidad de los slides se gestiona exclusivamente por CSS (opacity 0/1
     * mediante la clase "active"), delegando la transición de crossfade a sections.css.
     */
    const carousels = document.querySelectorAll(".fac-carousel");

    carousels.forEach(carousel => {
        const slides = carousel.querySelectorAll(".slide");
        let currentIndex = 0;
        let slideInterval;

        /**
         * Avanza al siguiente slide en orden circular.
         * Elimina "active" del slide actual y lo añade al siguiente.
         * El módulo aritmético (% slides.length) vuelve al índice 0
         * al llegar al último slide sin necesidad de comprobación explícita.
         */
        const nextSlide = () => {
            slides[currentIndex].classList.remove("active");
            currentIndex = (currentIndex + 1) % slides.length;
            slides[currentIndex].classList.add("active");
        };

        /**
         * Inicia el avance automático con un intervalo de 4 segundos.
         * La referencia se guarda en slideInterval para poder cancelarla
         * desde resetAutoPlay sin acumular intervalos huérfanos en memoria.
         */
        const startAutoPlay = () => {
            slideInterval = setInterval(nextSlide, 4000);
        };

        /**
         * Cancela el temporizador activo y arranca uno nuevo desde cero.
         * Se llama tras un clic manual para que el próximo avance automático
         * ocurra exactamente 4s después de la interacción, evitando que el
         * slide cambie dos veces en rápida sucesión.
         */
        const resetAutoPlay = () => {
            clearInterval(slideInterval);
            startAutoPlay();
        };

        // Clic sobre el carrusel: avance manual inmediato + reinicio del temporizador automático
        carousel.addEventListener("click", () => {
            nextSlide();
            resetAutoPlay();
        });

        // Arranca el ciclo automático al cargar la página
        startAutoPlay();
    });

});