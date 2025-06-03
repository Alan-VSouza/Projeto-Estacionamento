package br.ifsp.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoComVagaDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @Tag("UnitTest")
    void deveAceitarValoresValidos() {
        VeiculoComVagaDTO dto = new VeiculoComVagaDTO(
                "ABC1234",
                "carro",
                "civic",
                "preto",
                10
        );
        Set<ConstraintViolation<VeiculoComVagaDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO válido não deve gerar violações");
    }
}

