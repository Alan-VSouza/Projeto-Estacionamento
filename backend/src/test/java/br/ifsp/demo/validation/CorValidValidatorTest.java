package br.ifsp.demo.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.*;
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

    @Nested
    @DisplayName("Teste de Mutante")
    class TesteDeMutante {

        @Nested
        @DisplayName("Testes de Casos Válidos")
        class TestesDeCasosValidos {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cores válidas em minúsculas")
            void deveAceitarCoresValidasMinusculas() {
                assertTrue(validator.isValid("branco", context));
                assertTrue(validator.isValid("preto", context));
                assertTrue(validator.isValid("azul", context));
                assertTrue(validator.isValid("vermelho", context));

                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cores válidas em maiúsculas")
            void deveAceitarCoresValidasMaiusculas() {
                assertTrue(validator.isValid("BRANCO", context));
                assertTrue(validator.isValid("PRETO", context));
                assertTrue(validator.isValid("AZUL", context));

                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cores válidas com case misto")
            void deveAceitarCoresValidasCaseMisto() {
                assertTrue(validator.isValid("Branco", context));
                assertTrue(validator.isValid("PrEtO", context));
                assertTrue(validator.isValid("aZuL", context));

                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cores com espaços nas bordas")
            void deveAceitarCoresComEspacos() {
                assertTrue(validator.isValid("  branco  ", context));
                assertTrue(validator.isValid("\tpreto\t", context));
                assertTrue(validator.isValid("\nvermelho\n", context));

                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cores compostas válidas")
            void deveAceitarCoresCompostasValidas() {
                assertTrue(validator.isValid("off white", context));
                assertTrue(validator.isValid("verde claro", context));
                assertTrue(validator.isValid("azul marinho", context));

                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cores com acentos")
            void deveAceitarCoresComAcentos() {
                assertTrue(validator.isValid("índigo", context));
                assertTrue(validator.isValid("salmão", context));
                assertTrue(validator.isValid("açafrão", context));

                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cores muito longas")
            void deveAceitarCoresMuitoLongas() {
                assertTrue(validator.isValid("verde esmeralda claro", context));
                assertTrue(validator.isValid("azul turquesa metalizado", context));

                verifyNoInteractions(context);
            }
        }

        @Nested
        @DisplayName("Testes de Casos Inválidos - Null e Vazio")
        class TestesDeCasosInvalidosNullEVazio {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve rejeitar cor null")
            void deveRejeitarCorNull() {
                assertFalse(validator.isValid(null, context));
                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve rejeitar cor vazia")
            void deveRejeitarCorVazia() {
                assertFalse(validator.isValid("", context));
                verifyNoInteractions(context);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve rejeitar cor apenas com espaços")
            void deveRejeitarCorApenasEspacos() {
                assertFalse(validator.isValid("   ", context));
                assertFalse(validator.isValid("\t\t", context));
                assertFalse(validator.isValid("\n\n", context));

                verifyNoInteractions(context);
            }
        }

        @Nested
        @DisplayName("Testes de Casos Inválidos - Números")
        class TestesDeCasosInvalidosNumeros {

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(strings = {"123", "456", "789", "0", "999", "12345"})
            @DisplayName("Deve rejeitar cores que são apenas números")
            void deveRejeitarCoresApenasNumeros(String corNumerica) {
                assertFalse(validator.isValid(corNumerica, context));

                verify(context).disableDefaultConstraintViolation();
                verify(context).buildConstraintViolationWithTemplate(
                        "Cor não pode ser um número. Use nomes como: branco, preto, azul, etc."
                );
                verify(violationBuilder).addConstraintViolation();
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve rejeitar números com espaços")
            void deveRejeitarNumerosComEspacos() {
                assertFalse(validator.isValid("  123  ", context));

                verify(context).disableDefaultConstraintViolation();
                verify(context).buildConstraintViolationWithTemplate(
                        "Cor não pode ser um número. Use nomes como: branco, preto, azul, etc."
                );
                verify(violationBuilder).addConstraintViolation();
            }
        }

        @Nested
        @DisplayName("Testes de Casos Inválidos - Caracteres Especiais")
        class TestesDeCasosInvalidosCaracteresEspeciais {

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(strings = {"azul@", "verde#", "vermelho!", "preto$", "branco%",
                    "cor&", "teste*", "azul()", "verde+", "cor=",
                    "teste[", "cor]", "azul{", "verde}", "cor|",
                    "teste\\", "azul:", "verde;", "cor\"", "teste'",
                    "azul<", "verde>", "cor?", "teste/"})
            @DisplayName("Deve rejeitar cores com caracteres especiais")
            void deveRejeitarCoresComCaracteresEspeciais(String corComEspeciais) {
                assertFalse(validator.isValid(corComEspeciais, context));

                verify(context).disableDefaultConstraintViolation();
                verify(context).buildConstraintViolationWithTemplate(
                        "Cor não pode conter caracteres especiais (@#!$%). Use apenas letras."
                );
                verify(violationBuilder).addConstraintViolation();
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve rejeitar cores com múltiplos caracteres especiais")
            void deveRejeitarCoresComMultiplosEspeciais() {
                assertFalse(validator.isValid("azul@#$", context));
                assertFalse(validator.isValid("verde!@#", context));

                verify(context, times(2)).disableDefaultConstraintViolation();
                verify(context, times(2)).buildConstraintViolationWithTemplate(
                        "Cor não pode conter caracteres especiais (@#!$%). Use apenas letras."
                );
                verify(violationBuilder, times(2)).addConstraintViolation();
            }
        }

        @Nested
        @DisplayName("Testes de Casos Inválidos - Tamanho Mínimo")
        class TestesDeCasosInvalidosTamanhoMinimo {

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(strings = {"a", "ab", "x", "zz"})
            @DisplayName("Deve rejeitar cores com menos de 3 caracteres")
            void deveRejeitarCoresMuitoCurtas(String corCurta) {
                assertFalse(validator.isValid(corCurta, context));

                verify(context).disableDefaultConstraintViolation();
                verify(context).buildConstraintViolationWithTemplate(
                        "Cor deve ter pelo menos 3 caracteres"
                );
                verify(violationBuilder).addConstraintViolation();
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve rejeitar cores curtas com espaços")
            void deveRejeitarCoresCurtasComEspacos() {
                assertFalse(validator.isValid("  a  ", context));
                assertFalse(validator.isValid("\tab\t", context));

                verify(context, times(2)).disableDefaultConstraintViolation();
                verify(context, times(2)).buildConstraintViolationWithTemplate(
                        "Cor deve ter pelo menos 3 caracteres"
                );
                verify(violationBuilder, times(2)).addConstraintViolation();
            }
        }

        @Nested
        @DisplayName("Testes de Casos Limite")
        class TestesDeCasosLimite {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve aceitar cor com exatamente 3 caracteres")
            void deveAceitarCorCom3Caracteres() {
                assertTrue(validator.isValid("azul", context));
                assertTrue(validator.isValid("cor", context));

                verifyNoInteractions(context);
            }
        }

        @Nested
        @DisplayName("Testes de Comportamento dos Mocks")
        class TestesDeComportamentoDosMocks {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve configurar mensagem customizada para números")
            void deveConfigurarMensagemCustomizadaParaNumeros() {
                validator.isValid("123", context);

                verify(context).disableDefaultConstraintViolation();
                verify(context).buildConstraintViolationWithTemplate(
                        "Cor não pode ser um número. Use nomes como: branco, preto, azul, etc."
                );
                verify(violationBuilder).addConstraintViolation();
                verifyNoMoreInteractions(context, violationBuilder);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve configurar mensagem customizada para caracteres especiais")
            void deveConfigurarMensagemCustomizadaParaEspeciais() {
                validator.isValid("azul@", context);

                verify(context).disableDefaultConstraintViolation();
                verify(context).buildConstraintViolationWithTemplate(
                        "Cor não pode conter caracteres especiais (@#!$%). Use apenas letras."
                );
                verify(violationBuilder).addConstraintViolation();
                verifyNoMoreInteractions(context, violationBuilder);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve configurar mensagem customizada para tamanho mínimo")
            void deveConfigurarMensagemCustomizadaParaTamanho() {
                validator.isValid("ab", context);

                verify(context).disableDefaultConstraintViolation();
                verify(context).buildConstraintViolationWithTemplate(
                        "Cor deve ter pelo menos 3 caracteres"
                );
                verify(violationBuilder).addConstraintViolation();
                verifyNoMoreInteractions(context, violationBuilder);
            }
        }
    }
}
