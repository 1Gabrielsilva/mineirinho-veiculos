package com.loja.carros.service;

import com.loja.carros.dto.AuthResponseDTO;
import com.loja.carros.dto.CadastroUsuarioRequestDTO;
import com.loja.carros.dto.LoginRequestDTO;
import com.loja.carros.dto.UsuarioLogadoDTO;
import com.loja.carros.entity.Perfil;
import com.loja.carros.entity.SessaoUsuario;
import com.loja.carros.entity.Usuario;
import com.loja.carros.exception.AutenticacaoException;
import com.loja.carros.exception.UsuarioJaExisteException;
import com.loja.carros.repository.SessaoUsuarioRepository;
import com.loja.carros.repository.UsuarioRepository;
import com.loja.carros.security.AuthContext;
import com.loja.carros.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SessaoUsuarioRepository sessaoUsuarioRepository;
    private final SenhaService senhaService;

    public AuthResponseDTO cadastrarCliente(CadastroUsuarioRequestDTO dto) {
        validarDadosCadastro(dto);

        if (usuarioRepository.existsByUsername(dto.username())) {
            throw new UsuarioJaExisteException(dto.username());
        }

        Usuario usuario = criarUsuario(dto.nome(), dto.username(), dto.senha(), Perfil.CLIENTE);
        usuarioRepository.save(usuario);

        return criarSessao(usuario);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByUsername(dto.username())
                .orElseThrow(() -> new AutenticacaoException("Usuario ou senha invalidos"));

        if (!senhaService.senhaConfere(dto.senha(), usuario.getSalt(), usuario.getSenhaHash())) {
            throw new AutenticacaoException("Usuario ou senha invalidos");
        }

        return criarSessao(usuario);
    }

    public UsuarioAutenticado autenticarToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AutenticacaoException("Token nao informado");
        }

        String token = authorizationHeader.substring(7);

        SessaoUsuario sessao = sessaoUsuarioRepository.findByToken(token)
                .orElseThrow(() -> new AutenticacaoException("Token invalido"));

        if (sessao.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new AutenticacaoException("Sessao expirada");
        }

        Usuario usuario = sessao.getUsuario();
        return new UsuarioAutenticado(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getPerfil()
        );
    }

    public UsuarioLogadoDTO usuarioLogado() {
        UsuarioAutenticado usuario = AuthContext.getUsuario()
                .orElseThrow(() -> new AutenticacaoException("Usuario nao autenticado"));

        return new UsuarioLogadoDTO(usuario.nome(), usuario.username(), usuario.perfil().name());
    }

    public Usuario criarUsuario(String nome, String username, String senha, Perfil perfil) {
        String salt = senhaService.gerarSalt();
        String hash = senhaService.gerarHash(senha, salt);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setUsername(username);
        usuario.setSalt(salt);
        usuario.setSenhaHash(hash);
        usuario.setPerfil(perfil);
        return usuario;
    }

    public void atualizarUsuarioPadrao(String username, String nome, String senha, Perfil perfil) {
        usuarioRepository.findByUsername(username).ifPresentOrElse(usuario -> {
            String salt = senhaService.gerarSalt();
            String hash = senhaService.gerarHash(senha, salt);

            usuario.setNome(nome);
            usuario.setSalt(salt);
            usuario.setSenhaHash(hash);
            usuario.setPerfil(perfil);
            usuarioRepository.save(usuario);
        }, () -> usuarioRepository.save(criarUsuario(nome, username, senha, perfil)));
    }

    private AuthResponseDTO criarSessao(Usuario usuario) {
        SessaoUsuario sessao = new SessaoUsuario();
        sessao.setToken(UUID.randomUUID().toString());
        sessao.setUsuario(usuario);
        sessao.setCriadoEm(LocalDateTime.now());
        sessao.setExpiraEm(LocalDateTime.now().plusHours(8));

        sessaoUsuarioRepository.save(sessao);

        return new AuthResponseDTO(
                sessao.getToken(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getPerfil().name()
        );
    }

    private void validarDadosCadastro(CadastroUsuarioRequestDTO dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new AutenticacaoException("Nome e obrigatorio");
        }

        if (dto.username() == null || dto.username().isBlank()) {
            throw new AutenticacaoException("Usuario e obrigatorio");
        }

        if (dto.senha() == null || dto.senha().length() < 8) {
            throw new AutenticacaoException("Senha deve ter pelo menos 8 caracteres");
        }
    }
}
