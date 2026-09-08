
# 🛡️ LogSentinel
> **Desenvolvido por:** Fernando Maiorani

![Java](https://img.shields.io/badge/Java_25_LTS-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)

O **LogSentinel** é um sistema de monitoramento contínuo em background projetado para vigiar arquivos de log locais e detectar anomalias instantaneamente. Focado em infraestrutura e APIs de produção, ele identifica erros críticos, tentativas de força bruta e cascatas de falhas, disparando alertas proativos no Slack em menos de 3 segundos.

---

## 📑 Índice Interativo
- [Objetivo do Projeto](#-objetivo-do-projeto)
- [Funcionalidades (MVP)](#-funcionalidades-mvp)
- [Stack Tecnológica](#-stack-tecnológica)
- [Arquitetura e Fluxo](#-arquitetura-e-fluxo)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Como Executar](#-como-executar)

---

## 🎯 Objetivo do Projeto
Servidores Linux e APIs geram volumes massivos de logs que são impossíveis de serem revisados manualmente. O LogSentinel resolve esse problema eliminando a necessidade de vigilância manual. Ele atua como uma sentinela automatizada, garantindo que administradores de infraestrutura e desenvolvedores back-end sejam notificados no exato momento em que uma anomalia ocorre, sem ruídos desnecessários.

---

## ✨ Funcionalidades (MVP)

- [x] **Monitoramento Contínuo:** Acompanha novas linhas escritas em arquivos de log de texto puro sem necessidade de reiniciar.
- [x] **Detecção por Palavras-Chave:** Identifica termos críticos configuráveis (ex: `ERROR`, `FATAL`, `Segmentation fault`) via busca *case-insensitive*.
- [x] **Alarme de Força Bruta:** Detecta 5 ou mais tentativas de ações suspeitas em uma janela de 60 segundos.
- [x] **Detecção de Cascata:** Identifica 7 ou mais erros ocorrendo em sequência em uma janela de 30 segundos.
- [x] **Alertas de Baixa Latência:** Dispara mensagens formatadas e com o trecho relevante do log para o Slack em **< 3 segundos**.
- [x] **Consolidação Inteligente:** Agrupa múltiplos gatilhos simultâneos em um único alerta, evitando *spam* no canal.
- [x] **Persistência Assíncrona:** Salva o histórico de anomalias localmente em SQLite sem bloquear o fluxo de alertas.
- [x] **Retomada Inteligente (Offset):** Em caso de reinício, retoma a leitura de onde parou, evitando duplicação de alertas.
- [x] **Resiliência:** Tratamento nativo para rotação/truncamento de logs e sistema de *retry* com *backoff* para falhas de rede na API do Slack.

---

## 💻 Stack Tecnológica

* **Java 25 (LTS) & Spring Boot:** Base do projeto. A escolha do Spring Boot agiliza a injeção de dependências e facilita o uso de execuções assíncronas (`@Async`) e propriedades mapeadas.
* **WatchService (java.nio.file):** Utilizado para reagir a eventos do sistema operacional, eliminando a latência do modelo tradicional de *polling*.
* **SQLite & Spring JdbcTemplate:** Persistência leve e livre de configurações complexas de dialeto, ideal para gravar o offset e o histórico de forma atômica e segura.
* **RestTemplate (Spring):** Cliente HTTP nativo do ecossistema utilizado para o disparo do *Incoming Webhook* do Slack.

---

## ⚙️ Arquitetura e Fluxo

O design do sistema prioriza a latência. As tarefas primárias (detectar e alertar) são desacopladas das tarefas secundárias (persistir no banco) usando filas internas em memória.

1. **Log Watcher:** Detecta a nova linha e repassa ao sistema.
2. **Anomaly Detector:** Aplica as regras de negócio (palavras-chave, cascata, força bruta).
3. **Alert Consolidator:** Reúne gatilhos acionados ao mesmo tempo.
4. **Slack Notifier & History Store:** O fluxo se divide. O alerta sobe para o Slack via HTTP, enquanto a persistência em SQLite ocorre em thread separada de forma não bloqueante.

---

## 📂 Estrutura do Projeto

```text
logsentinel/
├── src/
│   └── main/
│       ├── java/com/logsentinel/
│       │   ├── watcher/         # Log Watcher + Position Store (@Component)
│       │   ├── detector/        # Anomaly Detector (regras de negócio)
│       │   ├── alert/           # Alert Consolidator + Slack Notifier (@Async)
│       │   ├── history/         # History Store (JdbcTemplate + SQLite)
│       │   ├── config/          # @ConfigurationProperties (thresholds, keywords)
│       │   └── LogSentinelApplication.java
│       └── resources/
│           └── application.properties
├── docs/
│   ├── spec.md
│   ├── plan.md
│   └── tasks.md
├── pom.xml
└── README.md
