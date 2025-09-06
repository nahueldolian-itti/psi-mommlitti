# GitHub Copilot Instructions

This document provides guidelines for working with this project using GitHub Copilot, following the established architecture and practices.

## Important Process Requirements
- ALWAYS present a detailed plan and wait for explicit approval before implementing any code changes
- Do not proceed with implementation until receiving confirmation from the user
- When presenting the plan, provide a step-by-step breakdown of all files to be created or modified
- Ask directly: "Do you approve this plan before I proceed with implementation?"

## Project Structure

This project follows a hexagonal architecture (ports and adapters) with the following layers:

### 1. Domain (`domain`)
- **Entities**: Pure data models without external dependencies
- **Repositories**: Interfaces for data access (defined in usecases)
- **UseCases**: Interfaces that define the application's use cases
- **Enums**: Enumerations used in the domain
- **Criteria**: Criteria for queries and filters
- **Strategy**: Strategy patterns for behavior variations

### 2. Business (`business`)
- **Ports**: Interfaces that define how the domain communicates with the outside world
- **UseCases**: Concrete implementations of domain use cases

### 3. Infrastructure (`infrastructure`)
- **Adapters**:
    - `inbound`: Input adapters (REST, etc.)
    - `outbound`: Output adapters (MongoDB, etc.)
- **Configurations**: Spring Boot configurations
- **Data**: Concrete repository implementations
- **Interfaces**: REST controllers and other entry points

### 4. GitHub Configuration Files (`.github`)
- **Workflows**: Workflow definitions for CI/CD in GitHub Actions
- **Pull Request Templates**: Predefined templates for pull requests
- **Issue Templates**: Templates for creating issues
- **Dependabot**: Configurations for automatic dependency updates
- **Copilot Instructions**: This file with instructions for GitHub Copilot

## Code Conventions

### General Principles
1. Follow SOLID principles
2. Use dependency injection
3. Implement interfaces to define contracts between layers
4. Keep domain entities free from external dependencies

### Kotlin
1. Use data classes for immutable models
2. Leverage Kotlin's functional features (extension functions, lambdas)
3. Use functional interfaces with `fun interface` when appropriate
4. Prefer immutable properties (val) over variables (var) when possible

### Design Patterns
1. **Port-Adapter**: Separates business logic from implementation details
2. **Dependency Inversion**: Dependencies point towards the domain
3. **Repository**: Abstraction for data access
4. **Strategy**: When multiple algorithms or alternative behaviors exist

## Workflow

### New Features
When adding new features, follow this order:
1. Define entities in the domain
2. Define use case interfaces in the domain
3. Implement use cases in the business layer
4. Implement adapters for infrastructure

### Testing
- Write unit tests for domain logic
- Use Mockito to mock dependencies
- Implement integration tests to verify connectivity with external services

## Technologies

- **Spring Boot 3.x**: Main framework
- **Kotlin 1.9.x**: Programming language
- **MongoDB**: Database
- **OpenAPI/Swagger**: API documentation
- **New Relic**: Monitoring

## Naming Conventions

- **Domain Entities**: Descriptive names without suffixes
- **Use Cases**: Verbs describing the action + noun + `UseCase`
- **Controllers**: Noun + `Controller`
- **Repositories**: Noun + `Repository`
- **DTOs**: Noun + purpose + `Request`/`Response`
- **Criteria**: Noun + `Criteria`
- **Strategies**: Action + `Strategy`

## Error Handling

- Use custom exceptions for domain errors
- Implement a global exception handler to translate errors to appropriate HTTP responses
- Log errors with appropriate log levels

## REST API

- Follow RESTful principles
- Use appropriate HTTP verbs (GET, POST, PUT, DELETE)
- Implement responses with proper HTTP status codes
- Document endpoints with OpenAPI annotations

## Logging

- Use SLF4J for logging
- Include relevant contextual information in logs
- Avoid excessive logging in critical paths