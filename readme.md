# PetClinic modernizado

Esta versión conserva el dominio y la lógica de negocio de Spring PetClinic, pero se ejecuta como una aplicación autónoma con:

- Java 17.
- Spring Boot 4.
- Spring MVC.
- Spring Data JPA como implementación de persistencia predeterminada.
- Tomcat embebido.
- Un JAR ejecutable en lugar de un WAR.
- Puerto `8000` por defecto.

Ya no es necesario instalar, configurar ni desplegar la aplicación en Jetty o Tomcat. El punto de entrada anterior, `PetclinicInitializer`, fue sustituido por `PetClinicApplication`.

## Requisitos

- JDK 17 o posterior. Se recomienda comprobarlo con `java -version`.
- No es necesario instalar Maven: el repositorio incluye Maven Wrapper.

## Inicio rápido

Desde el directorio `modernizacion-petclinic`:

```bash
./mvnw clean package
java -jar target/petclinic.jar
```

En Windows:

```bat
mvnw.cmd clean package
java -jar target\petclinic.jar
```

La aplicación queda disponible en [http://localhost:8000](http://localhost:8000). Para detenerla, use `Ctrl+C`.

El JAR es autocontenido: puede copiarse y ejecutarse desde otro directorio siempre que el equipo tenga Java 17 o posterior.

## Ejecución durante el desarrollo

Spring Boot también puede iniciarse directamente con Maven:

```bash
./mvnw spring-boot:run
```

La clase principal que debe seleccionarse al crear una configuración de ejecución en el IDE es:

```text
org.springframework.samples.petclinic.PetClinicApplication
```

No se debe crear una configuración de despliegue WAR ni asociar un servidor Jetty o Tomcat externo.

## Puerto

El puerto predeterminado se define en `src/main/resources/application.properties`:

```properties
server.port=8000
```

Puede sobrescribirse sin modificar el proyecto:

```bash
java -jar target/petclinic.jar --server.port=8080
```

## Persistencia

La configuración predeterminada usa Spring Data JPA y una base H2 en memoria. El esquema y los datos de ejemplo se cargan al iniciar; se pierden al detener la aplicación.

Las implementaciones de repositorio existentes continúan disponibles mediante perfiles de Spring:

```bash
# Spring Data JPA (predeterminado)
java -jar target/petclinic.jar --spring.profiles.active=spring-data-jpa

# JPA tradicional
java -jar target/petclinic.jar --spring.profiles.active=jpa

# Spring JDBC
java -jar target/petclinic.jar --spring.profiles.active=jdbc
```

### MySQL

Prepare la base `petclinic` y ejecute:

```bash
./mvnw clean package -PMySQL
java -jar target/petclinic.jar --spring.profiles.active=spring-data-jpa
```

Los valores predeterminados del perfil Maven son `jdbc:mysql://localhost:3306/petclinic`, usuario `petclinic` y contraseña `petclinic`. Pueden sobrescribirse al compilar con propiedades Maven, por ejemplo `-Djdbc.url=...`.

### PostgreSQL

Prepare la base `petclinic` y ejecute:

```bash
./mvnw clean package -PPostgreSQL
java -jar target/petclinic.jar --spring.profiles.active=spring-data-jpa
```

Los valores predeterminados del perfil Maven son `jdbc:postgresql://localhost:5432/petclinic`, usuario `postgres` y contraseña `petclinic`.

## Pruebas

Para ejecutar todas las pruebas y generar el JAR:

```bash
./mvnw clean verify
```

El artefacto resultante es `target/petclinic.jar`.

## Recursos CSS

El CSS compilado se conserva en `src/main/webapp/resources/css`. Si se modifican los archivos SCSS, puede regenerarse con:

```bash
./mvnw generate-resources -Pcss
```

## Cambio arquitectónico de inicialización

`PetClinicApplication` inicia Spring Boot, carga la configuración XML existente de MVC, servicios y persistencia, y crea Tomcat embebido. Durante el empaquetado, las vistas JSP y los recursos web se incorporan al JAR. Al ejecutar el artefacto, esos recursos se preparan automáticamente en un directorio temporal para que Tomcat pueda compilar las JSP; no se necesita conservar el código fuente junto al JAR.

La configuración se distribuye así:

- `PetClinicApplication.java`: punto de entrada y servidor embebido.
- `application.properties`: puerto y perfil predeterminado.
- `spring/*.xml`: configuración existente de MVC, servicios y repositorios.
- `pom.xml`: dependencias Spring Boot y creación del JAR ejecutable.

## Proyecto original

Este proyecto deriva de [spring-framework-petclinic](https://github.com/spring-petclinic/spring-framework-petclinic) y conserva su licencia Apache 2.0.
