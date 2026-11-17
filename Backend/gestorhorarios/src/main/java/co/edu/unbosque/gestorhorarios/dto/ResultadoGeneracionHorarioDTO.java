package co.edu.unbosque.gestorhorarios.dto;

import java.util.List;

public class ResultadoGeneracionHorarioDTO {

    private List<SesionClaseDTO> sesionesUbicadas;
    private List<SesionSinUbicarDTO> sesionesSinUbicar;
    private List<IncidenciaDTO> advertencias;
    private int totalUbicadas;
    private int totalSinUbicar;

    public ResultadoGeneracionHorarioDTO() {
    }

    public ResultadoGeneracionHorarioDTO(List<SesionClaseDTO> sesionesUbicadas,
            List<SesionSinUbicarDTO> sesionesSinUbicar, List<IncidenciaDTO> advertencias) {
        this.sesionesUbicadas = sesionesUbicadas;
        this.sesionesSinUbicar = sesionesSinUbicar;
        this.advertencias = advertencias;
        this.totalUbicadas = sesionesUbicadas.size();
        this.totalSinUbicar = sesionesSinUbicar.size();
    }

    public List<SesionClaseDTO> getSesionesUbicadas() {
        return sesionesUbicadas;
    }

    public void setSesionesUbicadas(List<SesionClaseDTO> sesionesUbicadas) {
        this.sesionesUbicadas = sesionesUbicadas;
    }

    public List<SesionSinUbicarDTO> getSesionesSinUbicar() {
        return sesionesSinUbicar;
    }

    public void setSesionesSinUbicar(List<SesionSinUbicarDTO> sesionesSinUbicar) {
        this.sesionesSinUbicar = sesionesSinUbicar;
    }

    public List<IncidenciaDTO> getAdvertencias() {
        return advertencias;
    }

    public void setAdvertencias(List<IncidenciaDTO> advertencias) {
        this.advertencias = advertencias;
    }

    public int getTotalUbicadas() {
        return totalUbicadas;
    }

    public void setTotalUbicadas(int totalUbicadas) {
        this.totalUbicadas = totalUbicadas;
    }

    public int getTotalSinUbicar() {
        return totalSinUbicar;
    }

    public void setTotalSinUbicar(int totalSinUbicar) {
        this.totalSinUbicar = totalSinUbicar;
    }
}
