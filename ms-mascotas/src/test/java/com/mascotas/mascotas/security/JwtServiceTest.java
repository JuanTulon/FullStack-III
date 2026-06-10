package com.mascotas.mascotas.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails testUser;
    
    // Una clave base64 válida segura para HMAC-SHA256 (mínimo 256 bits)
    private final String TEST_SECRET = "Q29kZUNvZGVDb2RlQ29kZUNvZGVDb2RlQ29kZUNvZGVDb2RlQ29kZUNvZGVDb2Rl"; 

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        // Como las variables usan @Value y aquí no levantamos Spring completo por rendimiento,
        // usamos ReflectionTestUtils para setear las propiedades privadas directamente.
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60 * 60); // 1 hora

        // Creamos un UserDetails simulado simulando el Email como username
        testUser = new User("correo@test.com", "password", Collections.emptyList());
    }

    @Test
    @DisplayName("generateToken: Debería generar un token no nulo y válido")
    void generateToken_RetornaTokenValido() {
        String token = jwtService.generateToken(testUser);
        
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        // Un JWT estándar tiene siempre 3 partes separadas por puntos
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("extractUsername: Debería extraer correctamente el correo del token")
    void extractUsername_ExtraeElCorreoCorrectamente() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);
        
        assertThat(username).isEqualTo("correo@test.com");
    }

    @Test
    @DisplayName("isTokenValid: Debería ser true para el usuario correcto y token vigente")
    void isTokenValid_RetornaTrueSiEsValido() {
        String token = jwtService.generateToken(testUser);
        
        boolean isValid = jwtService.isTokenValid(token, testUser);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid: Debería ser false si se compara con otro usuario")
    void isTokenValid_RetornaFalseSiUsuarioNoCoincide() {
        String token = jwtService.generateToken(testUser);
        
        UserDetails anotherUser = new User("otro@test.com", "password", Collections.emptyList());
        
        boolean isValid = jwtService.isTokenValid(token, anotherUser);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("extractUsername: Debería lanzar excepción si el token es falso o su firma es inválida")
    void extractUsername_LanzaExcepcionTokenMalformado() {
        String badToken = "eyJhbGciOiJIUzI1NiJ9.TokenFalso.firmaInvalida";
        
        // Cambiamos JwtException/MalformedJwtException por SignatureException
        assertThrows(io.jsonwebtoken.security.SignatureException.class, () -> {
            jwtService.extractUsername(badToken);
        });
    }

    @Test
    @DisplayName("isTokenValid: Debería lanzar excepción si el token ha expirado")
    void isTokenValid_LanzaExcepcionSiExpirado() {
        // Configuramos la expiración a 1 milisegundo (quedará obsoleto casi instantaneamente)
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1);
        
        String token = jwtService.generateToken(testUser);
        
        // Esperamos a que pase 1 milisegundo asegurándonos que expira
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cuando el token espiró, JwtService lanza automáticamente ExpiredJwtException o lo rechaza
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, testUser);
        });
    }
}
