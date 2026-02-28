gsap.registerPlugin(ScrollTrigger);

document.addEventListener("DOMContentLoaded", () => {

    const indicatorText   = document.querySelector(".indicator-text");
    const indicatorPanel  = document.querySelector(".scroll-indicator");
    const indicatorThumb  = document.querySelector(".indicator-thumb");
    const activeLine      = document.querySelector(".indicator-active-line");

    // 1. ¡CAMBIO CLAVE! Ahora busca cualquier elemento con la clase global
    const allTrackedSections = document.querySelectorAll(".scroll-tracked-section");
    const totalSections = allTrackedSections.length;

    if (totalSections === 0) {
        if(indicatorPanel) indicatorPanel.style.display = "none";
        return;
    }

    gsap.set(indicatorPanel, { opacity: 0, pointerEvents: "none" });

    const trackHeight = indicatorPanel.offsetHeight;
    const dynamicThumbHeight = trackHeight / totalSections;
    activeLine.style.height = `${dynamicThumbHeight}px`;

    const maxTravel = trackHeight - dynamicThumbHeight;
    const stepSize  = totalSections > 1 ? maxTravel / (totalSections - 1) : 0;

    const updateTextSmoothly = (newText) => {
        if (indicatorText.textContent !== newText) {
            gsap.to(indicatorText, {
                opacity: 0, y: -5, duration: 0.4, ease: "power1.inOut",
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

    ScrollTrigger.create({
        trigger: allTrackedSections[0],
        start: "top bottom", 
        onEnter: () => gsap.set(indicatorPanel, { opacity: 1, pointerEvents: "auto" }),
        onLeaveBack: () => gsap.set(indicatorPanel, { opacity: 0, pointerEvents: "none" })
    });

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

    // 2. ¡CAMBIO CLAVE! Ahora el tope es dinámico (etiqueta main)
    const mainContainer = document.querySelector("main");
    if(mainContainer) {
        ScrollTrigger.create({
            trigger: mainContainer, 
            start: "bottom 80%",         
            onEnter: () => gsap.set(indicatorPanel, { opacity: 0, pointerEvents: "none" }),
            onLeaveBack: () => gsap.set(indicatorPanel, { opacity: 1, pointerEvents: "auto" })
        });
    }
});