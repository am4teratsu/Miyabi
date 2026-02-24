gsap.registerPlugin(ScrollTrigger);

document.addEventListener("DOMContentLoaded", () => {

    // =========================================================================
    // BARRA INDICADORA LATERAL (Texto + Posición por sección)
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