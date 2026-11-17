package co.edu.unbosque.gestorhorarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReglaHorarioServiceTest {

    @Test
    void cupoMaximoConToleranciaSeCalculaConAritmeticaEntera() {
        DatosDePrueba datos = new DatosDePrueba();
        assertEquals(44, datos.reglas().cupoMaximoConTolerancia());
        datos.cambiarParametro(ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE, 0);
        assertEquals(40, datos.reglas().cupoMaximoConTolerancia());
        datos.cambiarParametro(ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE, 20);
        assertEquals(48, datos.reglas().cupoMaximoConTolerancia());
        datos.cambiarParametro(ReglaHorarioService.CUPO_MAXIMO, 33);
        datos.cambiarParametro(ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE, 10);
        assertEquals(36, datos.reglas().cupoMaximoConTolerancia());
    }

    @Test
    void horasDeInicioValidasSonDoceEntreSemanaYCuatroElSabado() {
        ReglaHorarioService reglas = new DatosDePrueba().reglas();
        assertEquals(List.of(420, 480, 540, 600, 780, 840, 900, 960, 1020, 1080, 1140, 1200),
                reglas.minutosInicioValidos(DiaSemana.LUNES.getNumero()));
        assertEquals(List.of(420, 480, 540, 600), reglas.minutosInicioValidos(DiaSemana.SABADO.getNumero()));
    }
}
