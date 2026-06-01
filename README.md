# Examen DAD - Unidad 2: Academia de Talleres (Microservicios)

Sistema de gestion de una academia de talleres construido con **Spring Boot 3.5.14**,
**Java 21** y **Spring Cloud 2025.0.2**.

## Arquitectura

| # | Servicio | Puerto | Rol |
|---|----------|--------|-----|
| 1 | `ms-admin-registry-server` | 8761 | Servidor de registro (Eureka Server) |
| 2 | `ms-admin-config-server` | 8888 | Servidor de configuracion (Config Server, perfil `native`) |
| 3 | `ms-admin-api-gateway` | 8080 | Puerta de enlace (Spring Cloud Gateway) |
| 4 | `ms-gestion-instructor` | 8081 | CRUD de instructores (BD `db_instructor`) |
| 5 | `ms-gestion-alumno` | 8082 | CRUD de alumnos (BD `db_alumno`) |
| 6 | `ms-gestion-taller` | 8083 | Servicio **compuesto** de talleres (BD `db_taller`) |
| 7 | `ms-seguridad` | 8084 | **Seguridad**: usuarios, roles, permisos, login/register y emision de **JWT** (BD `db_seguridad`) |

La configuracion de cada microservicio (puerto, datasource, JPA, Eureka) se entrega
desde el **Config Server** (carpeta `ms-admin-config-server/src/main/resources/configurations`).
El **Gateway** enruta las peticiones del cliente y `ms-gestion-taller` consume a los
otros dos microservicios mediante **OpenFeign** (descubrimiento por Eureka, `lb://`).

## Seguridad (JWT)

El microservicio **`ms-seguridad`** gestiona las tablas `usuario`, `rol` y `permiso`
(relaciones M:N) y emite tokens **JWT** firmados con HMAC-SHA256. Las contrasenas se
guardan cifradas con **BCrypt**.

- **Validacion descentralizada:** cada microservicio (`instructor`, `alumno`, `taller`)
  valida el JWT por su cuenta con el **secreto compartido** (`jwt.secret`, entregado por
  el Config Server). Si el token falta o es invalido responde **401**; si el rol no tiene
  permiso, **403**.
- **Propagacion del token:** `ms-gestion-taller` reenvia la cabecera `Authorization` a sus
  llamadas Feign internas (interceptor en `FeignClientConfig`), para que `instructor` y
  `alumno` acepten las peticiones compuestas.
- **Roles y permisos** (se crean solos al arrancar `ms-seguridad`):

| Rol | Puede |
|-----|-------|
| `ROLE_ALUMNO` | Ver talleres, inscribirse y cancelar su inscripcion |
| `ROLE_INSTRUCTOR` | Todo lo anterior + crear/editar/eliminar talleres y gestionar alumnos/instructores |
| `ROLE_ADMIN` | Todo + gestionar usuarios, roles y permisos en `ms-seguridad` |

- **Usuario administrador inicial:** `admin` / `admin123`.
- En el registro (`/api/auth/register`) un usuario solo puede pedir el rol **ALUMNO** o
  **INSTRUCTOR**; el rol **ADMIN** se asigna desde el CRUD de usuarios.

### Registro = cuenta + ficha (auto-aprovisionamiento)

Al registrarse, `ms-seguridad` crea **dos cosas a la vez**:

1. La **cuenta** en `db_seguridad` (para hacer login).
2. La **ficha de negocio** en el microservicio que corresponde al rol, llamando por
   **OpenFeign** a `ms-gestion-instructor` o `ms-gestion-alumno`.

Por eso el body del registro trae tambien los datos de la ficha. Segun el rol:

| Rol | Crea ficha en | Campos extra requeridos |
|-----|---------------|-------------------------|
| `ALUMNO` | `ms-gestion-alumno` | `nombres`, `apellidos`, `dni`, `telefono` |
| `INSTRUCTOR` | `ms-gestion-instructor` | `nombres`, `apellidos`, `especialidad`, `telefono` |

La cuenta guarda `idReferencia` (el id de la ficha creada) y `tipoFicha`, para saber
a que ficha pertenece. Como el usuario aun no tiene token al registrarse, `ms-seguridad`
genera un **token de servicio** interno (rol ADMIN, vida de 1 minuto) para esa llamada
Feign. Si la ficha no se puede crear (p. ej. dni o correo duplicado), el registro falla
y no se crea la cuenta.

> Los endpoints `POST /api/instructores` y `POST /api/alumnos` siguen existiendo, pero
> ahora son de uso **administrativo** (alta manual por un ADMIN/INSTRUCTOR). El alta normal
> de usuarios es por `/api/auth/register`.

### Como usarlo

```bash
# 1a) Registrarse como ALUMNO (crea cuenta + ficha de alumno) -> devuelve el JWT
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{
  "username": "luis", "correo": "luis@correo.com", "password": "alumno123",
  "rol": "ALUMNO", "nombres": "Luis", "apellidos": "Gomez",
  "dni": "70123456", "telefono": "999111222"
}'

# 1b) ...o login con una cuenta existente (p. ej. el admin) -> devuelve el JWT
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{
  "username": "admin", "password": "admin123"
}'

# 2) Usar el token en las demas peticiones
curl http://localhost:8080/api/talleres -H "Authorization: Bearer <TOKEN_AQUI>"
```

En Postman, la carpeta **0. Seguridad (Auth)** guarda el token automaticamente en la
variable `{{token}}` y todas las peticiones lo envian como `Bearer` (auth heredada).

## Resiliencia (Resilience4j)

El microservicio compuesto **`ms-gestion-taller`** aplica **Circuit Breaker + Retry**
sobre sus llamadas Feign a instructor y alumno, encapsuladas en una capa *gateway*
(`InstructorGateway` / `AlumnoGateway`) con anotaciones `@CircuitBreaker` y `@Retry`.
Se usan anotaciones (no el circuit breaker automatico de Feign) para que la llamada
corra en el **mismo hilo** y el interceptor que propaga el JWT siga funcionando.

| Patron | Configuracion |
|--------|---------------|
| **Timeout** | `connect/read-timeout` de Feign = 3s |
| **Retry** | 3 intentos, 500ms de espera, solo ante fallos de red/5xx (no en 404) |
| **Circuit Breaker** | abre si fallan >=50% de 10 llamadas; 10s abierto; ignora 404 |
| **Fallback** | degradacion segun el caso (ver abajo) |

**Comportamiento del fallback (lectura vs escritura):**
- **Lectura** `GET /api/talleres/{id}/detalle`: **degrada** — si instructor cae muestra
  "No disponible"; si alumno cae muestra la lista de inscritos vacia. La peticion no falla.
- **Escritura** (crear taller, inscribir): **falla con 503** si el servicio externo no
  responde (no se crea un taller ni una inscripcion sin poder verificar al instructor/alumno).
- Un **404** real del servicio externo se distingue de una caida: devuelve **404** (no existe),
  no 503, y no abre el circuito.

Estado de los circuitos (actuator, en el puerto directo 8083):
<http://localhost:8083/actuator/circuitbreakers> y `/actuator/health`.

**Como demostrarlo:** crea instructor/alumno/taller, apaga `ms-gestion-instructor`, y llama
`GET /api/talleres/{id}/detalle` (responde degradado) vs `POST /api/talleres` (responde 503).

## Consistencia de datos (Saga por orquestacion)

El **registro** (`POST /api/auth/register`) es una transaccion distribuida sobre DOS bases
de datos, asi que usa el patron **Saga orquestada** con `ms-seguridad` como coordinador:

```
Paso 1 (remoto): crear la ficha en db_instructor / db_alumno   (Feign)
Paso 2 (local) : crear la cuenta en db_seguridad
   └─ si el Paso 2 falla -> COMPENSACION: eliminar la ficha del Paso 1 (DELETE Feign)
```

Sin la Saga, si el Paso 2 fallara (p. ej. choque de `username` en una carrera), quedaria
una **ficha huerfana** (instructor/alumno sin cuenta). La accion compensatoria
(`InstructorClient.eliminar` / `AlumnoClient.eliminar`) revierte el Paso 1 para que el
sistema quede consistente: o existen **ambos** (ficha + cuenta) o **ninguno**.

Detalles de la implementacion (en `AuthService.registrar`):
- Se usa `saveAndFlush` para forzar el INSERT de la cuenta y que el error caiga dentro del
  bloque que dispara la compensacion (no al cerrar la transaccion).
- Es **orquestada** (no por coreografia) porque la comunicacion es sincrona (Feign) y no
  hay broker de mensajes; asi se logra consistencia sin infraestructura extra.
- Los pasos y la compensacion quedan registrados en el log (`SAGA registro:` / `SAGA compensacion:`).

## Requisitos previos

- JDK 21
- MySQL en `localhost:3307` con usuario `root` / contrasena `mysql123`
  (las bases de datos se crean solas gracias a `createDatabaseIfNotExist=true`).
  Si tus credenciales son distintas, cambialas en los `.yml` de `configurations/`.

## Orden de arranque (IMPORTANTE)

Arranca cada servicio desde su carpeta con `./mvnw spring-boot:run` (o desde el IDE):

```
1. ms-admin-registry-server   (Eureka)      -> esperar a que levante
2. ms-admin-config-server     (Config)
3. ms-admin-api-gateway       (Gateway)
4. ms-seguridad               (login/register + JWT)
5. ms-gestion-instructor
6. ms-gestion-alumno
7. ms-gestion-taller
```

Paneles utiles:
- Eureka: <http://localhost:8761>
- Config Server (ejemplo): <http://localhost:8888/ms-gestion-instructor/default>

## Endpoints (todo pasa por el Gateway en el puerto 8080)

### Instructores  `/api/instructores`
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/instructores` | Listar |
| GET | `/api/instructores/{id}` | Buscar por id |
| POST | `/api/instructores` | Crear |
| PUT | `/api/instructores/{id}` | Actualizar |
| DELETE | `/api/instructores/{id}` | Eliminar |

### Alumnos  `/api/alumnos`
Mismo CRUD + `GET /api/alumnos/por-ids?ids=1,2,3` (usado por talleres).

### Talleres (compuesto)  `/api/talleres`
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/talleres` | Listar talleres |
| POST | `/api/talleres` | Crear taller (valida que el instructor exista) |
| GET | `/api/talleres/{id}/detalle` | **Vista compuesta**: taller + instructor + alumnos inscritos |
| POST | `/api/talleres/{idTaller}/inscribir/{idAlumno}` | Inscribir alumno (valida cupo y duplicados) |
| DELETE | `/api/talleres/{idTaller}/inscribir/{idAlumno}` | Cancelar inscripcion |

### Inscripciones (CRUD)  `/api/inscripciones`
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/inscripciones` | Listar todas (o filtrar con `?idTaller=1`) |
| GET | `/api/inscripciones/{id}` | Buscar por id |
| POST | `/api/inscripciones` | Crear (body `{ "idTaller": 1, "idAlumno": 1 }`; valida cupo y duplicados) |
| PUT | `/api/inscripciones/{id}` | Actualizar |
| DELETE | `/api/inscripciones/{id}` | Eliminar |

## Ejemplo de prueba (via Gateway)

> Todas las rutas (menos `/api/auth/**`) exigen un JWT valido. Primero inicia sesion
> y guarda el token en una variable; luego envialo en la cabecera `Authorization`.

```bash
# 0) Login como administrador y guardar el token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r .token)

# 1) Crear un instructor (rol INSTRUCTOR o ADMIN)
curl -X POST http://localhost:8080/api/instructores \
  -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{
  "nombres": "Ana", "apellidos": "Torres", "especialidad": "Pintura",
  "correo": "ana@academia.com", "telefono": "987654321"
}'

# 2) Crear un alumno
curl -X POST http://localhost:8080/api/alumnos \
  -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{
  "nombres": "Luis", "apellidos": "Gomez", "dni": "70123456",
  "correo": "luis@correo.com", "telefono": "999111222"
}'

# 3) Crear un taller (idInstructor = 1)
curl -X POST http://localhost:8080/api/talleres \
  -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{
  "nombre": "Acuarela basica", "descripcion": "Taller introductorio",
  "fechaInicio": "2026-07-01", "cupoMaximo": 20, "idInstructor": 1
}'

# 4) Inscribir al alumno 1 en el taller 1
curl -X POST http://localhost:8080/api/talleres/1/inscribir/1 \
  -H "Authorization: Bearer $TOKEN"

# 5) Ver la vista compuesta (instructor + alumnos via OpenFeign)
curl http://localhost:8080/api/talleres/1/detalle \
  -H "Authorization: Bearer $TOKEN"
```
