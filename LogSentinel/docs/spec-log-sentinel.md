# Especificação — LogSentinel
*Gerado em: 04/09/2026*

## Objetivo
Monitorar continuamente um arquivo de log local, detectar automaticamente padrões de anomalia (erros críticos, força bruta e cascatas de falhas) e disparar alertas quase instantâneos, para que equipes de infraestrutura não precisem revisar logs manualmente.

## Problema que Resolve
Servidores Linux e APIs de produção geram um volume de logs impossível de ser revisado manualmente por uma pessoa. Erros críticos, tentativas de invasão e falhas em cascata podem passar despercebidos por horas ou dias até que causem impacto real, porque não há vigilância automática e imediata sobre esses arquivos.

## Usuários-Alvo

| Persona | Contexto de uso | Necessidade principal |
|---|---|---|
| Administrador de infraestrutura / SRE | Responsável por servidores Linux de produção, monitorando de forma reativa ou por amostragem | Ser avisado no momento em que algo crítico acontece, sem precisar vasculhar logs manualmente |
| Desenvolvedor backend | Mantém uma API em produção e precisa saber rapidamente quando algo quebra | Alertas rápidos e específicos sobre erros e padrões suspeitos, sem ruído |

## Funcionalidades

### Essenciais (MVP)
- [ ] **Monitoramento contínuo de arquivo de log local**: o sistema acompanha um único arquivo de log em texto puro (linhas soltas), detectando novas linhas conforme são escritas. Critério de aceitação: novas linhas adicionadas ao arquivo são lidas e processadas sem reiniciar o sistema.
- [ ] **Detecção de erro crítico por palavras-chave**: identificar linhas que contenham qualquer termo de uma lista configurável de palavras-chave (ex: `ERROR`, `FATAL`, `CRITICAL`, `PANIC`, `EXCEPTION`, `Segmentation fault`, `Out of memory`, `connection refused`, `timeout`, `unauthorized`, `denied`), buscando de forma case-insensitive (confirmado). Critério de aceitação: uma linha contendo qualquer palavra-chave da lista gera um evento de anomalia; a lista pode ser editada sem alterar código.
- [ ] **Detecção de força bruta**: identificar 5 ou mais tentativas de login (ou ação equivalente) dentro de uma janela de 60 segundos. Critério de aceitação: 5+ tentativas dentro de 60 segundos geram um evento de anomalia.
- [ ] **Detecção de cascata de erros**: identificar 7 ou mais erros ocorrendo em sequência, dentro de uma janela de 30 segundos. Critério de aceitação: 7+ erros dentro de 30 segundos geram um evento de anomalia.
- [ ] **Disparo de alerta quase instantâneo**: ao detectar qualquer anomalia, disparar um alerta em menos de 3 segundos após o evento aparecer no log. Critério de aceitação: tempo entre a escrita da linha no log e o envio do alerta é menor que 3 segundos em testes controlados.
- [ ] **Envio de alerta para Slack**: enviar o alerta para um canal do Slack. Critério de aceitação: mensagem de alerta chega no canal do Slack configurado, contendo tipo de anomalia e trecho do log relevante.
- [ ] **Persistência de histórico de anomalias**: registrar cada anomalia detectada (tipo, timestamp, trecho do log) de forma assíncrona, sem bloquear o disparo do alerta. Critério de aceitação: toda anomalia detectada gera um registro persistido, consultável posteriormente, sem atrasar o envio do alerta.
- [ ] **Configuração externa de parâmetros**: thresholds (força bruta, cascata) e lista de palavras-chave ficam em um arquivo de configuração externo, não fixos no código. Critério de aceitação: alterar um valor no arquivo de configuração e reiniciar o sistema muda o comportamento de detecção, sem precisar editar código-fonte.
- [ ] **Retomada de posição após reinício**: ao reiniciar, o sistema continua a leitura a partir da última posição registrada no arquivo de log (offset), sem reprocessar nem pular linhas. Critério de aceitação: após um reinício controlado, apenas linhas novas (escritas após o desligamento) são processadas.
- [ ] **Tratamento de rotação/truncamento de arquivo**: detectar quando o arquivo monitorado foi truncado (tamanho encolheu) ou trocado (rotação) e continuar a leitura a partir do arquivo atual. Critério de aceitação: após uma rotação/truncamento simulado, o sistema continua monitorando sem travar, mesmo que algumas linhas da janela de transição não sejam capturadas (limitação conhecida, ver Restrições).
- [ ] **Retry no envio de alerta**: em caso de falha ao enviar o alerta ao Slack, tentar novamente algumas vezes, registrando cada tentativa (sucesso ou falha) no histórico. Critério de aceitação: uma falha simulada de envio gera tentativas subsequentes registradas no histórico.
- [ ] **Consolidação de alertas simultâneos**: quando múltiplas regras disparam para o mesmo conjunto de eventos, enviar um único alerta consolidado listando todos os gatilhos, em vez de um alerta por regra. Critério de aceitação: um cenário que ativa mais de uma regra ao mesmo tempo gera apenas uma mensagem no Slack, contendo todos os gatilhos identificados.

### Desejáveis (pós-MVP)
- [ ] Suporte a envio de alerta também via Discord
- [ ] Suporte a múltiplos arquivos/fontes de log simultaneamente
- [ ] Suporte a logs estruturados (JSON), além de texto puro
- [ ] Painel/dashboard visual com histórico de anomalias
- [ ] Detecção "inteligente" (estatística/ML) de padrões anômalos, além das regras fixas

## Fora do Escopo — v1
- Monitoramento de múltiplos servidores/arquivos ao mesmo tempo — motivo: aumenta a complexidade de coordenação; o foco do MVP é validar a detecção e o alerta em um único arquivo antes de escalar
- Suporte a logs estruturados (JSON) — motivo: o MVP cobre apenas texto puro, formato mais comum e mais simples de processar primeiro
- Interface visual/dashboard — motivo: o MVP prioriza a cadeia detecção → alerta funcionando ponta a ponta

## Critérios de Sucesso
- [ ] O sistema detecta corretamente os três tipos de anomalia definidos (palavra-chave, força bruta 5+/60s, cascata 7+/30s) em um arquivo de log de teste
- [ ] O tempo entre o evento no log e o alerta enviado é consistentemente menor que 3 segundos
- [ ] O alerta chega de forma legível no Slack, com contexto suficiente para entender o que aconteceu
- [ ] Toda anomalia detectada é persistida no histórico, sem atrasar o alerta
- [ ] O sistema sobrevive a um reinício controlado sem reprocessar nem perder linhas (retoma do offset salvo)
- [ ] O sistema sobrevive a uma rotação/truncamento do arquivo sem travar, continuando o monitoramento
- [ ] Múltiplas regras disparadas pelo mesmo conjunto de eventos geram apenas um alerta consolidado no Slack

## Restrições Conhecidas
- MVP restrito a um único arquivo de log local, em texto puro, com parser genérico por linha (não específico de Apache/syslog/etc.)
- Meta de latência do alerta: < 3 segundos
- Preferência declarada por usar mecanismos de temporização/agendamento do Java para atingir a meta de latência — a ser avaliado na fase de `/plan` junto com a stack completa
- Preferência declarada por manter arquitetura orientada a objetos (POO) — a ser considerado na fase de `/plan`
- Limitação conhecida da v1: durante o exato instante de uma rotação/truncamento do arquivo de log, linhas escritas na janela de transição podem não ser capturadas. Não há reconciliação/recuperação desse gap no MVP.

## Perguntas em Aberto
- [ ] Nenhuma pendência de escopo do MVP — todas as decisões foram fechadas nesta fase
