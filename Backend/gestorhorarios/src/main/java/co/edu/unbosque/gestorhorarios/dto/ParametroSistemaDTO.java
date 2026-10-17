package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

import co.edu.unbosque.gestorhorarios.enums.TipoDatoParametro;

public class ParametroSistemaDTO {

    private Long id;
    private String clave;
    private String valor;
    private TipoDatoParametro tipoDato;
    private String descripcion;
    private boolean activo;

    public ParametroSistemaDTO() {
    }

    public ParametroSistemaDTO(Long id, String clave, String valor, TipoDatoParametro tipoDato,
            String descripcion, boolean activo) {
        this.id = id;
        this.clave = clave;
        this.valor = valor;
        this.tipoDato = tipoDato;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public TipoDatoParametro getTipoDato() { return tipoDato; }
    public void setTipoDato(TipoDatoParametro tipoDato) { this.tipoDato = tipoDato; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    @Override
    public int hashCode() {
        return Objects.hash(id, clave, valor, tipoDato, descripcion, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParametroSistemaDTO other = (ParametroSistemaDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(clave, other.clave)
                && Objects.equals(valor, other.valor)
                && Objects.equals(tipoDato, other.tipoDato)
                && Objects.equals(descripcion, other.descripcion)
                && activo == other.activo;
    }

    @Override
    public String toString() {
        return "ParametroSistemaDTO [id=" + id + ", clave=" + clave + ", valor=" + valor + ", tipoDato=" + tipoDato + ", descripcion=" + descripcion + ", activo=" + activo + "]";
    }
}
