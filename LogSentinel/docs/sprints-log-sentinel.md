# Sprints — LogSentinel
*Baseado no seu bloco fixo de "Projeto da Semana" (seg-sex, 16h30-18h30 = 10h/semana)*

## 📊 Visão geral

| Sprint | Bloco | Horas | Status |
|---|---|---|---|
| Sprint 1 | Setup e Configuração | 4h | ✅ Concluído |
| Sprint 2 | Persistência (SQLite) | 7h | 🟡 5h concluídas / 2h restantes |
| Sprint 3 | Log Watcher | 9h | ⬜ Não iniciado |
| Sprint 4 | Anomaly Detector | 8h | ⬜ Não iniciado |
| Sprint 5 | Alertas | 10h | ⬜ Não iniciado |
| Sprint 6 | Finalização | 7h | ⬜ Não iniciado |

**Restante: ~36h → cerca de 3,5 a 4 semanas**, no seu ritmo de 10h/semana (sem contar o sábado de Deploy, que pode absorver picos ou o README/GitHub final).

---

## Sprint 1 — Setup e Configuração ✅ (concluído)
- [x] T01 — Criar repositório e estrutura de pastas `[1h]`
- [x] T02 — Configurar dependências no `pom.xml` `[1h]`
- [x] T03 — `application.properties` + `LogSentinelProperties` `[2h]`

## Sprint 2 — Persistência (SQLite) 🟡 (em andamento)
- [x] T04 — Schema SQLite (`anomaly_history`, `file_position`) `[2h]`
- [x] T05 — `HistoryRepository` (salvar + listar anomalias) `[3h]`
- [ ] T06 — `PositionStore` (salvar/recuperar offset do arquivo) `[2h]`

## Sprint 3 — Log Watcher ⬜
- [ ] T07 — Leitura a partir do offset salvo (`RandomAccessFile`) `[3h]`
- [ ] T08 — `WatchService` (detectar novas linhas em tempo real) `[3h]`
- [ ] T09 — Detecção de rotação/truncamento do arquivo `[3h]`

## Sprint 4 — Anomaly Detector ⬜
- [ ] T10 — Detecção por palavra-chave `[2h]`
- [ ] T11 — Detecção de força bruta (5+/60s) `[3h]`
- [ ] T12 — Detecção de cascata (7+/30s) `[3h]`

## Sprint 5 — Alertas ⬜
- [ ] T13 — Consolidador de alertas `[3h]`
- [ ] T14 — Envio ao Slack via `RestTemplate` `[2h]`
- [ ] T15 — Retry com backoff `[3h]`
- [ ] T16 — `@Async` (histórico não bloqueia alerta) `[2h]`

## Sprint 6 — Finalização ⬜
- [ ] T17 — Integração do fluxo completo `[3h]`
- [ ] T18 — Testes manuais contra a spec `[2h]`
- [ ] T19 — Medir latência (< 3s) `[1h]`
- [ ] T20 — README `[30min]`
- [ ] T21 — `/converge` `[30min]`
