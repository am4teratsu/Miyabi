// =============================================================================
// Rooms.js — Lógica de la página de Habitaciones
// Responsabilidades:
//   1. Carrusel automático de imágenes por habitación (click + autoplay)
//   2. Animaciones de entrada secuencial con GSAP + ScrollTrigger
// Dependencias externas: GSAP 3.x, ScrollTrigger plugin
// =============================================================================


// =============================================================================
// MÓDULO 1: CARRUSEL AUTOMÁTICO DE IMÁGENES
// Inicializa un carrusel independiente por cada habitación renderizada en el DOM.
// Cada carrusel avanza automáticamente cada 4 segundos y responde a clics del usuario.
// =============================================================================

document.addEventListener("DOMContentLoaded", () => {

    // Seleccionamos todos los carruseles del DOM (uno por habitación)
    const carousels = document.querySelectorAll(".room-custom-carousel");
    console.log(`Se encontraron ${carousels.length} carruseles en la página.`);

    carousels.forEach((carousel, index) => {
        const slides = carousel.querySelectorAll(".room-slide");

        // El "Chivato": Te dirá en la consola (F12) cuántas fotos tiene cada habitación
        console.log(`Habitación ${index + 1}: Tiene ${slides.length} imágenes.`);

        // Habitaciones con una sola imagen no necesitan lógica de carrusel
        if (slides.length <= 1) return;

        // Índice de la slide actualmente visible; arranca siempre en la primera
        let currentIndex = 0;

        // Referencia al intervalo de autoplay, guardada para poder detenerlo y reiniciarlo
        let slideInterval;

        // === Funciones del Carrusel ===

        /**
         * Avanza al siguiente slide de forma circular.
         * Elimina la clase 'active' del slide actual y se la asigna al siguiente,
         * logrando la transición visual controlada por CSS.
         */
        const nextSlide = () => {
            slides[currentIndex].classList.remove("active");
            currentIndex = (currentIndex + 1) % slides.length; // Vuelve a 0 al llegar al final
            slides[currentIndex].classList.add("active");
        };

        /**
         * Inicia el ciclo automático de slides con un intervalo fijo de 4 segundos.
         * Cada tick llama a nextSlide() para avanzar al siguiente.
         */
        const startAutoPlay = () => {
            slideInterval = setInterval(nextSlide, 4000);
        };

        /**
         * Reinicia el temporizador de autoplay desde cero.
         * Se usa tras una interacción manual del usuario para evitar un salto de slide
         * inmediato después del clic (el contador empieza a contar desde el clic).
         */
        const resetAutoPlay = () => {
            clearInterval(slideInterval);
            startAutoPlay();
        };

        // === Eventos del DOM ===

        // Al hacer clic sobre el carrusel, avanzamos manualmente y reiniciamos el timer
        // para no interrumpir la experiencia con un salto doble inmediato
        carousel.addEventListener("click", () => {
            nextSlide();
            resetAutoPlay();
        });

        // Arrancamos el autoplay al cargar la página
        startAutoPlay();
    });

});


// =============================================================================
// MÓDULO 2: ANIMACIONES DE ENTRADA CON GSAP + SCROLLTRIGGER
// Orquesta la aparición secuencial de cada bloque de habitación mientras el usuario
// hace scroll. El orden de entrada es: imagen → título → resto del contenido.
// Requiere que GSAP y el plugin ScrollTrigger estén cargados previamente en el HTML.
// =============================================================================

document.addEventListener("DOMContentLoaded", () => {

    // --- 1. LÓGICA DE APARICIÓN SECUENCIAL (GSAP + ScrollTrigger) ---
    // Priorizando la calidad y el delay solicitado

    // Registramos el plugin de ScrollTrigger en la instancia global de GSAP
    // antes de crear cualquier animación que dependa de él
    gsap.registerPlugin(ScrollTrigger);

    // Cada '.room-dynamic-layout' es un bloque completo de habitación (imagen + texto)
    // Creamos una timeline de animación aislada para cada uno, de modo que no interfieran
    document.querySelectorAll('.room-dynamic-layout').forEach(block => {

        // Seleccionamos los elementos específicos DENTRO de este bloque actual
        const imageColumn     = block.querySelector('.room-images-side');
        const titleElement    = block.querySelector('.room-nametype');

        // Seleccionamos todos los hijos directos del envoltorio que creamos en el HTML.
        // Esto incluye: separador, descripción, plano, descripción corta, lista de specs y botón CTA.
        // Al usar el selector padre > *, GSAP puede aplicar stagger sobre cada hijo individualmente.
        const remainingTextBlocks = block.querySelectorAll('.info-text-reveal > *');

        // --- Configuración de la Timeline ---

        // Cada timeline está anclada a su propio bloque como trigger,
        // garantizando que la animación se dispare solo cuando ESE bloque entra en viewport
        const tl = gsap.timeline({
            scrollTrigger: {
                trigger: block,            // El disparador es el bloque completo
                start: "top 80%",          // Se activa cuando el borde superior del bloque alcanza el 80% del viewport
                toggleActions: "play none none none" // Solo se reproduce una vez (al bajar); no se revierte al subir
            }
        });

        // --- Secuencia de Animación ---

        // FASE 1: La columna de imágenes entra primero para anclar visualmente el bloque
        // Fade in + micro-subida de 50px para sensación de profundidad
        tl.from(imageColumn, {
            opacity: 0,
            y: 50,
            duration: 1.2,
            ease: "power2.out"
        });

        // FASE 2: El título aparece mientras la imagen todavía está entrando (-=0.7s de solapamiento)
        // El overlap evita que el usuario perciba pausas vacías entre fases
        tl.from(titleElement, {
            opacity: 0,
            y: 30,
            duration: 0.8,
            ease: "power2.out"
        }, "-=0.7");

        // FASE 3: El resto del contenido aparece en cascada con stagger
        // Cada elemento se retrasa 0.15s respecto al anterior, creando un efecto de "lluvia" controlada
        // El solapamiento de -=0.3s mantiene el flujo sin cortes perceptibles
        tl.from(remainingTextBlocks, {
            opacity: 0,
            y: 20,
            stagger: 0.15, // Retraso de 0.15s entre cada elemento de la lista (efecto cascada)
            duration: 0.7,
            ease: "power2.out"
        }, "-=0.3"); // Comienza un poco antes de que termine el título

    });


    // --- 2. LÓGICA DE TU CARRUSEL PERSONALIZADO (Código anterior intacto) ---
    // (Asegúrate de mantener tu código que hace funcionar el carrusel aquí abajo)
    const carousels = document.querySelectorAll(".room-custom-carousel");
    // ... tu lógica de carrusel ...
});