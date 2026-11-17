package co.edu.unbosque.gestorhorarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import co.edu.unbosque.gestorhorarios.enums.TipoAula;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidadoresTest {

    private DatosDePrueba datos;
    private Asignatura calculo;
    private Asignatura fisica;
    private Docente docente;
    private Aula aulaA;
    private Aula aulaB;

    @BeforeEach
    void prepararDatos() {
        datos = new DatosDePrueba();
        calculo = datos.asignatura("CALC", 1, TipoAula.COMUN);
        fisica = datos.asignatura("FIS", 1, TipoAula.COMUN);
        docente = datos.docente("Docente D", calculo, fisica);
        aulaA = datos.aula("A", 50, TipoAula.COMUN);
        aulaB = datos.aula("B", 50, TipoAula.COMUN);
    }

    private List<String> codigos(List<IncidenciaDTO> incidencias) {
        return incidencias.stream().map(IncidenciaDTO::getCodigo).toList();
    }

    @Test
    void rf20DosSesionesSolapadasDelMismoDocenteProducenError() {
        datos.ubicar(datos.grupo(calculo, 1, 30, docente), 0, DiaSemana.LUNES, 7, 9, aulaA);
        datos.ubicar(datos.grupo(fisica, 1, 30, docente), 0, DiaSemana.LUNES, 8, 10, aulaB);
        List<IncidenciaDTO> incidencias = new ValidadorChoques().validar(datos.horario());
        assertEquals(List.of(ValidadorChoques.CHOQUE_DOCENTE), codigos(incidencias));
        assertEquals(SeveridadIncidencia.ERROR, incidencias.get(0).getSeveridad());
    }

    @Test
    void rf20DosSesionesContiguasDelMismoDocenteNoProducenError() {
        datos.ubicar(datos.grupo(calculo, 1, 30, docente), 0, DiaSemana.LUNES, 7, 9, aulaA);
        datos.ubicar(datos.grupo(fisica, 1, 30, docente), 0, DiaSemana.LUNES, 9, 11, aulaB);
        assertTrue(new ValidadorChoques().validar(datos.horario()).isEmpty());
    }

    @Test
    void rf21DosSesionesSolapadasEnLaMismaAulaProducenError() {
        Docente otroDocente = datos.docente("Docente E", fisica);
        datos.ubicar(datos.grupo(calculo, 1, 30, docente), 0, DiaSemana.MARTES, 9, 11, aulaA);
        datos.ubicar(datos.grupo(fisica, 1, 30, otroDocente), 0, DiaSemana.MARTES, 10, 12, aulaA);
        assertEquals(List.of(ValidadorChoques.CHOQUE_AULA), codigos(new ValidadorChoques().validar(datos.horario())));
    }

    @Test
    void rf21DosSesionesContiguasEnLaMismaAulaNoProducenError() {
        Docente otroDocente = datos.docente("Docente E", fisica);
        datos.ubicar(datos.grupo(calculo, 1, 30, docente), 0, DiaSemana.MARTES, 9, 11, aulaA);
        datos.ubicar(datos.grupo(fisica, 1, 30, otroDocente), 0, DiaSemana.MARTES, 11, 13, aulaA);
        assertTrue(new ValidadorChoques().validar(datos.horario()).isEmpty());
    }

    private List<String> codigosDeFranja(DiaSemana dia, int horaInicio, int horaFin) {
        DatosDePrueba datosFranja = new DatosDePrueba();
        Asignatura asignatura = datosFranja.asignatura("CALC", 1, TipoAula.COMUN);
        Grupo grupo = datosFranja.grupo(asignatura, 1, 30, datosFranja.docente("Docente D", asignatura));
        datosFranja.ubicar(grupo, 0, dia, horaInicio, horaFin, datosFranja.aula("A", 50, TipoAula.COMUN));
        return codigos(new ValidadorFranja(datosFranja.reglas()).validar(datosFranja.horario()));
    }

    @Test
    void rf22SesionesFueraDeLaFranjaOSobreLaPausaProducenError() {
        assertEquals(List.of(ValidadorFranja.FUERA_DE_FRANJA), codigosDeFranja(DiaSemana.LUNES, 6, 8));
        assertEquals(List.of(ValidadorFranja.FUERA_DE_FRANJA), codigosDeFranja(DiaSemana.LUNES, 21, 23));
        assertEquals(List.of(ValidadorFranja.CRUCE_CON_PAUSA), codigosDeFranja(DiaSemana.LUNES, 11, 13));
        assertEquals(List.of(ValidadorFranja.FUERA_DE_FRANJA, ValidadorFranja.CRUCE_CON_PAUSA),
                codigosDeFranja(DiaSemana.SABADO, 12, 14));
    }

    @Test
    void rf22SesionesDentroDeLaFranjaNoProducenError() {
        assertTrue(codigosDeFranja(DiaSemana.LUNES, 7, 9).isEmpty());
        assertTrue(codigosDeFranja(DiaSemana.LUNES, 10, 12).isEmpty());
        assertTrue(codigosDeFranja(DiaSemana.LUNES, 13, 15).isEmpty());
        assertTrue(codigosDeFranja(DiaSemana.LUNES, 20, 22).isEmpty());
    }

    @Test
    void rf23SesionSobreUnaRestriccionDelDocenteProduceError() {
        datos.restriccion(docente, DiaSemana.LUNES, 7, 16);
        datos.ubicar(datos.grupo(calculo, 1, 30, docente), 0, DiaSemana.LUNES, 14, 16, aulaA);
        assertEquals(List.of(ValidadorDocente.RESTRICCION_DOCENTE),
                codigos(new ValidadorDocente().validar(datos.horario())));
    }

    @Test
    void rf23SesionFueraDeLaRestriccionNoProduceError() {
        datos.restriccion(docente, DiaSemana.LUNES, 7, 16);
        datos.ubicar(datos.grupo(calculo, 1, 30, docente), 0, DiaSemana.LUNES, 16, 18, aulaA);
        datos.ubicar(datos.grupo(fisica, 1, 30, docente), 0, DiaSemana.MARTES, 14, 16, aulaA);
        assertTrue(new ValidadorDocente().validar(datos.horario()).isEmpty());
    }

    @Test
    void rf24DocenteSinHabilitacionProduceErrorYConHabilitacionNo() {
        Asignatura quimica = datos.asignatura("QUI", 1, TipoAula.COMUN);
        datos.grupo(quimica, 1, 30, docente);
        assertEquals(List.of(ValidadorDocente.DOCENTE_NO_HABILITADO),
                codigos(new ValidadorDocente().validar(datos.horario())));
        DatosDePrueba datosHabilitados = new DatosDePrueba();
        Asignatura asignatura = datosHabilitados.asignatura("QUI", 1, TipoAula.COMUN);
        datosHabilitados.grupo(asignatura, 1, 30, datosHabilitados.docente("Docente D", asignatura));
        assertTrue(new ValidadorDocente().validar(datosHabilitados.horario()).isEmpty());
    }

    @Test
    void rf24GrupoSinDocenteSoloProduceErrorCuandoSuSesionEstaUbicada() {
        Grupo grupo = datos.grupo(calculo, 1, 30, null);
        assertTrue(new ValidadorDocente().validar(datos.horario()).isEmpty());
        datos.ubicar(grupo, 0, DiaSemana.LUNES, 7, 9, aulaA);
        assertEquals(List.of(ValidadorDocente.DOCENTE_NO_HABILITADO),
                codigos(new ValidadorDocente().validar(datos.horario())));
    }

    @Test
    void rf25Cupo44SeAceptaYCupo45ProduceErrorConToleranciaDel10PorCiento() {
        datos.grupo(calculo, 1, 44, docente);
        assertTrue(new ValidadorCupo(datos.reglas()).validar(datos.horario()).isEmpty());
        datos.grupo(calculo, 2, 45, docente);
        assertEquals(List.of(ValidadorCupo.CUPO_EXCEDIDO),
                codigos(new ValidadorCupo(datos.reglas()).validar(datos.horario())));
    }

    @Test
    void rf11ConToleranciaCeroSeAcepta40YSeRechaza41() {
        datos.cambiarParametro(ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE, 0);
        datos.grupo(calculo, 1, 40, docente);
        assertTrue(new ValidadorCupo(datos.reglas()).validar(datos.horario()).isEmpty());
        datos.grupo(calculo, 2, 41, docente);
        assertEquals(List.of(ValidadorCupo.CUPO_EXCEDIDO),
                codigos(new ValidadorCupo(datos.reglas()).validar(datos.horario())));
    }

    @Test
    void rf11AlCambiarElCupoA50SeAceptaUnGrupoDe55() {
        datos.cambiarParametro(ReglaHorarioService.CUPO_MAXIMO, 50);
        datos.grupo(calculo, 1, 55, docente);
        assertTrue(new ValidadorCupo(datos.reglas()).validar(datos.horario()).isEmpty());
    }

    @Test
    void rf26AulaDeOtroTipoProduceError() {
        Asignatura programacion = datos.asignatura("PROG", 1, TipoAula.COMPUTADORES);
        Docente docenteProgramacion = datos.docente("Docente P", programacion);
        datos.ubicar(datos.grupo(programacion, 1, 30, docenteProgramacion), 0, DiaSemana.LUNES, 7, 9, aulaA);
        assertEquals(List.of(ValidadorAula.TIPO_DE_AULA_INCORRECTO),
                codigos(new ValidadorAula().validar(datos.horario())));
    }

    @Test
    void rf26AulaConCapacidadMenorQueElCupoProduceErrorYConCapacidadIgualNo() {
        Asignatura programacion = datos.asignatura("PROG", 2, TipoAula.COMPUTADORES);
        Docente docenteProgramacion = datos.docente("Docente P", programacion);
        Grupo grupo = datos.grupo(programacion, 1, 35, docenteProgramacion);
        datos.ubicar(grupo, 0, DiaSemana.LUNES, 7, 9, datos.aula("SALA-30", 30, TipoAula.COMPUTADORES));
        assertEquals(List.of(ValidadorAula.CAPACIDAD_INSUFICIENTE),
                codigos(new ValidadorAula().validar(datos.horario())));
        grupo.getSesiones().get(0).desubicar();
        datos.ubicar(grupo, 1, DiaSemana.MIERCOLES, 7, 9, datos.aula("SALA-35", 35, TipoAula.COMPUTADORES));
        assertTrue(new ValidadorAula().validar(datos.horario()).isEmpty());
    }

    private List<IncidenciaDTO> consecutividad(int sesionesSemana, DiaSemana... dias) {
        DatosDePrueba datosConsecutividad = new DatosDePrueba();
        Asignatura asignatura = datosConsecutividad.asignatura("ALG", sesionesSemana, TipoAula.COMUN);
        Grupo grupo = datosConsecutividad.grupo(asignatura, 1, 30,
                datosConsecutividad.docente("Docente D", asignatura));
        Aula aula = datosConsecutividad.aula("A", 50, TipoAula.COMUN);
        for (int indice = 0; indice < dias.length; indice++) {
            datosConsecutividad.ubicar(grupo, indice, dias[indice], 7, 9, aula);
        }
        return new ValidadorConsecutividad().validar(datosConsecutividad.horario());
    }

    @Test
    void rf27SesionesEnDiasConsecutivosProducenAdvertenciaYNoError() {
        List<IncidenciaDTO> incidencias = consecutividad(2, DiaSemana.LUNES, DiaSemana.MARTES);
        assertEquals(List.of(ValidadorConsecutividad.DIAS_CONSECUTIVOS), codigos(incidencias));
        assertEquals(SeveridadIncidencia.ADVERTENCIA, incidencias.get(0).getSeveridad());
    }

    @Test
    void rf27SesionesEnDiasNoConsecutivosNoProducenAdvertencia() {
        assertTrue(consecutividad(2, DiaSemana.LUNES, DiaSemana.MIERCOLES).isEmpty());
    }

    @Test
    void rf27AsignaturasDeUnaOCuatroSesionesNoSeEvaluan() {
        assertTrue(consecutividad(1, DiaSemana.LUNES).isEmpty());
        assertTrue(consecutividad(4, DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES, DiaSemana.JUEVES)
                .isEmpty());
    }
}
