package org.springframework.samples.petclinic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

/**
 * Spring Boot entry point for the standalone PetClinic application.
 */
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
        WebMvcAutoConfiguration.class })
@ImportResource({ "classpath:spring/business-config.xml", "classpath:spring/tools-config.xml",
        "classpath:spring/mvc-core-config.xml" })
public class PetClinicApplication {

    private static final String WEB_RESOURCES = "META-INF/resources/";

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }

    /**
     * JSP compilation requires a real document root. During development the normal
     * webapp directory is used; an executable JAR extracts its bundled web resources
     * to a temporary directory before embedded Tomcat starts.
     */
    @Bean
    TomcatServletWebServerFactory servletWebServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setDocumentRoot(resolveDocumentRoot().toFile());
        return factory;
    }

    private Path resolveDocumentRoot() {
        Path sourceWebapp = Path.of("src", "main", "webapp").toAbsolutePath();
        if (Files.isDirectory(sourceWebapp)) {
            return sourceWebapp;
        }

        Path archive = Path.of(System.getProperty("java.class.path")).toAbsolutePath();
        if (!Files.isRegularFile(archive)) {
            throw new IllegalStateException("No se pudo localizar el JAR ejecutable para cargar los recursos web");
        }

        try {
            Path documentRoot = Files.createTempDirectory("petclinic-webapp-");
            documentRoot.toFile().deleteOnExit();
            extractWebResources(archive, documentRoot);
            return documentRoot;
        }
        catch (IOException ex) {
            throw new IllegalStateException("No se pudieron preparar los recursos web incluidos en el JAR", ex);
        }
    }

    private void extractWebResources(Path archive, Path documentRoot) throws IOException {
        try (ZipFile zipFile = new ZipFile(archive.toFile())) {
            var entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().startsWith(WEB_RESOURCES)) {
                    continue;
                }
                Path destination = documentRoot.resolve(entry.getName().substring(WEB_RESOURCES.length())).normalize();
                if (!destination.startsWith(documentRoot)) {
                    throw new IOException("Ruta de recurso web no válida: " + entry.getName());
                }
                Files.createDirectories(destination.getParent());
                try (InputStream input = zipFile.getInputStream(entry)) {
                    Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
                }
                destination.toFile().deleteOnExit();
            }
        }
    }

}
