# FullStack-III

# 🛡️ Backend Sano y Salvo - Microservicio de Gestión de Mascotas

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-JSON%20Web%20Token-black?style=for-the-badge&logo=jsonwebtokens)

Este repositorio contiene el **Core Backend** del proyecto **Sano y Salvo**, una plataforma diseñada para la gestión integral de mascotas perdidas, encontradas y avistadas. Este microservicio ha sido construido bajo estándares de grado profesional, priorizando la seguridad, la integridad de los datos y el rendimiento.

---

## 🏛️ Visión General de la Arquitectura

El microservicio está diseñado bajo una arquitectura de **Capas (Layered Architecture)**, asegurando un desacoplamiento efectivo entre la exposición de APIs, la lógica de negocio y la persistencia de datos.

### Características Arquitectónicas:
- **Stateless Authentication:** Implementación estricta de JWT (JSON Web Token) para eliminar la necesidad de sesiones en servidor.
- **Patrón DTO (Data Transfer Object):** Desacoplamiento total de las entidades JPA y los objetos expuestos al cliente para mayor seguridad.
- **Manejo Global de Excepciones:** Centralización de errores mediante `@RestControllerAdvice` para respuestas estandarizadas.
- **Validación Zero Trust:** Verificación de propiedad de recursos en cada operación de escritura/borrado.

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología |
| :--- | :--- |
| **Lenguaje** | Java 21 (LTS) |
| **Framework Principal** | Spring Boot 3.2.5 |
| **Seguridad** | Spring Security 6.2 |
| **Persistencia** | Spring Data JPA / Hibernate |
| **Base de Datos** | MySQL 8.0 |
| **DB para Testing** | H2 Database (In-memory) |
| **Documentación** | SpringDoc OpenAPI / Swagger UI |
| **Validación de Datos** | Jakarta Bean Validation (Hibernate Validator) |
| **Gestión de Dependencias** | Maven |

*Todas las tecnologías reflejan las versiones configuradas en el archivo principal del proyecto.*

---

## 🔐 Seguridad y Autenticación

El sistema implementa un esquema de seguridad de alta robustez basado en **Spring Security**:

### 1. Flujo de Autenticación (JWT)
- **Generación:** Al autenticarse, el servidor genera un token firmado con algoritmo **HS256**.
- **Validación:** Cada petición privada es interceptada por un `JwtAuthenticationFilter` que valida la firma, la expiración y extrae los privilegios (Roles).
- **Cifrado de Contraseñas:** Uso de `BCryptPasswordEncoder` para el hashing de credenciales sensibles.

### 2. Control de Acceso (RBAC)
- **Rutas Públicas:** Registro de usuarios, login, listado de reportes y visualización de recursos multimedia (`/uploads/**`).
- **Rutas de Usuario:** Creación de mascotas, reportes y edición de perfil propio.
- **Rutas de Administrador:** Gestión total de la base de usuarios y promoción de roles.

### 3. Reglas Zero Trust
Se implementó lógica de validación en la capa de `@Service` para asegurar que:
- Solo el dueño de un reporte puede editarlo o eliminarlo.
- Solo el dueño de una mascota puede asociarle reportes o modificar sus datos.

---

## 📦 Módulos y Funcionalidades

### 👤 Gestión de Usuarios
- **Validación de RUN:** Implementación de algoritmo Módulo 11 para la validación de RUN chileno.
- **Unicidad:** Restricciones de base de datos para prevenir duplicados en Email y RUN.
- **Roles Dinámicos:** Soporte para escalado de privilegios.

### 🐕 Gestión de Mascotas
- **Campos Flexibles:** Soporte para mascotas con y sin microchip.
- **Enums Estrictos:** Clasificación por Especie (PERRO, GATO, etc.), Tamaño (PEQUEÑO, MEDIANO, GRANDE) y Sexo.

### 🚨 Sistema de Reportes y Multimedia
- **Geolocalización:** Registro exacto mediante latitud y longitud.
- **Estados de Reporte:** Flujo de vida del reporte (ACTIVO, RESUELTO, CANCELADO).
- **File System Storage:** Las imágenes se almacenan físicamente en el servidor (directorio `/uploads`) utilizando UUIDs para garantizar la unicidad de los nombres de archivos.

---

## 📖 Documentación Interactiva (Swagger)

La API está completamente documentada bajo el estándar OpenAPI 3. Una vez iniciada la aplicación, acceda a:

👉 **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

*Nota: Swagger ha sido configurado para permitir el envío de tokens Bearer en las cabeceras de prueba.*

---

## 🧪 Pruebas Automáticas (Testing)

El proyecto cuenta con una cobertura integral de tests dividida en:

* **Unit Tests:** Validación de lógica pura en servicios.
* **Repository Tests:** Pruebas de integración con base de datos H2 para validar queries de Spring Data JPA.
* **Controller Tests:** Uso de `MockMvc` para simular peticiones HTTP y validar el flujo de seguridad.

---

## 📂 Estructura del Proyecto

```text
src/main/java/com/mascotas/mascotas/
├── config/             # Configuraciones globales (Swagger, Web, CORS)
├── controller/         # Adaptadores de entrada (REST Controllers)
├── dto/                # Objetos de Transferencia de Datos (Request/Response)
├── exception/          # Manejo de errores personalizado
├── model/              # Entidades JPA y Enums de dominio
├── repository/         # Acceso a datos (Spring Data JPA)
├── security/           # Lógica JWT y Configuración de Spring Security
├── service/            # Lógica de negocio (Intermediario)
└── util/               # Clases de utilidad (Validadores de RUT, etc.)

📡 Integración con Sistemas Externos
Este backend está optimizado para ser consumido por un BFF (Backend For Frontend) u otro cliente orquestador.

Acepta cabeceras Authorization: Bearer <TOKEN>.

Permite peticiones multipart/form-data para el procesamiento conjunto de JSON y archivos binarios.

Expone los recursos multimedia a través de la ruta /uploads/ con configuración de recursos estáticos.

Desarrollado como núcleo del ecosistema Sano y Salvo.