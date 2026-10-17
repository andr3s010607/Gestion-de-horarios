package co.edu.unbosque.gestorhorarios.service;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;
import co.edu.unbosque.gestorhorarios.repository.ParametroSistemaRepository;

@Service
public class ReglaHorarioService {

    public static final String CUPO_MAXIMO_GRUPO = "CUPO_MAXIMO_GRUPO";
    public static final String TOLERANCIA_CUPO_PORCENTAJE = "TOLERANCIA_CUPO_PORCENTAJE";
    public static final String HORA_INICIO_LUNES_VIERNES = "HORA_INICIO_LUNES_VIERNES";
    public static final String HORA_FIN_LUNES_VIERNES = "HORA_FIN_LUNES_VIERNES";
    public static final String HORA_INICIO_SABADO = "HORA_INICIO_SABADO";
    public static final String HORA_FIN_SABADO = "HORA_FIN_SABADO";
    public static final String HORA_INICIO_ALMUERZO = "HORA_INICIO_ALMUERZO";
    public static final String HORA_FIN_ALMUERZO = "HORA_FIN_ALMUERZO";
    public static final String DURACION_SESION_MINUTOS = "DURACION_SESION_MINUTOS";

    private final ParametroSistemaRepository parametroSistemaRepository;

    public ReglaHorarioService(ParametroSistemaRepository parametroSistemaRepository) {
        this.parametroSistemaRepository = parametroSistemaRepository;
    }

    public int cupoMaximoGrupo() { return entero(CUPO_MAXIMO_GRUPO, 40); }
    public BigDecimal toleranciaCupoPorcentaje() { return decimal(TOLERANCIA_CUPO_PORCENTAJE, new BigDecimal("10")); }
    public LocalTime horaInicioSemana() { return hora(HORA_INICIO_LUNES_VIERNES, LocalTime.of(7, 0)); }
    public LocalTime horaFinSemana() { return hora(HORA_FIN_LUNES_VIERNES, LocalTime.of(22, 0)); }
    public LocalTime horaInicioSabado() { return hora(HORA_INICIO_SABADO, LocalTime.of(7, 0)); }
    public LocalTime horaFinSabado() { return hora(HORA_FIN_SABADO, LocalTime.of(13, 0)); }
    public LocalTime horaInicioAlmuerzo() { return hora(HORA_INICIO_ALMUERZO, LocalTime.of(12, 0)); }
    public LocalTime horaFinAlmuerzo() { return hora(HORA_FIN_ALMUERZO, LocalTime.of(13, 0)); }
    public int duracionSesionMinutos() { return entero(DURACION_SESION_MINUTOS, 120); }

    private int entero(String clave, int valorPorDefecto) {
        try { return Integer.parseInt(valor(clave, String.valueOf(valorPorDefecto))); }
        catch (NumberFormatException e) { return valorPorDefecto; }
    }

    private BigDecimal decimal(String clave, BigDecimal valorPorDefecto) {
        try { return new BigDecimal(valor(clave, valorPorDefecto.toPlainString())); }
        catch (NumberFormatException e) { return valorPorDefecto; }
    }

    private LocalTime hora(String clave, LocalTime valorPorDefecto) {
        try { return LocalTime.parse(valor(clave, valorPorDefecto.toString())); }
        catch (Exception e) { return valorPorDefecto; }
    }

    private String valor(String clave, String valorPorDefecto) {
        return parametroSistemaRepository.findByClave(clave).filter(ParametroSistema::isActivo)
                .map(ParametroSistema::getValor).orElse(valorPorDefecto);
    }
}
