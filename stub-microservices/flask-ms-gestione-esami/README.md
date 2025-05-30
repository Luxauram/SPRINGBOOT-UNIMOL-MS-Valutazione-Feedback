# 🎓 Microservizio Stub - Gestione Esami

Questo è un microservizio stub creato in Flask per simulare il sistema di Gestione Esami per il testing del microservizio Valutazione e Feedback.

## 🚀 Avvio Rapido

### Prerequisiti
- Python 3.8 o superiore
- pip (gestore pacchetti Python)

### Installazione

1. **Vai nella directory del microservizio:**
   ```bash
   cd gestione-esami
   ```

2. **Crea un ambiente virtuale (CONSIGLIATO):**
   ```bash
   python -m venv venv
   
   # Su Windows:
   venv\Scripts\activate
   
   # Su Linux/Mac:
   source venv/bin/activate
   ```

3. **Installa le dipendenze:**
   ```bash
   pip install -r requirements.txt
   ```

4. **Avvia il microservizio:**
   ```bash
   python app.py
   ```

5. **Verifica che funzioni:**
    - Il servizio sarà disponibile su: `http://localhost:5002`
    - Dovresti vedere nel terminale: "🚀 Avvio Microservizio Stub: Gestione Esami"

## 📡 Informazioni del Servizio

- **Nome**: Gestione Esami
- **Porta**: `5002`
- **Base URL**: `http://localhost:5002`
- **Health Check**: `http://localhost:5002/health`

## 📋 API Endpoints

### 🔍 Esami - Consultazione

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/exams` | Lista tutti gli esami (con filtri) |
| `GET` | `/api/v1/exams/{id}` | Dettaglio singolo esame |
| `GET` | `/api/v1/exams/{id}/exists` | Verifica se un esame esiste |
| `GET` | `/api/v1/exams/{id}/info` | Info essenziali di un esame |
| `GET` | `/api/v1/exams/course/{courseId}` | Esami per un corso |
| `GET` | `/api/v1/exams/teacher/{teacherId}` | Esami per un docente |
| `GET` | `/api/v1/exams/calendar` | Calendario esami pubblico |
| `GET` | `/api/v1/exams/available` | Esami disponibili per iscrizione |

### 📝 Esami - Gestione

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/api/v1/exams` | Crea nuovo esame |

### ✏️ Iscrizioni Esami - Studenti

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/api/v1/exams/{examId}/enroll` | Iscrizione a esame |
| `GET` | `/api/v1/enrollments/my` | Le mie iscrizioni |
| `GET` | `/api/v1/enrollments/{id}` | Dettaglio iscrizione |
| `DELETE` | `/api/v1/enrollments/{id}` | Cancella iscrizione |

### 📊 Iscrizioni Esami - Amministrativi

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/exams/{examId}/enrollments` | Iscrizioni per esame |
| `GET` | `/api/v1/enrollments` | Tutte le iscrizioni |
| `GET` | `/api/v1/enrollments/student/{studentId}` | Iscrizioni di uno studente |

### 🎯 Voti Esami - Docenti

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/api/v1/exams/{examId}/grades` | Registra voto |
| `GET` | `/api/v1/exams/{examId}/grades` | Voti per esame |
| `GET` | `/api/v1/grades/{id}` | Dettaglio voto |

### 📈 Voti Esami - Studenti/Statistiche

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/grades/my` | I miei voti |
| `GET` | `/api/v1/grades/student/{studentId}` | Voti di uno studente |
| `GET` | `/api/v1/grades/course/{courseId}/statistics` | Statistiche voti corso |

### ❤️ Monitoraggio

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/health` | Health check del servizio |

## 🧪 Test degli Endpoint

### Test Base (Health Check)
```bash
curl http://localhost:5002/health
```
**Risposta attesa:**
```json
{
  "service": "Gestione Esami",
  "status": "UP",
  "timestamp": "2024-05-29T10:30:00",
  "port": 5002
}
```

### Recupera tutti gli esami
```bash
curl http://localhost:5002/api/v1/exams
```

### Recupera un esame specifico
```bash
curl http://localhost:5002/api/v1/exams/1
```

### Verifica esistenza esame (per il tuo microservizio)
```bash
curl http://localhost:5002/api/v1/exams/1/exists
```
**Risposta:**
```json
{
  "exists": true,
  "examId": 1,
  "examInfo": {
    "courseId": 101,
    "courseName": "Microservizi e Architetture Distribuite",
    "teacherId": 201,
    "teacherName": "Prof. Marco Rossi",
    "status": "SCHEDULED"
  }
}
```

### Recupera info esame (per valutazioni)
```bash
curl http://localhost:5002/api/v1/exams/1/info
```

### Calendario esami pubblico
```bash
curl http://localhost:5002/api/v1/exams/calendar
```

### Esami disponibili per iscrizione
```bash
curl http://localhost:5002/api/v1/exams/available
```

### Iscrizione a esame
```bash
curl -X POST http://localhost:5002/api/v1/exams/1/enroll \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 305,
    "studentName": "Test Studente",
    "notes": "Prima iscrizione"
  }'
```

### Le mie iscrizioni (simulato per studentId=301)
```bash
curl "http://localhost:5002/api/v1/enrollments/my?studentId=301"
```

### Registra un voto
```bash
curl -X POST http://localhost:5002/api/v1/exams/1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "enrollmentId": 1,
    "studentId": 301,
    "studentName": "Mario Rossi",
    "grade": 27,
    "withHonors": false,
    "notes": "Buona preparazione",
    "feedback": "Ottimo lavoro, continua così"
  }'
```

### I miei voti (simulato per studentId=301)
```bash
curl "http://localhost:5002/api/v1/grades/my?studentId=301"
```

### Statistiche voti per corso
```bash
curl http://localhost:5002/api/v1/grades/course/101/statistics
```

### Crea nuovo esame
```bash
curl -X POST http://localhost:5002/api/v1/exams \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": 104,
    "courseName": "Nuovo Corso Test",
    "teacherId": 204,
    "teacherName": "Prof. Test",
    "examDate": "2024-08-15",
    "examTime": "10:00:00",
    "classroom": "Aula Test",
    "maxEnrollments": 25
  }'
```

## 📊 Dati Mock Precaricati

### Esami Disponibili:
- **ID 1**: "Microservizi e Architetture Distribuite" (Corso 101, Prof. Rossi, 15/07/2024, SCHEDULED)
- **ID 2**: "Basi di Dati Avanzate" (Corso 102, Prof.ssa Bianchi, 18/07/2024, SCHEDULED)
- **ID 3**: "Microservizi e Architetture Distribuite" (Corso 101, Prof. Rossi, 20/06/2024, COMPLETED)
- **ID 4**: "Ingegneria del Software" (Corso 103, Prof. Verdi, 10/08/2024, SCHEDULED)

### Iscrizioni Disponibili:
- **ID 1**: Studente 301 (Mario Rossi) → Esame 1 (ENROLLED)
- **ID 2**: Studente 302 (Giulia Bianchi) → Esame 1 (ENROLLED)
- **ID 3**: Studente 301 (Mario Rossi) → Esame 2 (ENROLLED)
- **ID 4**: Studente 303 (Luca Verde) → Esame 3 (CONFIRMED) - già sostenuto
- **ID 5**: Studente 304 (Sara Neri) → Esame 1 (CANCELLED)

### Voti Disponibili:
- **ID 1**: Studente 303 → Esame 3: Voto 28 (senza lode)
- **ID 2**: Studente 305 → Esame 3: Voto 30 e lode

### Stati Disponibili:

**Esami:**
- `SCHEDULED` - Programmato
- `ACTIVE` - In corso
- `COMPLETED` - Completato
- `CANCELLED` - Annullato

**Iscrizioni:**
- `ENROLLED` - Iscritto
- `CONFIRMED` - Confermato
- `CANCELLED` - Cancellato
- `REJECTED` - Rifiutato

## 🔗 Integrazione con Microservizio Valutazione

Questo stub è progettato per supportare il tuo microservizio SpringBoot. Esempi di utilizzo:

### Nel tuo AssessmentDTO quando referenceType = "EXAM":
1. **Verifica esistenza**: `GET /api/v1/exams/{referenceId}/exists`
2. **Recupera info**: `GET /api/v1/exams/{referenceId}/info`
3. **Valida courseId e teacherId** dall'esame recuperato
4. **Verifica stato esame** (solo COMPLETED dovrebbe essere valutabile)

### Per recuperare voti esistenti:
1. **Voti esame**: `GET /api/v1/exams/{examId}/grades`
2. **Voti studente**: `GET /api/v1/grades/student/{studentId}`
3. **Statistiche corso**: `GET /api/v1/grades/course/{courseId}/statistics`

### Esempio configurazione SpringBoot:
```properties
# application.properties
exam.service.url=http://localhost:5002
```

### Esempio codice Java:
```java
// Verifica se un esame esiste e è completato
@Value("${exam.service.url:http://localhost:5002}")
private String examServiceUrl;

public boolean examExistsAndCompleted(Long examId) {
    try {
        ResponseEntity<Map> response = restTemplate.getForEntity(
            examServiceUrl + "/api/v1/exams/" + examId + "/exists", 
            Map.class
        );
        Map<String, Object> body = response.getBody();
        boolean exists = (Boolean) body.get("exists");
        if (!exists) return false;
        
        Map<String, Object> examInfo = (Map<String, Object>) body.get("examInfo");
        String status = (String) examInfo.get("status");
        return "COMPLETED".equals(status);
    } catch (Exception e) {
        return false;
    }
}
```

## 🛠️ Troubleshooting

### Il servizio non si avvia
1. **Verifica Python**: `python --version` (deve essere 3.8+)
2. **Verifica dipendenze**: `pip list` (deve includere Flask)
3. **Porta occupata**: Cambia porta nel codice o chiudi altri servizi sulla 5002

### Errore 404 su endpoint
- Verifica che l'URL sia corretto (includi `/api/v1/`)
- Controlla che il servizio sia avviato
- Usa `curl http://localhost:5002/health` per testare

### Errore CORS (se chiamato da browser)
- Il servizio include Flask-CORS configurato
- Verifica che Flask-CORS sia installato: `pip install Flask-CORS`

### Errore nei voti
- I voti devono essere tra 18 e 30
- Per voti con lode: voto=30 e withHonors=true

## 📝 Logs

Il servizio stampa automaticamente:
- 🚀 Messaggio di avvio con lista endpoint
- 📍 Tutte le richieste HTTP ricevute
- ❌ Eventuali errori

## ⚠️ Note Importanti

- **Solo per testing**: Questo è uno stub, i dati sono in memoria
- **Dati persi al riavvio**: Non c'è persistenza
- **Non per produzione**: Mancano autenticazione, validation, ecc.
- **Porta fissa**: Il servizio usa sempre la porta 5002
- **Simulazione studentId**: Per endpoint "my", usa il parametro `studentId` per simulare autenticazione

## 🔄 Riavvio del Servizio

Per riavviare il servizio:
1. Premi `Ctrl+C` nel terminale per fermarlo
2. Rilancia con `python app.py`
3. I dati mock torneranno allo stato originale

## 📚 Struttura Dati Mock

### ExamDTO
```json
{
  "id": 1,
  "courseId": 101,
  "courseName": "Microservizi e Architetture Distribuite",
  "teacherId": 201,
  "teacherName": "Prof. Marco Rossi",
  "examDate": "2024-07-15",
  "examTime": "09:00:00",
  "classroom": "Aula Magna A",
  "status": "SCHEDULED",
  "maxEnrollments": 50,
  "currentEnrollments": 25,
  "creationDate": "2024-05-01T10:00:00"
}
```

### EnrollmentDTO
```json
{
  "id": 1,
  "examId": 1,
  "studentId": 301,
  "studentName": "Mario Rossi",
  "status": "ENROLLED",
  "enrollmentDate": "2024-05-15T14:30:00",
  "notes": "Prima iscrizione",
  "adminNotes": ""
}
```

### GradeDTO
```json
{
  "id": 1,
  "enrollmentId": 4,
  "studentId": 303,
  "studentName": "Luca Verde",
  "examId": 3,
  "grade": 28,
  "withHonors": false,
  "recordingDate": "2024-06-20T16:30:00",
  "notes": "Ottima preparazione teorica",
  "feedback": "Molto bene sugli aspetti teorici, migliorare la parte pratica"
}
```