package co.edu.unbosque.gestorhorarios;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ArquitecturaTest {

    private static final Path FUENTES = Path.of("src", "main", "java", "co", "edu", "unbosque", "gestorhorarios");
    private static final String PAQUETE_BASE = "import co.edu.unbosque.gestorhorarios.";

    private List<Path> archivosDe(String paquete) throws IOException {
        try (Stream<Path> archivos = Files.list(FUENTES.resolve(paquete))) {
            return archivos.filter(archivo -> archivo.toString().endsWith(".java")).toList();
        }
    }

    private boolean importa(Path archivo, String paquete) throws IOException {
        return Files.readAllLines(archivo).stream().anyMatch(linea -> linea.startsWith(PAQUETE_BASE + paquete));
    }

    @Test
    void ningunControladorImportaRepositoriosNiEntidades() throws IOException {
        List<Path> controladores = archivosDe("controller");
        assertFalse(controladores.isEmpty());
        for (Path controlador : controladores) {
            assertFalse(importa(controlador, "repository."), controlador + " importa un repositorio");
            assertFalse(importa(controlador, "entity."), controlador + " importa una entidad");
        }
    }

    @Test
    void ningunServicioNiRepositorioImportaControladores() throws IOException {
        for (String paquete : List.of("service", "repository")) {
            for (Path archivo : archivosDe(paquete)) {
                assertFalse(importa(archivo, "controller."), archivo + " importa un controlador");
            }
        }
    }

    @Test
    void ningunValidadorDependeDeOtroValidador() throws IOException {
        List<Path> validadores = archivosDe("service").stream()
                .filter(archivo -> archivo.getFileName().toString().matches("Validador[A-Z]\\w*\\.java"))
                .toList();
        assertTrue(validadores.size() == 6, "Se esperaban seis validadores y hay " + validadores.size());
        for (Path validador : validadores) {
            String contenido = Files.readString(validador);
            assertTrue(contenido.contains("implements Validador "), validador + " no implementa Validador");
            for (Path otro : validadores) {
                String nombreOtro = otro.getFileName().toString().replace(".java", "");
                if (!otro.equals(validador)) {
                    assertFalse(contenido.contains(nombreOtro), validador + " depende de " + nombreOtro);
                }
            }
        }
    }
}
