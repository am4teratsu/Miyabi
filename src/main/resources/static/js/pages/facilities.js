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

