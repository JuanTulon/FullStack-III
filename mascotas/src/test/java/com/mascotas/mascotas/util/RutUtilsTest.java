package com.mascotas.mascotas.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RutUtilsTest {

    @Test
    @DisplayName("validarFormatoRut: Debería retornar true para formatos válidos (con y sin K)")
    void validarFormatoRut_FormatosValidos() {
        assertThat(RutUtils.validarFormatoRut("12345678-5")).isTrue();
        assertThat(RutUtils.validarFormatoRut("19876543-0")).isTrue();
        assertThat(RutUtils.validarFormatoRut("20123456-K")).isTrue();
        assertThat(RutUtils.validarFormatoRut("20123456-k")).isTrue();
        assertThat(RutUtils.validarFormatoRut("1-9")).isTrue();
    }

    @Test
    @DisplayName("validarFormatoRut: Debería retornar false para formatos inválidos o valores nulos")
    void validarFormatoRut_FormatosInvalidos() {
        assertThat(RutUtils.validarFormatoRut(null)).isFalse(); // Nulo
        assertThat(RutUtils.validarFormatoRut("")).isFalse();   // Vacío
        assertThat(RutUtils.validarFormatoRut("123456785")).isFalse(); // Sin guion
        assertThat(RutUtils.validarFormatoRut("12.345.678-5")).isFalse(); // Con puntos (no soportado por tu formato actual)
        assertThat(RutUtils.validarFormatoRut("12345678-M")).isFalse();  // DV inválido
        assertThat(RutUtils.validarFormatoRut("1234567890-5")).isFalse(); // Más de 8 dígitos en RUN
    }

    @Test
    @DisplayName("validarRut: Debería retornar true para RUTs matemáticamente válidos")
    void validarRut_RutsValidos() {
        // RUTs calculados y verídicos algorítmicamente
        assertThat(RutUtils.validarRut("12345678-5")).isTrue();
        assertThat(RutUtils.validarRut("19876543-0")).isTrue();
        assertThat(RutUtils.validarRut("1-9")).isTrue();
    }

    @Test
    @DisplayName("validarRut: Debería retornar false para RUTs inválidos matemáticamente")
    void validarRut_RutsInvalidos() {
        // El formato está bien, pero el DV es incorrecto
        assertThat(RutUtils.validarRut("12345678-4")).isFalse();
        assertThat(RutUtils.validarRut("1-8")).isFalse();
    }

    @Test
    @DisplayName("validarDV: Debería calcular y comprobar el Dígito Verificador correctamente")
    void validarDV_CalculoCorrecto() {
        assertThat(RutUtils.validarDV("12345678", "5")).isTrue();
        assertThat(RutUtils.validarDV("19876543", "0")).isTrue();
        assertThat(RutUtils.validarDV("1", "9")).isTrue();
        
        // Falso si le paso un DV erróneo
        assertThat(RutUtils.validarDV("12345678", "K")).isFalse();
    }

    @Test
    @DisplayName("calcularDV: Debería retornar el DV correcto (String) a partir de un entero")
    void calcularDV_RetornoDVCorrecto() {
        assertThat(RutUtils.calcularDV(12345678)).isEqualTo("5");
        assertThat(RutUtils.calcularDV(19876543)).isEqualTo("0");
        assertThat(RutUtils.calcularDV(1)).isEqualTo("9");
    }
}
