# Tarefas — LogSentinel
*Gerado em: 04/09/2026*
*Total estimado: ~45 horas*

## Bloco 1: Setup e Configuração
- [ ] **T01** — Criar repositório e estrutura de pastas do projeto Spring Boot (via Spring Initializr, Maven) `[~1h]`
  - Critério: projeto gerado com a estrutura de pastas conforme o plano técnico, versionado no Git
- [ ] **T02** — Configurar dependências no `pom.xml` (`spring-boot-starter`, `spring-boot-starter-jdbc`, driver `sqlite-jdbc`, módulo web mínimo para `RestTemplate` — sem `spring-boot-starter-web` completo) `[~1h]`
  - Critério: projeto compila e roda localmente sem erros, sem subir servidor Tomcat
- [ ] **T03** — Criar `application.properties` inicial e classe `@ConfigurationProperties` para thresholds, palavras-chave e URL do webhook do Slack `[~2h]`
  - Critério: alterar um valor no `application.properties` e reiniciar muda o comportamento da aplicação, sem tocar em código

## Bloco 2: Persistência (SQLite + JdbcTemplate)
- [ ] **T04** — Criar schema SQLite (tabelas de histórico de anomalias e de posição do arquivo) com inicialização automática ao subir a aplicação `[~2h]`
  - Critério: ao rodar a aplicação pela primeira vez, o arquivo `.db` e as tabelas são criados automaticamente
- [ ] **T05** — Implementar repositório de histórico (`JdbcTemplate`) para inserir e consultar anomalias detectadas `[~3h]`
  - Critério: é possível inserir uma anomalia de teste e consultá-la de volta via SQL
- [ ] **T06** — Implementar armazenamento de posição do arquivo (offset) via `JdbcTemplate` `[~2h]`
  - Critério: salvar um offset, reiniciar a aplicação e recuperar o mesmo valor salvo

## Bloco 3: Log Watcher
- [ ] **T07** — Implementar leitura do arquivo de log a partir do offset salvo, usando `RandomAccessFile` `[~3h]`
  - Critério: reiniciar a aplicação com um offset salvo lê apenas as linhas novas, sem reprocessar as antigas
- [ ] **T08** — Implementar `WatchService` para detectar novas linhas no arquivo em tempo real `[~3h]`
  - Critério: uma linha escrita no arquivo de teste é detectada e processada em menos de 3 segundos
- [ ] **T09** — Implementar detecção de rotação/truncamento (tamanho do arquivo encolheu ou o arquivo foi trocado) e reabertura automática `[~3h]`
  - Critério: truncar o arquivo de teste manualmente faz o sistema continuar monitorando sem travar, a partir do novo conteúdo

## Bloco 4: Anomaly Detector
- [ ] **T10** — Implementar regra de detecção por palavra-chave (lista configurável, busca case-insensitive) `[~2h]`
  - Critério: uma linha contendo qualquer palavra-chave da lista gera um evento de anomalia
- [ ] **T11** — Implementar regra de força bruta (5+ tentativas de login em até 60 segundos, janela deslizante) `[~3h]`
  - Critério: simular 5 tentativas em 60s gera o evento; 4 tentativas não geram
- [ ] **T12** — Implementar regra de cascata (7+ erros em até 30 segundos, janela deslizante) `[~3h]`
  - Critério: simular 7 erros em 30s gera o evento; 6 erros não geram

## Bloco 5: Alertas
- [ ] **T13** — Implementar consolidador de alertas (agrupar gatilhos simultâneos do mesmo conjunto de eventos em um único alerta) `[~3h]`
  - Critério: um cenário que ativa mais de uma regra ao mesmo tempo gera apenas um alerta consolidado
- [ ] **T14** — Implementar envio ao Slack via `RestTemplate` (montar payload e disparar o Incoming Webhook) `[~2h]`
  - Critério: um alerta de teste chega de forma legível no canal do Slack configurado
- [ ] **T15** — Implementar retry com backoff em caso de falha no envio, registrando cada tentativa no histórico `[~3h]`
  - Critério: simular uma falha de rede gera tentativas subsequentes, todas registradas no histórico
- [ ] **T16** — Implementar `@Async` na gravação do histórico, desacoplando do envio do alerta `[~2h]`
  - Critério: o alerta é enviado mesmo que a gravação do histórico esteja lenta/atrasada

## Bloco 6: Finalização
- [ ] **T17** — Integrar todos os componentes no fluxo principal (Watcher → Detector → Consolidator → Notifier / History) `[~3h]`
  - Critério: uma linha de log real, escrita no arquivo monitorado, percorre o fluxo completo até o alerta no Slack
- [ ] **T18** — Testes manuais contra os critérios de aceitação da spec (cada tipo de anomalia, rotação, falha do Slack, reinício) `[~2h]`
  - Critério: todos os critérios de sucesso listados na spec são verificados manualmente e passam
- [ ] **T19** — Medir a latência ponta a ponta (evento no log → alerta no Slack) e validar a meta de < 3s `[~1h]`
  - Critério: latência medida em pelo menos 5 execuções fica consistentemente abaixo de 3 segundos
- [ ] **T20** — Atualizar README com instruções de uso, configuração e como rodar o projeto `[~30min]`
- [ ] **T21** — `/converge` — validar a implementação contra a spec original `[~30min]`
