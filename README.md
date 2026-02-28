<div align="center">

# 🏯 Hotel Miyabi — Sistema de Gestión Hotelera

**Plataforma web transaccional para la gestión integral de reservas y operaciones hoteleras bajo el concepto Ryokan**

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

</div>

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Funcionalidades](#-funcionalidades)
- [Tecnologías](#️-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Ejecución](#-ejecución)
- [Autores](#-autores)

---

## 📖 Descripción

**Hotel Miyabi** es una plataforma web transaccional desarrollada con **Spring Boot** y **MySQL**, orientada a digitalizar y automatizar los procesos operativos y comerciales de un hotel boutique de concepto *Ryokan* (alojamiento tradicional japonés).

El sistema elimina la dependencia de procesos manuales (hojas de cálculo, cuadernos), centralizando toda la información en una base de datos relacional que optimiza la toma de decisiones y reduce el error humano.

### Entornos funcionales

| Módulo | Descripción |
|---|---|
| 🌐 **Front-office** | Portal público para huéspedes: catálogo de habitaciones, disponibilidad en tiempo real y gestión de reservas autónoma. |
| 🔧 **Back-office** | Panel administrativo para el personal: control de ocupación, check-in/check-out y facturación. |

---

## ✨ Funcionalidades

- ✅ **Gestión de Reservas** — Validación lógica de fechas para evitar overbooking
- ✅ **Ciclo de Hospedaje** — Registro de Check-in y Check-out con cálculo automático de estancia
- ✅ **Control de Inventario** — Administración de habitaciones, estados (limpio/ocupado) y tarifas
- ✅ **Servicios Adicionales** — Registro de consumos extras cargados a la cuenta del huésped
- ✅ **Panel "Mis Reservas"** — Historial de reservas y descarga de comprobantes en PDF
- ✅ **Interfaz Responsive** — Diseño accesible desde dispositivos móviles y escritorio

---

## 🛠️ Tecnologías

| Categoría | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 17+ |
| Framework Backend | Spring Boot | 3.x |
| Motor de plantillas | Thymeleaf | — |
| Base de datos | MySQL | 8.x |
| ORM | Spring Data JPA / Hibernate | — |
| Build Tool | Apache Maven | 3.x |
| Frontend Estático | HTML5, CSS3, JavaScript | — |

---

## 📁 Estructura del Proyecto

```
miyabi/
├── src/
│   ├── main/
│   │   ├── java/com/miyabi/
│   │   │   ├── controller/       # Controladores MVC (rutas y lógica de presentación)
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── models/           # Entidades JPA (mapeo a tablas de BD)
│   │   │   ├── repository/       # Interfaces Spring Data JPA
│   │   │   └── service/          # Lógica de negocio
│   │   └── resources/
│   │       ├── scripts/          # Scripts SQL (creación e inicialización de BD)
│   │       ├── static/
│   │       │   ├── css/          # Hojas de estilo
│   │       │   ├── img/          # Imágenes y recursos visuales
│   │       │   └── js/           # Scripts JavaScript
│   │       ├── templates/
│   │       │   ├── admin/        # Vistas del panel administrativo (Back-office)
│   │       │   ├── fragments/    # Componentes reutilizables (navbar, footer)
│   │       │   ├── pages/        # Páginas públicas (Front-office)
│   │       │   └── users/        # Vistas relacionadas al huésped
│   │       └── application.properties
└── pom.xml
```

---

## 📦 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado lo siguiente:

- [Java JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Apache Maven 3.x](https://maven.apache.org/download.cgi)
- [MySQL 8.x](https://dev.mysql.com/downloads/mysql/)
- Un IDE compatible (recomendado: [IntelliJ IDEA](https://www.jetbrains.com/idea/))

---

## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/am4teratsu/Miyabi.git
cd Miyabi
```

### 2. Crear la base de datos

Conéctate a tu servidor MySQL y ejecuta el script de inicialización incluido en el proyecto:

```bash
mysql -u TU_USUARIO -p < src/main/resources/scripts/DB_Miyabi.sql
```

> Esto creará la base de datos `DB_Miyabi` con todas sus tablas y datos iniciales.

### 3. Configurar `application.properties`

Navega a `src/main/resources/` y crea o edita el archivo `application.properties` con tus credenciales de MySQL:

```properties
spring.application.name=miyabi

# Configuración de Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/DB_Miyabi?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=USUARIO_DE_MYSQL
spring.datasource.password=CONTRASEÑA_DE_MYSQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

> ⚠️ **Importante:** Reemplaza `USUARIO_DE_MYSQL` y `CONTRASEÑA_DE_MYSQL` con tus credenciales reales. No subas este archivo con datos sensibles al repositorio. Se recomienda añadir `application.properties` al `.gitignore`.

---

## ▶️ Ejecución

### Opción A — Desde Spring Tool Suite (STS) o Eclipse

Abre el proyecto, haz clic derecho sobre él y selecciona:

```
Run As > Spring Boot App
```

O bien usa el atajo de teclado: `Alt + Shift + X, B`

### Opción B — Compilar y ejecutar el JAR

```bash
mvn clean package
java -jar target/miyabi-0.0.1-SNAPSHOT.jar
```

### Opción C — Desde la terminal con Maven

```bash
mvn spring-boot:run
```

---

Una vez iniciado, accede a la aplicación en tu navegador:

```
http://localhost:8080
```

---

## 👥 Autores

Proyecto desarrollado por estudiantes de **Computación e Informática — CIBERTEC**, ciclo IV (2026).

| Nombre | Rol |
|---|---|
| **Mathias Porras Vilca** | Coordinador |
| **Fabricio Sullca Sanchez** | Desarrollador |
| **Miguel Uriarte Pacheco** | Desarrollador |
| **Piero Hilario Velasquez** | Desarrollador |

**Docente:** Juan Pablo Huaman Rojas

---

<div align="center">
  <sub>Desarrollado con ❤️ y filosofía <em>Omotenashi</em> · CIBERTEC 2026</sub>
</div>