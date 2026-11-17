package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExportacionService {

    private static final byte[] MARCA_UTF8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String ENCABEZADO = "asignatura,grupo,docente,aula,día,hora de inicio,hora de fin";
    private static final String SALTO_DE_LINEA = "\r\n";
    private static final String INICIOS_DE_FORMULA = "=+-@\t\r";

    public byte[] exportarCsv(List<SesionClaseDTO> sesiones) {
        StringBuilder contenido = new StringBuilder(ENCABEZADO).append(SALTO_DE_LINEA);
        for (SesionClaseDTO sesion : sesiones) {
            if (!sesion.isUbicada()) {
                continue;
            }
            contenido.append(escapar(sesion.getNombreAsignatura())).append(',')
                    .append(sesion.getNumeroGrupo()).append(',')
                    .append(escapar(sesion.getNombreDocente())).append(',')
                    .append(escapar(sesion.getCodigoAula())).append(',')
                    .append(sesion.getDia().getEtiqueta()).append(',')
                    .append(sesion.getHoraInicio()).append(',')
                    .append(sesion.getHoraFin()).append(SALTO_DE_LINEA);
        }
        byte[] texto = contenido.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream archivo = new ByteArrayOutputStream(MARCA_UTF8.length + texto.length);
        archivo.writeBytes(MARCA_UTF8);
        archivo.writeBytes(texto);
        return archivo.toByteArray();
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        String seguro = valor;
        if (!seguro.isEmpty() && INICIOS_DE_FORMULA.indexOf(seguro.charAt(0)) >= 0) {
            // Evita que Excel interprete el texto como una fórmula (inyección CSV).
            seguro = "'" + seguro;
        }
        return '"' + seguro.replace("\"", "\"\"") + '"';
    }
}
