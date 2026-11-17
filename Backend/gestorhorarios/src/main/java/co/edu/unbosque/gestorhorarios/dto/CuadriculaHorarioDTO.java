package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import java.time.LocalTime;
import java.util.List;

public class CuadriculaHorarioDTO {

    private HorarioDTO horario;
    private List<DiaSemana> dias;
    private List<LocalTime> horasInicioLunesViernes;
    private List<LocalTime> horasInicioSabado;
    private List<SesionClaseDTO> sesionesUbicadas;
    private List<SesionClaseDTO> sesionesSinUbicar;

    public CuadriculaHorarioDTO() {
    }

    public CuadriculaHorarioDTO(HorarioDTO horario, List<DiaSemana> dias, List<LocalTime> horasInicioLunesViernes,
            List<LocalTime> horasInicioSabado, List<SesionClaseDTO> sesionesUbicadas,
            List<SesionClaseDTO> sesionesSinUbicar) {
        this.horario = horario;
        this.dias = dias;
        this.horasInicioLunesViernes = horasInicioLunesViernes;
        this.horasInicioSabado = horasInicioSabado;
        this.sesionesUbicadas = sesionesUbicadas;
        this.sesionesSinUbicar = sesionesSinUbicar;
    }

    public HorarioDTO getHorario() {
        return horario;
    }

    public void setHorario(HorarioDTO horario) {
        this.horario = horario;
    }

    public List<DiaSemana> getDias() {
        return dias;
    }

    public void setDias(List<DiaSemana> dias) {
        this.dias = dias;
    }

    public List<LocalTime> getHorasInicioLunesViernes() {
        return horasInicioLunesViernes;
    }

    public void setHorasInicioLunesViernes(List<LocalTime> horasInicioLunesViernes) {
        this.horasInicioLunesViernes = horasInicioLunesViernes;
    }

    public List<LocalTime> getHorasInicioSabado() {
        return horasInicioSabado;
    }

    public void setHorasInicioSabado(List<LocalTime> horasInicioSabado) {
        this.horasInicioSabado = horasInicioSabado;
    }

    public List<SesionClaseDTO> getSesionesUbicadas() {
        return sesionesUbicadas;
    }

    public void setSesionesUbicadas(List<SesionClaseDTO> sesionesUbicadas) {
        this.sesionesUbicadas = sesionesUbicadas;
    }

    public List<SesionClaseDTO> getSesionesSinUbicar() {
        return sesionesSinUbicar;
    }

    public void setSesionesSinUbicar(List<SesionClaseDTO> sesionesSinUbicar) {
        this.sesionesSinUbicar = sesionesSinUbicar;
    }
}
