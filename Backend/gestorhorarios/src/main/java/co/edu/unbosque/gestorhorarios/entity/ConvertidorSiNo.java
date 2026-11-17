package co.edu.unbosque.gestorhorarios.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Guarda un valor lógico como 'S' o 'N', como define la Tabla 33 del informe. */
@Converter
public class ConvertidorSiNo implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "S" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String valor) {
        return "S".equals(valor);
    }
}
