- Atualizacao para testar o sonar analysis 
# link do video: https://youtu.be/s_uZOUwfi6M
# App README

- [ ] TODO Replace or update this README with instructions relevant to your application
# build.yml:
- tem como objetivo automatizar o processo de compilação de um projeto Java sempre que ocorre um push na branch principal (main). Ao ser acionado, o workflow executa um conjunto de etapas num ambiente Ubuntu: primeiro, faz o checkout do repositório para garantir acesso ao código-fonte; em seguida, instala o JDK 21 da distribuição Temurin, necessário para compilar o projeto. Depois, utiliza o Maven para limpar e compilar o código através do comando mvn clean package, gerando o ficheiro .jar dentro da pasta target/. Por fim, o workflow carrega automaticamente o ficheiro .jar como um artefacto denominado build-jar, permitindo que este seja descarregado a partir da secção de Actions do GitHub. Desta forma, o processo de build torna-se contínuo, padronizado e independente do ambiente local de desenvolvimento.
- Código de trigger que faz a ativacao do build.yml ao ser feito push no branch main:
  ``` on:
  push:
    branches:
      - main


## Project Structure

The sources of your App have the following structure:

```
src
├── main/frontend
│   └── themes
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       ├── MainErrorHandler.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional 
architectural layers. It includes two feature packages: `base` and `examplefeature`.

* The `base` package contains classes meant for reuse across different features, either through composition or 
  inheritance. You can use them as-is, tweak them to your needs, or remove them.
* The `examplefeature` package is an example feature package that demonstrates the structure. It represents a 
  *self-contained unit of functionality*, including UI components, business logic, data access, and an integration test.
  Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains an empty theme called `default`, based on the Lumo theme. It is activated in
the `Application` class, using the `@Theme` annotation.

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
App implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.
