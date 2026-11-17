package co.edu.unbosque.gestorhorarios.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger REGISTRO = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException excepcion) {
        return responder(HttpStatus.NOT_FOUND, excepcion.getMessage());
    }

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarDatoInvalido(DatoInvalidoException excepcion) {
        return responder(HttpStatus.BAD_REQUEST, excepcion.getMessage());
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> manejarDatoDuplicado(DatoDuplicadoException excepcion) {
        return responder(HttpStatus.CONFLICT, excepcion.getMessage());
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<Map<String, Object>> manejarOperacionNoPermitida(OperacionNoPermitidaException excepcion) {
        return responder(HttpStatus.CONFLICT, excepcion.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(CredencialesInvalidasException excepcion) {
        return responder(HttpStatus.UNAUTHORIZED, excepcion.getMessage());
    }

    @ExceptionHandler(IncidenciasHorarioException.class)
    public ResponseEntity<Map<String, Object>> manejarIncidenciasHorario(IncidenciasHorarioException excepcion) {
        Map<String, Object> cuerpo = construirCuerpo(HttpStatus.CONFLICT, excepcion.getMessage());
        cuerpo.put("incidencias", excepcion.getIncidencias());
        cuerpo.put("sesionesSinUbicar", excepcion.getSesionesSinUbicar());
        return new ResponseEntity<>(cuerpo, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarCuerpoIlegible(HttpMessageNotReadableException excepcion) {
        return responder(HttpStatus.BAD_REQUEST,
                "La solicitud contiene un valor con formato inválido o fuera de los valores permitidos");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(MethodArgumentTypeMismatchException excepcion) {
        return responder(HttpStatus.BAD_REQUEST, "El parámetro " + excepcion.getName() + " tiene un formato inválido");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarRestriccionDeDominio(ConstraintViolationException excepcion) {
        return responder(HttpStatus.BAD_REQUEST, "Los datos enviados no cumplen las reglas de integridad");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridadDeDatos(DataIntegrityViolationException excepcion) {
        return responder(HttpStatus.CONFLICT,
                "La operación viola una restricción de integridad de la base de datos");
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, Object>> manejarAccesoDenegado(AccesoDenegadoException excepcion) {
        return responder(HttpStatus.FORBIDDEN, excepcion.getMessage());
    }

    @ExceptionHandler(DemasiadosIntentosException.class)
    public ResponseEntity<Map<String, Object>> manejarDemasiadosIntentos(DemasiadosIntentosException excepcion) {
        return responder(HttpStatus.TOO_MANY_REQUESTS, excepcion.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorInesperado(Exception excepcion) throws Exception {
        if (excepcion instanceof ErrorResponse respuestaDeSpring) {
            HttpStatus estado = HttpStatus.resolve(respuestaDeSpring.getStatusCode().value());
            if (estado != null && estado.is4xxClientError()) {
                return responder(estado, estado == HttpStatus.NOT_FOUND ? "El recurso solicitado no existe"
                        : "La solicitud no es válida para este recurso");
            }
        }
        if (excepcion instanceof AccessDeniedException || excepcion instanceof AuthenticationException) {
            throw excepcion;
        }
        REGISTRO.error("Error inesperado al atender la solicitud", excepcion);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor");
    }

    private ResponseEntity<Map<String, Object>> responder(HttpStatus estado, String mensaje) {
        return new ResponseEntity<>(construirCuerpo(estado, mensaje), estado);
    }

    private Map<String, Object> construirCuerpo(HttpStatus estado, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("estado", estado.value());
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}
