![NavbarGit](frontend/src/assets/images/NavbarGit.png)

# 🚗 Sistema de Controle de Estacionamento

Projeto completo de gerenciamento de estacionamento, desenvolvido em **Spring Boot** com front-end integrado.  
Permite cadastro de veículos, controle de entrada/saída, relatórios e autenticação de usuários.

---

## ✨ Funcionalidades

### Back-end
- Cadastro de veículos (placa, modelo, cor)
- Registro de entrada/saída com cálculo automático de permanência
- Geração de relatórios diários (PDF/CSV)
- Autenticação JWT para acesso seguro

### Front-end
- Interface Web responsiva (React)
- Login e registro de usuários
- Dashboard com métricas em tempo real
- Formulários para cadastro de veículos e pagamentos

---

## 🔧 Tecnologias Utilizadas

### Back-end
- Java 21
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- H2 Database (testes)
- Lombok
- OpenAPI (Swagger)

### Front-end
- React 18
- Axios (integração com API)
- Bootstrap 5

### Testes
- Mockito
- Pitest (mutation testing)

---

## 🚀 Configuração e Execução

### Pré-requisitos
- Java 21
- Node.js 18+
- MySQL 8+ (opcional para produção)

### Back-end

Clonar repositório
git clone https://github.com/Alan-VSouza/Projeto-Estacionamento.git

Entrar na pasta do projeto
cd Projeto-Estacionamento/backend

Executar aplicação através da execução de DemoAuthAppApplication

### Front-end
cd Projeto-Estacionamento/frontend

Instalar dependências
npm install

Executar aplicação
npm start

Acesse: `http://localhost:3000`

## 🔒 Autenticação
A autenticação é protegidos por JWT. Sendo utilizado os Endpoints:
- **Registro:** POST `/api/auth/register`
- **Login:** POST `/api/auth/login`

Faça o registro na página de Register e o devido Login para ter acesso as funções.

---

## 📊 Qualidade
![Cobertura de Código](https://img.shields.io/badge/coverage-100%25-brightgreen)
![Mutantes Mortos](https://img.shields.io/badge/mutants-99%25-brightgreen)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

---

## 📄 Documentação da API
Acesse `http://localhost:8080/swagger-ui.html` após iniciar o back-end.
