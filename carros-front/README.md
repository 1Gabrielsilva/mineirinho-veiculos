# Front Loja de Carros

Front-end simples em HTML, CSS e JavaScript para testar a API Spring Boot.
Agora ele possui login simples com dois perfis:

- `ADMIN`: cadastra, edita, exclui, acessa estoque e relatorios.
- `CLIENTE`: acessa apenas estoque e atualiza a lista.

## Estrutura

```text
index.html
assets/
  css/
    styles.css
  js/
    app.js
```

- `index.html`: estrutura da tela e campos do formulario.
- `assets/css/styles.css`: visual, cores, layout e responsividade.
- `assets/js/app.js`: chamadas para a API, cadastro, edicao, exclusao, filtro e formatacao de moeda.
- Login padrao:
  - Admin: `admin` / `admin123`
  - Cliente: `cliente` / `cliente123`

## Como usar

1. Suba o backend:

```bash
cd "D:\projeto relembrar\carros\carros"
.\mvnw.cmd spring-boot:run
```

2. Suba o servidor do front:

```bash
cd "D:\projeto relembrar\carros\carros-front"
python -m http.server 5500
```

3. Abra no navegador:

```text
http://localhost:5500
```

## API usada

```text
http://localhost:8080
```
