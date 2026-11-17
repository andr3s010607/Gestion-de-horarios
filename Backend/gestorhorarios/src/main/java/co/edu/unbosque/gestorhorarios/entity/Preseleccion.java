package co.edu.unbosque.gestorhorarios.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Grupo elegido por un estudiante en su preselección confirmada (RF-34). Solo
 * informa la demanda: no descuenta cupo ni es una inscripción. La llave
 * compuesta impide elegir dos veces el mismo grupo y ambas llaves foráneas se
 * borran en cascada (Tabla 33).
 */
@Entity
@Table(name = "preseleccion")
public class Preseleccion {

    @EmbeddedId
    private PreseleccionId id;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    @MapsId("grupoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Grupo grupo;

    public Preseleccion() {
    }

    public Preseleccion(Usuario usuario, Grupo grupo) {
        this.id = new PreseleccionId(usuario.getId(), grupo.getId());
        this.usuario = usuario;
        this.grupo = grupo;
    }

    public PreseleccionId getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Grupo getGrupo() {
        return grupo;
    }
}
