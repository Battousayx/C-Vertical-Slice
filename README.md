 C-Vertical-Slice
#Projeto de Lista de Tarefas com Vertical Slice Architecture

Este projeto é uma aplicação de lista de tarefas (Todo List) desenvolvida em C# utilizando o framework .NET Core 9. Ele foi arquitetado seguindo o padrão Vertical Slice Architecture, que organiza o código por funcionalidades completas (slices) em vez das camadas tradicionais (como UI, Business Logic, Data Access). Essa abordagem visa reduzir a complexidade, aumentar a coesão e diminuir o acoplamento entre as diferentes partes da aplicação, tornando-a mais fácil de manter e escalar.

#Visão Geral do Projeto

A aplicação implementa um sistema completo de gerenciamento de tarefas utilizando o padrão Vertical Slice Architecture. Este padrão organiza cada funcionalidade (ou "slice") como uma unidade independente e coesa, contendo todos os componentes necessários para sua execução: requisições, handlers, validações e modelos de domínio. Essa estrutura promove maior clareza, facilita testes e reduz dependências entre funcionalidades.

#Funcionalidades

A aplicação oferece as seguintes funcionalidades principais para gerenciamento de tarefas:

- Criar novas tarefas: Adicionar novas tarefas à lista com um título e descrição.
- Listar todas as tarefas: Visualizar todas as tarefas existentes com paginação e filtros.
- Atualizar tarefas existentes: Modificar o título, descrição ou outros atributos de uma tarefa.
- Marcar tarefas como concluídas: Alterar o status de uma tarefa para "concluída".
- Deletar tarefas: Remover tarefas da lista de forma permanente.
- Filtrar tarefas por status: Visualizar tarefas pendentes, concluídas ou todas as tarefas.
- Persistência de dados: As tarefas são armazenadas de forma persistente em banco de dados.
- Validação de dados: Validação robusta de entrada de dados em todas as operações.

#Tecnologias Utilizadas

Este projeto faz uso das seguintes tecnologias e padrões:

- C# 13: Linguagem de programação moderna com recursos avançados.
- .NET Core 9: Framework de desenvolvimento multiplataforma.
- ASP.NET Core 9: Framework para construção da API web RESTful.
- Entity Framework Core: ORM (Object-Relational Mapper) para persistência de dados e acesso ao banco de dados.
- Padrão Vertical Slice Architecture: Organização do código por funcionalidade completa.
- MediatR: Biblioteca para implementação do padrão Mediator, facilitando a comunicação entre comandos/queries e seus respectivos handlers.
- FluentValidation: Biblioteca para validação fluente e expressiva de objetos de requisição.
- Banco de dados: Suporte para SQL Server ou SQLite (configurável conforme necessidade).
- Dependency Injection: Injeção de dependências nativa do ASP.NET Core para gerenciamento de ciclo de vida.

#Estrutura do Código

A estrutura do código é organizada em "vertical slices", onde cada funcionalidade ou caso de uso é encapsulada em sua própria pasta. Por exemplo, uma funcionalidade como "Criar Tarefa" terá todos os seus componentes relacionados dentro de uma única pasta.

Organização de Pastas

`
VerticalSliceTodoList

├── Features

│   ├── CreateTodo

│   │   ├── CreateTodoCommand.cs

│   │   ├── CreateTodoCommandHandler.cs

│   │   ├── CreateTodoValidator.cs

│   │   └── CreateTodoResponse.cs

│   ├── GetTodos

│   │   ├── GetTodosQuery.cs

│   │   ├── GetTodosQueryHandler.cs

│   │   └── GetTodosResponse.cs

│   ├── UpdateTodo

│   │   ├── UpdateTodoCommand.cs

│   │   ├── UpdateTodoCommandHandler.cs

│   │   └── UpdateTodoValidator.cs

│   ├── DeleteTodo

│   │   ├── DeleteTodoCommand.cs

│   │   └── DeleteTodoCommandHandler.cs

│   └── CompleteTodo

│       ├── CompleteTodoCommand.cs

│       └── CompleteTodoCommandHandler.cs

├── Domain

│   ├── Entities

│   │   └── Todo.cs

│   └── ValueObjects

├── Infrastructure

│   ├── Data

│   │   ├── TodoDbContext.cs

│   │   └── Migrations

│   └── Repositories

├── Controllers

│   └── TodoController.cs

├── Program.cs

└── appsettings.json

#Componentes de Cada Slice

Dentro de cada "slice" (pasta de funcionalidade), você encontrará tipicamente os seguintes componentes:

- Command/Query: Representa a requisição para executar uma ação (Command) ou buscar dados (Query). Contém os dados necessários para a operação.
- Handler: Contém a lógica de negócios para processar o Command ou Query correspondente. Interage com repositórios e o banco de dados.
- Validator: Define as regras de validação para o Command ou Query, utilizando FluentValidation. Garante que os dados de entrada estejam corretos.
- Modelo de domínio: Entidades e objetos de valor relacionados à funcionalidade (ex: classe Todo).
- Response/DTO: Objeto de transferência de dados retornado pela operação.

Essa organização promove a independência entre as funcionalidades e facilita a compreensão e manutenção do código.

#Compilação e Execução

Siga os passos abaixo para compilar e executar a aplicação em sua máquina local:

Pré-requisitos

- .NET 9 SDK: Certifique-se de ter o SDK do .NET 9 instalado. Você pode baixá-lo em https://dotnet.microsoft.com/download
- Visual Studio 2022 ou Visual Studio Code: Um ambiente de desenvolvimento integrado (IDE) para C#.
- Git: Para clonar o repositório.

Passos para Compilação e Execução

1.  Clone o repositório:
    `bash
    git clone https://github.com/Battousayx/C-Vertical-Slice.git
    cd C-Vertical-Slice/vertical-slice-todo-list-main/vertical-slice-todo-list-main
    `

2.  Restaure as dependências:
    Navegue até o diretório raiz do projeto e execute o comando para restaurar os pacotes NuGet:
    `bash
    dotnet restore
    `

3.  Configure o banco de dados (se necessário):
    Se o projeto utiliza migrações do Entity Framework Core, execute:
    `bash
    dotnet ef database update
    `

4.  Compile o projeto:
    Compile o projeto para verificar se há erros e gerar os executáveis:
    `bash
    dotnet build
    `

5.  Execute a aplicação:
    Para iniciar a aplicação, execute:
    `bash
    dotnet run
    `
    A aplicação será iniciada e estará disponível em http://localhost:5000 (ou outra porta configurada no launchSettings.json).

6.  Acesse a aplicação:
    Abra seu navegador e navegue até http://localhost:5000 para interagir com a API. Você pode usar ferramentas como:
    -   Postman: Para testar endpoints manualmente.
    -   Swagger/OpenAPI: Se configurado no projeto, acesse http://localhost:5000/swagger para documentação interativa.
    -   curl: Para testes via linha de comando.

Exemplo de Requisição

`bash
Criar uma nova tarefa
curl -X POST http://localhost:5000/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Minha primeira tarefa", "description": "Descrição da tarefa"}'

Listar todas as tarefas
curl http://localhost:5000/api/todos

Atualizar uma tarefa
curl -X PUT http://localhost:5000/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Tarefa atualizada", "description": "Nova descrição"}'

Deletar uma tarefa
curl -X DELETE http://localhost:5000/api/todos/1
`

#Dependências

As principais dependências NuGet utilizadas neste projeto incluem:

-   MediatR (v12.0.0+): Para implementação do padrão Mediator, permitindo comunicação desacoplada entre requisições e handlers.
-   MediatR.Extensions.Microsoft.DependencyInjection: Extensões para integração com o container de injeção de dependências do ASP.NET Core.
-   FluentValidation (v11.0.0+): Para validação fluente e expressiva de objetos.
-   FluentValidation.AspNetCore: Integração do FluentValidation com ASP.NET Core.
-   Microsoft.EntityFrameworkCore (v9.0.0+): ORM para acesso a dados.
-   Microsoft.EntityFrameworkCore.SqlServer: Provedor do Entity Framework Core para SQL Server.
-   Microsoft.EntityFrameworkCore.Sqlite: Provedor do Entity Framework Core para SQLite (alternativa).
-   Microsoft.EntityFrameworkCore.Design: Ferramentas de design para Entity Framework Core (para migrações).
-   AutoMapper (opcional): Para mapeamento automático entre DTOs e entidades de domínio.
-   AutoMapper.Extensions.Microsoft.DependencyInjection (opcional): Integração do AutoMapper com injeção de dependências.

Para visualizar todas as dependências, consulte o arquivo VerticalSliceTodoList.csproj.

#Contribuição

Contribuições são bem-vindas! Se você deseja contribuir para este projeto, por favor, siga estas diretrizes:

1.  Faça um fork do repositório: Clique no botão "Fork" no GitHub.
2.  Crie uma nova branch para sua funcionalidade:
    `bash
    git checkout -b feature/minha-nova-funcionalidade
    `
3.  Faça suas alterações: Implemente sua funcionalidade ou correção de bug.
4.  Adicione testes: Se aplicável, adicione testes unitários para sua funcionalidade.
5.  Commit suas alterações:
    `bash
    git commit -m 'Adiciona nova funcionalidade X'
    `
6.  Envie para a branch:
    `bash
    git push origin feature/minha-nova-funcionalidade
    `
7.  Abra um Pull Request: Descreva suas mudanças e o motivo da contribuição.

Licença

Este projeto é de código aberto e está disponível sob a licença MIT. Sinta-se à vontade para usar, modificar e distribuir o código conforme os termos da licença.

Para mais informações sobre a licença MIT, consulte o arquivo LICENSE no repositório.

---

Autor: Herbet Terrone Borges  
Repositório: https://github.com/Battousayx/C-Vertical-Slice  
Última atualização: Janeiro de 2026
