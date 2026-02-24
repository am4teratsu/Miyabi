/**
 * @file facilities.js
 * @description Lógica de animaciones e interacciones de la página de instalaciones.
 *              Orquesta cuatro sistemas independientes que trabajan en conjunto:
 *              animación de entrada de la intro, animaciones por sección en scroll,
 *              carrusel automático con control manual y barra indicadora lateral.
 *
 * @requires gsap          - Biblioteca de animaciones (GreenSock Animation Platform)
 * @requires ScrollTrigger - Plugin oficial de GSAP (registrado al inicio del archivo)
 *
 * @systems
 *   [1] Intro Animation   — Entrada escalonada del bloque introductorio al cargar
 *   [2] Section Animation — Animación de cada .fac-section activada por scroll
 *   [3] Image Carousel    — Rotación automática cada 4s con avance manual por clic
 *   [4] Scroll Indicator  — Barra lateral con texto y posición sincronizados al scroll
 */

gsap.registerPlugin(ScrollTrigger);

document.addEventListener("DOMContentLoaded", () => {

    // =========================================================================
    // SISTEMA 1 — ANIMACIÓN DE LA INTRODUCCIÓN
    // =========================================================================

    /**
     * Timeline de entrada de la sección introductoria.
     * Se ejecuta inmediatamente al cargar el DOM, sin ScrollTrigger, porque
     * la intro es el primer elemento visible de la página y debe animarse
     * al instante sin esperar ningún evento de scroll.
     *
     * Secuencia:
     *   1. Bloque izquierdo (.fac-intro-left): kanji + título H1
     *   2. Bloque derecho (.fac-intro-right): párrafo de contexto
     * El solapamiento (-=0.8) crea una apertura simultánea desde ambos lados.
     */
    const introTl = gsap.timeline();

    // El bloque izquierdo (identidad visual) entra primero para anclar la marca
    introTl.from(".fac-intro-left", {
        y: 40,
        opacity: 0,
        duration: 1.2,
        ease: "power3.out"
    })
        // El párrafo entra solapando los últimos 0.8s del bloque izquierdo
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
     * Aplica una Timeline individual a cada .fac-section conforme entran
     * en el viewport, usando el mismo patrón de trigger por elemento que
     * en features.js para garantizar activación independiente por sección.
     *
     * La secuencia de tres pasos sigue una jerarquía visual deliberada:
     *   1. Carrusel (.fac-carousel)              — impacto visual inmediato (y: 50)
     *   2. Título + divisor (.fac-section-title, .fac-divider) — contexto (x: -20, stagger)
     *   3. Párrafo descriptivo (p)               — lectura tras captar la atención (y: 20)
     *
     * Los desplazamientos decrecientes (y:50 → x:20 → y:20) dan más energía
     * a los elementos de mayor peso visual y suavizan la entrada del texto.
     *
     * toggleActions: "play none none reverse" permite re-animación al volver a subir.
     */
    const sections = document.querySelectorAll(".fac-section");

    sections.forEach((sec) => {
        const tl = gsap.timeline({
            scrollTrigger: {
                trigger: sec,
                start: "top 75%",
                toggleActions: "play none none reverse"
            }
        });

        // Paso 1: El carrusel entra desde abajo con el mayor desplazamiento
        tl.from(sec.querySelector(".fac-carousel"), {
            y: 50,
            opacity: 0,
            duration: 1.2,
            ease: "power3.out"
        })
            // Paso 2: Título y divisor entran desde la izquierda en cascada (stagger: 0.2)
            // El array pasa ambos elementos a GSAP para animarlos como una unidad secuencial
            .from([sec.querySelector(".fac-section-title"), sec.querySelector(".fac-divider")], {
                x: -20,
                opacity: 0,
                duration: 0.8,
                stagger: 0.2,
                ease: "power2.out"
            }, "-=0.6")
            // Paso 3: El párrafo cierra la secuencia con el movimiento más sutil
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
     * Cada instancia gestiona su propio estado (índice actual e intervalo de tiempo)
     * de forma aislada, por lo que los carruseles no interfieren entre sí.
     *
     * @behavior Automático: avanza al siguiente slide cada 4000ms via setInterval.
     * @behavior Manual: un clic sobre el carrusel adelanta el slide inmediatamente
     *   y reinicia el contador de 4s para evitar un salto doble involuntario.
     *
     * El avance circular usa módulo aritmético (% slides.length) para volver al
     * primer slide sin necesidad de comprobar si se llegó al final del array.
     */
    const carousels = document.querySelectorAll(".fac-carousel");

    carousels.forEach(carousel => {
        const slides = carousel.querySelectorAll(".slide");
        let currentIndex = 0;
        let slideInterval;

        /**
         * Avanza al siguiente slide en orden circular.
         * Elimina "active" del slide actual y lo añade al siguiente,
         * delegando la transición visual (fade/crossfade) al CSS.
         */
        const nextSlide = () => {
            slides[currentIndex].classList.remove("active");
            // Módulo aritmético: al llegar al último slide, vuelve al índice 0
            currentIndex = (currentIndex + 1) % slides.length;
            slides[currentIndex].classList.add("active");
        };

        /**
         * Inicia el avance automático del carrusel con un intervalo de 4 segundos.
         * Se almacena la referencia del intervalo en slideInterval para poder
         * cancelarlo desde resetAutoPlay sin acumular intervalos huérfanos.
         */
        const startAutoPlay = () => {
            slideInterval = setInterval(nextSlide, 4000);
        };

        /**
         * Reinicia el temporizador de avance automático.
         * Se llama tras un clic manual para que el próximo avance automático
         * ocurra exactamente 4s después de la interacción del usuario,
         * evitando que el slide cambie dos veces en rápida sucesión.
         */
        const resetAutoPlay = () => {
            clearInterval(slideInterval);
            startAutoPlay();
        };

        // Evento de clic: avance manual inmediato + reinicio del temporizador automático
        carousel.addEventListener("click", () => {
            nextSlide();
            resetAutoPlay();
        });

        // Arranca el ciclo automático al cargar la página
        startAutoPlay();
    });


    // =========================================================================
    // SISTEMA 4 — BARRA INDICADORA LATERAL (Texto + Posición por sección)
    // =========================================================================

    // === Referencias DOM =====================================================
    const indicatorText   = document.querySelector(".indicator-text");
    const indicatorPanel  = document.querySelector(".scroll-indicator");
    const indicatorThumb  = document.querySelector(".indicator-thumb");
    /** Incluye tanto .fac-intro como todas las .fac-section para que la barra
     *  arranque en la primera sección visible desde el inicio de la página. */
    const allTrackedSections = document.querySelectorAll(".fac-intro, .fac-section");


    // === Función utilitaria — Actualización suave del texto ==================

    /**
     * Actualiza el texto del indicador lateral con una animación de fade.
     * Primero desvanece el texto actual hacia arriba (opacity: 0, y: -5),
     * cambia el contenido en el callback onComplete y lo hace aparecer
     * desde abajo (y: 5 → y: 0) para simular un efecto de "salida arriba /
     * entrada abajo" coherente con la dirección del scroll descendente.
     *
     * La comprobación inicial evita relanzar la animación si la sección
     * activa ya está mostrando el mismo nombre (p.ej., al hacer scroll lento).
     *
     * @param {string} newText - Valor del atributo data-name de la sección activa.
     */
    const updateTextSmoothly = (newText) => {
        if (indicatorText.textContent !== newText) {
            gsap.to(indicatorText, {
                opacity: 0,
                y: -5,
                duration: 0.4,
                ease: "power1.inOut",
                onComplete: () => {
                    indicatorText.textContent = newText;
                    gsap.fromTo(indicatorText,
                        { y: 5 },
                        { opacity: 1, y: 0, duration: 0.4, ease: "power1.out" }
                    );
                }
            });
        }
    };


    // === Cálculo de posiciones del thumb =====================================

    /**
     * Divide el recorrido disponible del track entre el número de secciones
     * para obtener la posición exacta (en px) del thumb en cada paso.
     *
     * trackHeight - thumbHeight = maxTravel: el espacio real en el que el thumb
     * puede moverse sin salirse del contenedor.
     * stepSize = maxTravel / (n-1): distancia uniforme entre cada sección,
     * de forma que la primera sección posiciona el thumb en 0 y la última
     * lo posiciona en maxTravel (fondo del track).
     */
    const trackHeight   = indicatorPanel.offsetHeight;
    const thumbHeight   = document.querySelector(".indicator-active-line").offsetHeight;
    const maxTravel     = trackHeight - thumbHeight;
    const totalSections = allTrackedSections.length;
    const stepSize      = totalSections > 1 ? maxTravel / (totalSections - 1) : 0;


    // === ScrollTriggers del indicador ========================================

    /**
     * Crea un ScrollTrigger por sección para detectar cuál es la activa.
     * Tanto onEnter (scroll hacia abajo) como onEnterBack (scroll hacia arriba)
     * actualizan el texto y la posición del thumb para que la barra refleje
     * correctamente la sección visible en ambas direcciones de navegación.
     */
    allTrackedSections.forEach((sec, index) => {
        ScrollTrigger.create({
            trigger: sec,
            start: "top center",
            end: "bottom center",
            onEnter: () => {
                updateTextSmoothly(sec.dataset.name);
                // Salta el thumb a la posición exacta de este índice en el track
                gsap.to(indicatorThumb, { y: index * stepSize, duration: 0.5, ease: "power2.out" });
            },
            onEnterBack: () => {
                updateTextSmoothly(sec.dataset.name);
                gsap.to(indicatorThumb, { y: index * stepSize, duration: 0.5, ease: "power2.out" });
            },
        });
    });

    // Oculta la barra cuando el pie de la página entra en el viewport para no
    // solaparla con el footer. Se revierte automáticamente si el usuario sube.
    ScrollTrigger.create({
        trigger: ".facilities-page",
        start: "bottom 60%",
        onEnter:     () => indicatorPanel.style.opacity = "0",
        onLeaveBack: () => indicatorPanel.style.opacity = "1"
    });

});