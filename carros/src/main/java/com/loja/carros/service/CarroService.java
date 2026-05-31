package com.loja.carros.service;

import com.loja.carros.dto.CarroRequestDTO;
import com.loja.carros.dto.CarroResponseDTO;
import com.loja.carros.entity.Carro;
import com.loja.carros.exception.CarroNaoEncontradoException;
import com.loja.carros.repository.CarroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarroService {

    private final CarroRepository carroRepository;

    public List<CarroResponseDTO> listar() {
        return carroRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CarroResponseDTO buscarPorId(Long id) {
        Carro carro = buscarEntidadePorId(id);
        return toResponseDTO(carro);
    }

    public CarroResponseDTO cadastrar(CarroRequestDTO dto) {
        Carro carro = new Carro();
        preencherDados(carro, dto);

        Carro carroSalvo = carroRepository.save(carro);
        return toResponseDTO(carroSalvo);
    }

    public CarroResponseDTO atualizar(Long id, CarroRequestDTO dto) {
        Carro carro = buscarEntidadePorId(id);
        preencherDados(carro, dto);

        Carro carroAtualizado = carroRepository.save(carro);
        return toResponseDTO(carroAtualizado);
    }

    public void remover(Long id) {
        Carro carro = buscarEntidadePorId(id);
        carroRepository.delete(carro);
    }

    private Carro buscarEntidadePorId(Long id) {
        return carroRepository.findById(id)
                .orElseThrow(() -> new CarroNaoEncontradoException(id));
    }

    private void preencherDados(Carro carro, CarroRequestDTO dto) {
        carro.setMarca(dto.marca());
        carro.setModelo(dto.modelo());
        carro.setAno(dto.ano());
        carro.setPreco(dto.preco());
        carro.setCategoria(dto.categoria());
        carro.setTipoCarroceria(dto.tipoCarroceria());
        carro.setCor(dto.cor());
        carro.setQuilometragem(dto.quilometragem());
        carro.setCambio(dto.cambio());
    }

    private CarroResponseDTO toResponseDTO(Carro carro) {
        return new CarroResponseDTO(
                carro.getId(),
                carro.getMarca(),
                carro.getModelo(),
                carro.getAno(),
                carro.getPreco(),
                carro.getCategoria(),
                carro.getTipoCarroceria(),
                carro.getCor(),
                carro.getQuilometragem(),
                carro.getCambio()
        );
    }
}
