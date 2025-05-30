from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime, timedelta
import jwt
import hashlib
import time

app = Flask(__name__)
CORS(app)

# Configurazione JWT
JWT_SECRET = "stub_secret_key_for_testing_only"
JWT_ALGORITHM = "HS256"

# =============================================================================
# DATI MOCK IN MEMORIA
# =============================================================================

# Ruoli predefiniti
roles_data = [
    {
        "id": "role_1",
        "nome": "SUPER_ADMIN",
        "descrizione": "Amministratore di sistema con accesso completo"
    },
    {
        "id": "role_2",
        "nome": "ADMIN",
        "descrizione": "Amministratore con permessi di gestione utenti"
    },
    {
        "id": "role_3",
        "nome": "DOCENTE",
        "descrizione": "Docente universitario"
    },
    {
        "id": "role_4",
        "nome": "STUDENTE",
        "descrizione": "Studente universitario"
    },
    {
        "id": "role_5",
        "nome": "AMMINISTRATIVO",
        "descrizione": "Personale amministrativo"
    }
]

# Utenti predefiniti (password hashate con MD5 per semplicità)
users_data = [
    {
        "id": "user_1",
        "username": "superadmin",
        "email": "superadmin@university.it",
        "password": hashlib.md5("password123".encode()).hexdigest(),
        "nome": "Super",
        "cognome": "Admin",
        "ruolo": "role_1",
        "dataCreazione": int(time.time() * 1000) - 86400000,  # 1 giorno fa
        "ultimoLogin": int(time.time() * 1000) - 3600000      # 1 ora fa
    },
    {
        "id": "user_2",
        "username": "admin.rossi",
        "email": "admin.rossi@university.it",
        "password": hashlib.md5("admin123".encode()).hexdigest(),
        "nome": "Mario",
        "cognome": "Rossi",
        "ruolo": "role_2",
        "dataCreazione": int(time.time() * 1000) - 172800000,  # 2 giorni fa
        "ultimoLogin": int(time.time() * 1000) - 7200000       # 2 ore fa
    },
    {
        "id": "user_3",
        "username": "prof.bianchi",
        "email": "prof.bianchi@university.it",
        "password": hashlib.md5("prof123".encode()).hexdigest(),
        "nome": "Anna",
        "cognome": "Bianchi",
        "ruolo": "role_3",
        "dataCreazione": int(time.time() * 1000) - 259200000,  # 3 giorni fa
        "ultimoLogin": int(time.time() * 1000) - 1800000       # 30 min fa
    },
    {
        "id": "user_4",
        "username": "stud.verdi",
        "email": "stud.verdi@student.university.it",
        "password": hashlib.md5("stud123".encode()).hexdigest(),
        "nome": "Luca",
        "cognome": "Verdi",
        "ruolo": "role_4",
        "dataCreazione": int(time.time() * 1000) - 345600000,  # 4 giorni fa
        "ultimoLogin": int(time.time() * 1000) - 900000        # 15 min fa
    },
    {
        "id": "user_5",
        "username": "segr.neri",
        "email": "segr.neri@university.it",
        "password": hashlib.md5("segr123".encode()).hexdigest(),
        "nome": "Giulia",
        "cognome": "Neri",
        "ruolo": "role_5",
        "dataCreazione": int(time.time() * 1000) - 432000000,  # 5 giorni fa
        "ultimoLogin": int(time.time() * 1000) - 10800000      # 3 ore fa
    }
]

# Token JWT attivi (per simulare sessioni)
active_tokens = {}

# =============================================================================
# HELPER FUNCTIONS
# =============================================================================

def get_role_by_id(role_id):
    """Trova un ruolo per ID"""
    return next((role for role in roles_data if role["id"] == role_id), None)

def get_user_by_id(user_id):
    """Trova un utente per ID"""
    return next((user for user in users_data if user["id"] == user_id), None)

def get_user_by_username(username):
    """Trova un utente per username"""
    return next((user for user in users_data if user["username"] == username), None)

def get_user_by_email(email):
    """Trova un utente per email"""
    return next((user for user in users_data if user["email"] == email), None)

def generate_user_id():
    """Genera un nuovo ID utente"""
    max_id = max([int(user["id"].split("_")[1]) for user in users_data])
    return f"user_{max_id + 1}"

def create_jwt_token(user_id, username, role_id):
    """Crea un token JWT"""
    payload = {
        "userId": user_id,
        "username": username,
        "roleId": role_id,
        "exp": datetime.utcnow() + timedelta(hours=24),
        "iat": datetime.utcnow()
    }
    token = jwt.encode(payload, JWT_SECRET, algorithm=JWT_ALGORITHM)
    return token

def verify_jwt_token(token):
    """Verifica e decodifica un token JWT"""
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        return payload
    except jwt.ExpiredSignatureError:
        return None
    except jwt.InvalidTokenError:
        return None

def format_user_response(user, include_sensitive=False):
    """Formatta la risposta utente"""
    role = get_role_by_id(user["ruolo"])
    response = {
        "idUtente": user["id"],
        "username": user["username"],
        "email": user["email"],
        "nome": user["nome"],
        "cognome": user["cognome"],
        "ruolo": {
            "id": role["id"],
            "nome": role["nome"],
            "descrizione": role["descrizione"]
        } if role else None,
        "dataCreazione": user["dataCreazione"],
        "ultimoLogin": user["ultimoLogin"]
    }

    if include_sensitive:
        response["password"] = user["password"]

    return response

def get_auth_token():
    """Estrae il token JWT dall'header Authorization"""
    auth_header = request.headers.get('Authorization')
    if auth_header and auth_header.startswith('Bearer '):
        return auth_header.split(' ')[1]
    return None

def require_auth():
    """Decorator per richiedere autenticazione"""
    def decorator(f):
        def wrapper(*args, **kwargs):
            token = get_auth_token()
            if not token:
                return jsonify({"error": "Token mancante"}), 401

            payload = verify_jwt_token(token)
            if not payload:
                return jsonify({"error": "Token non valido o scaduto"}), 401

            # Aggiungi i dati utente alla richiesta
            request.user_data = payload
            return f(*args, **kwargs)
        wrapper.__name__ = f.__name__
        return wrapper
    return decorator

def require_admin():
    """Decorator per richiedere permessi admin"""
    def decorator(f):
        @require_auth()
        def wrapper(*args, **kwargs):
            user_role_id = request.user_data.get('roleId')
            role = get_role_by_id(user_role_id)

            if not role or role["nome"] not in ["SUPER_ADMIN", "ADMIN"]:
                return jsonify({"error": "Permessi insufficienti"}), 403

            return f(*args, **kwargs)
        wrapper.__name__ = f.__name__
        return wrapper
    return decorator

# =============================================================================
# HEALTH CHECK
# =============================================================================

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "service": "Gestione Utenti e Ruoli",
        "status": "UP",
        "timestamp": datetime.now().isoformat(),
        "version": "1.0.0"
    })

# =============================================================================
# SUPER ADMIN ENDPOINTS
# =============================================================================

@app.route('/init/superadmin', methods=['POST'])
def init_superadmin():
    """Crea account Super Admin se assente"""
    data = request.get_json()

    if not data or not all(k in data for k in ['username', 'email', 'password']):
        return jsonify({"error": "Dati mancanti: username, email, password richiesti"}), 400

    # Controlla se esiste già un super admin
    existing_super_admin = next((user for user in users_data
                                 if get_role_by_id(user["ruolo"]) and
                                 get_role_by_id(user["ruolo"])["nome"] == "SUPER_ADMIN"), None)

    if existing_super_admin:
        return jsonify(False)

    # Crea il super admin
    new_user = {
        "id": generate_user_id(),
        "username": data["username"],
        "email": data["email"],
        "password": hashlib.md5(data["password"].encode()).hexdigest(),
        "nome": "Super",
        "cognome": "Admin",
        "ruolo": "role_1",  # SUPER_ADMIN
        "dataCreazione": int(time.time() * 1000),
        "ultimoLogin": 0
    }

    users_data.append(new_user)
    return jsonify(True)

# =============================================================================
# ADMIN+ ENDPOINTS
# =============================================================================

@app.route('/users', methods=['POST'])
@require_admin()
def create_user():
    """Creazione nuovo utente"""
    data = request.get_json()

    required_fields = ['username', 'email', 'password', 'nome', 'cognome', 'idRuolo']
    if not data or not all(k in data for k in required_fields):
        return jsonify({"error": f"Dati mancanti: {', '.join(required_fields)} richiesti"}), 400

    # Verifica che username e email non esistano già
    if get_user_by_username(data["username"]):
        return jsonify({"error": "Username già esistente"}), 409

    if get_user_by_email(data["email"]):
        return jsonify({"error": "Email già esistente"}), 409

    # Verifica che il ruolo esista
    if not get_role_by_id(data["idRuolo"]):
        return jsonify({"error": "Ruolo non trovato"}), 404

    # Crea il nuovo utente
    new_user = {
        "id": generate_user_id(),
        "username": data["username"],
        "email": data["email"],
        "password": hashlib.md5(data["password"].encode()).hexdigest(),
        "nome": data["nome"],
        "cognome": data["cognome"],
        "ruolo": data["idRuolo"],
        "dataCreazione": int(time.time() * 1000),
        "ultimoLogin": 0
    }

    users_data.append(new_user)
    return jsonify(True)

@app.route('/users/<user_id>', methods=['PUT'])
@require_admin()
def update_user(user_id):
    """Modifica utente"""
    user = get_user_by_id(user_id)
    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"error": "Dati mancanti"}), 400

    # Aggiorna i campi forniti
    if 'email' in data:
        # Verifica che l'email non sia già usata da un altro utente
        existing_user = get_user_by_email(data['email'])
        if existing_user and existing_user['id'] != user_id:
            return jsonify({"error": "Email già utilizzata"}), 409
        user['email'] = data['email']

    if 'nome' in data:
        user['nome'] = data['nome']

    if 'cognome' in data:
        user['cognome'] = data['cognome']

    return jsonify(format_user_response(user))

@app.route('/users/<user_id>', methods=['GET'])
@require_admin()
def get_user(user_id):
    """Visualizzazione utente"""
    user = get_user_by_id(user_id)
    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    return jsonify(format_user_response(user))

@app.route('/users/<user_id>', methods=['DELETE'])
@require_admin()
def delete_user(user_id):
    """Eliminazione utente"""
    user = get_user_by_id(user_id)
    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    # Non permettere eliminazione del proprio account
    if request.user_data.get('userId') == user_id:
        return jsonify({"error": "Non puoi eliminare il tuo stesso account"}), 400

    users_data.remove(user)
    return jsonify(True)

@app.route('/roles', methods=['GET'])
@require_admin()
def get_roles():
    """Lista ruoli disponibili"""
    formatted_roles = []
    for role in roles_data:
        formatted_roles.append({
            "idRuolo": role["id"],
            "nome": role["nome"],
            "descrizione": role["descrizione"]
        })

    return jsonify(formatted_roles)

@app.route('/users/<user_id>/roles', methods=['POST'])
@require_admin()
def assign_role(user_id):
    """Assegna ruolo a utente"""
    user = get_user_by_id(user_id)
    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    data = request.get_json()
    if not data or 'idRuolo' not in data:
        return jsonify({"error": "ID ruolo mancante"}), 400

    role = get_role_by_id(data['idRuolo'])
    if not role:
        return jsonify({"error": "Ruolo non trovato"}), 404

    user['ruolo'] = data['idRuolo']
    return jsonify(True)

@app.route('/users/<user_id>/roles', methods=['PUT'])
@require_admin()
def update_user_role(user_id):
    """Aggiorna ruoli utente"""
    return assign_role(user_id)  # Stessa logica

# =============================================================================
# PERMESSI GENERICI (AUTENTICAZIONE)
# =============================================================================

@app.route('/auth/login', methods=['POST'])
def login():
    """Login utente"""
    data = request.get_json()

    if not data or not all(k in data for k in ['username', 'password']):
        return jsonify({"error": "Username e password richiesti"}), 400

    user = get_user_by_username(data['username'])
    if not user:
        return jsonify({"error": "Credenziali non valide"}), 401

    # Verifica password
    password_hash = hashlib.md5(data['password'].encode()).hexdigest()
    if user['password'] != password_hash:
        return jsonify({"error": "Credenziali non valide"}), 401

    # Aggiorna ultimo login
    user['ultimoLogin'] = int(time.time() * 1000)

    # Crea token JWT
    token = create_jwt_token(user['id'], user['username'], user['ruolo'])
    active_tokens[token] = user['id']

    return jsonify(token)

@app.route('/auth/logout', methods=['POST'])
@require_auth()
def logout():
    """Logout utente"""
    token = get_auth_token()
    if token in active_tokens:
        del active_tokens[token]

    return jsonify({"message": "Logout effettuato con successo"})

@app.route('/refresh-token', methods=['POST'])
@require_auth()
def refresh_token():
    """Rinnovo scadenza token"""
    user_data = request.user_data
    user = get_user_by_id(user_data['userId'])

    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    # Crea nuovo token
    new_token = create_jwt_token(user['id'], user['username'], user['ruolo'])

    # Rimuovi il vecchio token se presente
    old_token = get_auth_token()
    if old_token in active_tokens:
        del active_tokens[old_token]

    active_tokens[new_token] = user['id']

    return jsonify(new_token)

@app.route('/users/profile', methods=['GET'])
@require_auth()
def get_profile():
    """Visualizza profilo utente"""
    user_data = request.user_data
    user = get_user_by_id(user_data['userId'])

    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    # Formato semplificato per il profilo
    profile = {
        "idUtente": user["id"],
        "username": user["username"],
        "email": user["email"],
        "nome": user["nome"],
        "cognome": user["cognome"],
        "dataCreazione": user["dataCreazione"],
        "ultimoLogin": user["ultimoLogin"]
    }

    return jsonify(profile)

@app.route('/users/profile', methods=['PUT'])
@require_auth()
def update_profile():
    """Modifica profilo utente"""
    user_data = request.user_data
    user = get_user_by_id(user_data['userId'])

    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"error": "Dati mancanti"}), 400

    # Aggiorna i campi forniti
    if 'email' in data:
        # Verifica che l'email non sia già usata
        existing_user = get_user_by_email(data['email'])
        if existing_user and existing_user['id'] != user['id']:
            return jsonify({"error": "Email già utilizzata"}), 409
        user['email'] = data['email']

    if 'nome' in data:
        user['nome'] = data['nome']

    if 'cognome' in data:
        user['cognome'] = data['cognome']

    # Formato risposta
    response = {
        "id": user["id"],
        "username": user["username"],
        "email": user["email"],
        "nome": user["nome"],
        "cognome": user["cognome"]
    }

    return jsonify(response)

@app.route('/users/forgot-password', methods=['POST'])
def forgot_password():
    """Reset password utente"""
    data = request.get_json()

    if not data or 'email' not in data:
        return jsonify({"error": "Email richiesta"}), 400

    user = get_user_by_email(data['email'])
    if not user:
        # Non rivelare se l'email esiste o meno per sicurezza
        return jsonify({"message": "Se l'email esiste, riceverai istruzioni per il reset"})

    # In un sistema reale qui si invierebbe una email
    # Per lo stub, simuliamo solo la risposta
    return jsonify({"message": "Email di reset inviata"})

@app.route('/users/change-password', methods=['PUT'])
@require_auth()
def change_password():
    """Modifica password"""
    user_data = request.user_data
    user = get_user_by_id(user_data['userId'])

    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    data = request.get_json()
    if not data or not all(k in data for k in ['currentPassword', 'newPassword']):
        return jsonify({"error": "Password attuale e nuova password richieste"}), 400

    # Verifica password attuale
    current_password_hash = hashlib.md5(data['currentPassword'].encode()).hexdigest()
    if user['password'] != current_password_hash:
        return jsonify({"error": "Password attuale non corretta"}), 400

    # Aggiorna password
    user['password'] = hashlib.md5(data['newPassword'].encode()).hexdigest()

    return jsonify(True)

# =============================================================================
# ENDPOINT AGGIUNTIVI PER INTEGRAZIONE
# =============================================================================

@app.route('/users/exists/<user_id>', methods=['GET'])
def user_exists(user_id):
    """Verifica se un utente esiste (per altri microservizi)"""
    user = get_user_by_id(user_id)
    return jsonify({
        "exists": user is not None,
        "userId": user_id
    })

@app.route('/users/<user_id>/info', methods=['GET'])
def get_user_info(user_id):
    """Info essenziali di un utente (per altri microservizi)"""
    user = get_user_by_id(user_id)
    if not user:
        return jsonify({"error": "Utente non trovato"}), 404

    role = get_role_by_id(user["ruolo"])
    return jsonify({
        "id": user["id"],
        "username": user["username"],
        "nome": user["nome"],
        "cognome": user["cognome"],
        "email": user["email"],
        "ruolo": role["nome"] if role else None,
        "ruoloId": user["ruolo"]
    })

@app.route('/users/role/<role_name>', methods=['GET'])
def get_users_by_role(role_name):
    """Ottieni utenti per nome ruolo (per altri microservizi)"""
    role = next((r for r in roles_data if r["nome"] == role_name.upper()), None)
    if not role:
        return jsonify({"error": "Ruolo non trovato"}), 404

    users = [format_user_response(user) for user in users_data if user["ruolo"] == role["id"]]
    return jsonify(users)

# =============================================================================
# AVVIO APPLICAZIONE
# =============================================================================

if __name__ == '__main__':
    print("🚀 Avvio Microservizio Stub: Gestione Utenti e Ruoli")
    print("📍 Endpoints disponibili:")
    print("   - Health Check: GET /health")
    print("   - Super Admin: POST /init/superadmin")
    print("   - Login: POST /auth/login")
    print("   - Logout: POST /auth/logout")
    print("   - Profilo: GET /users/profile")
    print("   - Gestione Utenti: GET/POST/PUT/DELETE /users")
    print("   - Gestione Ruoli: GET /roles")
    print("   - Integrazione: GET /users/{id}/exists, /users/{id}/info")
    print("📚 Documenti mock caricati:")
    print(f"   - {len(roles_data)} ruoli disponibili")
    print(f"   - {len(users_data)} utenti precaricati")
    print("🔐 Credenziali di test:")
    print("   - superadmin / password123 (SUPER_ADMIN)")
    print("   - admin.rossi / admin123 (ADMIN)")
    print("   - prof.bianchi / prof123 (DOCENTE)")
    print("   - stud.verdi / stud123 (STUDENTE)")
    print("   - segr.neri / segr123 (AMMINISTRATIVO)")
    print("🌐 Server in ascolto su: http://localhost:5003")

    app.run(host='0.0.0.0', port=5003, debug=True)