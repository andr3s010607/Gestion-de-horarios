package co.edu.unbosque.gestorhorarios.exception;

public class DatoDuplicadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DatoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
