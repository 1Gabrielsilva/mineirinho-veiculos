package com.loja.carros.exception;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        Integer status,
        String erro,
        String mensagem,
        String caminho
) {
}
