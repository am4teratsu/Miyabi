/**
 * @file cuisine-menu.js
 * @description Gestiona la interactividad y las animaciones de entrada de la sección
 *              de menús estacionales. Controla el sistema de pestañas (tabs) por
 *              temporada y orquesta las animaciones GSAP + ScrollTrigger para cada
 *              grupo de platos visible en pantalla.
 *
 * @requires gsap        - Librería de animaciones (debe estar cargada antes que este script)
 * @requires ScrollTrigger - Plugin de GSAP para animaciones basadas en scroll
 */

document.addEventListener("DOMContentLoaded", () => {

    // Registramos ScrollTrigger como plugin de GSAP antes de cualquier uso
    gsap.registerPlugin(ScrollTrigger);

    // === Variables de Estado ===

    /** @type {NodeList} Todas las pestañas de temporada del DOM */
    const tabs = document.querySelectorAll(".season-tab");

    /** @type {NodeList} Todos los paneles de contenido estacional del DOM */
    const contents = document.querySelectorAll(".season-content");


    // === Animaciones de Entrada ===

    /**
     * Animación de aparición escalonada de las pestañas de temporada.
     * Se dispara una sola vez cuando la sección de menús entra en el viewport,
     * haciendo que cada tab suba desde abajo y aparezca en secuencia de izquierda a derecha.
     * El trigger se ancla al 80% de la altura de la ventana para activarse
     * antes de que el usuario llegue al borde inferior.
     */
    gsap.from(".season-tab", {
        scrollTrigger: {
            trigger: ".seasonal-menus-section",
            start: "top 80%"
        },
        y: 20,
        opacity: 0,
        duration: 0.6,
        stagger: 0.1, // Aparecen uno por uno de izquierda a derecha
        ease: "power2.out"
    });


    // === Funciones Utilitarias ===

    /**
     * Registra animaciones ScrollTrigger para cada grupo de menú dentro
     * del panel de temporada activo en ese momento.
     *
     * Se llama en dos momentos:
     *  1. Al cargar la página, para animar el panel activo por defecto.
     *  2. Cada vez que el usuario cambia de pestaña, para animar el nuevo panel.
     *
     * Para cada grupo (.menu-group) dentro del panel activo:
     *  - El título de la columna izquierda aparece primero.
     *  - Los ítems de la columna derecha (platos y condiciones de mejora) aparecen
     *    a continuación de forma escalonada, con un pequeño retardo respecto al título.
     *
     * Nota: Resetea la opacidad y posición antes de registrar el trigger para garantizar
     * que los elementos re-animados partan siempre desde el estado invisible,
     * evitando que queden "ya visibles" si vienen de una pestaña anterior.
     *
     * @returns {void}
     */
    const setupMenuAnimations = () => {
        // Seleccionamos solo los grupos dentro de la pestaña activa
        const activeGroups = document.querySelectorAll(".season-content.active .menu-group");

        activeGroups.forEach(group => {
            const title = group.querySelector(".menu-col-left");
            const rightContentItems = group.querySelectorAll(".menu-col-right li, .menu-col-right .upgrade-item");

            // Reseteamos opacidad por si venimos de otra pestaña
            gsap.set([title, rightContentItems], { opacity: 0, y: 15 });

            // Creamos la animación atada al scroll
            ScrollTrigger.create({
                trigger: group,
                start: "top 85%",
                onEnter: () => {
                    // Primero el título izquierdo...
                    gsap.to(title, { opacity: 1, y: 0, duration: 0.5, ease: "power2.out" });

                    // ...y con un pequeño retraso, el contenido derecho (elemento por elemento)
                    gsap.to(rightContentItems, {
                        opacity: 1,
                        y: 0,
                        duration: 0.5,
                        stagger: 0.05,
                        delay: 0.2, // Retraso respecto al título
                        ease: "power2.out"
                    });
                }
            });
        });
    };

    // Inicializamos las animaciones para la pestaña que viene activa por defecto
    setupMenuAnimations();


    // === Eventos del DOM ===

    /**
     * Event listener de click para cada pestaña de temporada.
     *
     * Al hacer clic en una tab:
     *  1. Evita re-procesar si la pestaña ya está activa.
     *  2. Desactiva todas las pestañas y paneles.
     *  3. Activa la pestaña clicada y su panel de contenido correspondiente
     *     (identificado mediante el atributo data-target de la tab).
     *  4. Refresca ScrollTrigger para recalcular posiciones tras el cambio de
     *     altura del DOM (el panel nuevo puede tener distinta altura que el anterior).
     *  5. Re-registra las animaciones para los grupos del nuevo panel activo.
     *  6. Despacha un evento "scroll" sintético para que ScrollTrigger evalúe
     *     inmediatamente qué grupos ya están en el viewport sin necesidad de
     *     que el usuario mueva la página.
     */
    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            // Si ya está activa, no hacemos nada
            if (tab.classList.contains("active")) return;

            const targetId = tab.getAttribute("data-target");

            // Quitamos la clase active de todos
            tabs.forEach(t => t.classList.remove("active"));
            contents.forEach(c => c.classList.remove("active"));

            // Añadimos active al clickeado
            tab.classList.add("active");
            document.getElementById(targetId).classList.add("active");

            // Importante: Refrescamos ScrollTrigger porque el DOM cambió de tamaño
            ScrollTrigger.refresh();

            // Re-ejecutamos la animación para los nuevos elementos visibles
            setupMenuAnimations();

            // Forzamos un pequeño check de scroll para disparar las animaciones inmediatas
            window.dispatchEvent(new Event("scroll"));
        });
    });
});