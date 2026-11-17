package co.edu.unbosque.gestorhorarios.exception;

public class DemasiadosIntentosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DemasiadosIntentosException(String mensaje) {
        super(mensaje);
    }
}
