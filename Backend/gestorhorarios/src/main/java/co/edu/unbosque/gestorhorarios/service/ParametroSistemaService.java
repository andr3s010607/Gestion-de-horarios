package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.ParametroSistemaDTO;
import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.repository.ParametroSistemaRepository;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParametroSistemaService {

    private final ParametroSistemaRepository parametroSistemaRepository;
    private final ReglaHorarioService reglaHorarioService;
    private final ProteccionHorarioAprobadoService proteccionHorarioAprobado;

    public ParametroSistemaService(ParametroSistemaRepository parametroSistemaRepository,
            ReglaHorarioService reglaHorarioService, ProteccionHorarioAprobadoService proteccionHorarioAprobado) {
        this.parametroSistemaRepository = parametroSistemaRepository;
        this.reglaHorarioService = reglaHorarioService;
        this.proteccionHorarioAprobado = proteccionHorarioAprobado;
    }

    @Transactional(readOnly = true)
    public ParametroSistemaDTO getParametros() {
        ParametroSistemaDTO dto = new ParametroSistemaDTO();
        dto.setHoraInicioLunesViernes(ReglaHorarioService.convertirAHora(reglaHorarioService.minutoInicioFranja()));
        dto.setHoraFinLunesViernes(ReglaHorarioService
                .convertirAHora(reglaHorarioService.minutoFinFranja(DiaSemana.LUNES.getNumero())));
        dto.setHoraFinSabado(ReglaHorarioService
                .convertirAHora(reglaHorarioService.minutoFinFranja(DiaSemana.SABADO.getNumero())));
        dto.setHoraInicioPausa(ReglaHorarioService.convertirAHora(reglaHorarioService.minutoInicioPausa()));
        dto.setHoraFinPausa(ReglaHorarioService.convertirAHora(reglaHorarioService.minutoFinPausa()));
        dto.setDuracionSesionHoras(reglaHorarioService.duracionSesionHoras());
        dto.setCupoMaximo(reglaHorarioService.cupoMaximo());
        dto.setToleranciaCupoPorcentaje(reglaHorarioService.toleranciaCupoPorcentaje());
        return dto;
    }

    public ParametroSistemaDTO actualizar(ParametroSistemaDTO dto) {
        validar(dto);
        proteccionHorarioAprobado.aplicarSinDanarHorariosAprobados(() -> {
            guardarHora(ReglaHorarioService.HORA_INICIO_LUNES_VIERNES, dto.getHoraInicioLunesViernes());
            guardarHora(ReglaHorarioService.HORA_FIN_LUNES_VIERNES, dto.getHoraFinLunesViernes());
            guardarHora(ReglaHorarioService.HORA_FIN_SABADO, dto.getHoraFinSabado());
            guardarHora(ReglaHorarioService.HORA_INICIO_PAUSA, dto.getHoraInicioPausa());
            guardarHora(ReglaHorarioService.HORA_FIN_PAUSA, dto.getHoraFinPausa());
            guardarEntero(ReglaHorarioService.DURACION_SESION_HORAS, dto.getDuracionSesionHoras());
            guardarEntero(ReglaHorarioService.CUPO_MAXIMO, dto.getCupoMaximo());
            guardarEntero(ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE, dto.getToleranciaCupoPorcentaje());
        });
        return getParametros();
    }

    private void validar(ParametroSistemaDTO dto) {
        if (dto == null || dto.getHoraInicioLunesViernes() == null || dto.getHoraFinLunesViernes() == null
                || dto.getHoraFinSabado() == null || dto.getHoraInicioPausa() == null
                || dto.getHoraFinPausa() == null || dto.getDuracionSesionHoras() == null
                || dto.getCupoMaximo() == null || dto.getToleranciaCupoPorcentaje() == null) {
            throw new DatoInvalidoException("Todos los parámetros son obligatorios");
        }
        if (!dto.getHoraFinLunesViernes().isAfter(dto.getHoraInicioLunesViernes())) {
            throw new DatoInvalidoException(
                    "La hora de fin de la franja de lunes a viernes debe ser posterior a la de inicio");
        }
        if (!dto.getHoraFinSabado().isAfter(dto.getHoraInicioLunesViernes())) {
            throw new DatoInvalidoException("La hora de fin del sábado debe ser posterior a la de inicio de la franja");
        }
        if (!dto.getHoraFinPausa().isAfter(dto.getHoraInicioPausa())) {
            throw new DatoInvalidoException("La hora de fin de la pausa del mediodía debe ser posterior a la de inicio");
        }
        if (dto.getDuracionSesionHoras() <= 0) {
            throw new DatoInvalidoException("La duración de la sesión debe ser un valor positivo");
        }
        if (dto.getCupoMaximo() <= 0) {
            throw new DatoInvalidoException("El cupo máximo debe ser un valor positivo");
        }
        if (dto.getToleranciaCupoPorcentaje() < 0) {
            throw new DatoInvalidoException("El porcentaje de tolerancia no puede ser negativo");
        }
        if (dto.getHoraInicioPausa().isBefore(dto.getHoraInicioLunesViernes())
                || dto.getHoraFinPausa().isAfter(dto.getHoraFinLunesViernes())) {
            throw new DatoInvalidoException("La pausa del mediodía debe quedar dentro de la franja de lunes a viernes");
        }
        int duracionMinutos = dto.getDuracionSesionHoras() * ReglaHorarioService.MINUTOS_POR_HORA;
        if (ReglaHorarioService.calcularIniciosValidos(
                ReglaHorarioService.convertirAMinutos(dto.getHoraInicioLunesViernes()),
                ReglaHorarioService.convertirAMinutos(dto.getHoraFinLunesViernes()),
                ReglaHorarioService.convertirAMinutos(dto.getHoraInicioPausa()),
                ReglaHorarioService.convertirAMinutos(dto.getHoraFinPausa()), duracionMinutos).isEmpty()) {
            throw new DatoInvalidoException("Con esa duración de sesión no cabe ninguna sesión en la franja de lunes "
                    + "a viernes sin cruzar la pausa");
        }
    }

    private void guardarHora(String clave, LocalTime hora) {
        guardarEntero(clave, ReglaHorarioService.convertirAMinutos(hora));
    }

    private void guardarEntero(String clave, Integer valor) {
        ParametroSistema parametro = parametroSistemaRepository.findById(clave)
                .orElseGet(() -> new ParametroSistema(clave, null));
        parametro.setValor(String.valueOf(valor));
        parametroSistemaRepository.save(parametro);
    }
}
