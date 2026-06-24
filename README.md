# NecoarcMarket
Este es un proyecto semestral para la asignatura de **DesarrolloFullStack I** que consiste en un Marketplace escalable y desacoplado basado en una arquitectura de microservicios. El sistema utiliza **Spring Boot 4** Java 17 para la lógica de negocio, **MySQL** (administrado vía Laragon) para la persistencia de datos relacionales, y **Flyway** para el control de versiones y gestión de migraciones automáticas de las bases de datos.

# Integrantes

* **David Miranda**
* **Sebastián Apablaza**

## Estructura General del Proyecto
El ecosistema de **NecoArcMarket** está compuesto por 10 microservicios independientes y especializados. Cada uno maneja su propio ciclo de vida, puerto y esquema de base de datos, comunicándose de forma síncrona mediante peticiones HTTP HTTP-REST estructuradas a través de `WebClient`.

A continuación, se detalla la responsabilidad, el stack tecnológico y la lógica de integración/conexión de cada componente:

---

### Tecnologías y Dependencias
- **Spring Web**: Publicación de la API REST para la administración de usuarios.
- **Spring Data JPA**: Abstracción y operaciones CRUD sobre MySQL (`db_necoarcmarket_usuarios`).
- **Lombok**: Reducción de código repetitivo (Entidades, DTOs y Mappers).
- **WebClient (`.block()`)**: Cliente HTTP síncrono para interactuar con servicios financieros.

### Conexiones e Integración

---

## 1. Microservicio de Usuario (Puerto: 8080)
Encargado de la gestión de perfiles de clientes, credenciales, datos de contacto y la orquestación inicial de intenciones de pago en la plataforma.

## Pruebas unitarias del servicio de usuarios

### Reglas de Negocio Críticas del Servicio de Usuarios

- **Correo Electronico único**: No se puede registrar ni actualizar un usuario si el correo electrónico ya existe en el sistema.

- **Validar CRUD del sistema**: Garantizar que las operaciones de consulta, actualización y eliminación validen estrictamente la existencia previa del identificador (id).

| Regla | Estado | Casos Cubiertos | Checklist |
| :--- | :---: | :--- | :--- |
| **1. Registro de Usuarios** | **`Cubierta`** | • Registro exitoso (Caso feliz).<br>• Bloqueo por correo electrónico ya existente. | [x] Caso feliz.<br>[x] Caso de error. |
| **2. Consulta de Usuarios** | **`Cubierta`** | • Listado completo de registros maestros.<br>• Búsqueda por ID existente.<br>• Control de ID no localizado. | [x] Caso feliz.<br>[x] Caso de error. |
| **3. Actualización de Datos** | **`Cubierta`** | • Modificación de datos de usuario existente.<br>• Intento de actualización sobre ID no válido.<br>• Conflicto por uso de correo duplicado. | [x] Caso feliz.<br>[x] Caso de error. |
| **4. Eliminación de Registros** | **`Cubierta`** | • Remoción de usuario existente por ID.<br>• Intento de borrado sobre usuario inexistente. | [x] Caso feliz.<br>[x] Caso de error. |


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

## Pruebas Unitarias del servicio de Envio

### Reglas de Negocio Críticas del Servicio de Envíos

### Conexiones e Integración
- **Vinculacion con el microservicio de ordenes**: Al registrar o gestionar un despacho, el servicio utiliza WebClient para conectarse síncronamente con el microservicio de Órdenes (ms-ordenes en el Puerto 8088). A través de esta consulta, verifica la existencia de la ordenId para extraer los datos necesarios y vincular directamente el envío con el usuario correspondiente.

- **Regla de Negocio**: El sistema valida que la relación sea exclusiva, impidiendo que se genere duplicidad de registros, es decir, una orden de compra no puede tener mas de un envio asociado en la persistencia local.

1. **El envio tiene que ser único por orden:** Una orden de compra no puede registrar más de un envío asociado para evitar duplicidades en el despacho.
2. **Vinculación Comercial:** Cada envío debe estar obligatoriamente enlazado a una orden válida y existente obtenida desde el servicio externo.
3. **Validación del Ciclo de Transporte:** Cualquier actualización en el estado del paquete (ej. "ENTREGADO", "EN_CAMINO") exige la comprobación de la existencia previa del identificador (`id`) en los registros.


Regla | Estado | Casos Cubiertos | Checklist |
| :--- | :---: | :--- | :--- |
| **1. Generación de Despachos** | **`Cubierta`** | • Registro exitoso vinculando `ordenId` (Caso feliz). | [x] Caso feliz.<br>[ ] Falta probar error por orden duplicada. |
| **2. Actualización de Estados** | **`Cubierta`** | • Transición de estado a "ENTREGADO" de forma correcta.<br>• Bloqueo con excepción al intentar actualizar un ID inexistente. | [x] Caso feliz.<br>[x] Caso de error. |

---

## 8. Microservicio de Carrito (Puerto: 8087)
Se encarga de guardar los productos que el usuario va agregando a un carrito virtual de compras antes de iniciar el proceso de crear la orden

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia relacional sobre `db_necoarcmarket_carrito`.
- **Flyway / Validation**
- **WebClient (`.block()`)**

### Conexiones e Integración

- **Generación del Carrito**: Para que un carrito o item pueda ser creado de manera valida en el sistema, el servicio se conecta síncronamente mediante WebClient con el Microservicio de Usuarios (ms-usuario en el Puerto 8080) y el Microservicio de Productos (ms-productos en el Puerto 8081).

## Pruebas Unitarias del servicio de Carrito

### Reglas de Negocio Críticas del Servicio de Carrito

1. **Validación de Identidades Maestras:** No se puede generar ni listar un carrito si el usuario o los productos no existen o no están vigentes en los microservicios externos (`ms-usuario` y `ms-productos`).
2. **Control de Cantidades Mínimas:** Toda adición de productos a la bolsa debe contar con una cantidad estrictamente mayor a cero.
3. **Acumulación Dinámica:** Si un usuario agrega un producto que ya tiene en su bolsa, el sistema debe acumular la cantidad y recalcular el monto total automáticamente en lugar de duplicar la fila.
4. **Validación de Existencia del Ítem:** Cualquier operación de consulta o actualización sobre un carrito vacío o un identificador inválido debe disparar el control de excepciones correspondiente.


| Regla| Estado | Casos Cubiertos | Checklist |
| :--- | :---: | :--- | :--- |
| **1. Adición de Productos (Nuevo)** | **`Cubierta`** | • Registro de producto nuevo, cálculo del total y almacenamiento exitoso (Caso feliz). | [x] Caso feliz.<br>[ ] Falta simular error si falla el cliente externo de producto/usuario. |
| **2. Acumulación de Productos** | **`Cubierta`** | • Suma de cantidades y actualización del monto total sobre un ítem existente en la bolsa. | [x] Caso feliz.<br>[x] Caso alternativo. |
| **3. Control de Cantidades** | **`Cubierta`** | • Bloqueo con excepción `IllegalArgumentException` al intentar ingresar cantidad menor o igual a cero. | [x] Caso feliz.<br>[x] Caso de error. |
| **4. Listado por Usuario** | **`Cubierta`** | • Retorno correcto de la lista de ítems activos.<br>• Disparo de `NoSuchElementException` si la bolsa está vacía. | [x] Caso feliz.<br>[x] Caso de error. |
| **5. Actualización de Ítems** | **`Cubierta`** | • Lanzamiento de `NoSuchElementException` al intentar modificar un identificador de ítem que no existe. | [x] Caso de error.<br>[ ] Falta testear caso feliz de actualización de cantidad. |

---

## 9. Microservicio de Ordenes (Puerto: 8088)
Es el encargado de armar los pedidos de los clientes. Su tarea principal es tomar todos los productos que el usuario dejó guardados en su carrito, juntarlos para armar la boleta final y guardar el historial de lo que ha comprado cada persona.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia crítica sobre `db_necoarcmarket_ordenes`.
- **WebClient (`.block()`)**

### Conexiones e Integración
- **Flujo de Consolidación y Despacho**: Al momento de generar una nueva orden, el servicio utiliza `WebClient` para conectarse con el **Microservicio de Carrito** (`ms-carrito`, Puerto 8087) y el **Microservicio de Inventario** (`ms-inventario`, Puerto 8082) ejecutando los siguientes pasos de negocio:
  1. **Validación**: Verifica que el carrito asociado al `usuarioId` realmente exista y contenga productos.
  2. **Persistencia**: Registra y guarda el detalle de la compra en su base de datos local.
  3. **Actualización de Stock**: Se comunica con el servicio de inventario para descontar las unidades compradas de la bodega física.
  4. **Limpieza**: Una vez asegurada la orden y el stock, da la instrucción de vaciar el carrito del usuario y
  asignarle un estado de orden "Pendiende" para dejarlo listo para procesar en ms-pagos.

---

## 10. Microservicio de Pagos (Puerto: 8092)
Es el encargado de manejar los procesos financieros del sistema. Su funcion es revisar que el dinero que intenta pagar el cliente coincida exactamente con el total de su pedido para aprobar o rechazar la transacción.

### Tecnologías y Dependencias
- **Spring Web / Spring Data JPA**: Persistencia de transacciones sobre `db_neroarcmarket_pagos`.
- **Flyway / Lombok**
- **WebClient (`.block()`)**

### Conexiones e Integración
- **Flujo de Verificación y Cambio de Estado**: Este servicio no trabaja solo; se activa cuando se realiza un intento de pago y utiliza `WebClient` para conectarse con el **Microservicio de Ordenes** (`ms-ordenes`, Puerto 8088) realizando los siguientes pasos:
  1. **Revisión**: Busca la orden que está en estado "pendiente" y comprueba mediante la petición que el `ordenId`, el `usuarioId` y el monto ingresado sean los correctos y coincidan con el total registrado.
  2. **Procesamiento**: Si todos los datos calzan perfectamente, el sistema aprueba el pago. Si hay algún error o los montos no coinciden, lo rechaza.
  3. **Actualización**: Automáticamente cambia el estado de la orden a "pagada" o "rechazada" en el servicio de órdenes. Si queda como pagada, el pedido se deja listo para que el **Microservicio de Envío** pueda tomarlo y armar el despacho.

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
| `ms-resena` | **8086** | `db_necoarcmarket_resenas` | `ms-usuario` (8080) |
| `ms-notificacion`| **8085** | `db_necoarcmarket_notificacion` | `ms-usuario` (8080) |
| `ms-envio` | **8084** | `db_necoarcmarket_envio` | `ms-ordenes` (8088) |
| `ms-carrito` | **8087** | `db_necoarcmarket_carrito` | `ms-usuario` (8080) y `ms-productos` (8081) |
| `ms-ordenes` | **8088** | `db_necoarcmarket_ordenes` | `ms-carrito` (8087) y `ms-notificacion` (8085) |
| `ms-pagos` | **8092** | `db_neroarcmarket_pagos` | `ms-ordenes` (8088) |

### Ejemplo de Configuración Base en YAML (`application.yml`)

Cada microservicio cuenta con su archivo de propiedades estructurado en formato **YAML**, ubicado en `src/main/resources/application.yml`. A modo de ilustración, esta es la arquitectura limpia que permite la interconectividad del **Microservicio de Carrito**:

```yaml
server:
  port: 8087

spring:
  application:
    name: carrito

  # Configuración de Base de Datos relacional en Laragon
  datasource:
    url: jdbc:mysql://localhost:3306/db_necoarcmarket_carrito?createDatabaseIfNotExist=true&serverTimezone=UTC
    username: root
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver

  # Configuración de ORM Hibernate y Gestión de Migraciones con Flyway
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  flyway:
    enabled: true
    baseline-on-migrate: true

# Endpoints de Microservicios Externos Vinculados para WebClient
services:
  usuario:
    baseUrl: http://localhost:8080
  producto:
    baseUrl: http://localhost:8081
```

---

## Documentación de APIs con Swagger 

El proyecto utiliza **Springdoc OpenAPI 3** para la generación automática de la documentación interactiva de cada microservicio. Esto permite probar los endpoints en tiempo real directamente desde el navegador sin necesidad de configurar colecciones externas en Postman.

### Acceso Local a las Interfaces de Usuario

Una vez que los microservicios estén levantados de forma local, puedes acceder a la documentación individual de cada uno a través de las siguientes URLs en tu navegador:

| Microservicio | Puerto | Enlace a Swagger UI |
| :--- | :---: | :--- |
| `ms-usuario` | **8080** | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| `ms-productos` | **8081** | [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html) |
| `ms-inventario` | **8082** | [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html) |
| `ms-busqueda` | **8083** | [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html) |
| `ms-envio` | **8084** | [http://localhost:8084/swagger-ui/index.html](http://localhost:8084/swagger-ui/index.html) |
| `ms-notificacion`| **8085** | [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html) |
| `ms-resena` | **8086** | [http://localhost:8086/swagger-ui/index.html](http://localhost:8086/swagger-ui/index.html) |
| `ms-carrito` | **8087** | [http://localhost:8087/swagger-ui/index.html](http://localhost:8087/swagger-ui/index.html) |
| `ms-ordenes` | **8088** | [http://localhost:8088/swagger-ui/index.html](http://localhost:8088/swagger-ui/index.html) |
| `ms-pagos` | **8092** | [http://localhost:8092/swagger-ui/index.html](http://localhost:8092/swagger-ui/index.html) |

---

### Configuración Base de la Dependencia

Para que estas rutas funcionen, cada microservicio incluye la siguiente dependencia en su archivo de configuración:

xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>

---

## 🔀 Configuración del API Gateway (Puerto: 8090)

El proyecto utiliza **Spring Cloud Gateway** como punto único de entrada para todas las solicitudes de los clientes (Frontend, aplicaciones móviles o pruebas en Postman). Este componente actúa como un proxy inverso distribuyendo el tráfico de manera inteligente hacia los 10 microservicios del ecosistema, evitando la necesidad de exponer múltiples puertos al exterior.

### 🛣️ Matriz de Enrutamiento y Mapeo de Rutas

Todas las peticiones externas deben apuntar al dominio o puerto base del Gateway: `http://localhost:9090`. El Gateway intercepta el prefijo de la URL y redirige síncronamente la petición al puerto interno correspondiente:

| Endpoint Expuesto (Gateway) | Método HTTP | Microservicio Destino | Puerto Interno |
| :--- | :---: | :--- | :---: |
| `/api/v1/usuarios/**` | *Cualquiera* | `ms-usuario` | **8080** |
| `/api/v1/productos/**` | *Cualquiera* | `ms-productos` | **8081** |
| `/api/v1/inventario/**` | *Cualquiera* | `ms-inventario` | **8082** |
| `/api/v1/busqueda/**` | `GET` | `ms-busqueda` | **8083** |
| `/api/v1/envios/**` | *Cualquiera* | `ms-envio` | **8084** |
| `/api/v1/notificaciones/**` | *Cualquiera* | `ms-notificacion` | **8085** |
| `/api/v1/resenas/**` | *Cualquiera* | `ms-resena` | **8086** |
| `/api/v1/carrito/**` | *Cualquiera* | `ms-carrito` | **8087** |
| `/api/v1/ordenes/**` | *Cualquiera* | `ms-ordenes` | **8088** |
| `/api/v1/pagos/**` | *Cualquiera* | `ms-pagos` | **8092** |

---

### Ejemplo de Configuración en el Gateway (`application.yml`)

Para lograr este comportamiento descentralizado, el archivo de configuración del microservicio de Gateway (`ms-gateway`) define las rutas de la siguiente manera:

```yaml
server:
  port: 9090

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        # Enrutamiento hacia el Microservicio de Carrito
        - id: carrito_route
          uri: http://localhost:8087
          predicates:
            - Path=/api/v1/carrito/**

        # Enrutamiento hacia el Microservicio de Órdenes
        - id: ordenes_route
          uri: http://localhost:8088
          predicates:
            - Path=/api/v1/ordenes/**

        # Enrutamiento hacia el Microservicio de Usuarios
        - id: usuarios_route
          uri: http://localhost:8080
          predicates:
            - Path=/api/v1/usuarios/**
```