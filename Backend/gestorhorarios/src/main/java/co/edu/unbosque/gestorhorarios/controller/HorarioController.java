package co.edu.unbosque.gestorhorarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.gestorhorarios.dto.HorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.ResultadoGeneracionHorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.service.HorarioService;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody HorarioDTO dto) {
        int status = horarioService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("Horario creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear Horario", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<HorarioDTO>> obtenerTodos() {
        return new ResponseEntity<>(horarioService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioDTO> obtenerPorId(@PathVariable Long id) {
        HorarioDTO dto = horarioService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody HorarioDTO dto) {
        int status = horarioService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("Horario actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar Horario", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = horarioService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("Horario eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Horario no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(horarioService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(horarioService.exist(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/generar")
    public ResponseEntity<ResultadoGeneracionHorarioDTO> generarPropuesta(@PathVariable Long id) {
        ResultadoGeneracionHorarioDTO resultado = horarioService.generarPropuesta(id);
        if (!horarioService.exist(id)) {
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @GetMapping("/{id}/validar")
    public ResponseEntity<List<String>> validar(@PathVariable Long id) {
        if (!horarioService.exist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(horarioService.validarHorario(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/sesiones")
    public ResponseEntity<List<SesionClaseDTO>> sesiones(@PathVariable Long id) {
        if (!horarioService.exist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(horarioService.getSesiones(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/docente/{docenteId}")
    public ResponseEntity<List<SesionClaseDTO>> consultarPorDocente(@PathVariable Long id, @PathVariable Long docenteId) {
        if (!horarioService.exist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(horarioService.getSesionesPorDocente(id, docenteId), HttpStatus.OK);
    }

    @GetMapping("/{id}/aula/{aulaId}")
    public ResponseEntity<List<SesionClaseDTO>> consultarPorAula(@PathVariable Long id, @PathVariable Long aulaId) {
        if (!horarioService.exist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(horarioService.getSesionesPorAula(id, aulaId), HttpStatus.OK);
    }

    @GetMapping("/{id}/asignatura/{asignaturaId}")
    public ResponseEntity<List<SesionClaseDTO>> consultarPorAsignatura(@PathVariable Long id,
            @PathVariable Long asignaturaId) {
        if (!horarioService.exist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(horarioService.getSesionesPorAsignatura(id, asignaturaId), HttpStatus.OK);
    }

    @GetMapping("/{id}/exportar")
    public ResponseEntity<byte[]> exportar(@PathVariable Long id) {
        byte[] archivo = horarioService.exportarCsv(id);
        if (archivo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=horario-" + id + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(archivo);
    }
}

