# 👥 Microservizio Stub - Gestione Utenti e Ruoli

Questo è un microservizio stub creato in Flask per simulare il sistema di Gestione Utenti e Ruoli per il testing del microservizio Valutazione e Feedback.

## 🚀 Avvio Rapido

### Prerequisiti
- Python 3.8 o superiore
- pip (gestore pacchetti Python)

### Installazione

1. **Vai nella directory del microservizio:**
   ```bash
   cd gestione-utenti
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
    - Il servizio sarà disponibile su: `http://localhost:5003`
    - Dovresti vedere nel terminale: "🚀 Avvio Microservizio Stub: Gestione Utenti e Ruoli"

## 📡 Informazioni del Servizio

- **Nome**: Gestione Utenti e Ruoli
- **Porta**: `5003`
- **Base URL**: `http://localhost:5003`
- **Health Check**: `http://localhost:5003/health`

## 🔐 Credenziali di Test Precaricate

| Username | Password | Ruolo | Email |
|----------|----------|-------|-------|
| `superadmin` | `password123` | SUPER_ADMIN | superadmin@university.it |
| `admin.rossi` | `admin123` | ADMIN | admin.rossi@university.it |
| `prof.bianchi` | `prof123` | DOCENTE | prof.bianchi@university.it |
| `stud.verdi` | `stud123` | STUDENTE | stud.verdi@student.university.it |
| `segr.neri` | `segr123` | AMMINISTRATIVO | segr.neri@university.it |

## 📋 API Endpoints

### 🔰 Super Admin

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/init/superadmin` | Crea account Super Admin se assente |

### 👨‍💼 Admin+ (Richiede autenticazione ADMIN/SUPER_ADMIN)

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/users` | Creazione nuovo utente |
| `PUT` | `/users/{id}` | Modifica utente |
| `GET` | `/users/{id}` | Visualizzazione utente |
| `DELETE` | `/users/{id}` | Eliminazione utente |
| `GET` | `/roles` | Lista ruoli disponibili |
| `POST` | `/users/{id}/roles` | Assegna ruolo a utente |
| `PUT` | `/users/{id}/roles` | Aggiorna ruoli utente |

### 🔓 Permessi Generici (Autenticazione)

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `POST` | `/auth/login` | Login utente |
| `POST` | `/auth/logout` | Logout utente |
| `POST` | `/refresh-token` | Rinnovo scadenza token |
| `GET` | `/users/profile` | Visualizza profilo utente |
| `PUT` | `/users/profile` | Modifica profilo utente |
| `POST` | `/users/forgot-password` | Reset password utente |
| `PUT` | `/users/change-password` | Modifica password |

### 🔗 Integrazione (Per altri microservizi)

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/users/exists/{id}` | Verifica se un utente esiste |
| `GET` | `/users/{id}/info` | Info essenziali di un utente |
| `GET` | `/users/role/{roleName}` | Ottieni utenti per nome ruolo |

### ❤️ Monitoraggio

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/health` | Health check del servizio |

## 🧪 Test degli Endpoint

### Test Base (Health Check)
```bash
curl http://localhost:5003/health
```
**Risposta attesa:**
```json
{
  "service": "Gestione Utenti e Ruoli",
  "status": "UP",
  "timestamp": "2024-05-29T10:30:00",
  "version": "1.0.0"
}
```

### 1. Autenticazione - Login
```bash
curl -X POST http://localhost:5003/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin.rossi",
    "password": "admin123"
  }'
```
**Risposta:** Un token JWT che dovrai usare negli endpoint protetti.

### 2. Profilo Utente (richiede token)
```bash
# Prima salva il token dalla risposta del login
TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:5003/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Lista Ruoli (richiede token admin)
```bash
curl -X GET http://localhost:5003/roles \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Visualizza Utente Specifico (richiede token admin)
```bash
curl -X GET http://localhost:5003/users/user_3 \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Crea Nuovo Utente (richiede token admin)
```bash
curl -X POST http://localhost:5003/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuovo.utente",
    "email": "nuovo@university.it",
    "password": "password123",
    "nome": "Nuovo",
    "cognome": "Utente",
    "idRuolo": "role_4"
  }'
```

### 6. Verifica Esistenza Utente (per integrazione)
```bash
curl http://localhost:5003/users/exists/user_1
```
**Risposta:**
```json
{
  "exists": true,
  "userId": "user_1"
}
```

### 7. Info Utente (per integrazione)
```bash
curl http://localhost:5003/users/user_3/info
```
**Risposta:**
```json
{
  "id": "user_3",
  "username": "prof.bianchi",
  "nome": "Anna",
  "cognome": "Bianchi",
  "email": "prof.bianchi@university.it",
  "ruolo": "DOCENTE",
  "ruoloId": "role_3"
}
```

### 8. Utenti per Ruolo (per integrazione)
```bash
curl http://localhost:5003/users/role/DOCENTE
```

### 9. Modifica Profilo (autenticato)
```bash
curl -X PUT http://localhost:5003/users/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nuova.email@university.it",
    "nome": "NuovoNome"
  }'
```

### 10. Cambio Password (autenticato)
```bash
curl -X PUT http://localhost:5003/users/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "admin123",
    "newPassword": "nuovaPassword123"
  }'
```

## 📊 Dati Mock Precaricati

### Ruoli Disponibili:
- **role_1**: SUPER_ADMIN - Amministratore di sistema con accesso completo
- **role_2**: ADMIN - Amministratore con permessi di gestione utenti
- **role_3**: DOCENTE - Docente universitario
- **role_4**: STUDENTE - Studente universitario
- **role_5**: AMMINISTRATIVO - Personale amministrativo

### Utenti Disponibili:
- **user_1**: superadmin (SUPER_ADMIN)
- **user_2**: admin.rossi (ADMIN)
- **user_3**: prof.bianchi (DOCENTE)
- **user_4**: stud.verdi (STUDENTE)
- **user_5**: segr.neri (AMMINISTRATIVO)

## 🔐 Sistema di Autenticazione

### Come Funziona
1. **Login**: Invia username e password a `/auth/login`
2. **Token JWT**: Ricevi un token che scade dopo 24 ore
3. **Autorizzazione**: Includi il token nell'header `Authorization: Bearer <token>`
4. **Refresh**: Usa `/refresh-token` per rinnovare il token prima della scadenza

### Livelli di Autorizzazione
- **Pubblico**: Health check, login, reset password
- **Autenticato**: Profilo, cambio password, logout
- **Admin+**: Gestione utenti, ruoli (solo ADMIN e SUPER_ADMIN)
- **Super Admin**: Inizializzazione sistema

## 🔗 Integrazione con Microservizio Valutazione

Questo stub è progettato per supportare il tuo microservizio SpringBoot. Esempi di utilizzo:

### Nel tuo microservizio quando hai bisogno di validare utenti:
1. **Verifica esistenza**: `GET /users/exists/{userId}`
2. **Recupera info**: `GET /users/{userId}/info`
3. **Valida ruolo**: Controlla il campo `ruolo` nella risposta

### Esempio configurazione SpringBoot:
```properties
# application.properties
user.service.url=http://localhost:5003
```

### Esempio codice Java:
```java
// Nel tuo microservizio SpringBoot
@Value("${user.service.url:http://localhost:5003}")
private String userServiceUrl;

public boolean userExists(String userId) {
    try {
        ResponseEntity<Map> response = restTemplate.getForEntity(
            userServiceUrl + "/users/exists/" + userId, 
            Map.class
        );
        return (Boolean) response.getBody().get("exists");
    } catch (Exception e) {
        return false;
    }
}

public UserInfo getUserInfo(String userId) {
    try {
        ResponseEntity<UserInfo> response = restTemplate.getForEntity(
            userServiceUrl + "/users/" + userId + "/info", 
            UserInfo.class
        );
        return response.getBody();
    } catch (Exception e) {
        return null;
    }
}
```

## 🛠️ Troubleshooting

### Il servizio non si avvia
1. **Verifica Python**: `python --version` (deve essere 3.8+)
2. **Verifica dipendenze**: `pip list` (deve includere Flask, PyJWT)
3. **Porta occupata**: Cambia porta nel codice o chiudi altri servizi sulla 5003

### Errore 401 (Unauthorized)
- Verifica di aver fatto il login e ottenuto un token
- Controlla che il token sia nell'header: `Authorization: Bearer <token>`
- Il token potrebbe essere scaduto (durata: 24 ore)

### Errore 403 (Forbidden)
- L'utente autenticato non ha i permessi necessari
- Verifica il ruolo dell'utente (alcuni endpoint richiedono ADMIN+)

### Errore CORS (se chiamato da browser)
- Il servizio include Flask-CORS configurato
- Verifica che Flask-CORS sia installato: `pip install Flask-CORS`

## 📝 Logs

Il servizio stampa automaticamente:
- 🚀 Messaggio di avvio con credenziali di test
- 📍 Tutte le richieste HTTP ricevute
- 🔐 Eventi di autenticazione (login/logout)
- ❌ Eventuali errori di validazione

## 🔧 Personalizzazione

### Modifica Ruoli
Puoi modificare i ruoli disponibili nel file `app.py` nella sezione `roles_data`.

### Aggiungi Utenti
Puoi aggiungere utenti di test nella sezione `users_data` del file `app.py`.

### Modifica Token JWT
- **Scadenza**: Modifica `timedelta(hours=24)` nella funzione `create_jwt_token`
- **Secret**: Cambia `JWT_SECRET` per maggiore sicurezza

## ⚠️ Note Importanti

- **Solo per testing**: Questo è uno stub, i dati sono in memoria
- **Dati persi al riavvio**: Non c'è persistenza su database
- **Security semplificata**: Password MD5, JWT secret fisso
- **Non per produzione**: Mancano validazioni avanzate, rate limiting, ecc.
- **Porta fissa**: Il servizio usa sempre la porta 5003

## 🔄 Riavvio del Servizio

Per riavviare il servizio:
1. Premi `Ctrl+C` nel terminale per fermarlo
2. Rilancia con `python app.py`
3. I dati mock torneranno allo stato originale
4. I token attivi verranno invalidati

## 📚 Esempi di Workflow Completo

### Workflow Admin - Crea Nuovo Docente
```bash
# 1. Login come admin
TOKEN=$(curl -s -X POST http://localhost:5003/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin.rossi","password":"admin123"}' | tr -d '"')

# 2. Visualizza ruoli disponibili
curl -H "Authorization: Bearer $TOKEN" http://localhost:5003/roles

# 3. Crea nuovo docente
curl -X POST http://localhost:5003/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "prof.nuovo",
    "email": "prof.nuovo@university.it", 
    "password": "docente123",
    "nome": "Nuovo",
    "cognome": "Professore",
    "idRuolo": "role_3"
  }'

# 4. Verifica creazione
curl -H "Authorization: Bearer $TOKEN" http://localhost:5003/users/role/DOCENTE
```

### Workflow Studente - Gestione Profilo
```bash
# 1. Login come studente
TOKEN=$(curl -s -X POST http://localhost:5003/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"stud.verdi","password":"stud123"}' | tr -d '"')

# 2. Visualizza profilo
curl -H "Authorization: Bearer $TOKEN" http://localhost:5003/users/profile

# 3. Modifica email
curl -X PUT http://localhost:5003/users/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"email": "luca.verdi.nuovo@student.university.it"}'

# 4. Cambia password
curl -X PUT http://localhost:5003/users/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "stud123",
    "newPassword": "nuovaPassword123"
  }'
```