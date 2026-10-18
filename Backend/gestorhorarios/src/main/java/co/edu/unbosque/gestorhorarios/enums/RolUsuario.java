package co.edu.unbosque.gestorhorarios.enums;

/** Roles iniciales del sistema de horarios. */
public enum RolUsuario {
    ADMINISTRADOR,
    PLANIFICADOR,
    ESTUDIANTE,
    /**
     * Rol heredado. Los nuevos usuarios de consulta deben registrarse como
     * ESTUDIANTE para conservar el lenguaje del producto.
     */
    CONSULTA
}
