package co.edu.unbosque.gestorhorarios.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.DocenteAsignatura;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;
import co.edu.unbosque.gestorhorarios.entity.RestriccionHorariaDocente;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.TipoAula;
import co.edu.unbosque.gestorhorarios.enums.TipoVinculacion;
import co.edu.unbosque.gestorhorarios.repository.ParametroSistemaRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class DatosDePrueba {

    private long siguienteId = 1;
    private final Map<String, String> parametros = new HashMap<>();
    private final Horario horario = new Horario();

    DatosDePrueba() {
        horario.setId(siguienteId++);
        horario.setSemestre("2026-2");
        parametros.put(ReglaHorarioService.HORA_INICIO_LUNES_VIERNES, "420");
        parametros.put(ReglaHorarioService.HORA_FIN_LUNES_VIERNES, "1320");
        parametros.put(ReglaHorarioService.HORA_FIN_SABADO, "780");
        parametros.put(ReglaHorarioService.HORA_INICIO_PAUSA, "720");
        parametros.put(ReglaHorarioService.HORA_FIN_PAUSA, "780");
        parametros.put(ReglaHorarioService.DURACION_SESION_HORAS, "2");
        parametros.put(ReglaHorarioService.CUPO_MAXIMO, "40");
        parametros.put(ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE, "10");
    }

    Horario horario() {
        return horario;
    }

    void cambiarParametro(String clave, int valor) {
        parametros.put(clave, String.valueOf(valor));
    }

    ReglaHorarioService reglas() {
        ParametroSistemaRepository repositorio = mock(ParametroSistemaRepository.class);
        when(repositorio.findById(anyString())).thenAnswer(invocacion -> {
            String clave = invocacion.getArgument(0);
            return Optional.ofNullable(parametros.get(clave)).map(valor -> new ParametroSistema(clave, valor));
        });
        return new ReglaHorarioService(repositorio);
    }

    Asignatura asignatura(String codigo, int sesionesSemana, TipoAula tipoAula) {
        Asignatura asignatura = new Asignatura();
        asignatura.setId(siguienteId++);
        asignatura.setCodigo(codigo);
        asignatura.setNombre("Asignatura " + codigo);
        asignatura.setSesionesSemana(sesionesSemana);
        asignatura.setTipoAulaRequerido(tipoAula);
        return asignatura;
    }

    Docente docente(String nombre, Asignatura... habilitadas) {
        Docente docente = new Docente();
        docente.setId(siguienteId++);
        docente.setNombre(nombre);
        docente.setVinculacion(TipoVinculacion.TIEMPO_COMPLETO);
        for (Asignatura asignatura : habilitadas) {
            docente.getHabilitaciones().add(new DocenteAsignatura(docente, asignatura));
        }
        return docente;
    }

    void restriccion(Docente docente, DiaSemana dia, int horaInicio, int horaFin) {
        RestriccionHorariaDocente restriccion = new RestriccionHorariaDocente();
        restriccion.setId(siguienteId++);
        restriccion.setDocente(docente);
        restriccion.setDia(dia.getNumero());
        restriccion.setMinutoInicio(horaInicio * ReglaHorarioService.MINUTOS_POR_HORA);
        restriccion.setMinutoFin(horaFin * ReglaHorarioService.MINUTOS_POR_HORA);
        docente.getRestricciones().add(restriccion);
    }

    Aula aula(String codigo, int capacidad, TipoAula tipo) {
        Aula aula = new Aula();
        aula.setId(siguienteId++);
        aula.setCodigo(codigo);
        aula.setCapacidad(capacidad);
        aula.setTipo(tipo);
        return aula;
    }

    Grupo grupo(Asignatura asignatura, int numero, int cupo, Docente docente) {
        Grupo grupo = new Grupo();
        grupo.setId(siguienteId++);
        grupo.setNumero(numero);
        grupo.setCupo(cupo);
        grupo.setAsignatura(asignatura);
        grupo.setHorario(horario);
        grupo.setDocente(docente);
        for (int indice = 0; indice < asignatura.getSesionesSemana(); indice++) {
            SesionClase sesion = new SesionClase(grupo);
            sesion.setId(siguienteId++);
            grupo.getSesiones().add(sesion);
        }
        horario.getGrupos().add(grupo);
        return grupo;
    }

    SesionClase ubicar(Grupo grupo, int indiceSesion, DiaSemana dia, int horaInicio, int horaFin, Aula aula) {
        SesionClase sesion = grupo.getSesiones().get(indiceSesion);
        sesion.ubicar(dia.getNumero(), horaInicio * ReglaHorarioService.MINUTOS_POR_HORA,
                horaFin * ReglaHorarioService.MINUTOS_POR_HORA, aula);
        return sesion;
    }
}
