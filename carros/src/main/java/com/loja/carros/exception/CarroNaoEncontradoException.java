package com.loja.carros.exception;

public class CarroNaoEncontradoException extends RuntimeException {

    public CarroNaoEncontradoException(Long id) {
        super("Carro não encontrado com id: " + id);
    }
}
