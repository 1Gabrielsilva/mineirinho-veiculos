package com.loja.carros.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class SenhaService {

    private static final int ITERACOES = 12000;
    private static final int TAMANHO_CHAVE = 256;
    private final SecureRandom secureRandom = new SecureRandom();

    public String gerarSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String gerarHash(String senha, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    senha.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    ITERACOES,
                    TAMANHO_CHAVE
            );

            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao gerar hash da senha", exception);
        }
    }

    public boolean senhaConfere(String senhaDigitada, String salt, String hashSalvo) {
        return gerarHash(senhaDigitada, salt).equals(hashSalvo);
    }
}
