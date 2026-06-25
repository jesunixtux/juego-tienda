package cl.duoc.authentication.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHashServiceTests {
    private final PasswordHashService passwordHashService = new PasswordHashService();

    @Test
    void hashNoGuardaPasswordPlanoYPermiteValidar() {
        String hash = passwordHashService.hash("cliente123");

        assertThat(hash).startsWith("sha256$");
        assertThat(hash).doesNotContain("cliente123");
        assertThat(passwordHashService.matches("cliente123", hash)).isTrue();
        assertThat(passwordHashService.matches("incorrecta", hash)).isFalse();
    }

    @Test
    void matchesRechazaHashesAntiguosOInvalidos() {
        assertThat(passwordHashService.matches("cliente123", null)).isFalse();
        assertThat(passwordHashService.matches("cliente123", "")).isFalse();
        assertThat(passwordHashService.matches("cliente123", "$2a$10$hash-antiguo")).isFalse();
    }
}
