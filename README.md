# CineVerse

CineVerse es una aplicación web desarrollada con **Java y Spring Boot** en el backend y **HTML, CSS y JavaScript** en el frontend.  
Permite explorar series, ver lanzamientos recientes, contenidos populares y filtrar por categorías, consumiendo información desde una **API externa (OMDb)** y almacenando datos en **PostgreSQL**.

El proyecto sigue una arquitectura cliente-servidor con una clara separación de responsabilidades.

---

##  Tecnologías utilizadas

###  Backend
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Maven
- PostgreSQL
- API externa OMDb
- Arquitectura REST
- DTOs
- Configuración CORS

###  Frontend
- HTML5
- CSS
- JavaScript 
- Fetch API
- LocalStorage 

---

##  Estructura del proyecto

### Backend
```
src/main/java/com/platform/cineverse
├── config
│   └── CorsConfiguration
├── controller
│   └── SerieController
├── dto
│   ├── EpisodioDTO
│   └── SerieDTO
├── model
│   ├── Category
│   ├── Episode
│   ├── EpisodeData
│   ├── SeasonData
│   ├── Serie
│   └── TvShowData
├── repository
│   └── TvShowRepository
├── service
│   ├── APIClient
│   ├── DataMapper
│   ├── IDataMapper
│   ├── SerieService
│   ├── CineVerse
│   └── CineVerseConsole
└── main
    └── Main
```

### Frontend
```
frontend
├── index.html
├── login.html
├── styles.css
├── css
│   ├── home.css
│   └── login.css
└── scripts
    ├── index.js
    ├── getDatos.js
    └── auth.js
```

---

##  Configuración del backend

1. Clonar el repositorio:
```
git clone https://github.com/tu-usuario/cineverse.git
```

2. Configurar la base de datos en `application.properties`:
```
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> Las credenciales se manejan mediante **variables de entorno** para mayor seguridad.

3. Ejecutar el proyecto:
```
mvn spring-boot:run 
```

El backend se ejecuta por defecto en:
```
http://localhost:8080
```

---

##  Obtención de la API OMDb

Este proyecto utiliza la **API OMDb** para obtener información sobre series y episodios.

### Pasos para obtener la API Key:

1. Accede al sitio oficial de OMDb:  
   https://www.omdbapi.com/

2. Da clic en **API Key** y regístrate con tu correo electrónico.

3. Recibirás una **API Key gratuita** en tu correo.

4. Configura la API Key en el backend, por ejemplo como variable de entorno:
```
OMDB_API_KEY=tu_api_key_aqui
```

5. La aplicación utiliza esta clave para realizar las peticiones a la API externa.

> La API gratuita tiene límites de uso diarios.

---

##  Funcionamiento del frontend

- El frontend es una aplicación web estática.
- Consume los servicios REST del backend usando **fetch API**.
- La autenticación se maneja con **LocalStorage**.
- Si no existe una sesión activa, el usuario es redirigido al login.

---

##  Comunicación Frontend ↔ Backend

### Endpoints principales
```
GET /series
GET /series/top5
GET /series/lanzamientos
GET /series/categoria/{categoria}
```

Los datos se intercambian en formato **JSON** y se renderizan dinámicamente en la interfaz.

---

##  Autenticación (Frontend)

- Registro e inicio de sesión implementados en JavaScript.
- Usuarios almacenados en LocalStorage.
- Sesión persistente mientras exista el usuario activo.

> Sistema con fines educativos.

---

##  Funcionalidades principales

- Visualización de series
- Series más populares (Top 5)
- Últimos lanzamientos
- Filtro por categorías
- Búsqueda dinámica
- Autenticación de usuarios
- Consumo de API externa (OMDb)

---

##  Herramientas utilizadas

- IntelliJ IDEA
- Visual Studio Code
- PostgreSQL
- Git & GitHub
- Maven

---

## 👨‍💻 Autor

**Mario Alexis Juarez Anguiano**  
IPN - Unidad Profesional Interdisciplinaria en Ingeniería y Tecnologías Avanzadas (UPIITA) - Ingeniería en Telemática  

---

##  Nota

Proyecto de uso académico y educativo.
