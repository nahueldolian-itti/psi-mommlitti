# PSI-Mommlitti - Platform de Gestión de Sesiones

## Arquitectura

Este proyecto implementa una plataforma de agendamiento de sesiones de terapia online utilizando una arquitectura de microservicios basada en CQRS (Command Query Responsibility Segregation) y orientada a eventos.

### Servicios

#### 1. Booking Service (Puerto 8080)
- **Responsabilidad**: Gestiona comandos (agendar, cancelar, reagendar sesiones)
- **Base de Datos**: Amazon DynamoDB
- **Tecnologías**: Kotlin, Spring Boot, DynamoDB, SNS
- **Endpoints**:
  - `POST /api/v1/sessions` - Agendar sesión
  - `GET /api/v1/sessions/{id}` - Obtener sesión
  - `PUT /api/v1/sessions/{id}/cancel` - Cancelar sesión
  - `POST /api/v1/sessions/{id}/confirm` - Confirmar sesión con timezone
  - `GET /api/v1/psychologists/themes` - Obtener todas las temáticas
  - `GET /api/v1/psychologists/by-theme/{theme}` - Filtrar psicólogos por temática
  - `GET /api/v1/psychologists/{id}/weekly-availability` - Disponibilidad semanal

#### 2. Search Service (Puerto 8081)
- **Responsabilidad**: Gestiona consultas (búsqueda de psicólogos, disponibilidad)
- **Base de Datos**: MongoDB
- **Tecnologías**: Kotlin, Spring Boot, MongoDB, SQS
- **Endpoints**:
  - `GET /api/v1/psychologists` - Buscar psicólogos con filtros avanzados
  - `GET /api/v1/psychologists/{id}` - Obtener psicólogo
  - `GET /api/v1/psychologists/{id}/availability` - Obtener disponibilidad

### Challenge Features Implementadas

#### ✅ **Mostrar Psicólogos**
- **Endpoint**: `GET /api/v1/psychologists/themes`
- **Funcionalidad**: Lista todas las temáticas disponibles
- **Datos incluidos**: Nombre, temáticas, disponibilidad, rating, experiencia

#### ✅ **Filtrar por Temática**
- **Endpoint**: `GET /api/v1/psychologists/by-theme/{theme}`
- **Temáticas disponibles**: 
  - ANXIETY (Ansiedad)
  - DEPRESSION (Depresión) 
  - PHOBIAS (Fobias)
  - RELATIONSHIPS (Relaciones Personales)
  - SELF_ESTEEM (Autoestima)
  - STRESS (Estrés)
  - FAMILY_THERAPY (Terapia Familiar)
  - COUPLE_THERAPY (Terapia de Pareja)
  - GRIEF (Duelo)
  - TRAUMA (Trauma)
  - EATING_DISORDERS (Trastornos Alimentarios)
  - SLEEP_DISORDERS (Trastornos del Sueño)
  - ADDICTION (Adicciones)
  - CAREER_COUNSELING (Orientación Vocacional)
  - CHILD_THERAPY (Terapia Infantil)
  - ADOLESCENT_THERAPY (Terapia de Adolescentes)

#### ✅ **Visualizar Disponibilidad Semanal**
- **Endpoint**: `GET /api/v1/psychologists/{id}/weekly-availability?weekStartDate=2024-01-15&patientTimezone=America/Argentina/Buenos_Aires`
- **Funcionalidad**: Muestra calendario semanal con slots disponibles
- **Formato**: Vista tipo calendario con horarios convertidos al timezone del paciente

#### ✅ **Agendar Sesión con Confirmación**
- **Endpoint**: `POST /api/v1/sessions/{id}/confirm?patientTimezone=America/Argentina/Buenos_Aires`
- **Funcionalidad**: Confirma la sesión mostrando horarios en ambos timezones
- **Incluye**: Código de confirmación único (formato PSI-XXXXXXXX)

#### ✅ **Horario Adaptado al Huso Horario**
- **Funcionalidad**: Conversión automática de horarios según timezone del paciente
- **Soporte**: Múltiples zonas horarias de Argentina y el mundo
- **Default**: America/Argentina/Buenos_Aires

### Datos de Prueba Realistas

El sistema incluye 8 psicólogos ficticios con:
- **Especialidades diversas**: Desde ansiedad hasta terapia infantil
- **Horarios variados**: Diferentes días y horarios de trabajo
- **Ratings realistas**: Calificaciones de 4.6 a 4.9 estrellas
- **Experiencia**: De 6 a 15 años de experiencia
- **Disponibilidad automática**: 4 semanas de slots generados automáticamente

#### Psicólogos de Ejemplo:
1. **Dra. María Elena Rodríguez** - Ansiedad, Depresión, Estrés (Lun-Vie 9-17hs)
2. **Lic. Carlos Mendoza** - Relaciones, Terapia de Pareja (Lun-Jue 14-20hs, Sáb 9-13hs)
3. **Dra. Ana Sofía Morales** - Fobias, Trauma, EMDR (Mar-Vie 10-18hs, Sáb 10-14hs)
4. **Lic. Roberto Silva** - Autoestima, Orientación Vocacional (Lun, Mié, Vie 8-16hs)
5. **Dra. Laura Vega** - Terapia Infantil y Adolescente (Lun-Mar, Jue-Vie 9-17hs)
6. **Lic. Diego Herrera** - Adicciones, Trastornos Duales (Lun-Jue 11-19hs)
7. **Dra. Patricia Ramos** - Trastornos Alimentarios (Mar-Vie 8-16hs)
8. **Lic. Andrés Torres** - Duelo y Pérdidas (Lun, Mié, Vie 15-21hs)

### Arquitectura Hexagonal

Ambos servicios siguen una arquitectura hexagonal (ports and adapters):

```
├── domain/           # Entidades y casos de uso (núcleo del negocio)
├── business/         # Implementaciones de casos de uso y puertos
└── infrastructure/   # Adaptadores (REST, DynamoDB, MongoDB, SNS/SQS)
```

### Flujo de Eventos

1. **Booking Service** agenda una sesión en DynamoDB
2. **DynamoDB Streams** captura el cambio y publica a **SNS**
3. **SQS** recibe el evento del tópico SNS
4. **Search Service** consume el evento y actualiza MongoDB

### Características Clave

- **Idempotencia**: Previene double booking usando `Idempotency-Key`
- **Consistencia Eventual**: Los datos se sincronizan vía eventos
- **Escalabilidad**: Servicios independientes con bases de datos optimizadas
- **Observabilidad**: Integración con New Relic para monitoreo
- **Manejo de Timezone**: Conversión automática según ubicación del paciente
- **Datos Realistas**: Psicólogos con especialidades y horarios diversos

## Desarrollo Local

### Prerrequisitos

- JDK 17+
- Docker y Docker Compose
- AWS CLI configurado (para desarrollo local con LocalStack)

### Ejecutar los Servicios

```bash
# Construir ambos servicios
./gradlew build

# Ejecutar booking service
./gradlew :booking-service:bootRun

# Ejecutar search service
./gradlew :search-service:bootRun
```

### Infraestructura Local

```bash
# Iniciar DynamoDB local
docker run -p 8000:8000 amazon/dynamodb-local

# Iniciar MongoDB
docker run -p 27017:27017 mongo:latest

# Iniciar LocalStack para SNS/SQS
docker run -p 4566:4566 localstack/localstack
```

### Configuración de Tablas DynamoDB

```bash
# Crear tabla principal
aws dynamodb create-table \
  --endpoint-url http://localhost:8000 \
  --table-name psi-sessions \
  --attribute-definitions \
    AttributeName=PK,AttributeType=S \
    AttributeName=SK,AttributeType=S \
    AttributeName=idempotencyKey,AttributeType=S \
    AttributeName=theme,AttributeType=S \
  --key-schema \
    AttributeName=PK,KeyType=HASH \
    AttributeName=SK,KeyType=RANGE \
  --global-secondary-indexes \
    IndexName=idempotency-index,KeySchema=[{AttributeName=idempotencyKey,KeyType=HASH}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5} \
    IndexName=theme-index,KeySchema=[{AttributeName=theme,KeyType=HASH}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5} \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
```

### Ejemplos de Uso de la API

#### Obtener todas las temáticas
```bash
curl http://localhost:8080/api/v1/psychologists/themes
```

#### Buscar psicólogos por temática
```bash
curl http://localhost:8080/api/v1/psychologists/by-theme/ANXIETY
```

#### Ver disponibilidad semanal
```bash
curl "http://localhost:8080/api/v1/psychologists/1/weekly-availability?weekStartDate=2024-01-15&patientTimezone=America/Argentina/Buenos_Aires"
```

#### Agendar una sesión
```bash
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: unique-key-123" \
  -d '{
    "psychologistId": "1",
    "patientId": "patient-123",
    "startTime": "2024-01-15T10:00:00",
    "endTime": "2024-01-15T11:00:00"
  }'
```

#### Confirmar sesión con timezone
```bash
curl -X POST "http://localhost:8080/api/v1/sessions/session-id/confirm?patientTimezone=America/Argentina/Buenos_Aires"
```

## Monitoreo

Ambos servicios están instrumentados con New Relic para:
- APM (Application Performance Monitoring)
- Distributed Tracing
- Logs en contexto
- Métricas customizadas

## Testing

```bash
# Ejecutar tests unitarios
./gradlew test

# Ejecutar tests de integración
./gradlew integrationTest
```
