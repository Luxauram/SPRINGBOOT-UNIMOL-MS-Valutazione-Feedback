# 📊 Microservizio Stub - Analisi e Reportistica

Questo è un microservizio stub creato in Flask per simulare il sistema di Analisi e Reportistica per il testing del microservizio Valutazione e Feedback.

## 🚀 Avvio Rapido

### Prerequisiti
- Python 3.8 o superiore
- pip (gestore pacchetti Python)

### Installazione

1. **Vai nella directory del microservizio:**
   ```bash
   cd analisi-reportistica
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
    - Il servizio sarà disponibile su: `http://localhost:5004`
    - Dovresti vedere nel terminale: "🚀 Avvio Microservizio Stub: Analisi e Reportistica"

## 📡 Informazioni del Servizio

- **Nome**: Analisi e Reportistica
- **Porta**: `5004`
- **Base URL**: `http://localhost:5004`
- **Health Check**: `http://localhost:5004/health`

## 📋 API Endpoints

### 📊 Report Studenti

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/reports/students` | Recupera tutti i report degli studenti |
| `GET` | `/api/v1/reports/students/{id}` | Report dettagliato di uno studente |
| `GET` | `/api/v1/reports/students/{id}/performance` | Sommario performance studente |

### 📚 Report Corsi

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/reports/courses` | Recupera tutti i report dei corsi |
| `GET` | `/api/v1/reports/courses/{id}` | Report dettagliato di un corso |
| `GET` | `/api/v1/reports/courses/{id}/students` | Performance studenti in un corso |

### 👨‍🏫 Report Docenti

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/reports/teachers` | Recupera tutti i report dei docenti |
| `GET` | `/api/v1/reports/teachers/{id}` | Report dettagliato di un docente |
| `GET` | `/api/v1/reports/teachers/{id}/evaluations` | Sommario valutazioni date dal docente |

### 📈 Gestione Dati (Per Integrazione)

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/api/v1/data/evaluations` | Riceve dati di valutazione |
| `GET` | `/api/v1/data/evaluations` | Recupera dati valutazioni (con filtri) |

### 📊 Analisi Generali

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/analytics/summary` | Sommario generale delle analisi |

### 🔗 Integrazione e Supporto

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/integration/student/{id}/exists` | Verifica esistenza dati studente |
| `GET` | `/api/v1/integration/course/{id}/exists` | Verifica esistenza dati corso |

### ❤️ Monitoraggio

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/health` | Health check del servizio |

## 🧪 Test degli Endpoint

### Test Base (Health Check)
```bash
curl http://localhost:5004/health
```
**Risposta attesa:**
```json
{
  "service": "Analisi e Reportistica",
  "status": "UP",
  "timestamp": "2024-05-29T10:30:00",
  "version": "1.0.0"
}
```

### Report Studenti

#### Recupera tutti i report studenti
```bash
curl http://localhost:5004/api/v1/reports/students
```

#### Report di uno studente specifico
```bash
curl http://localhost:5004/api/v1/reports/students/301
```
**Risposta:**
```json
{
  "studentId": 301,
  "studentName": "Mario Rossi",
  "totalAssignments": 8,
  "completedAssignments": 7,
  "averageScore": 24.5,
  "performance": "EXCELLENT",
  "trends": {
    "improving": true,
    "consistentPerformance": true
  }
}
```

#### Performance sommario studente
```bash
curl http://localhost:5004/api/v1/reports/students/301/performance
```

### Report Corsi

#### Recupera tutti i report corsi
```bash
curl http://localhost:5004/api/v1/reports/courses
```

#### Report di un corso specifico
```bash
curl http://localhost:5004/api/v1/reports/courses/101
```

#### Performance studenti in un corso
```bash
curl http://localhost:5004/api/v1/reports/courses/101/students
```

### Report Docenti

#### Recupera tutti i report docenti
```bash
curl http://localhost:5004/api/v1/reports/teachers
```

#### Report di un docente specifico
```bash
curl http://localhost:5004/api/v1/reports/teachers/201
```

#### Valutazioni date da un docente
```bash
curl http://localhost:5004/api/v1/reports/teachers/201/evaluations
```

### Invio Dati di Valutazione (Dal tuo SpringBoot)

#### Invia una nuova valutazione
```bash
curl -X POST http://localhost:5004/api/v1/data/evaluations \
  -H "Content-Type: application/json" \
  -d '{
    "type": "ASSIGNMENT",
    "referenceId": 1,
    "studentId": 301,
    "courseId": 101,
    "teacherId": 201,
    "score": 27.5,
    "maxScore": 30.0,
    "feedback": "Ottimo lavoro sui microservizi",
    "evaluatedBy": 201
  }'
```

#### Recupera dati valutazioni con filtri
```bash
# Tutte le valutazioni
curl http://localhost:5004/api/v1/data/evaluations

# Valutazioni di uno studente
curl "http://localhost:5004/api/v1/data/evaluations?studentId=301"

# Valutazioni per un corso
curl "http://localhost:5004/api/v1/data/evaluations?courseId=101"

# Valutazioni per tipo
curl "http://localhost:5004/api/v1/data/evaluations?type=ASSIGNMENT"
```

### Analisi Generali

#### Sommario sistema
```bash
curl http://localhost:5004/api/v1/analytics/summary
```
**Risposta:**
```json
{
  "totalEvaluations": 15,
  "totalStudents": 3,
  "totalCourses": 3,
  "totalTeachers": 2,
  "overallAveragePercentage": 85.2,
  "systemStats": {
    "activeStudents": 3,
    "excellentPerformers": 1,
    "averageTeacherRating": 4.0
  }
}
```

### Test Integrazione (Per il tuo microservizio)

#### Verifica esistenza studente
```bash
curl http://localhost:5004/api/v1/integration/student/301/exists
```

#### Verifica esistenza corso
```bash
curl http://localhost:5004/api/v1/integration/course/101/exists
```

## 📊 Dati Mock Precaricati

### Studenti con Dati Analitici:
- **ID 301**: Mario Rossi - EXCELLENT (avg: 24.5, completion: 87.5%)
- **ID 302**: Giulia Bianchi - GOOD (avg: 21.2, completion: 83.3%)
- **ID 303**: Luca Verdi - AVERAGE (avg: 18.7, completion: 75.0%)

### Corsi con Statistiche:
- **ID 101**: Ingegneria del Software (25 studenti, 92% pass rate)
- **ID 102**: Database e Sistemi Informativi (18 studenti, 85% pass rate)
- **ID 103**: Programmazione Web (30 studenti, 95% pass rate)

### Docenti con Valutazioni:
- **ID 201**: Prof. Alessandro Neri (rating: 4.2/5, 43 studenti)
- **ID 202**: Prof.ssa Maria Giovanna Pecci (rating: 3.8/5, 18 studenti)

### Valutazioni Precaricate:
- **ID 1**: Studente 301 → Compito 1 (28.5/30)
- **ID 2**: Studente 302 → Compito 1 (25.0/30)
- **ID 3**: Studente 301 → Esame 1 (29.0/30)

## 🔗 Integrazione con Microservizio Valutazione e Feedback

Questo stub è progettato per raccogliere e analizzare i dati dal tuo microservizio SpringBoot:

### 1. Invio Dati di Valutazione
Quando crei una valutazione nel tuo SpringBoot, invia i dati qui:
```http
POST http://localhost:5004/api/v1/data/evaluations
Content-Type: application/json

{
  "type": "ASSIGNMENT" | "EXAM",
  "referenceId": 123,
  "studentId": 301,
  "courseId": 101,
  "teacherId": 201,
  "score": 25.5,
  "maxScore": 30.0,
  "feedback": "Feedback dettagliato...",
  "evaluatedBy": 201
}
```

### 2. Configurazione SpringBoot
```properties
# application.properties
analytics.service.url=http://localhost:5004
```

### 3. Esempio Service nel tuo SpringBoot
```java
@Service
public class AnalyticsService {
    
    @Value("${analytics.service.url}")
    private String analyticsServiceUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public void sendEvaluationData(AssessmentDTO assessment) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("type", assessment.getReferenceType());
            data.put("referenceId", assessment.getReferenceId());
            data.put("studentId", assessment.getStudentId());
            data.put("courseId", assessment.getCourseId());
            data.put("teacherId", assessment.getTeacherId());
            data.put("score", assessment.getScore());
            data.put("maxScore", assessment.getMaxScore());
            data.put("feedback", assessment.getFeedback());
            data.put("evaluatedBy", assessment.getEvaluatedBy());
            
            restTemplate.postForObject(
                analyticsServiceUrl + "/api/v1/data/evaluations",
                data,
                Map.class
            );
        } catch (Exception e) {
            log.warn("Failed to send analytics data: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getStudentReport(Long studentId) {
        return restTemplate.getForObject(
            analyticsServiceUrl + "/api/v1/reports/students/" + studentId,
            Map.class
        );
    }
}
```

## 🛠️ Funzionalità Avanzate

### Filtri Avanzati per Valutazioni
```bash
# Combinazione di filtri
curl "http://localhost:5004/api/v1/data/evaluations?studentId=301&courseId=101&type=ASSIGNMENT"
```

### Calcoli in Tempo Reale
Il servizio calcola statistiche live quando richiedi report specifici:
- Media percentuali aggiornata
- Ultimo timestamp di valutazione
- Conteggio totale valutazioni

### Supporto CORS
Il servizio include supporto CORS per chiamate da browser/frontend.

## 🛠️ Troubleshooting

### Il servizio non si avvia
1. **Verifica Python**: `python --version` (deve essere 3.8+)
2. **Verifica dipendenze**: `pip list` (deve includere Flask)
3. **Porta occupata**: Cambia porta nel codice o chiudi altri servizi sulla 5004

### Errore 404 su endpoint
- Verifica che l'URL sia corretto (includi `/api/v1/`)
- Controlla che il servizio sia avviato
- Usa `curl http://localhost:5004/health` per testare

### Errore nel POST di valutazioni
- Verifica che il Content-Type sia `application/json`
- Controlla che tutti i campi obbligatori siano presenti
- Verifica formato dei dati JSON

### Dati non persistenti
- **Normale**: I dati sono in memoria, si perdono al riavvio
- **Per persistenza**: Implementa database (non necessario per testing)

## 📝 Logs

Il servizio stampa automaticamente:
- 🚀 Messaggio di avvio con lista endpoint
- 📍 Tutte le richieste HTTP ricevute con parametri
- 📊 Dati ricevuti nelle POST
- ❌ Eventuali errori

## ⚠️ Note Importanti

- **Solo per testing**: Questo è uno stub, i dati sono in memoria
- **Dati persi al riavvio**: Non c'è persistenza (normale per testing)
- **Non per produzione**: Mancano autenticazione, validation completa, ecc.
- **Porta fissa**: Il servizio usa sempre la porta 5004
- **Thread-safe**: Flask gestisce richieste concorrenti

## 🔄 Riavvio del Servizio

Per riavviare il servizio:
1. Premi `Ctrl+C` nel terminale per fermarlo
2. Rilancia con `python app.py`
3. I dati mock torneranno allo stato originale
4. Eventuali dati inviati tramite POST saranno persi

## 🎯 Scenari di Test Consigliati

### Test Base
1. Avvia il servizio
2. Testa health check
3. Recupera report esistenti

### Test Integrazione
1. Avvia il tuo SpringBoot + questo stub
2. Crea una valutazione nel tuo microservizio
3. Verifica che invii dati a questo stub
4. Controlla che i report si aggiornino

### Test Filtri
1. Invia diverse valutazioni con POST
2. Testa filtri GET con parametri diversi
3. Verifica consistenza dati

## 📞 Supporto

Per problemi specifici:
1. Controlla i logs del servizio
2. Testa endpoint singolarmente con curl
3. Verifica formato JSON nelle POST
4. Controlla che tutti i servizi siano su porte diverse

---

**Buon testing! 🚀📊**