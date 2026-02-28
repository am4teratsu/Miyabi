// =============================================================================
// Rooms.js — Lógica de la página de Habitaciones | Proyecto: Miyabi Hotel
// =============================================================================
// Descripción   : Controla dos sistemas independientes de la página de Habitaciones:
//
//   MÓDULO 1 — Carrusel de imágenes
//     Instancia un carrusel autónomo por cada bloque '.room-custom-carousel'
//     encontrado en el DOM. Cada carrusel alterna slides automáticamente cada
//     4 segundos y responde a clics del usuario reiniciando el temporizador.
//     La transición visual es gestionada exclusivamente por CSS (opacity +
//     visibility), por lo que JS solo añade/quita la clase 'active'.
//
//   MÓDULO 2 — Animaciones de entrada con GSAP + ScrollTrigger
//     Orquesta la aparición secuencial de cada bloque de habitación al hacer
//     scroll. El orden de entrada es: imagen → título → contenido en cascada.
//     La sección de Información General tiene su propia timeline independiente.
//
// Flujo de ejecución:
//   DOMContentLoaded (carruseles) → DOMContentLoaded (GSAP + ScrollTrigger)
//
// Dependencias externas:
//   · GSAP 3.x          → https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js
//   · ScrollTrigger     → https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/ScrollTrigger.min.js
//   Ambas deben estar cargadas en el HTML ANTES que este script.
// =============================================================================


// =============================================================================
// MÓDULO 1: CARRUSEL AUTOMÁTICO DE IMÁGENES
// =============================================================================
// Espera a que el DOM esté completamente construido antes de buscar los
// carruseles, ya que Thymeleaf renderiza los slides de forma dinámica y no
// estarían disponibles en una ejecución síncrona inmediata.
// =============================================================================

document.addEventListener("DOMContentLoaded", () => {

    // === Variables de Estado ===

    // Seleccionamos todos los carruseles del DOM (uno por habitación generada por Thymeleaf)
    const carousels = document.querySelectorAll(".room-custom-carousel");
    console.log(`Se encontraron ${carousels.length} carruseles en la página.`);

    // === Inicialización de carruseles ===

    // Iteramos con índice para poder identificar cada carrusel en los logs de depuración
    carousels.forEach((carousel, index) => {
        const slides = carousel.querySelectorAll(".room-slide");

        // Log de diagnóstico: permite verificar en consola si Thymeleaf inyectó
        // el número correcto de imágenes por habitación durante el desarrollo
        console.log(`Habitación ${index + 1}: Tiene ${slides.length} imágenes.`);

        // Las habitaciones con una sola imagen no necesitan lógica de rotación;
        // salimos del iterador actual para no adjuntar eventos ni timers innecesarios
        if (slides.length <= 1) return;

        // Índice de la slide actualmente visible; arranca siempre en la primera (index 0),
        // que ya tiene la clase 'active' asignada por Thymeleaf en el renderizado inicial
        let currentIndex = 0;

        // Guardamos la referencia del setInterval para poder cancelarlo y reiniciarlo
        // en resetAutoPlay() sin acumular múltiples intervalos paralelos
        let slideInterval;

        // === Funciones del Carrusel ===

        /**
         * Avanza al siguiente slide de forma circular.
         * Elimina 'active' del slide actual y lo asigna al siguiente,
         * delegando la transición visual (opacity/visibility) al CSS.
         * El operador módulo (%) garantiza que al llegar al último slide
         * se vuelva automáticamente al primero sin lógica adicional.
         */
        const nextSlide = () => {
            slides[currentIndex].classList.remove("active");
            currentIndex = (currentIndex + 1) % slides.length; // Índice circular: 0 → 1 → 2 → 0
            slides[currentIndex].classList.add("active");
        };

        /**
         * Inicia el ciclo automático de slides.
         * Almacena el ID del intervalo en 'slideInterval' para poder
         * detenerlo posteriormente desde resetAutoPlay().
         */
        const startAutoPlay = () => {
            slideInterval = setInterval(nextSlide, 4000); // Avanza cada 4 segundos
        };

        /**
         * Cancela el temporizador activo y lo reinicia desde cero.
         * Se invoca tras un clic manual del usuario para evitar que el slide
         * avance de forma inmediata después de la interacción (el usuario
         * acaba de elegir un slide y debería permanecer visible 4s completos).
         */
        const resetAutoPlay = () => {
            clearInterval(slideInterval); // Cancela el tick pendiente
            startAutoPlay();              // Inicia un nuevo ciclo limpio desde 0
        };

        // === Eventos del DOM ===

        // Clic sobre el área del carrusel: avance manual + reinicio del timer.
        // El event listener está sobre el contenedor completo, no sobre cada slide,
        // para simplificar la delegación y evitar problemas con imágenes absolutas superpuestas
        carousel.addEventListener("click", () => {
            nextSlide();
            resetAutoPlay();
        });

        // Iniciamos el autoplay al cargar la página una vez que todo está configurado
        startAutoPlay();
    });

});


// =============================================================================
// MÓDULO 2: ANIMACIONES DE ENTRADA CON GSAP + SCROLLTRIGGER
// =============================================================================
// Segundo listener DOMContentLoaded independiente para aislar la lógica de
// animación del módulo de carrusel. Ambos se ejecutarán cuando el DOM esté listo,
// sin interferir entre sí.
//
// Estrategia de animación por bloque de habitación:
//   · Cada '.room-dynamic-layout' tiene su propia Timeline de GSAP aislada,
//     con un ScrollTrigger propio que la dispara solo cuando ESE bloque entra
//     en el viewport. Esto evita que una animación afecte a otra.
//
//   · El orden de entrada (imagen → título → contenido) está orquestado con
//     solapamientos de tiempo (-=Xs) para crear un flujo continuo sin pausas
//     vacías perceptibles entre fases.
// =============================================================================

document.addEventListener("DOMContentLoaded", () => {

    // === Inicialización de GSAP ===

    // El plugin ScrollTrigger debe registrarse antes de crear cualquier animación
    // que lo use; de lo contrario GSAP lo ignorará silenciosamente
    gsap.registerPlugin(ScrollTrigger);

    // === Animaciones por bloque de habitación ===

    // Iteramos sobre cada bloque completo de habitación generado dinámicamente por Thymeleaf
    document.querySelectorAll('.room-dynamic-layout').forEach(block => {

        // --- Selección de elementos objetivo dentro del bloque actual ---

        // Columna de imágenes: primer elemento en entrar para anclar visualmente el bloque
        const imageColumn = block.querySelector('.room-images-side');

        // Título de la habitación: segundo en aparecer, mientras la imagen aún está animándose
        const titleElement = block.querySelector('.room-nametype');

        // Todos los hijos directos del wrapper de texto: separador, descripción, plano,
        // descripción corta, lista de specs y botón CTA. Al seleccionar con '> *', GSAP
        // puede aplicar 'stagger' sobre cada hijo individualmente en cascada
        const remainingTextBlocks = block.querySelectorAll('.info-text-reveal > *');

        // --- Configuración de la Timeline por bloque ---

        /**
         * Timeline de GSAP anclada al bloque actual como trigger.
         * toggleActions: "play none none none" — se reproduce una sola vez al bajar;
         * no se revierte ni se repite si el usuario vuelve a subir la página.
         */
        const tl = gsap.timeline({
            scrollTrigger: {
                trigger: block,                          // Disparador: el bloque completo de la habitación
                start: "top 80%",                        // Se activa cuando el borde superior del bloque llega al 80% del viewport
                toggleActions: "play none none none"     // Solo se ejecuta una vez (entrada); sin reversión al hacer scroll hacia arriba
            }
        });

        // --- Secuencia de animación (3 fases solapadas) ---

        // FASE 1 — Columna de imágenes (base visual del bloque)
        // Entra primero con un fade + ligera subida para dar sensación de profundidad.
        // La duración larga (1.2s) permite que la imagen "aterrice" suavemente.
        tl.from(imageColumn, {
            opacity: 0,
            y: 50,           // Desplazamiento inicial de 50px hacia abajo
            duration: 1.2,
            ease: "power2.out"
        });

        // FASE 2 — Título de la habitación
        // Comienza 0.7s antes de que termine la imagen (-=0.7s) para que la
        // transición sea continua y el usuario no perciba un "corte" entre fases.
        tl.from(titleElement, {
            opacity: 0,
            y: 30,
            duration: 0.8,
            ease: "power2.out"
        }, "-=0.7"); // Solapamiento: empieza cuando la fase 1 lleva 0.5s (1.2 - 0.7)

        // FASE 3 — Contenido restante en efecto cascada (stagger)
        // Cada hijo de '.info-text-reveal' aparece 0.15s después del anterior,
        // creando un efecto de lluvia controlada de abajo a arriba.
        // El solapamiento de -=0.3s mantiene el flujo sin pausas vacías al final del título.
        tl.from(remainingTextBlocks, {
            opacity: 0,
            y: 20,
            stagger: 0.15,   // 0.15s de retraso entre cada elemento hijo (efecto cascada)
            duration: 0.7,
            ease: "power2.out"
        }, "-=0.3"); // Solapamiento: empieza antes de que el título termine completamente

    });

    // === Animación de la sección de Información General ===

    // Guardamos la referencia del carrusel aunque no se use aquí directamente,
    // por compatibilidad con posibles extensiones futuras del módulo
    const carousels = document.querySelectorAll(".room-custom-carousel");

    const infoSection = document.querySelector('.general-info-section');

    // La sección de Información General es estática (no dinámica), por lo que
    // verificamos su existencia antes de intentar animarla para evitar errores
    if (infoSection) {

        // --- Selección de elementos objetivo dentro de la sección ---

        // El título izquierdo entra con un deslizamiento horizontal (x: -30)
        // para diferenciarse visualmente del resto de elementos que entran verticalmente
        const infoTitle = infoSection.querySelector('.info-left-title');

        // Cada fila de datos (categoría + valor) se anima de forma independiente
        // para aprovechar el efecto stagger en cascada de arriba hacia abajo
        const infoRows  = infoSection.querySelectorAll('.info-row');

        /**
         * Timeline independiente para la sección de Información General.
         * Al ser una sección distinta y estática, tiene su propio ScrollTrigger
         * para no interferir con las timelines de los bloques de habitación.
         */
        const tlInfo = gsap.timeline({
            scrollTrigger: {
                trigger: infoSection,
                start: "top 80%",                        // Mismo umbral de activación que las habitaciones para coherencia
                toggleActions: "play none none none"
            }
        });

        // FASE 1 — Título izquierdo: deslizamiento desde la izquierda (x: -30)
        // El movimiento horizontal refuerza la identidad visual de esta columna
        // como "etiqueta" que aparece antes que los datos de la derecha
        tlInfo.from(infoTitle, {
            opacity: 0,
            x: -30,          // Entra desde la izquierda, alineado con su posición en el layout
            duration: 0.8,
            ease: "power2.out"
        });

        // FASE 2 — Filas de datos en cascada descendente (stagger)
        // Cada fila aparece 0.1s después de la anterior, simulando una tabla
        // que se "construye" progresivamente ante el usuario.
        // El solapamiento -=0.4s evita que el usuario perciba una pausa tras el título.
        tlInfo.from(infoRows, {
            opacity: 0,
            y: 20,
            duration: 0.6,
            stagger: 0.1,    // 0.1s entre filas: más rápido que el stagger de specs (0.15s) por ser elementos más simples
            ease: "power2.out"
        }, "-=0.4"); // Solapamiento con el título para mantener el flujo de entrada

    }

});