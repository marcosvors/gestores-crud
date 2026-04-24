# Gestores CRUD

Aplicação full-stack para gerenciamento de gestores, composta por uma API REST desenvolvida com Spring Boot e um frontend em HTML, CSS e JavaScript puro servido pelo próprio servidor.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Backend](#backend)
  - [Entidade](#entidade)
  - [Repositório](#repositório)
  - [Serviço](#serviço)
  - [Controller (Resource)](#controller-resource)
  - [Tratamento de Exceções](#tratamento-de-exceções)
- [Frontend](#frontend)
  - [Interface](#interface)
  - [Comunicação com a API](#comunicação-com-a-api)
- [Banco de Dados](#banco-de-dados)
- [Perfis de Ambiente](#perfis-de-ambiente)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Exemplos de Requisição](#exemplos-de-requisição)

---

## Visão Geral

O projeto implementa um CRUD completo (Create, Read, Update, Delete) para a entidade **Gestor**, com as seguintes características:

- API RESTful com respostas padronizadas e tratamento global de erros
- Banco de dados H2 em arquivo para o perfil de desenvolvimento (persistência entre reinicializações)
- Suporte a PostgreSQL via variável de ambiente para produção
- Frontend integrado ao servidor Spring Boot, sem necessidade de servidor separado
- Interface responsiva sem dependências externas

---

## Tecnologias

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| Linguagem | Java | 21 |
| Framework backend | Spring Boot | 4.0.6 |
| Persistência | Spring Data JPA / Hibernate | — |
| Banco dev/test | H2 Database (arquivo) | — |
| Banco produção | PostgreSQL | — |
| Build | Maven | 4.0.0 |
| Frontend | HTML5, CSS3, JavaScript (ES6+) | — |

---

## Arquitetura

O projeto segue a arquitetura em camadas padrão do Spring:

```
┌─────────────────────────────────┐
│         Frontend (Browser)      │
│   index.html · style.css        │
│   script.js  (fetch API)        │
└────────────────┬────────────────┘
                 │ HTTP
┌────────────────▼────────────────┐
│         Resource Layer          │
│   GestorResource (@RestController)
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│          Service Layer          │
│   GestorService (@Service)      │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│        Repository Layer         │
│   GestorRepository (JpaRepository)
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│           Database              │
│   H2 (dev) · PostgreSQL (prod)  │
└─────────────────────────────────┘
```

---

## Estrutura do Projeto

```
gestores-crud/
├── pom.xml
├── mvnw / mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/com/marcosvors/gestores_crud/
│   │   │   ├── GestoresCrudApplication.java
│   │   │   ├── entities/
│   │   │   │   └── Gestor.java
│   │   │   ├── repositories/
│   │   │   │   └── GestorRepository.java
│   │   │   ├── services/
│   │   │   │   ├── GestorService.java
│   │   │   │   └── exceptions/
│   │   │   │       ├── ResourceNotFoundException.java
│   │   │   │       └── DatabaseException.java
│   │   │   └── resources/
│   │   │       ├── GestorResource.java
│   │   │       └── exceptions/
│   │   │           ├── ResourceExceptionHandler.java
│   │   │           └── StandardError.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-test.properties
│   │       ├── application-prod.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── style.css
│   │           └── script.js
│   └── test/java/com/marcosvors/gestores_crud/
│       └── GestoresCrudApplicationTests.java
└── testdb.mv.db          ← gerado automaticamente (H2 persistente)
```

---

## Backend

### Entidade

**`Gestor`** — mapeada para a tabela `tb_gestor`.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | `Long` | Chave primária, gerada automaticamente (`IDENTITY`) |
| `nome` | `String` | Nome do gestor |
| `email` | `String` | E-mail do gestor |

Implementa `Serializable`. Igualdade baseada no campo `id`.

---

### Repositório

**`GestorRepository`** estende `JpaRepository<Gestor, Long>`, herdando todos os métodos CRUD padrão do Spring Data JPA — sem consultas customizadas adicionais.

---

### Serviço

**`GestorService`** centraliza toda a lógica de negócio:

| Método | Descrição |
|--------|-----------|
| `findAll()` | Retorna todos os gestores |
| `findById(Long id)` | Busca por ID; lança `ResourceNotFoundException` se não encontrado |
| `inserir(Gestor obj)` | Persiste um novo gestor |
| `atualizar(Long id, Gestor obj)` | Atualiza `nome` e `email`; lança `ResourceNotFoundException` se não encontrado |
| `deletar(Long id)` | Remove por ID; lança `ResourceNotFoundException` ou `DatabaseException` em caso de violação de integridade |

---

### Controller (Resource)

**`GestorResource`** expõe os endpoints REST em `/gestores`:

| Método HTTP | Endpoint | Ação | Status de Sucesso |
|-------------|----------|------|-------------------|
| `GET` | `/gestores` | Lista todos os gestores | `200 OK` |
| `GET` | `/gestores/{id}` | Busca gestor por ID | `200 OK` |
| `POST` | `/gestores` | Cria novo gestor | `201 Created` + header `Location` |
| `PUT` | `/gestores/{id}` | Atualiza gestor existente | `200 OK` |
| `DELETE` | `/gestores/{id}` | Remove gestor | `204 No Content` |

---

### Tratamento de Exceções

O projeto implementa tratamento global de erros via `@ControllerAdvice`:

**Exceções de domínio:**

| Classe | Herda de | Quando é lançada |
|--------|----------|-----------------|
| `ResourceNotFoundException` | `RuntimeException` | Recurso não encontrado pelo ID |
| `DatabaseException` | `RuntimeException` | Violação de integridade no banco de dados |

**Handler global — `ResourceExceptionHandler`:**

| Exceção capturada | Status HTTP | Mensagem de erro |
|-------------------|-------------|-----------------|
| `ResourceNotFoundException` | `404 Not Found` | "Resource not found" |
| `DatabaseException` | `400 Bad Request` | "Database error" |

**Corpo padronizado de erro — `StandardError`:**

```json
{
  "timestamp": "2025-04-24T10:30:45Z",
  "status": 404,
  "error": "Resource not found",
  "message": "Resource not found. id 99",
  "path": "/gestores/99"
}
```

---

## Frontend

Os arquivos do frontend ficam em `src/main/resources/static/` e são servidos diretamente pelo Spring Boot — sem configuração adicional de CORS necessária.

### Interface

- **Formulário** para criação e edição de gestores (nome e e-mail)
- **Tabela** com listagem completa: ID, Nome, E-mail e botões de ação por linha
- **Modo de edição**: ao clicar em "Editar", o formulário é preenchido com os dados da linha selecionada, com opção de cancelar
- **Confirmação** antes de excluir um registro
- **Mensagens de feedback** (sucesso em verde / erro em vermelho) com auto-ocultação após 4 segundos
- Layout responsivo com rolagem horizontal na tabela em telas menores

### Comunicação com a API

Toda a comunicação é feita via `fetch()` nativo:

| Operação | Método | Rota |
|----------|--------|------|
| Listar | `GET` | `/gestores` |
| Criar | `POST` | `/gestores` |
| Atualizar | `PUT` | `/gestores/{id}` |
| Excluir | `DELETE` | `/gestores/{id}` |

**Segurança:** dados renderizados na tabela passam por `escapar()`, que utiliza `textContent` para prevenir ataques XSS.

---

## Banco de Dados

### Perfil de desenvolvimento (padrão — `test`)

| Configuração | Valor |
|-------------|-------|
| Driver | `org.h2.Driver` |
| URL | `jdbc:h2:file:./testdb` |
| Usuário | `sa` |
| Senha | *(vazia)* |
| DDL | `update` |
| Console H2 | `http://localhost:8080/h2-console` |

O banco é armazenado em arquivo (`testdb.mv.db`), garantindo persistência dos dados entre reinicializações.

### Perfil de produção (`prod`)

Configurado via variável de ambiente `DATABASE_URL`, apontando para uma instância PostgreSQL.

---

## Perfis de Ambiente

O perfil ativo é definido pela variável de ambiente `APP_PROFILE` (padrão: `test`).

| Perfil | Banco | SQL Log | Console H2 |
|--------|-------|---------|-----------|
| `test` | H2 (arquivo) | Habilitado | Habilitado |
| `prod` | PostgreSQL (env) | Desabilitado | Desabilitado |

Para ativar o perfil de produção:

```bash
APP_PROFILE=prod mvn spring-boot:run
```

---

## Como Executar

**Pré-requisitos:** Java 21 e Maven instalados.

```bash
# Clonar o repositório
git clone https://github.com/marcosvors/gestores-crud.git
cd gestores-crud

# Executar com Maven
mvn spring-boot:run

# Ou gerar o JAR e executar
mvn clean package
java -jar target/gestores-crud-0.0.1-SNAPSHOT.jar
```

Após iniciar, acesse:

| Recurso | URL |
|---------|-----|
| Frontend | `http://localhost:8080` |
| API REST | `http://localhost:8080/gestores` |
| Console H2 | `http://localhost:8080/h2-console` |

---

## Endpoints da API

```
GET    /gestores          → 200 OK  — lista todos os gestores
GET    /gestores/{id}     → 200 OK  — busca por ID
                          → 404 Not Found
POST   /gestores          → 201 Created + Location header
PUT    /gestores/{id}     → 200 OK  — atualiza gestor
                          → 404 Not Found
DELETE /gestores/{id}     → 204 No Content
                          → 404 Not Found
```

---

## Exemplos de Requisição

**Criar gestor:**
```bash
curl -X POST http://localhost:8080/gestores \
  -H "Content-Type: application/json" \
  -d '{"nome": "Ana Silva", "email": "ana@empresa.com"}'
```

**Listar todos:**
```bash
curl http://localhost:8080/gestores
```

**Atualizar:**
```bash
curl -X PUT http://localhost:8080/gestores/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "Ana Costa", "email": "ana.costa@empresa.com"}'
```

**Excluir:**
```bash
curl -X DELETE http://localhost:8080/gestores/1
```

---

Desenvolvido por [marcosvors](https://github.com/marcosvors)
