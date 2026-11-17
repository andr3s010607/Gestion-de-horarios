package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.TipoAula;

public class AulaDTO {

    private Long id;
    private String codigo;
    private Integer capacidad;
    private TipoAula tipo;

    public AulaDTO() {
    }

    public AulaDTO(Long id, String codigo, Integer capacidad, TipoAula tipo) {
        this.id = id;
        this.codigo = codigo;
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public TipoAula getTipo() {
        return tipo;
    }

    public void setTipo(TipoAula tipo) {
        this.tipo = tipo;
    }
}
