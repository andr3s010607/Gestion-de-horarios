package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import co.edu.unbosque.gestorhorarios.enums.TipoDatoParametro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Regla configurable que será utilizada por los servicios de validación y generación. */
@Entity
@Table(name = "parametros_sistema")
public class ParametroSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String clave;

    @Column(nullable = false, length = 120)
    private String valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDatoParametro tipoDato;

    @Column(length = 250)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    public ParametroSistema() {
    }

    public ParametroSistema(String clave, String valor, TipoDatoParametro tipoDato, String descripcion, boolean activo) {
        this.clave = clave;
        this.valor = valor;
        this.tipoDato = tipoDato;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public TipoDatoParametro getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(TipoDatoParametro tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
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
        ParametroSistema other = (ParametroSistema) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(clave, other.clave)
                && Objects.equals(valor, other.valor)
                && Objects.equals(tipoDato, other.tipoDato)
                && Objects.equals(descripcion, other.descripcion)
                && activo == other.activo;
    }

    @Override
    public String toString() {
        return "ParametroSistema [id=" + id + ", clave=" + clave + ", valor=" + valor + ", tipoDato=" + tipoDato + ", descripcion=" + descripcion + ", activo=" + activo + "]";
    }
}
