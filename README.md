# Microservizio Valutazione e Feedback

## Indice
1. [Panoramica](#panoramica)
2. [Tech Stack](#tech-stack)
3. [Modello Dati](#modello-dati)
   - [DTO](#dto)
     - [Valutazione](#dto-per-la-valutazione)
     - [Feedback Dettagliato](#dto-per-il-feedback-dettagliato)
     - [Questionario Docente](#dto-per-il-questionario-docente)
     - [Risposta Questionario](#dto-per-risposta-questionario)
   - [Entità Princiapli JPA](#entità-principali-jpa)
     - [Assessment (Valutazione)](#assessment-valutazione)
     - [DetailedFeedback (Feedback Dettagliato)](#detailedfeedback-feedback-dettagliato)
     - [TeacherSurvey (Questionario Docente)](#teachersurvey-questionario-docente)
     - [SurveyResponse (Risposta Questionario)](#surveyresponse-risposta-questionario)
4. [API REST](#api-rest)
    - [Assessments Endpoint](#assessments-endpoint)
    - [Detailed Feedback Endpoint](#detailed-feedback-endpoint)
    - [Teacher Surveys Endpoint](#teacher-surveys-endpoint)
    - [Surveys Response Endpoint](#surveys-response-endpoint)
5. [Integrazione Microservizi Esterni](#integrazione-microservizi-esterni)
   - [Panoramica Generale](#panoramica-generale)
   - [RabbitMQ - Published Events](#rabbitmq---published-events)
   - [RabbitMQ - Consumed Events](#rabbitmq---consumed-events)
6. [Sicurezza e Autorizzazioni](#sicurezza-e-autorizzazioni)
7. [Come Farlo Funzionare sui Vostri OS](#come-farlo-funzionare-sui-vostri-os)
    - [1. Installazione di PostgreSQL](#1-installazione-di-postgresql)
    - [2. Installazione di RabbitMQ](#2-installazione-di-rabbitmq)
    - [3. Configurazione del Database](#3-configurazione-del-database)
    - [4. Avvio del Microservizio](#4-avvio-del-microservizio)
    - [5. Verifica dell'Avvio](#5-verifica-dellavvio)
    - [6. Test Rapido dell'API](#6-test-rapido-dellapi)

---

## Panoramica
Questo Microservizio è responsabile dell'aggiunta e della visualizzazione del feedback fornito dai docenti sui compiti e sugli esami:
- **(Docenti)** Fornitura di feedback dettagliato sui compiti e sugli esami.
- **(Studenti)** Visualizzazione del feedback ricevuto.
- **(Amministrativi)** Creazione di un questionario di feedback sui docenti
- **(Studenti)** Compilazione del questionario di feedback sui docenti

## Tech Stack
- **Framework**: SpringBoot
- **Message Broker**: RabbitMQ
- **Database**: PostgreSQL
- **Containerization**: Docker (non presente su questa repo)
- **Orchestration**: Kubernetes (non presente su questa repo)
- **API Documentation**: Swagger/OpenAPI 3.0

## Modello Dati

### DTO

_DataTransferObject presenti nel microservizio._

#### DTO per la Valutazione
```java
public class AssessmentDTO {
    private Long id;
    private Long referenceId;
    private ReferenceType referenceType;
    private Long studentId;
    private Long teacherId;
    private Double score;
    private LocalDateTime assessmentDate;
    private String notes;
    private Long courseId;
}
```

#### DTO per il Feedback Dettagliato
```java
public class DetailedFeedbackDTO {
   private Long id;
   private Long assessmentId;
   private String feedbackText;
   private FeedbackCategory category;
   private String strengths;
   private String improvementAreas;
}
```

#### DTO per il Questionario Docente
```java
public class TeacherSurveyDTO {
   private Long id;
   private Long courseId;
   private Long teacherId;
   private String academicYear;
   private Integer semester;
   private SurveyStatus status;
   private LocalDateTime creationDate;
   private LocalDateTime closingDate;
}
```

#### DTO per Risposta Questionario
```java
public class SurveyResponseDTO {
   private Long id;
   private Long surveyId;
   private Long studentId;
   private Long questionId;
   private Integer numericRating;
   private String textComment;
   private LocalDateTime submissionDate;
}
```

---

### Entità principali JPA
*Tabelle in PostgreSQL per strutture dati del microservizio*

#### Assessment *(Valutazione)*
   - `id` - ID valutazione
   - `reference_id` - ID riferimento (compito o esame)
   - `reference_type` - Tipo riferimento (enum: ASSIGNMENT, EXAM)
   - `student_id` - ID studente
   - `teacher_id` - ID docente
   - `score` - Punteggio/voto
   - `assessment_date` - Data valutazione
   - `notes` - Note/commenti generali

#### DetailedFeedback *(Feedback Dettagliato)*
   - `id` - ID feedback
   - `assessment_id` - ID valutazione (riferimento)
   - `feedback_text` - Testo feedback
   - `category` - Categoria feedback (enum: CONTENT, PRESENTATION, CORRECTNESS, OTHER)
   - `strengths` - Punti di forza (testo)
   - `improvement_areas` - Aree di miglioramento (testo)

#### TeacherSurvey *(Questionario Docente)*
   - `id` - ID questionario
   - `course_id` - ID corso
   - `teacher_id` - ID docente
   - `academic_year` - Anno accademico
   - `semester` - Semestre
   - `status` - Stato (enum: ACTIVE, CLOSED)
   - `creation_date` - Data creazione

#### SurveyResponse *(Risposta Questionario)*
   - `id` - ID risposta
   - `survey_id` - ID questionario
   - `student_id` - ID studente (opzionale, anonimizzato)
   - `question_id` - Domanda (reference a catalogo domande)
   - `numeric_rating` - Valutazione numerica (1-5)
   - `text_comment` - Commento testuale (opzionale)
   - `submission_date` - Data compilazione

## API REST

### Assessments Endpoint

```bash
######### Gestione Valutazioni (per Docenti) #########

#############################################
# Crea nuova valutazione
# @func: createAssessment()
# @param: AssessmentDTO assessmentDTO
# @return: ResponseEntity<AssessmentDTO>
#############################################
POST    /api/v1/assessments

#############################################
# Lista valutazioni
# @func: getAllAssessments()
# @param: none
# @return: ResponseEntity<List<AssessmentDTO>>
#############################################
GET     /api/v1/assessments

#############################################
# Dettaglio singola valutazione
# @func: getAssessmentById()
# @param: Long id
# @return: ResponseEntity<AssessmentDTO>
#############################################
GET     /api/v1/assessments/{id}

#############################################
# Aggiorna valutazione
# @func: updateAssessment()
# @param: Long id 
# @param: AssessmentDTO assessmentDTO
# @return: ResponseEntity<AssessmentDTO>
#############################################
PUT     /api/v1/assessments/{id}

#############################################
# Elimina valutazione
# @func: deleteAssessment()
# @param: Long id
# @return: ResponseEntity<Void>
#############################################
DELETE  /api/v1/assessments/{id}

#############################################
# Valutazioni per un compito
# @func: getAssessmentsByAssignment()
# @param: Long id
# @return: ResponseEntity<List<AssessmentDTO>>
#############################################
GET     /api/v1/assessments/assignment/{id}

#############################################
# Valutazioni per un esame
# @func: getAssessmentsByExam()
# @param: Long id
# @return: ResponseEntity<List<AssessmentDTO>>
#############################################
GET     /api/v1/assessments/exam/{id}

#############################################
# Valutazioni per uno studente
# @func: getAssessmentsByStudentId()
# @param: Long id
# @return: ResponseEntity<List<AssessmentDTO>>
#############################################
GET     /api/v1/assessments/student/{id}

#############################################
# Tutte le valutazioni per un corso
# @func: getAssessmentsByCourse()
# @param: Long id
# @return: ResponseEntity<List<AssessmentDTO>>
#############################################
GET     /api/v1/assessments/course/{id}
```

```bash
######### Visualizzazione Valutazioni (per Studenti) #########

#############################################
# Le mie valutazioni (studente)
# @func: getPersonalAssessments()
# @param: none
# @return: ResponseEntity<List<AssessmentDTO>>
#############################################
GET     /api/v1/assessments/personal

#############################################
# Dettaglio valutazione personale
# @func: getPersonalAssessmentDetails()
# @param: Long id
# @return: ResponseEntity<AssessmentDTO>
#############################################
GET     /api/v1/assessments/personal/{id}
```

---

### Detailed Feedback Endpoint

```bash
#############################################
# Crea nuovo feedback
# @func: createFeedback()
# @param: DetailedFeedbackDTO feedbackDTO
# @return: ResponseEntity<DetailedFeedbackDTO>
#############################################
POST    /api/v1/feedback

#############################################
# Feedback per una valutazione
# @func: getFeedbackByAssessmentId()
# @param: Long id
# @return: ResponseEntity<List<DetailedFeedbackDTO>>
#############################################
GET     /api/v1/feedback/assessment/{id}

#############################################
# Dettaglio singolo feedback
# @func: getFeedbackById()
# @param: Long id
# @return: ResponseEntity<DetailedFeedbackDTO>
#############################################
GET     /api/v1/feedback/{id}

#############################################
# Aggiorna singolo feedback
# @func: updateFeedback()
# @param: Long id 
# @param: DetailedFeedbackDTO feedbackDTO
# @return: ResponseEntity<DetailedFeedbackDTO>
#############################################
PUT     /api/v1/feedback/{id}

#############################################
# Elimina singolo feedback
# @func: deleteFeedback()
# @param: Long id
# @return: ResponseEntity<Void>
#############################################
DELETE  /api/v1/feedback/{id}
```

---

### Teacher Surveys Endpoint

```bash
#############################################
# Crea nuovo questionario
# @func: createSurvey()
# @param: TeacherSurveyDTO surveyDTO
# @return: ResponseEntity<TeacherSurveyDTO>
#############################################
POST    /api/v1/surveys

#############################################
# Lista questionari
# @func: getAllSurveys()
# @param: none
# @return: ResponseEntity<List<TeacherSurveyDTO>>
#############################################
GET     /api/v1/surveys

#############################################
# Dettaglio questionario
# @func: getSurveyById()
# @param: Long id
# @return: ResponseEntity<TeacherSurveyDTO>
#############################################
GET     /api/v1/surveys/{id}

#############################################
# Aggiorna questionario
# @func: updateSurvey()
# @param: Long id
# @param: TeacherSurveyDTO surveyDTO
# @return: ResponseEntity<TeacherSurveyDTO>
#############################################
PUT     /api/v1/surveys/{id}

#############################################
# Elimina questionario
# @func: deleteSurvey()
# @param: Long id
# @return: ResponseEntity<Void>
#############################################
DELETE  /api/v1/surveys/{id}

#############################################
# Cambia stato (attiva/chiudi)
# @func: changeSurveyStatus()
# @param: Long id 
# @param: SurveyStatus status
# @return: ResponseEntity<TeacherSurveyDTO>
#############################################
PUT     /api/v1/surveys/{id}/status

#############################################
# Questionari per corso
# @func: getSurveysByCourse()
# @param: Long id
# @return: ResponseEntity<List<TeacherSurveyDTO>>
#############################################
GET     /api/v1/surveys/course/{id}

#############################################
# Questionari per docente
# @func: getSurveysByTeacher()
# @param: Long id
# @return: ResponseEntity<List<TeacherSurveyDTO>>
#############################################
GET     /api/v1/surveys/teacher/{id}

#############################################
# Questionari disponibili per studente
# @func: getAvailableSurveys()
# @param: none
# @return: ResponseEntity<List<TeacherSurveyDTO>>
#############################################
GET     /api/v1/surveys/available

```

---

### Surveys Response Endpoint

```bash
######### Visualizzazione Risultati (per Docenti e Amministrativi) #########

#############################################
# Invia risposte questionario
# @func: submitSurveyResponses()
# @param: Long id
# @param: List<SurveyResponseDTO> responseDTOs
# @return: ResponseEntity<List<SurveyResponseDTO>>
#############################################
POST    /api/v1/surveys/{id}/responses

#############################################
# Crea singola risposta
# @func: createSurveyResponse()
# @param: SurveyResponseDTO responseDTO
# @return: ResponseEntity<SurveyResponseDTO>
#############################################
POST    /api/v1/surveys/response

#############################################
# Risultati aggregati questionario
# @func: getSurveyResults()
# @param: Long id
# @return: ResponseEntity<Map<Long, Double>>
#############################################
GET     /api/v1/surveys/{id}/results

#############################################
# Commenti testuali questionario
# @func: getSurveyComments()
# @param: Long id
# @return: ResponseEntity<List<SurveyResponseDTO>>
#############################################
GET     /api/v1/surveys/{id}/comments

#############################################
# Recupera risposte complete
# @func: getResponsesBySurveyId()
# @param: Long id
# @return: ResponseEntity<List<SurveyResponseDTO>>
#############################################
GET     /api/v1/surveys/{id}/responses

```

## Integrazione Microservizi Esterni

### Panoramica Generale

Il microservizio **Valutazione e Feedback** interagisce con i seguenti microservizi:

- **Gestione Utenti e Ruoli**: Per verificare autorizzazioni e ottenere informazioni su studenti e docenti
- **Gestione Compiti**: Per ottenere dettagli sui compiti da valutare
- **Gestione Esami**: Per ottenere dettagli sugli esami da valutare
- **Gestione Corsi**: Per ottenere informazioni sui corsi associati alle valutazioni

---

### RabbitMQ - Published Events
- `assessment.created`: Quando viene creata una nuova valutazione
- `assessment.updated`: Quando una valutazione viene modificata
- `assessment.deleted`: Quando una valutazione viene eliminata
- `feedback.created`: Quando viene aggiunto un nuovo feedback dettagliato
- `survey.activated`: Quando un questionario viene attivato
- `survey.closed`: Quando un questionario viene chiuso
- `survey.submitted`: Quando uno studente completa un questionario

### RabbitMQ - Consumed Events
- `assignment.submitted`: Per conoscere i nuovi compiti da valutare
- `exam.completed`: Per conoscere i nuovi esami da valutare
- `course.completed`: Per attivare automaticamente i questionari di valutazione docenti

## Sicurezza e Autorizzazioni

L'accesso alle API è regolato da autorizzazioni basate sui ruoli:

- **ROLE_TEACHER**: Può creare/modificare valutazioni e feedback solo per i propri corsi
- **ROLE_STUDENTS**: Può visualizzare solo le proprie valutazioni e compilare questionari
- **ROLE_ADMINISTRATIVE**: Può gestire i questionari e visualizzare risultati aggregati

_(Ipotizzo che il microservizio di **Gestione Utenti e Ruoli** utilizzi 3 ruoli con nomenclatura simile.)_

---

## Come Farlo Funzionare sui Vostri OS

Brevissima guida per l'installazione di PostgreSQL, 
RabbitMQ e l'avvio di Spring Boot (necessari a far 
funzionare il microservizio).

### 1. Installazione di PostgreSQL

#### Windows

1. Scarica l'installer da [postgresql.org/download/windows](https://www.postgresql.org/download/windows/).
2. Esegui l'installer e segui i passaggi:

    * Imposta una password per l'utente `postgres`.
    * Lascia la porta predefinita `5432`.
    * Installa anche **pgAdmin** se desideri un'interfaccia grafica.
3. Verifica l'installazione:

   ```bash
   psql --version
   ```

---

#### macOS

1. Assicurati di avere Homebrew installato.
2. Installa PostgreSQL:

   ```bash
   brew install postgresql
   ```

3\. Avvia il servizio:

```bash
brew services start postgresql
```

4\. Verifica l'installazione:

```bash
psql --version
```

---

#### Ubuntu

1. Aggiorna i pacchetti:

   ```bash
   sudo apt update
   ```


2\. Installa PostgreSQL:

```bash
sudo apt install postgresql postgresql-contrib
```


3\. Verifica l'installazione:

```bash
psql --version
```

---

### 2. Installazione di RabbitMQ

#### Windows

1. Installa Erlang da [erlang.org/downloads](https://www.erlang.org/downloads).
2. Scarica l'installer di RabbitMQ da [rabbitmq.com/install-windows.html](https://www.rabbitmq.com/install-windows.html).
3. Esegui l'installer e segui le istruzioni.
4. Abilita il plugin di gestione:

   ```bash
   rabbitmq-plugins enable rabbitmq_management
   ```

5\. Avvia il servizio RabbitMQ:

```bash
rabbitmq-service start
```

6\. Accedi all'interfaccia web su [http://localhost:15672](http://localhost:15672) con:

* **Username**: `guest`
* **Password**: `guest`

---

#### macOS

1. Assicurati di avere Homebrew installato.
2. Installa RabbitMQ:

   ```bash
   brew install rabbitmq
   ```

3\. Avvia il servizio:

```bash
brew services start rabbitmq
```

4\. Abilita il plugin di gestione:

```bash
rabbitmq-plugins enable rabbitmq_management
```

5\. Accedi all'interfaccia web su [http://localhost:15672](http://localhost:15672) con:

* **Username**: `guest`
* **Password**: `guest`([Stack Overflow][5])

---

#### Ubuntu

1. Aggiorna i pacchetti:

   ```bash
   sudo apt update
   ```

2\. Installa Erlang:

```bash
sudo apt install erlang
```

3\. Installa RabbitMQ:

```bash
sudo apt install rabbitmq-server
```

4\. Abilita il plugin di gestione:

```bash
sudo rabbitmq-plugins enable rabbitmq_management
```

5\. Avvia il servizio RabbitMQ:

```bash
sudo systemctl start rabbitmq-server
```

6\. Accedi all'interfaccia web su [http://localhost:15672](http://localhost:15672) con:

* **Username**: `guest`
* **Password**: `guest`

---

### 3. Configurazione del Database

#### Avvia PostgreSQL

##### macOS (con Homebrew)

```bash
brew services start postgresql
```

##### Ubuntu

```bash
sudo systemctl start postgresql
```

##### Windows

PostgreSQL dovrebbe avviarsi come servizio automaticamente. In caso contrario:

1. Apri `Servizi` (digita "services.msc" nel menu Start)
2. Cerca `postgresql-xx` e avvia manualmente il servizio

---

#### Accesso a PostgreSQL

##### macOS/Linux

```bash
psql -U postgres
```

##### Windows

1. Apri **SQL Shell (psql)** dal menu Start
2. Inserisci:

    * Server: `localhost`
    * Porta: `5432`
    * Database: `postgres`
    * Username: `postgres`
    * Password: (lascia vuoto o inserisci se configurato)

#### Se ottieni errori come:

```bash
psql: FATAL: role "postgres" does not exist
```

Crea il ruolo:

```bash
sudo -u postgres createuser -s postgres
# oppure entra come utente postgres (Linux/macOS)
sudo -i -u postgres
psql
```

e poi dopo:

```bash
CREATE ROLE postgres WITH LOGIN PASSWORD 'password';
ALTER ROLE postgres CREATEDB;
```

---

#### Crea il database

```sql
CREATE DATABASE assessment_feedback_db;
```

---

#### (Facoltativo) Crea un utente per l'app

```sql
CREATE USER feedback_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE assessment_feedback_db TO feedback_user;
```

---

### 4. Avvio del Microservizio

1. Clona il repository:

   ```bash
   git clone https://github.com/Luxauram/SPRINGBOOT-UNIMOL-MS-Valutazione-Feedback.git
   cd assessment-feedback-service
   ```

2\. Verifica il file `application.properties` con le seguenti configurazioni:

```properties
server.port=8082
spring.datasource.url=jdbc:postgresql://localhost:5432/assessment_feedback_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```


3\. Avvia l'applicazione:

```bash
./mvnw spring-boot:run
```

Oppure, se utilizzi Gradle:

```bash
./gradlew bootRun
```

---

### 5. Verifica dell'Avvio

* Accedi a Swagger UI su: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
* Verifica l'endpoint Actuator: [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)

---

### 6. Test Rapido dell'API

#### Creazione di una Valutazione

```bash
curl -X POST http://localhost:8082/api/v1/assessments \
  -H "Content-Type: application/json" \
  -d '{
    "referenceId": 1,
    "referenceType": "ASSIGNMENT",
    "studentId": 123,
    "teacherId": 456,
    "score": 28.5,
    "assessmentDate": "2025-05-23T10:00:00",
    "notes": "Ottima prova",
    "courseId": 789
  }'
```

#### Recupero delle Valutazioni

```bash
curl http://localhost:8082/api/v1/assessments
```

---