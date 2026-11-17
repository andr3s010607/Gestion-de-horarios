package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.TipoAula;

public class AsignaturaDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer sesionesSemana;
    private TipoAula tipoAulaRequerido;

    public AsignaturaDTO() {
    }

    public AsignaturaDTO(Long id, String codigo, String nombre, Integer sesionesSemana, TipoAula tipoAulaRequerido) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.sesionesSemana = sesionesSemana;
        this.tipoAulaRequerido = tipoAulaRequerido;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getSesionesSemana() {
        return sesionesSemana;
    }

    public void setSesionesSemana(Integer sesionesSemana) {
        this.sesionesSemana = sesionesSemana;
    }

    public TipoAula getTipoAulaRequerido() {
        return tipoAulaRequerido;
    }

    public void setTipoAulaRequerido(TipoAula tipoAulaRequerido) {
        this.tipoAulaRequerido = tipoAulaRequerido;
    }
}
