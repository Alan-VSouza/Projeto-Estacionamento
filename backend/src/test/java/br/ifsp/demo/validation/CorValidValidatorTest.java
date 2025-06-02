package br.ifsp.demo.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CorValidValidator - Testes de Validação de Cores")
class CorValidValidatorTest {

    private CorValidValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new CorValidValidator();
        lenient().when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);
        lenient().when(violationBuilder.addConstraintViolation())
                .thenReturn(context);
    }

    @Test
    @DisplayName("Deve aceitar cores válidas em minúsculas")
    void deveAceitarCoresValidasMinusculas() {
        assertTrue(validator.isValid("branco", context));
        assertTrue(validator.isValid("preto", context));
        assertTrue(validator.isValid("azul", context));
        assertTrue(validator.isValid("vermelho", context));

        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Deve aceitar cores válidas em maiúsculas")
    void deveAceitarCoresValidasMaiusculas() {
        assertTrue(validator.isValid("BRANCO", context));
        assertTrue(validator.isValid("PRETO", context));
        assertTrue(validator.isValid("AZUL", context));

        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Deve aceitar cores válidas com case misto")
    void deveAceitarCoresValidasCaseMisto() {
        assertTrue(validator.isValid("Branco", context));
        assertTrue(validator.isValid("PrEtO", context));
        assertTrue(validator.isValid("aZuL", context));

        verifyNoInteractions(context);
    }
}
