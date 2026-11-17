package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.repository.ParametroSistemaRepository;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReglaHorarioService {

    public static final String HORA_INICIO_LUNES_VIERNES = "HORA_INICIO_LUNES_VIERNES";
    public static final String HORA_FIN_LUNES_VIERNES = "HORA_FIN_LUNES_VIERNES";
    public static final String HORA_FIN_SABADO = "HORA_FIN_SABADO";
    public static final String HORA_INICIO_PAUSA = "HORA_INICIO_PAUSA";
    public static final String HORA_FIN_PAUSA = "HORA_FIN_PAUSA";
    public static final String DURACION_SESION_HORAS = "DURACION_SESION_HORAS";
    public static final String CUPO_MAXIMO = "CUPO_MAXIMO";
    public static final String TOLERANCIA_CUPO_PORCENTAJE = "TOLERANCIA_CUPO_PORCENTAJE";

    public static final int MINUTOS_POR_HORA = 60;
    private static final int BASE_PORCENTAJE = 100;

    private final ParametroSistemaRepository parametroSistemaRepository;

    public ReglaHorarioService(ParametroSistemaRepository parametroSistemaRepository) {
        this.parametroSistemaRepository = parametroSistemaRepository;
    }

    public static Integer convertirAMinutos(LocalTime hora) {
        return hora == null ? null : hora.getHour() * MINUTOS_POR_HORA + hora.getMinute();
    }

    public static LocalTime convertirAHora(Integer minutos) {
        return minutos == null ? null : LocalTime.of(minutos / MINUTOS_POR_HORA, minutos % MINUTOS_POR_HORA);
    }

    public static boolean seSolapan(int inicioUno, int finUno, int inicioDos, int finDos) {
        return inicioUno < finDos && inicioDos < finUno;
    }

    public int minutoInicioFranja() {
        return leerEntero(HORA_INICIO_LUNES_VIERNES);
    }

    public int minutoFinFranja(int dia) {
        return dia == DiaSemana.SABADO.getNumero() ? leerEntero(HORA_FIN_SABADO) : leerEntero(HORA_FIN_LUNES_VIERNES);
    }

    public int minutoInicioPausa() {
        return leerEntero(HORA_INICIO_PAUSA);
    }

    public int minutoFinPausa() {
        return leerEntero(HORA_FIN_PAUSA);
    }

    public int duracionSesionHoras() {
        return leerEntero(DURACION_SESION_HORAS);
    }

    public int duracionSesionMinutos() {
        return duracionSesionHoras() * MINUTOS_POR_HORA;
    }

    public int cupoMaximo() {
        return leerEntero(CUPO_MAXIMO);
    }

    public int toleranciaCupoPorcentaje() {
        return leerEntero(TOLERANCIA_CUPO_PORCENTAJE);
    }

    public int cupoMaximoConTolerancia() {
        return cupoMaximo() * (BASE_PORCENTAJE + toleranciaCupoPorcentaje()) / BASE_PORCENTAJE;
    }

    public List<Integer> minutosInicioValidos(int dia) {
        return calcularIniciosValidos(minutoInicioFranja(), minutoFinFranja(dia), minutoInicioPausa(),
                minutoFinPausa(), duracionSesionMinutos());
    }

    public static List<Integer> calcularIniciosValidos(int inicioFranja, int finFranja, int inicioPausa,
            int finPausa, int duracion) {
        List<Integer> inicios = new ArrayList<>();
        for (int inicio = inicioFranja; inicio + duracion <= finFranja; inicio += MINUTOS_POR_HORA) {
            if (!seSolapan(inicio, inicio + duracion, inicioPausa, finPausa)) {
                inicios.add(inicio);
            }
        }
        return inicios;
    }

    private int leerEntero(String clave) {
        return parametroSistemaRepository.findById(clave)
                .map(ParametroSistema::getValor)
                .map(Integer::parseInt)
                .orElseThrow(() -> new IllegalStateException("No existe el parámetro " + clave));
    }
}
