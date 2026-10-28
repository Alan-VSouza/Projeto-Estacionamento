![NavbarGit](frontend/src/assets/images/NavbarGit.png)

# 🚗 Sistema de Controle de Estacionamento

Projeto completo de gerenciamento de estacionamento, desenvolvido em **Spring Boot** com front-end integrado.  
Permite cadastro de veículos, controle de entrada/saída, relatórios e autenticação de usuários.

---

## ✨ Funcionalidades

### Back-end

```bash
git clone https://github.com/Alan-VSouza/Projeto-Estacionamento.git
cd Projeto-Estacionamento/backend
```

Defina a chave usada para assinar os tokens JWT (uma string longa e aleatória, com pelo menos 32 caracteres). Ela não fica no repositório:

```bash
export JWT_SECRET_KEY="troque-por-uma-string-longa-e-aleatoria"   # Windows (PowerShell): $env:JWT_SECRET_KEY="..."
./mvnw spring-boot:run
```

Também é possível executar a classe `DemoAuthAppApplication` pela IDE, com a variável de ambiente definida. O banco SQLite (`database.db`) é criado automaticamente na primeira execução.

### Front-end

```bash
cd Projeto-Estacionamento/frontend
npm install
npm start
```

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
 
