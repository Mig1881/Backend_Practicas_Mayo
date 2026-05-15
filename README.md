# API CozyBites  

API REST desarrollada con Spring Boot para la gestión de clientes, productos de menú y pedidos de una aplicación de restauración. El proyecto incluye autenticación con JWT, persistencia en MySQL, carga inicial de usuarios administradores, colección de Postman/Newman y pipeline de GitHub Actions para compilar, levantar la API y ejecutar pruebas.  


---

## Tabla de contenidos

- [Características principales](#características-principales)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Modelo de datos](#modelo-de-datos)
- [Seguridad y roles](#seguridad-y-roles)
- [Requisitos previos](#requisitos-previos)
- [Configuración local](#configuración-local)
- [Ejecución del proyecto](#ejecución-del-proyecto)
- [Datos iniciales](#datos-iniciales)
- [Uso de la API](#uso-de-la-api)
- [Endpoints](#endpoints)
- [Ejemplos de peticiones](#ejemplos-de-peticiones)
- [Postman y Newman](#postman-y-newman)
- [Docker](#docker)
- [GitHub Actions](#github-actions)
- [OpenAPI](#openapi)
- [Manejo de errores](#manejo-de-errores)
- [Notas importantes y mejoras recomendadas](#notas-importantes-y-mejoras-recomendadas)

---

## Características principales

- Registro y login de usuarios mediante email y contraseña.
- Generación y validación de tokens JWT.
- Gestión de clientes con endpoints protegidos por rol de administrador.
- Perfil propio del usuario autenticado: consulta, modificación y eliminación.
- Gestión de productos o items del menú.
- Soporte para imagen binaria en productos mediante campo `byte[]` y endpoint específico para servirla.
- Gestión de pedidos vinculados a clientes y productos.
- Filtros básicos en listados de clientes, productos y pedidos.
- Validación de DTOs con Jakarta Validation.
- Manejo centralizado de excepciones con respuestas JSON.
- Base de datos MySQL mediante Docker Compose.
- Pipeline CI con GitHub Actions, Docker y Newman.

---

## Tecnologías

- Java 21
- Spring Boot 3.4.2
- Spring Web
- Spring Data JPA
- Spring Security
- JWT con `jjwt` 0.12.3
- MySQL 8
- mariadb 11.3.2
- Lombok
- ModelMapper 3.1.1
- Maven Wrapper
- Docker / Docker Compose
- Postman / Newman
- GitHub Actions

---

## Estructura del proyecto

```text
.
├── .github/workflows/ci.yml                  # Pipeline de integración continua
├── .mvn/wrapper/maven-wrapper.properties     # Configuración del Maven Wrapper
├── Dockerfile.github                         # Imagen Docker de la API para CI/CD
├── docker-compose.dev.yaml                   # MySQL local para desarrollo
├── local.postman_environment.json            # Entorno Postman local
├── openapi.yaml                              # Especificación OpenAPI existente
├── pom.xml                                   # Dependencias y configuración Maven
├── practicas.postman_collection.json         # Colección Postman/Newman
└── src
    ├── main
    │   ├── java/com/svalero/apicozybites
    │   │   ├── config                        # Beans de configuración
    │   │   ├── controller                    # Controladores REST
    │   │   ├── domain                        # Entidades JPA
    │   │   ├── domain/dto                    # DTOs de entrada/salida
    │   │   ├── exception                     # Excepciones y handler global
    │   │   ├── repository                    # Repositorios Spring Data
    │   │   ├── security                      # Seguridad, JWT y filtros
    │   │   └── service                       # Lógica de negocio
    │   └── resources
    │       ├── application.properties         # Configuración de Spring
    │       └── data.sql                       # Datos iniciales
    └── test
        └── java/com/svalero/apicozybites      # Test de carga de contexto
```

---

## Modelo de datos

### Customer

Representa un cliente o usuario de la aplicación.

Campos principales:

| Campo | Tipo | Descripción |
|---|---:|---|
| `id` | `long` | Identificador autogenerado. |
| `name` | `String` | Nombre del cliente. |
| `email` | `String` | Email único y obligatorio. Se usa como username para login. |
| `phone` | `String` | Teléfono. |
| `password` | `String` | Contraseña almacenada. En `/auth/register` se guarda en BCrypt. |
| `role` | `String` | Rol del usuario. Por defecto `USER`. |
| `age` | `int` | Edad. |
| `advertising` | `boolean` | Indica si acepta publicidad. |
| `registrationDate` | `LocalDate` | Fecha de registro. |
| `profileImageUrl` | `String` | URL de imagen de perfil. |
| `orders` | `List<Order>` | Pedidos asociados. |

Relaciones:

- Un `Customer` puede tener muchos `Order`.
- La relación se define con `@OneToMany(mappedBy = "customer")`.

### Item

Representa un producto o plato del menú.

| Campo | Tipo | Descripción |
|---|---:|---|
| `id` | `long` | Identificador autogenerado. |
| `name` | `String` | Nombre del producto. |
| `description` | `String` | Descripción del producto. |
| `price` | `Float` | Precio. |
| `isNew` | `Boolean` | Indica si es novedad. Por defecto `true`. |
| `releaseDate` | `LocalDate` | Fecha de lanzamiento. |
| `image` | `byte[]` | Imagen almacenada como `MEDIUMBLOB` en MySQL. |
| `orders` | `List<Order>` | Pedidos asociados. |

Relaciones:

- Un `Item` puede aparecer en muchos `Order`.
- La imagen se devuelve mediante una URL generada en el DTO: `/items/{id}/image`.

### Order

Representa un pedido realizado por un cliente sobre un producto.

| Campo | Tipo | Descripción |
|---|---:|---|
| `id` | `Long` | Identificador autogenerado. |
| `orderDate` | `LocalDate` | Fecha del pedido. |
| `totalPrice` | `Float` | Precio total del pedido. |
| `customer` | `Customer` | Cliente asociado. |
| `item` | `Item` | Producto asociado. |

Relaciones:

- Muchos pedidos pueden pertenecer a un cliente: `@ManyToOne` hacia `Customer`.
- Muchos pedidos pueden estar asociados a un producto: `@ManyToOne` hacia `Item`.

---

## Seguridad y roles

La API usa Spring Security con sesiones stateless y autenticación JWT.

### Flujo de autenticación

1. El usuario se registra con `POST /auth/register`.
2. El usuario inicia sesión con `POST /auth/login`.
3. La API devuelve un token JWT.
4. Las peticiones protegidas deben enviar el token en la cabecera:

```http
Authorization: Bearer <token>
```

### Roles

El proyecto utiliza roles guardados como texto en la tabla `customers`:

- `USER`: rol por defecto para usuarios registrados desde `/auth/register`.
- `ADMIN`: rol de administración.

En `UserDetailsServiceImpl`, si el rol no empieza por `ROLE_`, se le añade automáticamente el prefijo requerido por Spring Security. Por ejemplo, `ADMIN` se convierte en `ROLE_ADMIN`.

### Reglas de acceso actuales

| Ruta | Acceso |
|---|---|
| `/auth/**` | Público. |
| `OPTIONS /**` | Público. |
| `GET /items/**` | Público. |
| `/customers/me` | Usuario autenticado. |
| `/customers/**` | Solo `ADMIN`. |
| Resto de rutas | Usuario autenticado. |

Por tanto:

- Consultar productos es público.
- Crear, modificar o eliminar productos requiere token.
- Gestionar pedidos requiere token.
- Consultar/modificar/eliminar el perfil propio requiere token.
- Administrar clientes por ID requiere rol `ADMIN`.

---

## Requisitos previos

Instalar en el equipo:

- Java 21.
- Docker y Docker Compose.
- Git.
- Maven opcional, porque el proyecto incluye Maven Wrapper.
- Postman opcional para probar manualmente.
- Node.js/npm opcional si se quiere ejecutar Newman localmente.

---

## Configuración local

La configuración actual está en `src/main/resources/application.properties`:

```properties
spring.application.name=apicozybites
spring.datasource.url=jdbc:mysql://localhost:3306/bbdd_cozybites_dev
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
server.port=8080
```

Base de datos esperada:

- Host: `localhost`
- Puerto: `3306`
- Base de datos: `bbdd_cozybites_dev`
- Usuario: `root`
- Contraseña: `root`

El JWT tiene valores por defecto definidos en `JwtUtils`:

- `jwt.secret`: `SuperSecretaFirmaDeKebapiApiQueTieneQueSerLarga1234567890`
- `jwt.expirationMs`: `86400000` milisegundos, equivalente a 24 horas.

También se pueden configurar mediante propiedades externas:

```properties
jwt.secret=UnaClaveMuyLargaYSeguraParaFirmarTokensJWT
jwt.expirationMs=86400000
```

---

## Ejecución del proyecto

### 1. Levantar MySQL

Desde la raíz del proyecto:

```bash
docker compose -f docker-compose.dev.yaml up -d
```

Esto levanta un contenedor MySQL 8 con:

```yaml
MYSQL_ROOT_PASSWORD: root
MYSQL_DATABASE: bbdd_cozybites_dev
```

Para comprobar que el contenedor está activo:

```bash
docker ps
```

### 2. Dar permisos al Maven Wrapper si hace falta

En algunos sistemas, especialmente si el proyecto se ha descargado como ZIP, puede ser necesario:

```bash
chmod +x mvnw
```

### 3. Ejecutar la API

```bash
./mvnw spring-boot:run
```

Alternativa si tienes Maven instalado:

```bash
mvn spring-boot:run
```

La API quedará disponible en:

```text
http://localhost:8080
```

### 4. Compilar el proyecto

```bash
./mvnw clean package -DskipTests
```

El JAR se generará en:

```text
target/*.jar
```

---

## Datos iniciales

El archivo `src/main/resources/data.sql` inserta tres usuarios administradores si todavía no existen:

| Nombre | Email | Contraseña | Rol |
|---|---|---:|---|
| Natalia | `natalia@kebapi.com` | `1111` | `ADMIN` |
| Nestor | `nestor@kebapi.com` | `2222` | `ADMIN` |
| Miguel | `miguel@kebapi.com` | `3333` | `ADMIN` |

Las contraseñas están guardadas ya cifradas con BCrypt.

Ejemplo de login como administrador:

```http
POST /auth/login
Content-Type: application/json

{
  "email": "natalia@kebapi.com",
  "password": "1111"
}
```

---

## Uso de la API

Base URL local:

```text
http://localhost:8080
```

Para endpoints protegidos, añadir:

```http
Authorization: Bearer <token>
```

Formato general de fechas:

```text
YYYY-MM-DD
```

Ejemplo:

```text
2026-05-15
```

---

## Endpoints

### Auth

| Método | Ruta | Seguridad | Descripción |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Registra un nuevo cliente con rol `USER`. |
| `POST` | `/auth/login` | Público | Autentica por email/contraseña y devuelve JWT. |

### Customers

| Método | Ruta | Seguridad | Descripción |
|---|---|---|---|
| `GET` | `/customers` | `ADMIN` | Lista clientes. Permite filtros `name` y `email`. |
| `POST` | `/customers` | `ADMIN` | Crea un cliente desde `CustomerInDto`. |
| `GET` | `/customers/{customerId}` | `ADMIN` | Obtiene un cliente por ID. |
| `PUT` | `/customers/{customerId}` | `ADMIN` | Modifica un cliente por ID. |
| `DELETE` | `/customers/{customerId}` | `ADMIN` | Elimina un cliente y sus pedidos asociados. |
| `GET` | `/customers/me` | Autenticado | Obtiene el perfil del usuario autenticado. |
| `PUT` | `/customers/me` | Autenticado | Actualiza el perfil del usuario autenticado. |
| `DELETE` | `/customers/me` | Autenticado | Elimina el perfil propio y sus pedidos asociados. |

Filtros de `/customers`:

| Query param | Tipo | Ejemplo |
|---|---|---|
| `name` | `String` | `/customers?name=Natalia` |
| `email` | `String` | `/customers?email=natalia@kebapi.com` |
| `name` + `email` | `String` | `/customers?name=Natalia&email=natalia@kebapi.com` |

### Items

| Método | Ruta | Seguridad | Descripción |
|---|---|---|---|
| `GET` | `/items` | Público | Lista productos. Permite filtros `name` y `description`. |
| `GET` | `/items/{itemId}` | Público | Obtiene un producto por ID. |
| `GET` | `/items/{itemId}/image` | Público | Devuelve la imagen binaria del producto como `image/jpeg`. |
| `POST` | `/items` | Autenticado | Crea un producto. |
| `PUT` | `/items/{itemId}` | Autenticado | Modifica un producto. |
| `DELETE` | `/items/{itemId}` | Autenticado | Elimina un producto. |

Filtros de `/items`:

| Query param | Tipo | Ejemplo |
|---|---|---|
| `name` | `String` | `/items?name=Avocado%20toast` |
| `description` | `String` | `/items?description=bread` |
| `name` + `description` | `String` | `/items?name=Avocado%20toast&description=bread` |

### Orders

| Método | Ruta | Seguridad | Descripción |
|---|---|---|---|
| `GET` | `/orders` | Autenticado | Lista pedidos. Permite filtros `orderDate` y `totalPrice`. |
| `GET` | `/orders/{orderId}` | Autenticado | Obtiene un pedido por ID. |
| `POST` | `/orders` | Autenticado | Crea un pedido asociado a cliente y producto. |
| `PUT` | `/orders/{orderId}` | Autenticado | Modifica un pedido. |
| `DELETE` | `/orders/{orderId}` | Autenticado | Elimina un pedido. |

Filtros de `/orders`:

| Query param | Tipo | Ejemplo |
|---|---|---|
| `orderDate` | `LocalDate` | `/orders?orderDate=2026-05-15` |
| `totalPrice` | `Float` | `/orders?totalPrice=12.5` |
| `orderDate` + `totalPrice` | Mixto | `/orders?orderDate=2026-05-15&totalPrice=12.5` |

---

## Ejemplos de peticiones

### Registro

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "juan",
    "email": "juan@gmail.com",
    "password": "juan",
    "phone": "+34 654789321",
    "age": 20,
    "advertising": false
  }'
```

Respuesta esperada:

```text
¡Cliente registrado con éxito!
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@gmail.com",
    "password": "juan"
  }'
```

Respuesta esperada:

```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "customerId": 4,
  "email": "juan@gmail.com",
  "roles": ["ROLE_USER"]
}
```

Guardar el token para las siguientes llamadas:

```bash
TOKEN="<jwt>"
```

### Crear producto

```bash
curl -X POST http://localhost:8080/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Avocado toast",
    "description": "Tostada con aguacate y pan artesanal",
    "price": 10.95,
    "isNew": true,
    "releaseDate": "2026-05-15"
  }'
```

Respuesta esperada:

```json
{
  "id": 1,
  "name": "Avocado toast",
  "description": "Tostada con aguacate y pan artesanal",
  "price": 10.95,
  "isNew": true,
  "releaseDate": "2026-05-15",
  "imageUrl": "/items/1/image"
}
```

### Crear producto con imagen en JSON

El DTO espera el campo `image`, de tipo `byte[]`. Jackson puede recibirlo como Base64:

```json
{
  "name": "Seed Bread",
  "description": "Pan con semillas",
  "price": 12,
  "isNew": true,
  "releaseDate": "2026-05-15",
  "image": "/9j/4AAQSkZJRgABAQ..."
}
```

Importante: el nombre correcto del campo es `image`. Si se envía `imagen`, el backend no lo mapeará al DTO actual.

### Listar productos

```bash
curl http://localhost:8080/items
```

### Obtener un producto

```bash
curl http://localhost:8080/items/1
```

### Obtener imagen de producto

```bash
curl http://localhost:8080/items/1/image --output item.jpg
```

Si el producto no tiene imagen, la API devuelve un error 404 con `Image Not Found`.

### Modificar producto

```bash
curl -X PUT http://localhost:8080/items/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Avocado toast light",
    "description": "Tostada ligera con aguacate",
    "price": 10.95,
    "isNew": true,
    "releaseDate": "2026-05-15"
  }'
```

Si no se envía una nueva imagen, el servicio mantiene la imagen anterior.

### Eliminar producto

```bash
curl -X DELETE http://localhost:8080/items/1 \
  -H "Authorization: Bearer $TOKEN"
```

Respuesta esperada:

```http
204 No Content
```

### Consultar perfil propio

```bash
curl http://localhost:8080/customers/me \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar perfil propio

```bash
curl -X PUT http://localhost:8080/customers/me \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Juan",
    "phone": "+34 600111222",
    "age": 21,
    "advertising": true,
    "profileImageUrl": "https://example.com/avatar.jpg"
  }'
```

### Crear pedido

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "orderDate": "2026-05-15",
    "totalPrice": 10.95,
    "customerId": 4,
    "itemId": 1
  }'
```

Respuesta esperada: un `OrderOutDto` con los datos del pedido, el cliente asociado y el producto asociado.

### Listar pedidos

```bash
curl http://localhost:8080/orders \
  -H "Authorization: Bearer $TOKEN"
```

### Filtrar pedidos

```bash
curl "http://localhost:8080/orders?orderDate=2026-05-15&totalPrice=10.95" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Postman y Newman

El proyecto incluye:

- `practicas.postman_collection.json`
- `local.postman_environment.json`

El entorno local contiene, entre otras, estas variables:

| Variable | Valor |
|---|---|
| `HOST2` | `http://localhost:8080` |
| `HOST` | `http://localhost:8081` |
| `jwt_token` | vacío inicialmente |

La colección realiza pruebas sobre:

- Registro.
- Login y guardado del token en `jwt_token`.
- CRUD básico de `items`.
- Casos de error 400 y 404 en `items`.

Para ejecutar la colección con Newman:

```bash
npm install -g newman
newman run -e local.postman_environment.json practicas.postman_collection.json
```

Antes de ejecutar Newman, la API debe estar levantada en `http://localhost:8080` y MySQL debe estar disponible.

---

## Docker

### Base de datos local

`docker-compose.dev.yaml` levanta solo MySQL:

```bash
docker compose -f docker-compose.dev.yaml up -d
```

Para parar y eliminar el contenedor:

```bash
docker compose -f docker-compose.dev.yaml down
```

### Imagen Docker de la API

El proyecto incluye `Dockerfile.github`:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Para construir la imagen manualmente:

```bash
./mvnw clean package -DskipTests
docker build -f Dockerfile.github -t apicozybites-api .
```

Para ejecutarla usando la red del host, como hace el workflow de GitHub Actions:

```bash
docker run -d --name apicozybites-api --network host apicozybites-api
```

En Windows o macOS, `--network host` puede no comportarse igual que en Linux. En esos casos conviene crear un `docker-compose` que levante API y MySQL en la misma red y configurar la URL de conexión con el nombre del servicio de base de datos.

---

## GitHub Actions

El workflow está en:

```text
.github/workflows/ci.yml
```

Se ejecuta en:

- `push` a `main`.
- `pull_request` contra `main`.

### Job `test`

Pasos principales:

1. Checkout del repositorio.
2. Configuración de Java 21 con Temurin.
3. Creación de archivo `.env` con valores de MySQL y JWT.
4. Arranque de MySQL con `docker-compose.dev.yaml`.
5. Compilación del proyecto con `mvn package -DskipTests`.
6. Construcción de imagen Docker de la API.
7. Arranque de la API con `docker run --network host`.
8. Espera de 30 segundos.
9. Instalación de Newman.
10. Ejecución de la colección Postman.
11. Impresión de logs de API y base de datos en caso de fallo.

### Job `build-and-push`

Solo se ejecuta si:

- El job `test` termina correctamente.
- El evento es `push`.
- La rama es `main`.

Construye y sube la imagen a Docker Hub con la etiqueta:

```text
mig1881/apicozybites-api:latest
```

Necesita estos secrets en GitHub:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

---

## OpenAPI

Existe un archivo `openapi.yaml`, pero no está completamente alineado con el código actual.

Diferencias detectadas:

- El servidor aparece como `http://localhost:8080/Kebapi`, pero la API actual no define `server.servlet.context-path`, por lo que la base real es `http://localhost:8080`.
- La especificación documenta `isVegetarian`, pero el DTO actual usa `isNew` y `releaseDate`.
- No aparecen todos los endpoints actuales, como `/auth/**`, `/orders/**`, `/customers/me` o `/items/{itemId}/image`.
- Algunos DTOs no reflejan todos los campos actuales.

Recomendación: actualizar `openapi.yaml` para que documente el contrato real de la API o generar la documentación automáticamente con `springdoc-openapi`.

---

## Manejo de errores

El proyecto tiene un `GlobalExceptionHandler` que devuelve errores estructurados para entidades no encontradas y errores de validación.

### Ejemplo 404 de producto inexistente

```json
{
  "error": "Item Not Found",
  "message": "This item does not exist"
}
```

### Ejemplo 404 de imagen inexistente

```json
{
  "error": "Image Not Found",
  "message": "This item haves no image"
}
```

### Ejemplo 400 por validación

Si se crea un item sin `name`:

```json
{
  "name": "Name is a mandatory field"
}
```

### Ejemplo 409 por email duplicado en clientes

El endpoint `POST /customers` captura `DataIntegrityViolationException` y responde con `409 Conflict`:

```text
El email o el nombre de usuario ya existen en la base de datos.
```

---

## Notas importantes y mejoras recomendadas

### 1. No devolver contraseñas en DTOs de salida

`CustomerOutDto` incluye actualmente el campo `password`. Aunque la contraseña esté cifrada, no debería enviarse nunca al cliente. Recomendación:

- Eliminar `password` de `CustomerOutDto`.
- Evitar devolver entidades `Customer` directamente en `GET /customers/{customerId}`.

### 2. Cifrar contraseñas también fuera de `/auth/register`

`/auth/register` guarda la contraseña con BCrypt. Sin embargo, los métodos administrativos que usan `CustomerInDto` mapean y guardan el password directamente. Recomendación:

- Cifrar password también en `CustomerService.add` y `CustomerService.modify`.
- O separar DTOs de administración y de autenticación.

### 3. Externalizar secretos y credenciales

La configuración actual tiene credenciales y secreto JWT por defecto en código/properties. Recomendación:

- Usar variables de entorno.
- No versionar secretos reales.
- Definir perfiles `dev`, `test` y `prod`.

### 4. Actualizar OpenAPI

El `openapi.yaml` parece corresponder a una versión anterior. Recomendación:

- Corregir base URL.
- Añadir autenticación Bearer JWT.
- Documentar `/auth`, `/orders`, `/customers/me` e imagen de items.
- Corregir `isVegetarian` por `isNew`.

### 5. Revisar el campo de imagen en Postman

La colección Postman usa `imagen` en algunos cuerpos JSON, pero el DTO espera `image`. Recomendación:

- Cambiar `imagen` por `image` en la colección.
- Confirmar si el valor enviado es Base64 válido.

### 6. Mejorar tests automáticos

Actualmente hay un test básico de contexto y pruebas Postman centradas en `items`. Recomendación:

- Añadir tests unitarios de servicios.
- Añadir tests de integración para auth, customers y orders.
- Añadir tests de autorización por rol.

### 7. Separar entornos de base de datos

Aunque H2 está en dependencias, el contexto apunta siempre a MySQL. Recomendación:

- Crear `application-test.properties` con H2 para tests.
- Dejar MySQL para desarrollo/producción.

### 8. Mejorar borrado y relaciones

El borrado de clientes elimina pedidos mediante `OrderRepository.deleteByCustomerId`. Es correcto para evitar errores de integridad, pero se puede valorar:

- `cascade` y `orphanRemoval` si encaja con el dominio.
- Restricciones más explícitas a nivel de base de datos.

### 9. Calcular el precio total del pedido

Actualmente `totalPrice` llega desde el cliente. Para evitar manipulación, se podría calcular en backend a partir del producto y la cantidad si se añade ese campo en el futuro.

---

## Resumen rápido para arrancar

```bash
# 1. Levantar MySQL
docker compose -f docker-compose.dev.yaml up -d

# 2. Dar permisos si hace falta
chmod +x mvnw

# 3. Arrancar API
./mvnw spring-boot:run

# 4. Login admin
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"natalia@kebapi.com","password":"1111"}'
```

La API estará disponible en:

```text
http://localhost:8080
```