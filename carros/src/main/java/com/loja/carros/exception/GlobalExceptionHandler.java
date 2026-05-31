package com.loja.carros.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CarroNaoEncontradoException.class)
    public ResponseEntity<ApiError> tratarCarroNaoEncontrado(
            CarroNaoEncontradoException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ApiError erro = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ApiError> tratarErroAutenticacao(
            AutenticacaoException exception,
            HttpServletRequest request
    ) {
        return criarErro(HttpStatus.UNAUTHORIZED, exception, request);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ApiError> tratarAcessoNegado(
            AcessoNegadoException exception,
            HttpServletRequest request
    ) {
        return criarErro(HttpStatus.FORBIDDEN, exception, request);
    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<ApiError> tratarUsuarioJaExiste(
            UsuarioJaExisteException exception,
            HttpServletRequest request
    ) {
        return criarErro(HttpStatus.CONFLICT, exception, request);
    }

    private ResponseEntity<ApiError> criarErro(
            HttpStatus status,
            RuntimeException exception,
            HttpServletRequest request
    ) {
        ApiError erro = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }
}
