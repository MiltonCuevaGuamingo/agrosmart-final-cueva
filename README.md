# AgroSmart Final - Milton Cueva

## Datos del estudiante

- Nombre: Milton Ariel Cueva Guamingo
- NRC: 30405
- Repositorio: `agrosmart-final-cueva`

## Semilla personal

Mi cédula termina en `46`, por eso mi semilla personal quedó así:

- Dos últimos dígitos: `46`
- Nombre de la tabla: `tbl_productos_base_46`
- Puerto del perfil `prod`: `8146`
- Último dígito: `6`
- Categoría asignada: `Flores`

La tabla se calculó con la regla `tbl_productos_base_` + los dos ultimos dígitos de la cédula.  
La categoría no cambia el nombre de la tabla solo cambia los datos sembrados.

## Tecnologías usadas

- Java 21
- Spring Boot
- Spring WebFlux
- Spring Data JPA
- PostgreSQL
- Docker Compose
- LangChain4j
- JUnit 5
- Mockito
- Reactor Test

## Ejecución del proyecto

### 1. Requisitos

- Java 21
- Docker Desktop encendido
- Git
- Maven Wrapper del proyecto (`mvnw.cmd`)

### 2. Base de datos

El proyecto usa PostgreSQL con Docker Compose.

En mi caso el puerto local `5432` tenia conflicto con otro servicio de mi máquina, por eso publique PostgreSQL en `5434` y mantuve `5432` dentro del contenedor.

### 3. Levantar PostgreSQL

```powershell
docker compose up -d
```

### 4. Ejecutar la aplicación
.\mvnw.cmd spring-boot:run
La aplicación arranca en:
http://localhost:8146

### 5. Ejecutar pruebas
.\mvnw.cmd test

### Configuración principal

### application.properties
```
spring.application.name=agrosmart
spring.profiles.active=prod
```
### application-prod.properties
```
server.port=8146

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.datasource.url=jdbc:postgresql://localhost:5434/agrosmart_db
spring.datasource.username=agrosmart
spring.datasource.password=agrosmart

langchain4j.open-ai.chat-model.api-key=demo
langchain4j.open-ai.chat-model.model-name=gpt-4o-mini
langchain4j.open-ai.chat-model.timeout=30s
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true
logging.level.dev.langchain4j=DEBUG
```

### Datos sembrados
La tabla creada es:
tbl_productos_base_46

Los productos sembrados pertenecen a la categoría Flores.
Válidos:
* Rosas de exportación
* Claveles premium
* Orquídeas seleccionadas

Inválidos:
* Girasoles sin precio
* Tulipanes sin notificación

### Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/productos` | Devuelve los productos comercializables |
| GET | `/api/productos/{id}` | Devuelve un producto por id |
| GET | `/api/agrosmart/publicidad` | Genera texto publicitario con IA |

### Ejemplos reales con curl
Productos comercializables
curl.exe http://localhost:8146/api/productos
Salida real:
```
[{"id":1,"nombre":"ROSAS DE EXPORTACIÓN","categoria":"Flores","precioUsd":24.50,"correosNotificacion":["ventas@rosas.ec","logistica@rosas.ec"]},{"id":2,"nombre":"CLAVELES PREMIUM","categoria":"Flores","precioUsd":18.75,"correosNotificacion":["comercial@claveles.ec"]},{"id":3,"nombre":"ORQUÍDEAS SELECCIONADAS","categoria":"Flores","precioUsd":32.40,"correosNotificacion":["orquideas@agrosmart.ec"]}]
```
Producto por id
```
curl.exe http://localhost:8146/api/productos/1
```
Salida real:
```
{"id":1,"nombre":"Rosas de exportación","categoria":"Flores","precioUsd":24.50,"correosNotificacion":["ventas@rosas.ec","logistica@rosas.ec"]}
```
Producto inexistente
```
curl.exe -i http://localhost:8146/api/productos/9999
```
Salida real:
```
HTTP/1.1 404 Not Found
Content-Type: application/json
Content-Length: 127

{"timestamp":"2026-07-31T06:03:10.708Z","path":"/api/productos/9999","status":404,"error":"Not Found","requestId":"6d26d087-6"}
```
Publicidad con IA
curl.exe "http://localhost:8146/api/agrosmart/publicidad?producto=Rosas%20de%20exportacion&audiencia=floristerias%20premium"

Salida real:
```
"Eleva tus ramos con nuestras rosas de exportación: frescura, calidad y elegancia para tus clientes."
```

### Justificación de operadores reactivos

En ProductoService usé estos operadores:
Mono.fromCallable(...): para envolver la llamada bloqueante al repositorio JPA y diferir su ejecución.
subscribeOn(Schedulers.boundedElastic()): para mover la llamada bloqueante fuera del event loop de Netty.
flatMapMany(Flux::fromIterable): para convertir la lista del repositorio en un flujo reactivo.
map(ProductoMapper::toDominio): para convertir la entidad JPA al modelo de dominio inmutable.
map(ProductoFilters.A_MAYUSCULAS): para devolver un nuevo producto con el nombre en mayúsculas.
filter(ProductoFilters.IS_VALID): para dejar solo productos comercializables.
doOnNext(ProductoFilters.LOG_PRODUCTO): para imprimir trazas sin transformar los elementos.
defaultIfEmpty(PRODUCTO_GENERICO): para devolver un producto de respaldo si no queda ninguno válido.
switchIfEmpty(Mono.error(...)): para convertir un resultado vacio en error cuando un id no existe.
timeout(...): para evitar que la llamada a IA espere demasiado.
onErrorResume(...): para devolver un mensaje alternativo si el proveedor de IA falla.

### Puente bloqueante a reactivo
En este proyecto hay dos partes bloqueantes:
* JPA/Hibernate, porque usa JDBC
* LangChain4j, porque hace una llamada externa al modelo
Como la API esta hecha con WebFlux, no conviene ejecutar esas llamadas en el event loop de Netty.
Por eso las envolví con Mono.fromCallable(...) y las moví a Schedulers.boundedElastic().
Eso me permitio mantener servicios reactivos sin usar block() y sin bloquear los hilos que atienden peticiones HTTP.