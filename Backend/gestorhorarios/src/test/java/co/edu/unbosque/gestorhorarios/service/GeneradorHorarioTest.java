package co.edu.unbosque.gestorhorarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.TipoAula;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GeneradorHorarioTest {

    private static final long LIMITE_SEGUNDOS = 60;

    private ValidacionService validacion(ReglaHorarioService reglas) {
        return new ValidacionService(List.of(new ValidadorChoques(), new ValidadorFranja(reglas),
                new ValidadorDocente(), new ValidadorCupo(reglas), new ValidadorAula(),
                new ValidadorConsecutividad()));
    }

    private List<SesionClase> sesiones(DatosDePrueba datos) {
        return datos.horario().getGrupos().stream().flatMap(grupo -> grupo.getSesiones().stream()).toList();
    }

    @Test
    void rf15ConDatosFactiblesLaPropuestaNoContieneErroresNiAdvertencias() {
        DatosDePrueba datos = new DatosDePrueba();
        ReglaHorarioService reglas = datos.reglas();
        ValidacionService validacion = validacion(reglas);
        Asignatura calculo = datos.asignatura("CALC", 3, TipoAula.COMUN);
        Asignatura programacion = datos.asignatura("PROG", 2, TipoAula.COMPUTADORES);
        Docente docenteCalculo = datos.docente("Docente C", calculo);
        Docente docenteProgramacion = datos.docente("Docente P", programacion);
        datos.restriccion(docenteProgramacion, DiaSemana.LUNES, 7, 16);
        datos.grupo(calculo, 1, 40, docenteCalculo);
        datos.grupo(calculo, 2, 30, docenteCalculo);
        datos.grupo(programacion, 1, 35, docenteProgramacion);
        List<Aula> aulas = List.of(datos.aula("A-101", 44, TipoAula.COMUN),
                datos.aula("LAB-1", 35, TipoAula.COMPUTADORES));
        Map<SesionClase, IncidenciaDTO> sinUbicar = new GeneradorHorario(validacion, reglas, LIMITE_SEGUNDOS)
                .ubicarSesionesPendientes(datos.horario(), aulas);
        assertTrue(sinUbicar.isEmpty());
        assertTrue(sesiones(datos).stream().allMatch(SesionClase::estaUbicada));
        assertTrue(validacion.validar(datos.horario()).isEmpty());
    }

    @Test
    void rf16ConUnSoloBloqueDisponibleUnaSesionQuedaSinUbicarConSuCausa() {
        DatosDePrueba datos = new DatosDePrueba();
        datos.cambiarParametro(ReglaHorarioService.HORA_FIN_LUNES_VIERNES, 540);
        datos.cambiarParametro(ReglaHorarioService.HORA_FIN_SABADO, 540);
        ReglaHorarioService reglas = datos.reglas();
        ValidacionService validacion = validacion(reglas);
        Asignatura calculo = datos.asignatura("CALC", 4, TipoAula.COMUN);
        Asignatura fisica = datos.asignatura("FIS", 4, TipoAula.COMUN);
        Docente docente = datos.docente("Docente D", calculo, fisica);
        datos.grupo(calculo, 1, 30, docente);
        datos.grupo(fisica, 1, 30, docente);
        Map<SesionClase, IncidenciaDTO> sinUbicar = new GeneradorHorario(validacion, reglas, LIMITE_SEGUNDOS)
                .ubicarSesionesPendientes(datos.horario(), List.of(datos.aula("A-101", 44, TipoAula.COMUN)));
        long sinDia = sesiones(datos).stream().filter(sesion -> !sesion.estaUbicada()).count();
        assertEquals(2, sinDia);
        assertEquals(sinDia, sinUbicar.size());
        for (IncidenciaDTO causa : sinUbicar.values()) {
            assertNotNull(causa.getCodigo());
            assertFalse(causa.getMensaje().isBlank());
        }
        assertTrue(validacion.filtrarErrores(validacion.validar(datos.horario())).isEmpty());
    }

    @Test
    void rf16ConMenosAulasDeLasNecesariasInformaLaCausaDeAula() {
        DatosDePrueba datos = new DatosDePrueba();
        ReglaHorarioService reglas = datos.reglas();
        ValidacionService validacion = validacion(reglas);
        Asignatura programacion = datos.asignatura("PROG", 1, TipoAula.COMPUTADORES);
        datos.grupo(programacion, 1, 30, datos.docente("Docente P", programacion));
        Map<SesionClase, IncidenciaDTO> sinUbicar = new GeneradorHorario(validacion, reglas, LIMITE_SEGUNDOS)
                .ubicarSesionesPendientes(datos.horario(), List.of(datos.aula("A-101", 44, TipoAula.COMUN)));
        assertEquals(1, sinUbicar.size());
        assertEquals(ValidadorAula.TIPO_DE_AULA_INCORRECTO, sinUbicar.values().iterator().next().getCodigo());
    }

    @Test
    void generadorPrefiereUbicacionesSinAdvertenciaDeConsecutividad() {
        DatosDePrueba datos = new DatosDePrueba();
        ReglaHorarioService reglas = datos.reglas();
        ValidacionService validacion = validacion(reglas);
        Asignatura algebra = datos.asignatura("ALG", 2, TipoAula.COMUN);
        Grupo grupo = datos.grupo(algebra, 1, 30, datos.docente("Docente A", algebra));
        new GeneradorHorario(validacion, reglas, LIMITE_SEGUNDOS).ubicarSesionesPendientes(datos.horario(),
                List.of(datos.aula("A-101", 44, TipoAula.COMUN)));
        int primerDia = grupo.getSesiones().get(0).getDia();
        int segundoDia = grupo.getSesiones().get(1).getDia();
        assertTrue(Math.abs(primerDia - segundoDia) != 1);
        assertTrue(validacion.validar(datos.horario()).isEmpty());
    }

    @Test
    void rnf05ConCienGruposLaGeneracionTerminaDentroDelLimiteSinErrores() {
        DatosDePrueba datos = new DatosDePrueba();
        ReglaHorarioService reglas = datos.reglas();
        ValidacionService validacion = validacion(reglas);
        List<Aula> aulas = new ArrayList<>();
        for (int indice = 1; indice <= 12; indice++) {
            aulas.add(datos.aula("A-" + (100 + indice), 44, TipoAula.COMUN));
        }
        for (int indice = 1; indice <= 4; indice++) {
            aulas.add(datos.aula("LAB-" + indice, 40, TipoAula.COMPUTADORES));
        }
        for (int indice = 0; indice < 25; indice++) {
            TipoAula tipo = indice % 4 == 0 ? TipoAula.COMPUTADORES : TipoAula.COMUN;
            Asignatura asignatura = datos.asignatura("ASIG-" + indice, 2 + indice % 2, tipo);
            for (int numero = 1; numero <= 4; numero++) {
                Docente docente = datos.docente("Docente " + indice + "-" + numero, asignatura);
                if (numero == 1) {
                    datos.restriccion(docente, DiaSemana.LUNES, 7, 16);
                }
                datos.grupo(asignatura, numero, 25 + numero * 3, docente);
            }
        }
        assertEquals(100, datos.horario().getGrupos().size());
        long inicio = System.nanoTime();
        Map<SesionClase, IncidenciaDTO> sinUbicar = new GeneradorHorario(validacion, reglas, LIMITE_SEGUNDOS)
                .ubicarSesionesPendientes(datos.horario(), aulas);
        long segundos = (System.nanoTime() - inicio) / 1_000_000_000L;
        long sinDia = sesiones(datos).stream().filter(sesion -> !sesion.estaUbicada()).count();
        System.out.println("RNF-05: 100 grupos, " + sesiones(datos).size() + " sesiones, " + sinDia
                + " sin ubicar, " + segundos + " s");
        assertTrue(segundos <= LIMITE_SEGUNDOS);
        assertEquals(sinDia, sinUbicar.size());
        assertTrue(validacion.filtrarErrores(validacion.validar(datos.horario())).isEmpty());
    }
}
