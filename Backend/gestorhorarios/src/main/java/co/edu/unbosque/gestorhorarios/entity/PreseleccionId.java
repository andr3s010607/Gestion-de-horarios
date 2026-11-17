package co.edu.unbosque.gestorhorarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PreseleccionId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "grupo_id")
    private Long grupoId;

    public PreseleccionId() {
    }

    public PreseleccionId(Long usuarioId, Long grupoId) {
        this.usuarioId = usuarioId;
        this.grupoId = grupoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, grupoId);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        PreseleccionId otroId = (PreseleccionId) otro;
        return Objects.equals(usuarioId, otroId.usuarioId) && Objects.equals(grupoId, otroId.grupoId);
    }
}
