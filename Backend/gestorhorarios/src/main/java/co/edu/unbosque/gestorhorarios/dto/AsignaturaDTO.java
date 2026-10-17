package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class AsignaturaDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer sesionesSemanales;
    private boolean activa;

    public AsignaturaDTO() {
    }

    public AsignaturaDTO(Long id, String codigo, String nombre, Integer sesionesSemanales, boolean activa) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.sesionesSemanales = sesionesSemanales;
        this.activa = activa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getSesionesSemanales() { return sesionesSemanales; }
    public void setSesionesSemanales(Integer sesionesSemanales) { this.sesionesSemanales = sesionesSemanales; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, nombre, sesionesSemanales, activa);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AsignaturaDTO other = (AsignaturaDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(codigo, other.codigo)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(sesionesSemanales, other.sesionesSemanales)
                && activa == other.activa;
    }

    @Override
    public String toString() {
        return "AsignaturaDTO [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", sesionesSemanales=" + sesionesSemanales + ", activa=" + activa + "]";
    }
}
