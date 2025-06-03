# Testes Structural

## Análise de Cobertura: Filtros em Streams Java 

A justificativa abaixo se aplica a filtros com operadores lógicos (&&) em streams, como .filter(p -> p.getHoraEntrada() != null && p.getHoraSaida() != null).

| Classe                 | Linha Não Coberta | Justificativa da Não Cobertura                                                                                                                                                                                                                            |
|:-----------------------|:------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `RelatorioService`     | `204`             | **O filtro .filter(p -> p.getHoraEntrada() != null && p.getHoraSaida() != null) possui quatro combinações lógicas. Apenas true && true executa o bloco subsequente. O branch false && false não é contabilizado por limitação técnica e é inalcançável.** |