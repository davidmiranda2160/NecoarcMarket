# NecoarcMarket---
Este es un proyecto semestral para la asignatura de **DesarrolloFullStack I** que consiste en un Marketplace escalable y desacoplado basado en una arquitectura de microservicios. El sistema utiliza **Spring Boot 3** (Java 21) para la lógica de negocio, **MySQL** (administrado vía Laragon) para la persistencia de datos relacionales, y **Flyway** para el control de versiones y gestión de migraciones automáticas de las bases de datos.

## Estructura General del Proyecto
El ecosistema de **NecoArcMarket** está compuesto por 10 microservicios independientes y especializados. Cada uno maneja su propio ciclo de vida, puerto y esquema de base de datos, comunicándose de forma síncrona mediante peticiones HTTP HTTP-REST estructuradas a través de `WebClient`.

A continuación, se detalla la responsabilidad, el stack tecnológico y la lógica de integración/conexión de cada componente:

---

## 1. Microservicio de Usuario (Puerto: 8080)
Encargado de la gestión de perfiles de clientes, credenciales, datos de contacto y la orquestación inicial de intenciones de pago en la plataforma.

### Tecnologías y Dependencias
- **Spring Web**: Publicación de la API REST para la administración de usuarios.
- **Spring Data JPA**: Abstracción y operaciones CRUD sobre MySQL (`db_necoarcmarket_usuarios`).
- **Lombok**: Reducción de código repetitivo (Entidades, DTOs y Mappers).
- **WebClient (`.block()`)**: Cliente HTTP síncrono para interactuar con servicios financieros.

### Conexiones e Integración
- **Orquestación de pagos**: Expone el endpoint `POST /v1/usuario/{id}/pagos`. Cuando un usuario solicita procesar una transacción, este servicio utiliza `WebClient` para conectarse síncronamente con el microservicio de **Pagos** (`ms-pagos` en el Puerto 8092) enviando un `PagosRequest` mapeado con los datos de la transacción.

---

## 2. Microservicio de Productos (Puerto: 8081)
Gestiona el catálogo comercial de la tienda (nombres, descripciones, categorías y precios de los productos). Mantiene un desacoplamiento estricto al no guardar stock de forma física.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Operaciones del catálogo comercial sobre la base de datos `db_necoarcmarket_productos`.
- **Lombok y Flyway**
- **WebClient (`.block()`)**

### Conexiones e Integración
- **Vitrina**: Al listar los productos o buscar uno por ID, el servicio intercepta la petición y gatilla una consulta vía `WebClient` hacia `ms-inventario` (Puerto 8082).
- **Disponibilidad en tiempo real**: Evalúa las existencias físicas devueltas; si el stock es `0` o nulo, altera el estado del producto en el JSON de salida a `activo: false` de manera dinámica, protegiendo la experiencia del cliente sin corromper la persistencia local.

---

## 3. Microservicio de Inventario (Puerto: 8082)
Actúa como el control de bodega física centralizado del ecosistema. Su única responsabilidad es supervisar las existencias y procesar las mermas o reabastecimientos de stock.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia y control de stock sobre `db_necoarcmarket_inventario`.
- **Lombok**

### Conexiones e Integración
- **Aislamiento de stock**: Expone endpoints clave como `PUT /v1/inventario/producto/{id}/descontar`.
- **Consumo centralizado**: Es invocado síncronamente por dos frentes: por `ms-productos` (Puerto 8081) para calcular la disponibilidad de la vitrina.

---

## 4. Microservicio de Búsqueda (Puerto: 8083)
Motor de consultas optimizado. No maneja persistencia local de datos maestros para evitar la redundancia y la inconsistencia de información.

### Tecnologías y Dependencias
- **Spring Web / Lombok**
- **WebClient (`.block()`)**: Cliente para multi-consultas paralelas en el backend.

### Conexiones e Integración
- **Composición de datos**: Cuando un cliente busca o filtra elementos, este servicio consume en paralelo a `ms-productos` (Puerto 8081) para traer la información comercial y a `ms-inventario` (Puerto 8082) para cruzar el stock. Genera un resultado unificado (Match por ID) en una sola respuesta HTTP rápida hacia el consumidor.

---

## 5. Microservicio de Reseña (Puerto: 8086)
Centraliza el feedback de la comunidad, permitiendo a los clientes asignar calificaciones (de 1 a 5) y comentarios escritos a los artículos adquiridos.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia de valoraciones sobre `db_necoarcmarket_resenas`.
- **Validation**: Restricciones de integridad `@Min` y `@Max` para el control de la escala de estrellas.

### Conexiones e Integración
- **Validación cruzada**: Cada reseña guardada requiere un `idProducto` y un `idUsuario`. Antes de persistir, el servicio utiliza `WebClient` para verificar síncronamente en `ms-productos` (Puerto 8081) que el ítem realmente exista, evitando spam o registros huérfanos.

---

## 6. Microservicio de Notificación (Puerto: 8085)
Centralizador de comunicaciones encargado de simular de manera reactiva el envío de alertas y estados del sistema al cliente final.

### Tecnologías y Dependencias
- **Spring Web / Lombok**
- **Logger Unificado (`@Slf4j`)**

### Conexiones e Integración
- **Notificacion-usuario**: funciona haciendo conexion con usuario con `ms-usuario` indicando notificaciones de ofertas o ventas.

---

## 7. Microservicio de Envío (Puerto: 8084)
Módulo logístico encargado de la gestión de despachos, asignación de direcciones de entrega y seguimiento de los estados de envío de los paquetes.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Gestión del ciclo de transporte sobre `db_necoarcmarket_envio`.
- **WebClient (`.block()`)**

### Conexiones e Integración
- **Vinculación Comercial**: Cuando se consulta un envío por ID o se actualiza su estado (ej: "En Camino", "Entregado"), el servicio utiliza `WebClient` para conectarse con `ms-ordenes` (Puerto 8088) consumiendo el endpoint `/ordenes/{id}` para inyectar dinámicamente el detalle de la compra en el `EnvioResponse`.

---

## 8. Microservicio de Carrito (Puerto: 8087)
Gestiona la persistencia temporal de productos seleccionados por un usuario antes de iniciar el flujo de checkout.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia relacional sobre `db_necoarcmarket_carrito`.
- **Flyway / Validation**
- **WebClient (`.block()`)**

### Conexiones e Integración
- **Enriquecimiento del Carrito**: Al consultar el carrito de un usuario, consume por `WebClient` los servicios de `ms-usuario` (Puerto 8080) y `ms-productos` (Puerto 8081) para certificar que ambos sigan existiendo y vigentes, estructurando una respuesta enriquecida sin duplicar columnas en su base de datos local.

---

## 9. Microservicio de Órdenes (Puerto: 8088)
El núcleo transaccional del sistema. Consolida los carritos de compra activos, genera las órdenes de compra (boletas) y gestiona el historial transaccional del usuario.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia crítica sobre `db_necoarcmarket_ordenes`.
- **WebClient (`.block()`)**

### Conexiones e Integración
- **Flujo de Consolidación**: Al generar una orden, se comunica mediante `WebClient` con `ms-carrito` (Puerto 8087) para extraer recursivamente los ítems agregados por el `usuarioId`. 
- 

---

## 10. Microservicio de Pagos (Puerto: 8092)
Entidad financiera interna del Marketplace encargada de validar montos, procesar las pasarelas de pago (Crédito, Débito) y autorizar las transacciones.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia de transacciones sobre `db_neroarcmarket_pagos`.
- **Flyway / Lombok**
- **WebClient (`.block()`)**

### Conexiones e Integración
- **Validación de Flujos**: El microservicio no orquesta compras por sí mismo. Se activa e interactúa directamente dentro del flujo del microservicio de Usuarios (ms-usuario, Puerto 8080) al momento en que este procesa un pago, encargándose exclusivamente de autorizar los montos y registrar la transacción financiera.

---

## Configuración del Entorno e Instalación

### Requisitos Previos
* **Java Development Kit (JDK) 17** instalado correctamente en las variables de entorno.
* **Laragon** Con el servicio de **MySQL** activo.
* **Postman** 

### Matriz de Puertos y Persistencia

| Microservicio | Puerto | Base de Datos (MySQL - Laragon) | Conectividad Clave (WebClient a...) |
| :--- | :---: | :--- | :--- |
| `ms-usuario` | **8080** | `db_necoarcmarket_usuarios` | `ms-pagos` (8092) |
| `ms-productos` | **8081** | `db_necoarcmarket_productos` | `ms-inventario` (8082) |
| `ms-inventario` | **8082** | `db_necoarcmarket_inventario` | `ms-producto` (8081) |
| `ms-busqueda` | **8083** | `db_necoarcmarket_busqueda` | `ms-productos` (8081) y `ms-inventario` (8082) |
| `ms-resena` | **8086** | `db_necoarcmarket_resenas` | `ms-productos` (8081) |
| `ms-notificacion`| **8085** | `db_necoarcmarket_notificacion` | `ms-usuario` (8080) |
| `ms-envio` | **8084** | `db_necoarcmarket_envio` | `ms-ordenes` (8088) |
| `ms-carrito` | **8087** | `db_necoarcmarket_carrito` | `ms-usuario` (8080) y `ms-productos` (8081) |
| `ms-ordenes` | **8088** | `db_necoarcmarket_ordenes` | `ms-carrito` (8087) y `ms-notificacion` (8085) |
| `ms-pagos` | **8092** | `db_neroarcmarket_pagos` | `ms-ordenes` (8088) |

### Ejemplo de Configuración Base (`application.properties`)
Cada microservicio cuenta con su archivo de propiedades ubicado en `src/main/resources/application.properties`. A modo de ilustración, esta es la estructura que permite la interconectividad del **Microservicio de Carrito**:

```properties
spring.application.name=carrito
server.port=8087

# Configuración de Base de Datos e Inyección de Flyway
spring.datasource.url=jdbc:mysql://localhost:3306/db_necoarcmarket_carrito?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de ORM Hibernate / JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Endpoints de Microservicios Externos Vinculados
services.usuario.baseUrl=http://localhost:8080
service.producto.baseUrl=http://localhost:8081