# acad-srv-auth

Microsserviço **acadêmico** responsável por autenticar usuários e emitir tokens JWT assinados (RS256), permitindo que os demais microsserviços do ecossistema validem esses tokens de forma independente, apenas com a chave pública — sem nunca ter acesso à chave privada de assinatura.

Faz parte de um projeto de estudo com foco em arquitetura de microsserviços, Clean/Hexagonal Architecture e boas práticas de segurança com Spring Boot 4 e Java 21.

## Documentação interativa (Swagger)

A API é documentada via OpenAPI/Swagger e pode ser explorada diretamente pelo navegador:

🔗 **Swagger UI (produção):** [https://acad-srv-auth-296926616938.europe-west1.run.app/swagger-ui.html](https://acad-srv-auth-296926616938.europe-west1.run.app/swagger-ui.html)

📄 **OpenAPI JSON:** [https://acad-srv-auth-296926616938.europe-west1.run.app/v3/api-docs](https://acad-srv-auth-296926616938.europe-west1.run.app/v3/api-docs)

Em ambiente local, a mesma documentação fica disponível em `http://localhost:8081/swagger-ui.html`.

## Tecnologias

- Java 21
- Spring Boot 4.1.2
- Spring Security
- Spring Security OAuth2 JOSE
- JWT com assinatura RSA/RS256
- Maven

## Objetivo

Centralizar a autenticação e a emissão de tokens de acesso para outros microsserviços. Os serviços consumidores poderão validar os tokens usando a chave pública RSA, sem compartilhar a chave privada do serviço de autenticação.

## Arquitetura

O projeto utiliza **arquitetura hexagonal (Ports and Adapters)**, isolando as regras de negócio de detalhes de framework e infraestrutura. Essa escolha traz benefícios concretos para um serviço de autenticação central:

- **Independência de infraestrutura:** o caso de uso `AuthenticateUseCase` não conhece Spring Security, JWT ou HTTP — apenas contratos (`CredentialProviderPort`, `TokenProviderPort`). Trocar o provedor de credenciais (ex.: de arquivo YAML para um banco de dados ou LDAP) ou o algoritmo de assinatura do token não exige alterar a regra de negócio.
- **Testabilidade:** a camada de aplicação pode ser testada isoladamente, com *mocks* das portas de saída, sem subir contexto Spring ou gerar chaves reais.
- **Segurança por padrão:** tokens são assinados com **RSA/RS256** (par de chaves assimétrico). Apenas este serviço guarda a chave privada; os demais microsserviços validam o token com a chave pública, sem nunca precisarem confiar segredos compartilhados.
- **Stateless por design:** `SecurityConfig` desabilita sessão (`STATELESS`), reforçando que o serviço não mantém estado entre requisições — essencial para escalar horizontalmente em ambientes como Cloud Run.
- **Documentação viva:** a API é autodescritiva via OpenAPI/Swagger, facilitando a integração dos times consumidores sem depender de documentação manual desatualizada.

```text
br.com.acad.auth
├── application
│   ├── port
│   │   ├── in
│   │   │   └── AuthenticateUseCase.java
│   │   └── out
│   │       ├── CredentialProviderPort.java
│   │       └── TokenProviderPort.java
│   └── service
│       └── AuthenticateService.java
├── domain
│   └── model
│       └── AuthenticationResult.java
└── infrastructure
    ├── adapter
    │   ├── in
    │   │   └── web
    │   │       ├── AuthController.java
    │   │       └── dto
    │   │           ├── AuthRequest.java
    │   │           └── AuthResponse.java
    │   └── out
    │       ├── config
    │       │   └── YamlCredentialProvider.java
    │       └── jwt
    │           └── JwtTokenProvider.java
    └── config
        ├── AuthProperties.java
        ├── JwtProperties.java
        ├── RsaKeyConfig.java
        ├── JwtEncoderConfig.java
        └── SecurityConfig.java
```

A estrutura acima representa a organização esperada. Ajuste os diretórios conforme a localização real das classes no projeto.

### Responsabilidades

- **Domain:** contém os modelos centrais da aplicação.
- **Application:** define os casos de uso e as portas de entrada e saída.
- **Infrastructure:** implementa o controller REST, a validação das credenciais, a geração de JWTs e as configurações técnicas.

## Endpoint de autenticação

### Gerar token de acesso

| Propriedade | Valor |
|---|---|
| Método HTTP | `POST` |
| Endpoint | `/api/authenticate/token` |
| URL local | `http://localhost:8081/api/authenticate/token` |
| Content-Type | `application/json` |

### Requisição

```json
{
  "username": "admin",
  "password": "admin"
}
```

As credenciais acima são apenas um exemplo para desenvolvimento local. Os valores efetivos dependem da configuração da aplicação e das variáveis de ambiente.

### Resposta esperada

```json
{
  "accessToken": "<jwt-gerado>",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

- `accessToken`: JWT assinado pelo serviço.
- `tokenType`: tipo de token utilizado nas requisições protegidas.
- `expiresIn`: tempo de validade em segundos, conforme a configuração do JWT.

## Fluxo de autenticação

1. O cliente envia as credenciais para `POST /api/authenticate/token`.
2. `AuthController` recebe e valida os dados da requisição.
3. `AuthenticateUseCase` encaminha a operação para a camada de aplicação.
4. `AuthenticateService` verifica as credenciais por meio de `CredentialProviderPort`.
5. Com credenciais válidas, solicita a geração do token por `TokenProviderPort`.
6. `JwtTokenProvider` gera e assina o JWT utilizando RS256.
7. O controller devolve a resposta ao cliente.

## Configuração

As propriedades são definidas no `application.yml` e podem receber valores de variáveis de ambiente.

Exemplo:

```yaml
server:
  port: 8081

spring:
  application:
    name: auth-service

auth:
  credentials:
    username: ${AUTH_USERNAME:admin}
    password: ${AUTH_PASSWORD:admin}
  jwt:
    issuer: ${JWT_ISSUER:auth-service}
    expiration: ${JWT_EXPIRATION:900}
    private-key: ${JWT_PRIVATE_KEY}
    public-key: ${JWT_PUBLIC_KEY}
```

### Variáveis de ambiente

| Variável | Finalidade |
|---|---|
| `AUTH_USERNAME` | Usuário de desenvolvimento |
| `AUTH_PASSWORD` | Senha de desenvolvimento |
| `JWT_ISSUER` | Identificador do emissor |
| `JWT_EXPIRATION` | Validade do token em segundos |
| `JWT_PRIVATE_KEY` | Conteúdo Base64 da chave privada RSA |
| `JWT_PUBLIC_KEY` | Conteúdo Base64 da chave pública RSA |

As variáveis das chaves devem corresponder ao formato esperado pela implementação: conteúdo Base64 das chaves codificadas, sem os delimitadores PEM e sem quebras de linha.

O arquivo `.env` não é carregado automaticamente pelo Spring Boot. No desenvolvimento local, as variáveis devem ser exportadas pelo terminal ou carregadas por outro mecanismo de configuração.

## Executar localmente

### Pré-requisitos

- JDK 21
- Git
- Maven Wrapper

### Compilar

Linux, macOS ou Git Bash:

```bash
./mvnw clean compile
```

Windows:

```powershell
.\mvnw.cmd clean compile
```

### Executar

Linux, macOS ou Git Bash, após disponibilizar as variáveis de ambiente:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Validação de tokens por outros microsserviços

Os serviços consumidores devem validar os JWTs recebidos, incluindo:

- Assinatura com a chave pública correspondente.
- Emissor (`iss`).
- Expiração (`exp`).
- Regras de autorização necessárias para cada endpoint.

A chave privada deve permanecer exclusiva do serviço de autenticação. Para ambientes com vários consumidores, pode-se disponibilizar a chave pública por um mecanismo como JWKS.

## Segurança

- Não versione arquivos `.env` ou chaves privadas.
- Não registre senhas nem tokens completos nos logs.
- Utilize HTTPS em ambientes reais.
- Utilize um gerenciador de segredos no deploy.
- Não mantenha credenciais padrão em produção.
- Trate credenciais inválidas sem expor detalhes internos.
- Planeje a rotação das chaves de assinatura.

## Testes

Execute os testes automatizados com:

```bash
./mvnw test
```

Os cenários recomendados incluem autenticação válida e inválida, assinatura e expiração de JWTs, validação pela chave pública e regras de acesso aos endpoints.

## Status

Projeto em desenvolvimento. A documentação deve acompanhar os endpoints, DTOs, configurações e testes efetivamente implementados.
