# 🛡️ LogSentinel

**Desenvolvido por:** Fernando Maiorani Costa

`JAVA 25 LTS` · `SPRING BOOT` · `SQLITE` · `SLACK`

O **LogSentinel** é um sistema de monitoramento contínuo em background, projetado para vigiar arquivos de log locais e detectar anomalias instantaneamente. Focado em infraestrutura e APIs de produção, ele identifica erros críticos, tentativas de força bruta e cascatas de falhas, disparando alertas proativos no Slack em menos de 3 segundos.

## 📋 Índice

- [Objetivo do Projeto](#-objetivo-do-projeto)
- [Funcionalidades (MVP)](#-funcionalidades-mvp)
- [Performance](#-performance)
- [Stack Tecnológica](#-stack-tecnológica)
- [Arquitetura](#-arquitetura)
- [Como Rodar](#-como-rodar)
- [Configuração](#-configuração)
- [Limitações Conhecidas (v1)](#-limitações-conhecidas-v1)
- [Roadmap / Pós-MVP](#-roadmap--pós-mvp)

## 🎯 Objetivo do Projeto

Sistemas em produção geram um volume de logs impossível de revisar manualmente. O LogSentinel resolve isso monitorando um arquivo de log em tempo real, aplicando regras de detecção configuráveis, e enviando alertas consolidados para o Slack assim que uma anomalia é identificada — sem intervenção manual.

## ✅ Funcionalidades (MVP)

- **Monitoramento em tempo real** de um arquivo de log local, via `WatchService` (Java NIO)
- **Retomada de leitura**: em reinícios, o sistema continua exatamente de onde parou (offset persistido em banco)
- **Detecção de rotação/truncamento**: se o arquivo encolher (rotacionado/truncado), o sistema reseta automaticamente sem travar
- **Três regras de detecção**, configuráveis via `application.properties`:
  - **Palavra-chave**: `ERROR`, `FATAL`, `CRITICAL`, `PANIC`, `EXCEPTION`, `Segmentation fault`, `Out of memory`, `connection refused`, `timeout`, `unauthorized`, `denied` (case-insensitive)
  - **Força bruta**: 5+ tentativas de acesso não autorizado em 60 segundos (janela deslizante)
  - **Cascata de erros**: 7+ erros em 30 segundos (janela deslizante)
- **Consolidação de alertas**: se múltiplas regras disparam na mesma linha, gera **um único** alerta, não vários
- **Envio de alertas ao Slack** via Incoming Webhook, com **retry automático** (até 3 tentativas, com backoff exponencial) em caso de falha
- **Histórico persistente**: toda anomalia (enviada ou não) é registrada em banco SQLite, de forma **assíncrona** (não bloqueia o envio do alerta)

## ⚡ Performance

A latência ponta a ponta (do momento em que a linha é escrita no log até o alerta chegar no Slack) foi medida em **menos de 1 segundo**, bem dentro da meta de 3 segundos definida na especificação do projeto.

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 25 (LTS) |
| Framework | Spring Boot 4.1.1 |
| Persistência | SQLite via Spring JdbcTemplate |
| Monitoramento de arquivo | `java.nio.file.WatchService` + `RandomAccessFile` |
| Notificações | Slack Incoming Webhooks via `RestTemplate` |
| Build | Maven |

## 🏗️ Arquitetura

com.logsentinel
├── config/ → configurações do sistema (properties, beans, orquestração de testes)
├── history/ → persistência: histórico de anomalias e posição do arquivo (offset)
├── watcher/ → leitura e monitoramento do arquivo de log em tempo real
├── detector/ → regras de detecção de anomalias (keyword, força bruta, cascata)
└── alert/ → consolidação e envio de alertas (Slack)

**Fluxo de execução:** arquivo de log muda → `FileWatcherService` (WatchService) detecta a mudança → `LogWatcher` lê as linhas novas (a partir do offset salvo) → `AlertConsolidator` verifica as 3 regras de detecção → se houver anomalia, `SlackNotifier` envia o alerta (com retry) e salva no histórico (async). Se não houver anomalia, a linha é apenas descartada.

## 🚀 Como Rodar

**Pré-requisitos:** Java 25 (JDK) e Maven (ou use o wrapper incluso, `./mvnw`).

**Passos:** clone o repositório com `git clone https://github.com/Maiorani195/LogSentinel.git`, configure a variável de ambiente do Slack (veja a seção Configuração abaixo), e rode com `./mvnw spring-boot:run`. O sistema começa a monitorar o arquivo de log configurado automaticamente.

## ⚙️ Configuração

Todas as configurações ficam em `src/main/resources/application.properties`:

```properties
# Palavras-chave monitoradas
logsentinel.keywords=ERROR,FATAL,CRITICAL,PANIC,EXCEPTION,Segmentation fault,Out of memory,connection refused,timeout,unauthorized,denied

# Força bruta: N tentativas em M segundos
logsentinel.brute-force.attempts=5
logsentinel.brute-force.window-seconds=60

# Cascata: N erros em M segundos
logsentinel.cascade.errors=7
logsentinel.cascade.window-seconds=30

# Webhook do Slack (via variável de ambiente, nunca hardcoded)
logsentinel.slack.webhook-url=${SLACK_WEBHOOK_URL:}
```

**⚠️ Configurando o Webhook do Slack:** por segurança, a URL do webhook nunca fica no código-fonte — ela é lida de uma variável de ambiente. Crie um app no Slack em [api.slack.com/apps](https://api.slack.com/apps), ative Incoming Webhooks, e gere a URL para o canal desejado. Depois, defina a variável de ambiente `SLACK_WEBHOOK_URL` com essa URL — no IntelliJ, isso fica em Run → Edit Configurations → Environment variables; no terminal, use `export SLACK_WEBHOOK_URL=https://hooks.slack.com/services/...`.

## ⚠️ Limitações Conhecidas (v1)

**Retomada após reinício:** se o log for editado enquanto o programa está desligado, essas linhas só são processadas na próxima notificação de mudança do arquivo — não há uma leitura proativa ao iniciar. **Rotação de log:** em caso de truncamento, o sistema reseta a leitura para o início do arquivo novo, sem tentar recuperar o conteúdo perdido do arquivo anterior. **Fonte única:** monitora apenas um arquivo de log local por instância.

## 🗺️ Roadmap / Pós-MVP

Suporte a múltiplas fontes de log simultâneas, integração com Discord como canal alternativo de alerta, e um dashboard web para visualização do histórico de anomalias.
