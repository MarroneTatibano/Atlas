# Atlas

API REST para controle de estoque, desenvolvida com Java e Spring Boot.

O Atlas permite cadastrar produtos e categorias, registrar entradas e saídas de estoque e consultar o histórico de movimentações de cada produto. O estoque só é alterado por movimentações, garantindo que o histórico sempre explique o saldo atual.

## Funcionalidades

- **Produtos:** cadastro, listagem, busca por id, atualização e exclusão
- **Categorias:** CRUD completo, com nomes únicos (sem diferenciar maiúsculas e minúsculas)
- **Filtro** de produtos por categoria
- **Entrada e saída de estoque**, com bloqueio de saída maior que o estoque disponível
- **Histórico de movimentações** por produto, com tipo, quantidade e data/hora
- **Estoque inicial** do cadastro registrado automaticamente no histórico
- **Validação** dos dados de entrada, com mensagens de erro por campo
- **Regras de integridade:** uma categoria com produtos vinculados não pode ser excluída

## Tecnologias

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- Bean Validation
- PostgreSQL
- JUnit 5, Mockito e MockMvc
- Maven

## Arquitetura

O projeto segue uma arquitetura em camadas:

```
controller   → recebe as requisições HTTP e devolve as respostas
service      → regras de negócio
repository   → acesso ao banco de dados (Spring Data JPA)
model        → entidades JPA (Produto, Categoria, Movimentacao)
dto          → objetos de entrada e saída da API (records)
exception    → tratamento global de erros
```

As entidades não são expostas diretamente na API: a entrada e a saída usam DTOs, o que separa o formato da API da estrutura do banco.

## Endpoints

### Produtos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/produtos` | Lista os produtos |
| `GET` | `/produtos?categoriaId={id}` | Lista os produtos de uma categoria |
| `GET` | `/produtos/{id}` | Busca um produto |
| `POST` | `/produtos` | Cadastra um produto |
| `PUT` | `/produtos/{id}` | Atualiza nome, preço e categoria |
| `DELETE` | `/produtos/{id}` | Exclui um produto e seu histórico |

### Estoque

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/produtos/{id}/entrada` | Registra uma entrada de estoque |
| `POST` | `/produtos/{id}/saida` | Registra uma saída de estoque |
| `GET` | `/produtos/{id}/movimentacoes` | Lista o histórico do produto (mais recentes primeiro) |

### Categorias

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/categorias` | Lista as categorias |
| `GET` | `/categorias/{id}` | Busca uma categoria |
| `POST` | `/categorias` | Cadastra uma categoria |
| `PUT` | `/categorias/{id}` | Atualiza uma categoria |
| `DELETE` | `/categorias/{id}` | Exclui uma categoria sem produtos |

## Exemplos

**Cadastrar um produto**

`POST /produtos`

```json
{
  "nome": "Dipirona 500mg c/10",
  "preco": 3.49,
  "estoque": 20,
  "categoriaId": 1
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "nome": "Dipirona 500mg c/10",
  "preco": 3.49,
  "estoque": 20,
  "categoria": { "id": 1, "nome": "Medicamentos" }
}
```

**Registrar uma saída**

`POST /produtos/1/saida`

```json
{ "quantidade": 5 }
```

Se a quantidade for maior que o estoque, a resposta é `409 Conflict`:

```json
{ "erro": "Estoque insuficiente. Disponível: 20" }
```

**Erro de validação**

Um `POST /produtos` sem o preço retorna `400 Bad Request`:

```json
{ "preco": "não deve ser nulo" }
```

## Códigos de resposta

| Status | Quando |
|---|---|
| `200 OK` | Consulta, atualização ou movimentação realizada |
| `201 Created` | Produto ou categoria cadastrado |
| `204 No Content` | Exclusão realizada |
| `400 Bad Request` | Dados inválidos ou categoria informada inexistente |
| `404 Not Found` | Produto ou categoria não encontrado |
| `409 Conflict` | Estoque insuficiente, nome de categoria repetido ou categoria com produtos |

## Como executar

### Pré-requisitos

- Java 25
- PostgreSQL

### Passos

1. Clone o repositório:

   ```bash
   git clone https://github.com/MarroneTatibano/Atlas.git
   cd Atlas
   ```

2. Crie o banco de dados no PostgreSQL:

   ```sql
   CREATE DATABASE atlas_bd;
   ```

3. Defina a senha do banco na variável de ambiente `DB_PASSWORD`:

   ```bash
   # Linux / macOS / Git Bash
   export DB_PASSWORD=sua_senha
   ```

   ```powershell
   # PowerShell
   $env:DB_PASSWORD="sua_senha"
   ```

   O usuário e a URL de conexão estão em `src/main/resources/application.properties` (padrão: usuário `postgres`, banco `atlas_bd` em `localhost:5432`).

4. Execute a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

A API fica disponível em `http://localhost:8080`. As tabelas são criadas automaticamente na primeira execução.

## Testes

O projeto tem testes unitários dos services (com Mockito) e testes da camada web dos controllers (com `@WebMvcTest` e MockMvc), que não dependem do banco de dados.

```bash
./mvnw test
```

O teste `AtlasApplicationTests` sobe a aplicação completa e precisa do PostgreSQL disponível e da variável `DB_PASSWORD` definida.
