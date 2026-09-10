# Plano Técnico — LogSentinel
*Gerado em: 04/09/2026*

## Stack Recomendada

| Camada | Tecnologia | Alternativa Considerada | Motivo da Escolha |
|---|---|---|---|
| Linguagem/Runtime | Java 25 (LTS) + Spring Boot | Java "puro" (sem framework) | Você já sinalizou preferência por Java, POO, e tem o JDK 25 instalado. Optamos por Spring Boot em vez de Java puro porque é o framework mais usado no mercado para backend Java — pratica-se dependency injection, `@Async`, `@Scheduled` e `@ConfigurationProperties`, habilidades diretamente relevantes pra vagas de estágio/júnior. O ganho de aprendizado compensa a curva extra de configuração inicial. |
| Monitoramento de arquivo | `java.nio.file.WatchService` + `RandomAccessFile`, dentro de um `@Component` gerenciado pelo Spring | Polling manual (verificar o arquivo a cada X ms) | O Spring não tem um "file watcher" pronto — continuamos usando o `WatchService` nativo do Java (reage a eventos do SO em vez de ficar em loop), só que a inicialização/ciclo de vida da thread de monitoramento passa a ser gerenciada pelo Spring. |
| Persistência (histórico + offset) | SQLite via Spring `JdbcTemplate` | Spring Data JPA / Hibernate | `JdbcTemplate` é mais simples de aprender que o JPA completo e evita a dor de configurar o dialeto do SQLite no Hibernate (que não é totalmente padrão). Spring Data JPA fica como evolução natural pós-MVP, se quiser praticar depois. |
| Configuração externa | `application.properties` do Spring Boot + classe `@ConfigurationProperties` | `java.util.Properties` manual | O Spring já injeta os valores de configuração automaticamente e com checagem de tipo — menos código do que ler um `.properties` na mão, e é o padrão real usado em projetos Spring. |
| Alertas | Slack Incoming Webhook via `RestTemplate` do Spring | `HttpClient` nativo do Java | Já que o projeto é Spring, faz sentido usar o cliente HTTP do próprio ecossistema (`RestTemplate`), mantendo consistência em vez de misturar nativo + framework. |
| Frontend | N/A | — | O MVP é um serviço em background (sem interface visual) — a "interface" do usuário é o próprio alerta no Slack. |
| Deploy | Executável Spring Boot (`.jar`), **sem** `spring-boot-starter-web` completo | Docker | Como não expomos uma API web no MVP, evitamos subir um servidor Tomcat desnecessário — usamos só os módulos Spring que precisamos (core, jdbc, web-client), mantendo o serviço leve. |

## Arquitetura

O sistema é dividido em componentes desacoplados, conectados por filas internas (in-memory), para que o caminho crítico (detectar → alertar) nunca fique bloqueado por tarefas secundárias (persistir histórico, por exemplo).

```
┌─────────────────┐      ┌───────────────────┐      ┌────────────────┐
│   Config Loader  │─────▶│   Log Watcher      │─────▶│ Anomaly Detector│
│ (.properties)    │      │ (WatchService +    │      │ (regras:        │
│                   │      │  offset tracking)  │      │  keyword,       │
└─────────────────┘      └───────────────────┘      │  força bruta,   │
                                     │                  │  cascata)       │
                                     │                  └───────┬────────┘
                                     ▼                          │
                          ┌───────────────────┐                 ▼
                          │  Position Store    │        ┌────────────────┐
                          │  (offset no SQLite)│        │ Alert Consolidator│
                          └───────────────────┘        │ (agrupa gatilhos│
                                                        │  simultâneos)   │
                                                        └───────┬────────┘
                                                                │
                                        ┌───────────────────────┼───────────────────────┐
                                        ▼                                               ▼
                              ┌──────────────────┐                          ┌──────────────────────┐
                              │  Slack Notifier    │                         │  History Store         │
                              │  (HttpClient +      │                         │  (SQLite, assíncrono,  │
                              │   retry c/ backoff) │                         │   registra tentativas) │
                              └──────────────────┘                          └──────────────────────┘
```

**Fluxo:** o `Log Watcher` detecta novas linhas (ou rotação/truncamento) e as envia ao `Anomaly Detector`, que aplica as 3 regras. Anomalias detectadas vão simultaneamente para o `Alert Consolidator` (que agrupa gatilhos do mesmo evento em um único alerta) e, de forma assíncrona e não bloqueante, para o `History Store`. O `Slack Notifier` só depende do Consolidator — nunca espera o `History Store` terminar.

## Estrutura de Pastas

```
logsentinel/
├── src/
│   └── main/
│       ├── java/com/logsentinel/
│       │   ├── watcher/         # Log Watcher + Position Store (@Component)
│       │   ├── detector/        # Anomaly Detector (regras)
│       │   ├── alert/           # Alert Consolidator + Slack Notifier (@Async)
│       │   ├── history/         # History Store (JdbcTemplate + SQLite)
│       │   ├── config/          # @ConfigurationProperties (thresholds, keywords, slack)
│       │   └── LogSentinelApplication.java   # @SpringBootApplication
│       └── resources/
│           └── application.properties
├── docs/
│   ├── spec.md
│   ├── plan.md
│   └── tasks.md
├── pom.xml
└── README.md
```

## Decisões Técnicas Importantes

1. **Linguagem/Framework — Java 25 (LTS) + Spring Boot**: contexto — você já sinalizou preferência por Java, POO, e tem o JDK 25 instalado; além disso, você quer praticar Spring Boot deliberadamente, por ser o framework mais relevante pro mercado. → Escolhemos Spring Boot em vez de Java puro porque o ganho de prática (dependency injection, `@Async`, `@ConfigurationProperties`) tem retorno direto pra portfólio/entrevistas, mesmo custando uma curva de configuração inicial um pouco maior. Descartamos Python porque, além de não atender sua meta de prática, exigiria bibliotecas externas para cobrir o que Java+Spring já oferecem prontos.

2. **`WatchService` em vez de polling**: contexto — a meta de < 3s de latência é apertada. → Escolhemos `WatchService` porque é orientado a evento (o SO notifica a mudança), eliminando o atraso inerente de um loop de polling. Descartamos polling puro porque, para garantir baixa latência, exigiria um intervalo de verificação muito curto, desperdiçando CPU. O Spring gerencia o ciclo de vida do bean que inicia essa thread, mas a lógica de watch em si continua sendo Java puro.

3. **SQLite via `JdbcTemplate` (não JPA/Hibernate)**: contexto — precisamos persistir sem adicionar complexidade operacional, e você ainda está aprendendo Spring. → Escolhemos `JdbcTemplate` porque é a forma mais direta de usar SQLite dentro do Spring, sem lidar com as particularidades de configurar o dialeto do SQLite no Hibernate (que não é 100% padrão e gera dor de cabeça desnecessária num MVP). Descartamos Spring Data JPA para o MVP — fica como boa tarefa de evolução pós-MVP, quando você já estiver mais confortável com Spring.

4. **Arquitetura assíncrona via `@Async`**: contexto — a meta de < 3s não pode ser comprometida pela gravação em disco. → Escolhemos desacoplar o envio do alerta da gravação do histórico usando `@Async` do Spring (mais simples de configurar que gerenciar threads manualmente). Descartamos gravar de forma síncrona antes de alertar, porque isso colocaria a persistência no caminho crítico da latência.

5. **Retry com backoff no Slack**: contexto — falhas de rede/API são esperadas em qualquer integração externa. → Escolhemos um mecanismo de retry com espaçamento crescente entre tentativas (evita martelar a API do Slack em caso de instabilidade), registrando cada tentativa no histórico. Descartamos "tentar uma vez só e desistir" porque isso jogaria fora justamente os alertas mais críticos.

## Riscos Identificados

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| Perda de linhas durante rotação/truncamento do arquivo | Média | Médio | Já assumido como limitação conhecida na spec; detectar a troca rapidamente via `WatchService` minimiza a janela de perda |
| Não atingir a meta de < 3s sob rajada de logs | Média | Alto | Manter o caminho detecção→alerta sem I/O bloqueante; histórico e retries rodam em threads separadas |
| Rate limit ou instabilidade da API do Slack | Baixa/Média | Médio | Alertas consolidados reduzem volume de chamadas; retry com backoff evita martelar a API |
| Corrupção do offset ou do histórico em caso de encerramento abrupto (queda de energia, kill -9) | Baixa | Médio | Usar transações do SQLite (atômicas por padrão) para gravação do offset e do histórico |
| Escopo crescer além do MVP (comum em projeto solo de portfólio) | Média | Médio | Seguir rigorosamente as funcionalidades essenciais definidas na spec; tudo além vira tarefa pós-MVP explícita |
| Curva de aprendizado do Spring Boot atrasar o cronograma, se for a primeira vez usando o framework | Média | Médio | Tarefas do `/tasks` vão incluir explicação de cada conceito novo do Spring antes do código; começar pelo setup mínimo (sem web) reduz superfície de aprendizado inicial |

## Premissas do Plano

- A máquina onde o LogSentinel roda tem JVM (Java 25+) instalada
- O usuário já possui (ou vai criar) um Slack Incoming Webhook configurado manualmente antes de rodar o sistema — configurar isso está fora do escopo do software
- O volume de logs do MVP cabe em um único arquivo monitorado localmente, sem necessidade de arquitetura distribuída ou escalável horizontalmente
