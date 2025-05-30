# 📚 Microservizio Stub - Gestione Compiti

Questo è un microservizio stub creato in Flask per simulare il sistema di Gestione Compiti per il testing del microservizio Valutazione e Feedback.

## 🚀 Avvio Rapido

### Prerequisiti
- Python 3.8 o superiore
- pip (gestore pacchetti Python)

### Installazione

1. **Vai nella directory del microservizio:**
   ```bash
   cd gestione-compiti
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
    - Il servizio sarà disponibile su: `http://localhost:5001`
    - Dovresti vedere nel terminale: "🚀 Avvio Microservizio Stub: Gestione Compiti"

## 📡 Informazioni del Servizio

- **Nome**: Gestione Compiti
- **Porta**: `5001`
- **Base URL**: `http://localhost:5001`
- **Health Check**: `http://localhost:5001/health`

## 📋 API Endpoints

### 🔍 Consultazione Compiti

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/assignments` | Recupera tutti i compiti |
| `GET` | `/api/v1/assignments/{id}` | Recupera un compito specifico |
| `GET` | `/api/v1/assignments/course/{courseId}` | Compiti per un corso |
| `GET` | `/api/v1/assignments/teacher/{teacherId}` | Compiti per un docente |
| `GET` | `/api/v1/assignments/{id}/exists` | Verifica se un compito esiste |
| `GET` | `/api/v1/assignments/{id}/info` | Info essenziali di un compito |

### 📝 Gestione Consegne

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/v1/submissions` | Tutte le consegne |
| `GET` | `/api/v1/submissions/assignment/{assignmentId}` | Consegne per un compito |
| `GET` | `/api/v1/submissions/student/{studentId}` | Consegne di uno studente |
| `GET` | `/api/v1/submissions/{id}` | Dettaglio di una consegna |
| `POST` | `/api/v1/submissions` | Crea nuova consegna |
| `PUT` | `/api/v1/submissions/{id}/status` | Aggiorna stato consegna |

### ✅ Creazione

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/api/v1/assignments` | Crea nuovo compito |

### ❤️ Monitoraggio

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/health` | Health check del servizio |

## 🧪 Test degli Endpoint

### Test Base (Health Check)
```bash
curl http://localhost:5001/health
```
**Risposta attesa:**
```json
{
  "service": "Gestione Compiti",
  "status": "UP",
  "timestamp": "2024-05-29T10:30:00"
}
```

### Recupera tutti i compiti
```bash
curl http://localhost:5001/api/v1/assignments
```

### Recupera un compito specifico
```bash
curl http://localhost:5001/api/v1/assignments/1
```

### Verifica esistenza compito (per il tuo microservizio)
```bash
curl http://localhost:5001/api/v1/assignments/1/exists
```
**Risposta:**
```json
{
  "exists": true,
  "assignmentId": 1
}
```

### Recupera info compito (per valutazioni)
```bash
curl http://localhost:5001/api/v1/assignments/1/info
```

### Compiti per corso
```bash
curl http://localhost:5001/api/v1/assignments/course/101
```

### Consegne per compito
```bash
curl http://localhost:5001/api/v1/submissions/assignment/1
```

### Crea nuovo compito
```bash
curl -X POST http://localhost:5001/api/v1/assignments \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nuovo Compito Test",
    "description": "Descrizione del compito",
    "courseId": 101,
    "teacherId": 201,
    "dueDate": "2024-07-01T23:59:00",
    "maxScore": 25.0
  }'
```

## 📊 Dati Mock Precaricati

### Compiti Disponibili:
- **ID 1**: "Progetto Microservizi" (Corso 101, Docente 201, Max: 30pt)
- **ID 2**: "Analisi Database Relazionali" (Corso 102, Docente 202, Max: 25pt)
- **ID 3**: "Presentazione Spring Boot" (Corso 101, Docente 201, Max: 20pt, CHIUSO)

### Consegne Disponibili:
- **ID 1**: Studente 301 → Compito 1 (SUBMITTED)
- **ID 2**: Studente 302 → Compito 1 (SUBMITTED)
- **ID 3**: Studente 301 → Compito 2 (SUBMITTED)

## 🔗 Integrazione con Microservizio Valutazione

Questo stub è progettato per supportare il tuo microservizio SpringBoot. Esempi di utilizzo:

### Nel tuo AssessmentDTO quando referenceType = "ASSIGNMENT":
1. **Verifica esistenza**: `GET /api/v1/assignments/{referenceId}/exists`
2. **Recupera info**: `GET /api/v1/assignments/{referenceId}/info`
3. **Valida courseId e teacherId** dal compito recuperato

### Esempio configurazione SpringBoot:
```properties
# application.properties
assignment.service.url=http://localhost:5001
```

## 🛠️ Troubleshooting

### Il servizio non si avvia
1. **Verifica Python**: `python --version` (deve essere 3.8+)
2. **Verifica dipendenze**: `pip list` (deve includere Flask)
3. **Porta occupata**: Cambia porta nel codice o chiudi altri servizi sulla 5001

### Errore 404 su endpoint
- Verifica che l'URL sia corretto (includi `/api/v1/`)
- Controlla che il servizio sia avviato
- Usa `curl http://localhost:5001/health` per testare

### Errore CORS (se chiamato da browser)
- Il servizio include Flask-CORS configurato
- Verifica che Flask-CORS sia installato: `pip install Flask-CORS`

## 📝 Logs

Il servizio stampa automaticamente:
- 🚀 Messaggio di avvio con lista endpoint
- 📍 Tutte le richieste HTTP ricevute
- ❌ Eventuali errori

## ⚠️ Note Importanti

- **Solo per testing**: Questo è uno stub, i dati sono in memoria
- **Dati persi al riavvio**: Non c'è persistenza
- **Non per produzione**: Mancano autenticazione, validation, ecc.
- **Porta fissa**: Il servizio usa sempre la porta 5001

## 🔄 Riavvio del Servizio

Per riavviare il servizio:
1. Premi `Ctrl+C` nel terminale per fermarlo
2. Rilancia con `python app.py`
3. I dati mock torneranno allo stato originale