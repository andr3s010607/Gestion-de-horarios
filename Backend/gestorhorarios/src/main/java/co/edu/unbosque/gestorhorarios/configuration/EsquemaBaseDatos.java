package co.edu.unbosque.gestorhorarios.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
@Order(0)
public class EsquemaBaseDatos implements CommandLineRunner {

    private static final Logger REGISTRO = LoggerFactory.getLogger(EsquemaBaseDatos.class);

    private static final String[][] RESTRICCIONES = {
        {"SESION", "CK_SESION_RANGO", "CHECK (minuto_fin > minuto_inicio)"},
        {"SESION", "CK_SESION_UBICACION", "CHECK ((dia IS NULL AND minuto_inicio IS NULL AND minuto_fin IS NULL)"
                + " OR (dia IS NOT NULL AND minuto_inicio IS NOT NULL AND minuto_fin IS NOT NULL))"},
        {"RESTRICCION_HORARIO", "CK_RESTRICCION_RANGO", "CHECK (minuto_fin > minuto_inicio)"},
        {"HORARIO", "CK_HORARIO_SEMESTRE", "CHECK (REGEXP_LIKE(semestre, '^[0-9][0-9][0-9][0-9]-[12]$'))"},
        {"HORARIO", "CK_HORARIO_PRESELECCION", "CHECK (preseleccion_abierta IN ('S', 'N'))"},
    };

    private static final String[][] INDICES_UNICOS = {
        {"UQ_ASIGNATURA_CODIGO_CI", "asignatura (UPPER(codigo))"},
        {"UQ_AULA_CODIGO_CI", "aula (UPPER(codigo))"},
        {"UQ_USUARIO_NOMBRE_CI", "usuario (UPPER(nombre_usuario))"},
    };

    private static final String[][] DISPARADORES = {
        {"TRG_SESION_DOCENTE", """
            CREATE OR REPLACE TRIGGER trg_sesion_docente
            BEFORE INSERT ON sesion
            FOR EACH ROW
            BEGIN
                SELECT docente_id INTO :NEW.docente_id FROM grupo WHERE id = :NEW.grupo_id;
            END;"""},
        {"TRG_GRUPO_DOCENTE", """
            CREATE OR REPLACE TRIGGER trg_grupo_docente
            AFTER UPDATE OF docente_id ON grupo
            FOR EACH ROW
            BEGIN
                UPDATE sesion SET docente_id = :NEW.docente_id WHERE grupo_id = :NEW.id;
            END;"""},
        {"TRG_SESION_OCUPACION", """
            CREATE OR REPLACE TRIGGER trg_sesion_ocupacion
            AFTER INSERT OR UPDATE ON sesion
            FOR EACH ROW
            BEGIN
                DELETE FROM ocupacion_aula WHERE sesion_id = :NEW.id;
                DELETE FROM ocupacion_docente WHERE sesion_id = :NEW.id;
                IF :NEW.dia IS NOT NULL THEN
                    FOR b IN FLOOR(:NEW.minuto_inicio / 60) .. CEIL(:NEW.minuto_fin / 60) - 1 LOOP
                        IF :NEW.aula_id IS NOT NULL THEN
                            INSERT INTO ocupacion_aula (sesion_id, horario_id, aula_id, dia, bloque)
                            VALUES (:NEW.id, :NEW.horario_id, :NEW.aula_id, :NEW.dia, b);
                        END IF;
                        IF :NEW.docente_id IS NOT NULL THEN
                            INSERT INTO ocupacion_docente (sesion_id, horario_id, docente_id, dia, bloque)
                            VALUES (:NEW.id, :NEW.horario_id, :NEW.docente_id, :NEW.dia, b);
                        END IF;
                    END LOOP;
                END IF;
            END;"""},
    };

    private final JdbcTemplate jdbcTemplate;

    public EsquemaBaseDatos(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        for (String[] restriccion : RESTRICCIONES) {
            if (!existe("SELECT COUNT(*) FROM user_constraints WHERE constraint_name = ?", restriccion[1])) {
                ejecutar("la restricción " + restriccion[1],
                        "ALTER TABLE " + restriccion[0] + " ADD CONSTRAINT " + restriccion[1] + " " + restriccion[2]);
            }
        }
        for (String[] indice : INDICES_UNICOS) {
            if (!existe("SELECT COUNT(*) FROM user_indexes WHERE index_name = ?", indice[0])) {
                ejecutar("el índice " + indice[0], "CREATE UNIQUE INDEX " + indice[0] + " ON " + indice[1]);
            }
        }
        for (String[] disparador : DISPARADORES) {
            if (!existe("SELECT COUNT(*) FROM user_triggers WHERE trigger_name = ? AND status = 'ENABLED'",
                    disparador[0])) {
                ejecutar("el disparador " + disparador[0], disparador[1]);
            }
        }
    }

    private boolean existe(String consulta, String nombre) {
        Integer cantidad = jdbcTemplate.queryForObject(consulta, Integer.class, nombre);
        return cantidad != null && cantidad > 0;
    }

    private void ejecutar(String descripcion, String sentencia) {
        try {
            jdbcTemplate.execute(sentencia);
            REGISTRO.info("Se creó {} en la base de datos", descripcion);
        } catch (DataAccessException excepcion) {
            REGISTRO.warn("No se pudo crear {}: {}", descripcion, excepcion.getMostSpecificCause().getMessage());
        }
    }
}
