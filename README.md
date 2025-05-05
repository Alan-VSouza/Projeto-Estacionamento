# 🚗 Sistema de Controle de Estacionamento

Projeto completo de gerenciamento de estacionamento, desenvolvido em **Spring Boot**. Permite cadastro de veículos, controle de entrada e saída, cálculo de receita diária e geração de relatórios. Pensado para ser robusto e extensível, com arquitetura limpa e modular.

## ✨ Funcionalidades

- Cadastro de veículos (placa, modelo, cor, proprietário)
- Registro de entrada e saída
- Cálculo automático do tempo de permanência e valor a pagar
- Geração de relatório diário de receita
- Consulta de histórico de veículos estacionados
- Relatórios exportáveis (PDF/CSV)

## 🔧 Tecnologias Utilizadas

- Java 23
- Spring Boot
- Spring Data JPA
- H2 Database (para testes) / MySQL (produção)
- Lombok
- Swagger (documentação da API)

## 🛠 Testes
Os testes automatizados estão implementados com:

- JUnit 5
- Mockito
- Testcontainers (para integração)
