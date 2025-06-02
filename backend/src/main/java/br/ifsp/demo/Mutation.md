# Análise de Mutantes Equivalentes


## Tabela de Mutantes Equivalentes: `CalculadoraTempoPermanencia`

| Linha | Mutante | Justificativa da Equivalência |
| :--- | :--- | :--- |
| **~31** | `if (horas <= 6)` <br> ↳ _Changed conditional boundary_ | **Cenário de Teste:** `horas = 6`. <br> **• Código Original (`<=`):** O cálculo é `min(10.0 + 8*(6-1), 35.0)`, que resulta em `min(50.0, 35.0)` = `35.0`. <br> **• Código Mutante (`<`):** O fluxo cai para o próximo bloco, calculando `min(35.0 + 8*(6-6), 55.0)`, que resulta em `min(35.0, 55.0)` = `35.0`. <br> **Ambos produzem o mesmo resultado.** |
| **~34** | `if (horas <= 12)` <br> ↳ _Changed conditional boundary_ | **Cenário de Teste:** `horas = 12`. <br> **• Código Original (`<=`):** O cálculo é `min(35.0 + 8*(12-6), 55.0)`, que resulta em `min(83.0, 55.0)` = `55.0`. <br> **• Código Mutante (`<`):** O fluxo cai para o próximo bloco, calculando `min(55.0 + 8*(12-12), 120.0)`, que resulta em `min(55.0, 120.0)` = `55.0`. <br> **Ambos produzem o mesmo resultado.** |
| **~37** | `if (horas <= 24)` <br> ↳ _Changed conditional boundary_ | **Cenário de Teste:** `horas = 24`. <br> **• Código Original (`<=`):** O cálculo é `min(55.0 + 8*(24-12), 120.0)`, que resulta em `min(151.0, 120.0)` = `120.0`. <br> **• Código Mutante (`<`):** O fluxo cai para o `return` final, calculando `120.0 + 8*(24-24)`, que resulta em `120.0`. <br> **Ambos produzem o mesmo resultado.**|

---

## Tabela de Mutantes Equivalentes: `ValorPermanencia`

| Linha | Mutante | Justificativa da Equivalência |
| :--- | :--- | :--- |
| N/A | Nenhum | A classe `ValorPermanencia` contém apenas métodos `get` que retornam valores constantes. Mutações nessa classe (ex: alterar um valor de retorno) seriam facilmente "mortas" por qualquer teste que utilize a `CalculadoraTempoPermanencia`, não gerando mutantes equivalentes complexos como os da classe de lógica. |