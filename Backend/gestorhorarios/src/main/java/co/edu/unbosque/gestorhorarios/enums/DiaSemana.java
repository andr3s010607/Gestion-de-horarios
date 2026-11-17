package co.edu.unbosque.gestorhorarios.enums;

public enum DiaSemana {
    LUNES(1, "Lunes"),
    MARTES(2, "Martes"),
    MIERCOLES(3, "Miércoles"),
    JUEVES(4, "Jueves"),
    VIERNES(5, "Viernes"),
    SABADO(6, "Sábado");

    private final int numero;
    private final String etiqueta;

    DiaSemana(int numero, String etiqueta) {
        this.numero = numero;
        this.etiqueta = etiqueta;
    }

    public int getNumero() {
        return numero;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static DiaSemana desdeNumero(Integer numero) {
        if (numero == null) {
            return null;
        }
        for (DiaSemana dia : values()) {
            if (dia.numero == numero) {
                return dia;
            }
        }
        throw new IllegalArgumentException("El día debe estar entre 1 y 6");
    }
}
