# ConstruGestor — Backend

API REST de gestão de obras de construção civil. Serve o SPA React do repositório frontend
(**repositório separado**, não versionar junto).

## Repositórios

| Parte | Caminho local | Remote |
|---|---|---|
| Backend (este) | `C:/Users/Usuario/Documents/Coisas Vitor/coding/EstagioBackEndIntellij` | `github.com/VitorsosterF/EstagioBackEnd` |
| Frontend | `C:/Users/Usuario/Documents/Coisas Vitor/Faculdade/professores/luiz/EstagioFrontEnd` | `github.com/VitorsosterF/EstagioFrontEnd` |

Existe um segundo clone deste repo em `coding/EstagioBackEnd` — o clone **de trabalho é este (`...Intellij`)**.
O frontend tem seu próprio `CLAUDE.md` com o contrato da API na visão do cliente.

⚠️ Os dois clones locais podem ficar atrasados em relação ao `origin/main` sem aviso — já aconteceu uma vez
(alguém empurrou direto pro GitHub por fora das duas pastas). Rode `git fetch && git log HEAD..origin/main`
antes de assumir que o código local é o mais recente.

## Stack

- Spring Boot 4.0.5, Java 17, Maven (`./mvnw`).
- Spring Web + Data JPA + Security, PostgreSQL, JWT (`io.jsonwebtoken`), Jackson 3 (`tools.jackson`) para o
  `ObjectMapper` manual do multipart, mas anotações `com.fasterxml.jackson.annotation.*` continuam sendo usadas
  nas entidades (`@JsonIgnore`, `@JsonIgnoreProperties`) — não confundir os dois pacotes.
- Roda em `http://localhost:8080`. Banco: PostgreSQL `construgestor` em `localhost:5432`.
- `spring.datasource.password` e `jwt.secret` vêm de `${DB_PASSWORD:...}` / `${JWT_SECRET:...}` — funcionam
  sem nada configurado (fallback = valor atual), mas podem ser sobrescritos por variável de ambiente.
- `./mvnw spring-boot:run` · `./mvnw test`. `ddl-auto=update` (schema gerado pelo JPA a partir das entidades).

## Arquitetura

Pacote único `com.backendestagio.Obras`, camadas por pasta: `controller/ → service/ → repository/ → model/`,
mais `config/` e `dto/`.

- `config/SecurityConfig` — stateless; libera só `POST /auth/login` e `GET /uploads/**`, resto exige
  autenticação. **Sem autorização por role no Spring Security** — quem checa perfil é o próprio
  `NotificacaoController` (busca o `Usuario` pelo e-mail do `Authentication` e compara `perfil`).
- `config/JwtFilter`/`JwtService` — HS256, subject = e-mail, expiração 24h (`jwt.expiration`).
- `service/FileStorageService` — grava imagens em `uploads/`, servidas em `/uploads/**` (`WebConfig`).
- `dto/` — `ObraRequest`, `UsuarioRequest`, `NotificacaoManualRequest`: DTOs de entrada só para os campos que o
  cliente deve mandar (ex.: `ObraRequest.clienteId` em vez do objeto `Obra` inteiro). As respostas continuam
  sendo as entidades JPA serializadas direto (sem DTO de saída).
- Cada controller tem `@CrossOrigin(origins = "http://localhost:5173")`.
- `config/ClienteResponsavelMigration` — `CommandLineRunner` que só roda uma vez (quando a coluna antiga
  `obras.cliente_responsavel` ainda existe) para migrar pra `cliente_id`. **Só funciona em banco vazio** — se
  já houver obras cadastradas, o `ddl-auto=update` falha tentando criar a coluna `NOT NULL` sem valor
  default, antes mesmo do `CommandLineRunner` rodar. Se isso acontecer, a correção é popular `cliente_id`
  manualmente via SQL direto (backfill por nome + `ALTER COLUMN ... SET NOT NULL` + `DROP COLUMN
  cliente_responsavel`) **antes** de subir essa versão — não dá pra confiar na migração automática num banco
  com dados reais.

## Modelo de dados

- `Usuario { id, nome, sobrenome, email, senha (@JsonIgnore — nunca serializado), perfil (Admin|Cliente) }`.
- `Obra { id, nome, rua, numero, complemento, cliente (ManyToOne → Usuario, FK cliente_id), status, descricao,
  criadoEm, imagemUrl }`. **`cliente` é uma relação de verdade**, não mais uma string com o nome — renomear um
  usuário não quebra mais o vínculo com a obra.
- `Template { id, titulo, tipo (Email|WhatsApp), corpo, variaveis (derivado), criadoEm,
  padraoNotificacaoStatus }`. Só um template por vez pode ter `padraoNotificacaoStatus = true`
  (`TemplateService` desmarca o anterior automaticamente ao marcar um novo).
- `Notificacao { id, mensagem (TEXT, variáveis já resolvidas), dataCriacao, lida, obra (ManyToOne), template
  (ManyToOne) }`. Sem campo de "origem" (automática/manual) nem de "canal" próprio — o canal é sempre
  `notificacao.template.tipo`.
- `Usuario`, `Obra` e `Template` têm `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` —
  necessário porque aparecem como **relação lazy** em outra entidade (`Obra.cliente`, `Notificacao.obra`,
  `Notificacao.template`) e, sem isso, o Jackson serializa o proxy do Hibernate e vaza esses dois campos
  internos no JSON.

## Endpoints

| Recurso | Rotas |
|---|---|
| `/auth` | `POST /login` `{email,senha}` → `{token, id, nome, sobrenome, perfil}` (401 vazio se inválido) |
| `/usuarios` | `GET` (ordenado por id) · `POST` (409 "Email já cadastrado.") · `PUT /{id}` · `DELETE /{id}` (409 se `existsByClienteId`) — corpo é `UsuarioRequest` |
| `/obras` | `GET` · `GET /{id}` · `POST` (multipart: parte `obra` = `ObraRequest` JSON + `imagem` opcional) · `PUT /{id}` (mesmo multipart) · `DELETE /{id}` (409 se tiver notificação associada) |
| `/templates` | `GET` · `GET /{id}` · `POST` · `PUT /{id}` · `DELETE /{id}` — corpo é a entidade `Template` direto (sem DTO); `variaveis` é derivado do `corpo` no `TemplateService` |
| `/notificacoes` | `GET` (**admin only**, 403 senão) · `GET /minhas` (qualquer logado, só as da obra onde ele é `cliente`) · `POST` `{obraId,templateId}` (admin only) · `PATCH /{id}/lida` (dono ou admin) · `DELETE /{id}` (dono ou admin) |

Não existe endpoint de "marcar todas como lidas" — o front faz isso chamando `PATCH /{id}/lida` em loop para
cada notificação não lida.

## Regra de notificação

- **Automática**: `ObraService.atualizar` guarda o status anterior e, se mudou, chama
  `NotificacaoService.criarNotificacaoAutomatica(obra)`, que usa o template com
  `padraoNotificacaoStatus = true`. Sem template marcado como padrão, só loga um warning e não notifica
  ninguém — não falha a atualização da obra.
- **Manual**: `POST /notificacoes` pelo admin, escolhendo `obraId` + `templateId` livremente (não precisa ser
  o template padrão).
- Resolução de variáveis (`NotificacaoService.montarMensagem`): `{nome_cliente}`, `{obra}`, `{status}`,
  `{endereco}`. Placeholder desconhecido fica literal.
- Autorização de leitura/exclusão: `garantirPermissao` — admin pode qualquer notificação; cliente só a sua
  própria (`notificacao.obra.cliente.id == usuario.id`), senão `SecurityException` → 403.

## Gaps conhecidos (backlog)

1. Sem autorização por role no Spring Security — cada controller que precisa restringir por perfil faz a
   checagem na mão (só o `NotificacaoController` faz isso hoje). Os demais endpoints (`/obras`, `/usuarios`,
   `/templates`) não têm nenhuma restrição além de "estar autenticado".
2. Não tem endpoint de "marcar todas como lidas" nem de resolver variáveis desconhecidas de forma mais
   explícita (hoje fica literal, silenciosamente).
3. Segredos (`jwt.secret`, senha do banco) ainda têm o valor real como fallback no `application.properties`
   versionado — o `${VAR:fallback}` resolve "funciona sem configurar nada", não "segredo fora do git".
4. A migração `ClienteResponsavelMigration` não é segura para rodar num banco com dados — ver nota em
   "Arquitetura" acima.
