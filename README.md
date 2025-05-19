# Microservizio Valutazione e Feedback

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

## Modello dei dati

### Entità principali
*Tabelle in PostgreSQL per strutture dati del microservizio*

1. **Assessment** *(Valutazione)*
   - `id` - ID valutazione
   - `reference_id` - ID riferimento (compito o esame)
   - `reference_type` - Tipo riferimento (enum: ASSIGNMENT, EXAM)
   - `student_id` - ID studente
   - `teacher_id` - ID docente
   - `score` - Punteggio/voto
   - `assessment_date` - Data valutazione
   - `notes` - Note/commenti generali

2. **DetailedFeedback** *(Feedback Dettagliato)*
   - `id` - ID feedback
   - `assessment_id` - ID valutazione (riferimento)
   - `feedback_text` - Testo feedback
   - `category` - Categoria feedback (enum: CONTENT, PRESENTATION, CORRECTNESS, OTHER)
   - `strengths` - Punti di forza (testo)
   - `improvement_areas` - Aree di miglioramento (testo)

3. **TeacherSurvey** *(Questionario Docente)*
   - `id` - ID questionario
   - `course_id` - ID corso
   - `teacher_id` - ID docente
   - `academic_year` - Anno accademico
   - `semester` - Semestre
   - `status` - Stato (enum: ACTIVE, CLOSED)
   - `creation_date` - Data creazione

4. **SurveyResponse** *(Risposta Questionario)*
   - `id` - ID risposta
   - `survey_id` - ID questionario
   - `student_id` - ID studente (opzionale, anonimizzato)
   - `question_id` - Domanda (reference a catalogo domande)
   - `numeric_rating` - Valutazione numerica (1-5)
   - `text_comment` - Commento testuale (opzionale)
   - `submission_date` - Data compilazione

## API REST

### Assessments Endpoint

#### Gestione Valutazioni (per Docenti)
```bash
# Crea nuova valutazione
POST    /api/v1/assessments

# Lista valutazioni
GET     /api/v1/assessments

# Dettaglio singola valutazione
GET     /api/v1/assessments/{id}

# Aggiorna valutazione
PUT     /api/v1/assessments/{id}

# Elimina valutazione
DELETE  /api/v1/assessments/{id}

# Valutazioni per un compito
GET     /api/v1/assessments/assignment/{id}

# Valutazioni per un esame
GET     /api/v1/assessments/exam/{id}

# Valutazioni per uno studente
GET     /api/v1/assessments/student/{id}

# Tutte le valutazioni per un corso
GET     /api/v1/assessments/course/{id}
```

#### Visualizzazione Valutazioni (per Studenti)
```bash
# Le mie valutazioni (studente)
GET     /api/v1/assessments/personal

# Dettaglio singola valutazione
GET     /api/v1/assessments/personal/{id}
```

---

### Detailed Feedback Endpoint

```bash
# Crea nuovo feedback
POST    /api/v1/feedback

# Feedback singola valutazione
GET     /api/v1/feedback/assessment/{id}

# Aggiorna singolo feedback
PUT     /api/v1/feedback/{id}

# Elimina singolo feedback
DELETE  /api/v1/feedback/{id}
```

---

### Teacher Surveys Endpoint

#### Gestione Questionari (per Amministrativi)
```bash
# Crea nuovo questionario
POST    /api/v1/surveys

# Lista questionari
GET     /api/v1/surveys

# Dettaglio questionario
GET     /api/v1/surveys/{id}

# Aggiorna questionario
PUT     /api/v1/surveys/{id}

# Elimina questionario
DELETE  /api/v1/surveys/{id}

# Cambia stato (attiva/chiudi)
PUT     /api/v1/surveys/{id}/status

# Questionari per corso
GET     /api/v1/surveys/course/{id}

# Questionari per docente
GET     /api/v1/surveys/teacher/{id}
```

#### Compilazione Questionari (per Studenti)
```bash
# Questionari disponibili per studente
GET     /api/v1/surveys/available

# Invia risposte questionario
POST    /api/v1/surveys/{id}/responses
```

#### Visualizzazione Risultati (per Docenti e Amministrativi)
```bash
# Risultati aggregati questionario
GET     /api/v1/surveys/{id}/results

# Commenti testuali questionario
GET     /api/v1/surveys/{id}/comments
```

## Integrazione con altri Microservizi

Il microservizio **Valutazione e Feedback** interagisce con i seguenti microservizi:

- **Gestione Utenti e Ruoli**: Per verificare autorizzazioni e ottenere informazioni su studenti e docenti
- **Gestione Compiti**: Per ottenere dettagli sui compiti da valutare
- **Gestione Esami**: Per ottenere dettagli sugli esami da valutare
- **Gestione Corsi**: Per ottenere informazioni sui corsi associati alle valutazioni

---

### Eventi (RabbitMQ):

#### Published Events:
- `assessment.created`: Quando viene creata una nuova valutazione
- `assessment.updated`: Quando una valutazione viene modificata
- `assessment.deleted`: Quando una valutazione viene eliminata
- `feedback.created`: Quando viene aggiunto un nuovo feedback dettagliato
- `survey.activated`: Quando un questionario viene attivato
- `survey.closed`: Quando un questionario viene chiuso
- `survey.submitted`: Quando uno studente completa un questionario

#### Consumed Events:
- `assignment.submitted`: Per conoscere i nuovi compiti da valutare
- `exam.completed`: Per conoscere i nuovi esami da valutare
- `course.completed`: Per attivare automaticamente i questionari di valutazione docenti

## Sicurezza e Autorizzazioni

L'accesso alle API è regolato da autorizzazioni basate sui ruoli:

- **ROLE_TEACHER**: Può creare/modificare valutazioni e feedback solo per i propri corsi
- **ROLE_STUDENTS**: Può visualizzare solo le proprie valutazioni e compilare questionari
- **ROLE_ADMINISTRATIVE**: Può gestire i questionari e visualizzare risultati aggregati

_(Ipotizzio che il microservizio di **Gestione Utenti e Ruoli** utilizzi 3 ruoli con nomenclatura simile.)_

---