const API_BASE_URL = 'http://localhost:8080';
const CARROS_URL = `${API_BASE_URL}/carros`;
const AUTH_URL = `${API_BASE_URL}/auth`;

const loginView = document.querySelector('#loginView');
const appView = document.querySelector('#appView');
const loginForm = document.querySelector('#loginForm');
const cadastroClienteForm = document.querySelector('#cadastroClienteForm');
const tabLogin = document.querySelector('#tabLogin');
const tabCadastroCliente = document.querySelector('#tabCadastroCliente');
const loginMensagem = document.querySelector('#loginMensagem');

const form = document.querySelector('#carroForm');
const formTitulo = document.querySelector('#formTitulo');
const btnSalvar = document.querySelector('#btnSalvar');
const btnLimpar = document.querySelector('#btnLimpar');
const btnAtualizar = document.querySelector('#btnAtualizar');
const btnSair = document.querySelector('#btnSair');
const mensagem = document.querySelector('#mensagem');
const tabela = document.querySelector('#carrosTabela');
const contador = document.querySelector('#contador');
const contadorEstoque = document.querySelector('#contadorEstoque');
const estoqueCards = document.querySelector('#estoqueCards');
const busca = document.querySelector('#busca');
const campoAno = document.querySelector('#ano');
const campoQuilometragem = document.querySelector('#quilometragem');
const campoPreco = document.querySelector('#preco');

const relatorioTotal = document.querySelector('#relatorioTotal');
const relatorioMedia = document.querySelector('#relatorioMedia');
const relatorioMaior = document.querySelector('#relatorioMaior');
const relatorioMenor = document.querySelector('#relatorioMenor');
const relatorioCategorias = document.querySelector('#relatorioCategorias');

let carros = [];
let usuarioLogado = JSON.parse(localStorage.getItem('usuarioLogado'));

function manterApenasDigitos(valor) {
    return valor.replace(/\D/g, '');
}

function formatarInteiroBrasileiro(valor) {
    const digitos = manterApenasDigitos(valor);

    if (!digitos) {
        return '';
    }

    return Number(digitos).toLocaleString('pt-BR');
}

function converterMoedaParaNumero(valor) {
    if (!valor) {
        return 0;
    }

    return Number(valor.replace(/\./g, '').replace(',', '.'));
}

function converterInteiroFormatadoParaNumero(valor) {
    return Number(manterApenasDigitos(valor) || 0);
}

function formatarValorParaInput(valor) {
    if (valor === null || valor === undefined || valor === '') {
        return '';
    }

    return Number(valor).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function formatarMoeda(valor) {
    return Number(valor || 0).toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });
}

function tokenAtual() {
    return usuarioLogado?.token;
}

function usuarioEhAdmin() {
    return usuarioLogado?.perfil === 'ADMIN';
}

function headersAutenticados() {
    return {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${tokenAtual()}`
    };
}

async function fetchAutenticado(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            ...headersAutenticados(),
            ...(options.headers || {})
        }
    });

    if (response.status === 401) {
        sair();
        throw new Error('Sessao expirada. Faca login novamente.');
    }

    if (response.status === 403) {
        throw new Error('Seu usuario nao tem permissao para essa acao.');
    }

    return response;
}

function mostrarLoginMensagem(texto, tipo = '') {
    loginMensagem.textContent = texto;
    loginMensagem.className = `status-message ${tipo}`;
}

function mostrarMensagem(texto, tipo = '') {
    mensagem.textContent = texto;
    mensagem.className = `status-message ${tipo}`;
}

function alternarFormularioLogin(modo) {
    const criandoConta = modo === 'cadastro';
    loginForm.classList.toggle('hidden', criandoConta);
    cadastroClienteForm.classList.toggle('hidden', !criandoConta);
    tabLogin.classList.toggle('active', !criandoConta);
    tabCadastroCliente.classList.toggle('active', criandoConta);
    mostrarLoginMensagem('');
}

async function autenticar(username, senha) {
    const response = await fetch(`${AUTH_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, senha })
    });

    if (!response.ok) {
        throw new Error('Usuario ou senha invalidos.');
    }

    usuarioLogado = await response.json();
    localStorage.setItem('usuarioLogado', JSON.stringify(usuarioLogado));
    iniciarAplicacao();
}

async function cadastrarCliente(nome, username, senha) {
    const response = await fetch(`${AUTH_URL}/cadastrar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome, username, senha })
    });

    if (!response.ok) {
        const erro = await response.json().catch(() => null);
        throw new Error(erro?.mensagem || 'Nao foi possivel criar a conta.');
    }

    usuarioLogado = await response.json();
    localStorage.setItem('usuarioLogado', JSON.stringify(usuarioLogado));
    iniciarAplicacao();
}

function sair() {
    localStorage.removeItem('usuarioLogado');
    usuarioLogado = null;
    carros = [];
    appView.classList.add('hidden');
    loginView.classList.remove('hidden');
    alternarFormularioLogin('login');
}

function aplicarPermissoes() {
    document.querySelectorAll('.admin-only').forEach((elemento) => {
        elemento.classList.toggle('hidden', !usuarioEhAdmin());
    });

    if (!usuarioEhAdmin()) {
        trocarView('estoque');
    } else {
        trocarView('cadastro');
    }
}

function trocarView(viewName) {
    document.querySelectorAll('.app-section').forEach((section) => {
        section.classList.toggle('hidden', section.id !== `${viewName}View`);
    });

    document.querySelectorAll('.nav-button[data-view]').forEach((button) => {
        button.classList.toggle('active', button.dataset.view === viewName);
    });

    if (viewName === 'relatorios') {
        renderizarRelatorios();
    }

    if (viewName === 'estoque') {
        renderizarEstoque();
    }
}

async function iniciarAplicacao() {
    if (!usuarioLogado?.token) {
        sair();
        return;
    }

    loginView.classList.add('hidden');
    appView.classList.remove('hidden');
    aplicarPermissoes();
    await carregarCarros();
}

function montarPayload() {
    return {
        marca: document.querySelector('#marca').value.trim(),
        modelo: document.querySelector('#modelo').value.trim(),
        ano: Number(document.querySelector('#ano').value),
        preco: converterMoedaParaNumero(document.querySelector('#preco').value),
        categoria: document.querySelector('#categoria').value,
        tipoCarroceria: document.querySelector('#tipoCarroceria').value,
        cor: document.querySelector('#cor').value,
        quilometragem: converterInteiroFormatadoParaNumero(document.querySelector('#quilometragem').value),
        cambio: document.querySelector('#cambio').value
    };
}

function limparFormulario() {
    form.reset();
    document.querySelector('#carroId').value = '';
    formTitulo.textContent = 'Cadastrar veiculo';
    btnSalvar.innerHTML = '<span aria-hidden="true">+</span>Cadastrar veiculo';
    mostrarMensagem('');
}

function preencherFormulario(carro) {
    if (!usuarioEhAdmin()) {
        return;
    }

    document.querySelector('#carroId').value = carro.id;
    document.querySelector('#marca').value = carro.marca || '';
    document.querySelector('#modelo').value = carro.modelo || '';
    document.querySelector('#ano').value = carro.ano || '';
    document.querySelector('#preco').value = formatarValorParaInput(carro.preco);
    document.querySelector('#categoria').value = carro.categoria || '';
    document.querySelector('#tipoCarroceria').value = carro.tipoCarroceria || '';
    document.querySelector('#cor').value = carro.cor || '';
    document.querySelector('#quilometragem').value = formatarInteiroBrasileiro(String(carro.quilometragem || ''));
    document.querySelector('#cambio').value = carro.cambio || '';

    formTitulo.textContent = `Editando #${carro.id}`;
    btnSalvar.innerHTML = '<span aria-hidden="true">+</span>Salvar alteracoes';
    trocarView('cadastro');
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function formatarCampoPreco() {
    const valorNumerico = converterMoedaParaNumero(campoPreco.value);

    if (!Number.isNaN(valorNumerico) && campoPreco.value.trim() !== '') {
        campoPreco.value = formatarValorParaInput(valorNumerico);
    }
}

function formatarCampoPrecoEnquantoDigita(event) {
    const digitos = manterApenasDigitos(event.target.value);

    if (!digitos) {
        event.target.value = '';
        return;
    }

    event.target.value = Number(digitos).toLocaleString('pt-BR');
}

function validarAno() {
    campoAno.value = manterApenasDigitos(campoAno.value).slice(0, 4);
}

function validarAnoAntesDeSalvar() {
    if (!/^\d{4}$/.test(campoAno.value)) {
        mostrarMensagem('Informe um ano com exatamente 4 numeros.', 'error');
        campoAno.focus();
        return false;
    }

    return true;
}

function formatarCampoQuilometragem() {
    campoQuilometragem.value = formatarInteiroBrasileiro(campoQuilometragem.value);
}

function filtrarCarros() {
    const termo = busca.value.trim().toLowerCase();

    if (!termo) {
        return carros;
    }

    return carros.filter((carro) => {
        const texto = [
            carro.marca,
            carro.modelo,
            carro.cor,
            carro.categoria,
            carro.tipoCarroceria,
            carro.cambio
        ].join(' ').toLowerCase();

        return texto.includes(termo);
    });
}

function renderizarTabela() {
    const carrosFiltrados = filtrarCarros();
    contador.textContent = `${carrosFiltrados.length} ${carrosFiltrados.length === 1 ? 'item' : 'itens'}`;

    if (carrosFiltrados.length === 0) {
        tabela.innerHTML = `
            <tr>
                <td colspan="${usuarioEhAdmin() ? '5' : '4'}">
                    <div class="empty-state">Nenhum veiculo encontrado.</div>
                </td>
            </tr>
        `;
        return;
    }

    tabela.innerHTML = carrosFiltrados.map((carro) => `
        <tr>
            <td>
                <p class="car-name">${carro.marca || '-'} ${carro.modelo || ''}</p>
                <span class="car-sub">#${carro.id} - ${carro.categoria || 'Sem categoria'}</span>
            </td>
            <td>${carro.ano || '-'}</td>
            <td><span class="price">${formatarMoeda(carro.preco)}</span></td>
            <td>
                <div>
                    <span class="tag">${carro.tipoCarroceria || '-'}</span>
                    <span class="tag">${carro.cor || '-'}</span>
                    <span class="tag">${carro.cambio || '-'}</span>
                </div>
                <div class="km">${Number(carro.quilometragem || 0).toLocaleString('pt-BR')} km</div>
            </td>
            ${usuarioEhAdmin() ? `
                <td>
                    <div class="row-actions">
                        <button class="row-button edit" type="button" onclick="editarCarro(${carro.id})">Editar</button>
                        <button class="row-button delete" type="button" onclick="removerCarro(${carro.id})">Excluir</button>
                    </div>
                </td>
            ` : ''}
        </tr>
    `).join('');
}

function renderizarEstoque() {
    contadorEstoque.textContent = `${carros.length} ${carros.length === 1 ? 'item' : 'itens'}`;

    if (carros.length === 0) {
        estoqueCards.innerHTML = '<div class="empty-state">Nenhum veiculo em estoque.</div>';
        return;
    }

    estoqueCards.innerHTML = carros.map((carro) => `
        <article class="stock-card">
            <div>
                <p class="car-name">${carro.marca || '-'} ${carro.modelo || ''}</p>
                <span class="car-sub">${carro.ano || '-'} - ${carro.categoria || 'Sem categoria'}</span>
            </div>
            <strong class="price">${formatarMoeda(carro.preco)}</strong>
            <div>
                <span class="tag">${carro.tipoCarroceria || '-'}</span>
                <span class="tag">${carro.cor || '-'}</span>
                <span class="tag">${carro.cambio || '-'}</span>
            </div>
            <div class="km">${Number(carro.quilometragem || 0).toLocaleString('pt-BR')} km</div>
        </article>
    `).join('');
}

function renderizarRelatorios() {
    const total = carros.length;
    const precos = carros.map((carro) => Number(carro.preco || 0));
    const soma = precos.reduce((totalAtual, preco) => totalAtual + preco, 0);
    const maior = total ? Math.max(...precos) : 0;
    const menor = total ? Math.min(...precos) : 0;

    relatorioTotal.textContent = total;
    relatorioMedia.textContent = formatarMoeda(total ? soma / total : 0);
    relatorioMaior.textContent = formatarMoeda(maior);
    relatorioMenor.textContent = formatarMoeda(menor);

    const categorias = carros.reduce((resultado, carro) => {
        const categoria = carro.categoria || 'Sem categoria';
        resultado[categoria] = (resultado[categoria] || 0) + 1;
        return resultado;
    }, {});

    relatorioCategorias.innerHTML = Object.entries(categorias)
        .map(([categoria, quantidade]) => `
            <div class="category-row">
                <span>${categoria}</span>
                <strong>${quantidade}</strong>
            </div>
        `)
        .join('') || '<div class="empty-state">Sem dados para relatorio.</div>';
}

function renderizarTudo() {
    renderizarTabela();
    renderizarEstoque();
    renderizarRelatorios();
}

async function carregarCarros() {
    try {
        mostrarMensagem('Carregando veiculos...');
        const response = await fetchAutenticado(CARROS_URL);

        if (!response.ok) {
            throw new Error('Erro ao buscar carros.');
        }

        carros = await response.json();
        renderizarTudo();
        mostrarMensagem('Lista atualizada.');
    } catch (error) {
        mostrarMensagem(error.message || 'Nao consegui conectar na API.', 'error');
    }
}

async function salvarCarro(event) {
    event.preventDefault();

    if (!usuarioEhAdmin()) {
        mostrarMensagem('Apenas administradores podem cadastrar veiculos.', 'error');
        return;
    }

    if (!validarAnoAntesDeSalvar()) {
        return;
    }

    const id = document.querySelector('#carroId').value;
    const editando = Boolean(id);
    const url = editando ? `${CARROS_URL}/${id}` : CARROS_URL;
    const method = editando ? 'PUT' : 'POST';

    try {
        const response = await fetchAutenticado(url, {
            method,
            body: JSON.stringify(montarPayload())
        });

        if (!response.ok) {
            throw new Error('Erro ao salvar carro.');
        }

        limparFormulario();
        await carregarCarros();
        mostrarMensagem(editando ? 'Veiculo atualizado com sucesso.' : 'Veiculo cadastrado com sucesso.');
    } catch (error) {
        mostrarMensagem(error.message || 'Erro ao salvar.', 'error');
    }
}

function editarCarro(id) {
    const carro = carros.find((item) => item.id === id);

    if (carro) {
        preencherFormulario(carro);
    }
}

async function removerCarro(id) {
    if (!usuarioEhAdmin()) {
        mostrarMensagem('Apenas administradores podem excluir veiculos.', 'error');
        return;
    }

    const confirmou = window.confirm(`Deseja excluir o veiculo #${id}?`);

    if (!confirmou) {
        return;
    }

    try {
        const response = await fetchAutenticado(`${CARROS_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Erro ao excluir carro.');
        }

        await carregarCarros();
        mostrarMensagem('Veiculo excluido com sucesso.');
    } catch (error) {
        mostrarMensagem(error.message || 'Erro ao excluir.', 'error');
    }
}

loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    try {
        await autenticar(
            document.querySelector('#loginUsuario').value.trim(),
            document.querySelector('#loginSenha').value
        );
    } catch (error) {
        mostrarLoginMensagem(error.message, 'error');
    }
});

cadastroClienteForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    try {
        await cadastrarCliente(
            document.querySelector('#cadastroNome').value.trim(),
            document.querySelector('#cadastroUsuario').value.trim(),
            document.querySelector('#cadastroSenha').value
        );
    } catch (error) {
        mostrarLoginMensagem(error.message, 'error');
    }
});

tabLogin.addEventListener('click', () => alternarFormularioLogin('login'));
tabCadastroCliente.addEventListener('click', () => alternarFormularioLogin('cadastro'));

document.querySelectorAll('.nav-button[data-view]').forEach((button) => {
    button.addEventListener('click', () => trocarView(button.dataset.view));
});

form.addEventListener('submit', salvarCarro);
btnLimpar.addEventListener('click', limparFormulario);
btnAtualizar.addEventListener('click', carregarCarros);
btnSair.addEventListener('click', sair);
busca.addEventListener('input', renderizarTabela);
campoAno.addEventListener('input', validarAno);
campoQuilometragem.addEventListener('input', formatarCampoQuilometragem);
campoPreco.addEventListener('input', formatarCampoPrecoEnquantoDigita);
campoPreco.addEventListener('blur', formatarCampoPreco);

if (usuarioLogado?.token) {
    iniciarAplicacao();
} else {
    sair();
}
