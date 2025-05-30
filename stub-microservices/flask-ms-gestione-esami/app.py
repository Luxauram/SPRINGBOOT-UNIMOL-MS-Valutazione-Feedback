#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Microservizio Stub - Gestione Esami
Porta: 5002
Creato per il testing del microservizio Valutazione e Feedback
"""

from flask import Flask, jsonify, request
from flask_cors import CORS
from datetime import datetime, date, time, timedelta
import logging

# Configurazione Flask
app = Flask(__name__)
CORS(app)

# Configurazione logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ============================================================================
# DATI MOCK
# ============================================================================

# Enum per gli status
EXAM_STATUS = {
    'SCHEDULED': 'SCHEDULED',
    'ACTIVE': 'ACTIVE',
    'COMPLETED': 'COMPLETED',
    'CANCELLED': 'CANCELLED'
}

ENROLLMENT_STATUS = {
    'ENROLLED': 'ENROLLED',
    'CONFIRMED': 'CONFIRMED',
    'CANCELLED': 'CANCELLED',
    'REJECTED': 'REJECTED'
}

# Mock data per gli esami
mock_exams = {
    1: {
        "id": 1,
        "courseId": 101,
        "courseName": "Microservizi e Architetture Distribuite",
        "teacherId": 201,
        "teacherName": "Prof. Marco Rossi",
        "examDate": "2024-07-15",
        "examTime": "09:00:00",
        "classroom": "Aula Magna A",
        "status": EXAM_STATUS['SCHEDULED'],
        "maxEnrollments": 50,
        "currentEnrollments": 25,
        "creationDate": "2024-05-01T10:00:00"
    },
    2: {
        "id": 2,
        "courseId": 102,
        "courseName": "Basi di Dati Avanzate",
        "teacherId": 202,
        "teacherName": "Prof.ssa Anna Bianchi",
        "examDate": "2024-07-18",
        "examTime": "14:30:00",
        "classroom": "Lab. Informatica B",
        "status": EXAM_STATUS['SCHEDULED'],
        "maxEnrollments": 30,
        "currentEnrollments": 18,
        "creationDate": "2024-05-05T14:30:00"
    },
    3: {
        "id": 3,
        "courseId": 101,
        "courseName": "Microservizi e Architetture Distribuite",
        "teacherId": 201,
        "teacherName": "Prof. Marco Rossi",
        "examDate": "2024-06-20",
        "examTime": "10:00:00",
        "classroom": "Aula 3",
        "status": EXAM_STATUS['COMPLETED'],
        "maxEnrollments": 40,
        "currentEnrollments": 32,
        "creationDate": "2024-04-15T09:00:00"
    },
    4: {
        "id": 4,
        "courseId": 103,
        "courseName": "Ingegneria del Software",
        "teacherId": 203,
        "teacherName": "Prof. Luigi Verdi",
        "examDate": "2024-08-10",
        "examTime": "15:00:00",
        "classroom": "Aula 5",
        "status": EXAM_STATUS['SCHEDULED'],
        "maxEnrollments": 35,
        "currentEnrollments": 12,
        "creationDate": "2024-05-10T11:00:00"
    }
}

# Mock data per le iscrizioni
mock_enrollments = {
    1: {
        "id": 1,
        "examId": 1,
        "studentId": 301,
        "studentName": "Mario Rossi",
        "status": ENROLLMENT_STATUS['ENROLLED'],
        "enrollmentDate": "2024-05-15T14:30:00",
        "notes": "Prima iscrizione",
        "adminNotes": ""
    },
    2: {
        "id": 2,
        "examId": 1,
        "studentId": 302,
        "studentName": "Giulia Bianchi",
        "status": ENROLLMENT_STATUS['ENROLLED'],
        "enrollmentDate": "2024-05-16T09:15:00",
        "notes": "",
        "adminNotes": ""
    },
    3: {
        "id": 3,
        "examId": 2,
        "studentId": 301,
        "studentName": "Mario Rossi",
        "status": ENROLLMENT_STATUS['ENROLLED'],
        "enrollmentDate": "2024-05-18T16:20:00",
        "notes": "Seconda sessione",
        "adminNotes": ""
    },
    4: {
        "id": 4,
        "examId": 3,
        "studentId": 303,
        "studentName": "Luca Verde",
        "status": ENROLLMENT_STATUS['CONFIRMED'],
        "enrollmentDate": "2024-04-20T10:30:00",
        "notes": "",
        "adminNotes": "Studente confermato per l'esame"
    },
    5: {
        "id": 5,
        "examId": 1,
        "studentId": 304,
        "studentName": "Sara Neri",
        "status": ENROLLMENT_STATUS['CANCELLED'],
        "enrollmentDate": "2024-05-12T12:00:00",
        "notes": "Annullata per malattia",
        "adminNotes": "Iscrizione cancellata su richiesta"
    }
}

# Mock data per i voti
mock_grades = {
    1: {
        "id": 1,
        "enrollmentId": 4,
        "studentId": 303,
        "studentName": "Luca Verde",
        "examId": 3,
        "grade": 28,
        "withHonors": False,
        "recordingDate": "2024-06-20T16:30:00",
        "notes": "Ottima preparazione teorica",
        "feedback": "Molto bene sugli aspetti teorici, migliorare la parte pratica"
    },
    2: {
        "id": 2,
        "enrollmentId": 6, # Enrollment fittizia
        "studentId": 305,
        "studentName": "Elena Gialli",
        "examId": 3,
        "grade": 30,
        "withHonors": True,
        "recordingDate": "2024-06-20T16:45:00",
        "notes": "Eccellente in tutti gli aspetti",
        "feedback": "Preparazione completa e approfondita, continuare così"
    }
}

# Contatori per ID auto-incrementali
next_exam_id = max(mock_exams.keys()) + 1 if mock_exams else 1
next_enrollment_id = max(mock_enrollments.keys()) + 1 if mock_enrollments else 1
next_grade_id = max(mock_grades.keys()) + 1 if mock_grades else 1

# ============================================================================
# UTILITY FUNCTIONS
# ============================================================================

def log_request():
    """Log delle richieste HTTP"""
    logger.info(f"📍 {request.method} {request.path} - {request.remote_addr}")

def get_current_timestamp():
    """Timestamp corrente in formato ISO"""
    return datetime.now().strftime("%Y-%m-%dT%H:%M:%S")

def filter_by_status(items, status_param):
    """Filtra items per status"""
    if not status_param:
        return items
    return [item for item in items if item.get('status') == status_param]

def paginate_results(items, page=1, size=10):
    """Paginazione dei risultati"""
    try:
        page = int(page) if page else 1
        size = int(size) if size else 10
        start = (page - 1) * size
        end = start + size
        return items[start:end]
    except:
        return items[:10]

# ============================================================================
# HEALTH CHECK
# ============================================================================

@app.route('/health', methods=['GET'])
def health_check():
    """Health check del microservizio"""
    log_request()
    return jsonify({
        "service": "Gestione Esami",
        "status": "UP",
        "timestamp": get_current_timestamp(),
        "port": 5002
    })

# ============================================================================
# EXAMS ENDPOINTS
# ============================================================================

@app.route('/api/v1/exams', methods=['GET'])
def get_all_exams():
    """Lista tutti gli esami con filtri opzionali"""
    log_request()

    # Parametri di filtro
    start_date = request.args.get('startDate')
    end_date = request.args.get('endDate')
    course_id = request.args.get('courseId')
    teacher_id = request.args.get('teacherId')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    exams = list(mock_exams.values())

    # Applica filtri
    if course_id:
        exams = [e for e in exams if e['courseId'] == int(course_id)]
    if teacher_id:
        exams = [e for e in exams if e['teacherId'] == int(teacher_id)]
    if start_date:
        exams = [e for e in exams if e['examDate'] >= start_date]
    if end_date:
        exams = [e for e in exams if e['examDate'] <= end_date]

    # Paginazione
    paginated_exams = paginate_results(exams, page, size)

    return jsonify(paginated_exams)

@app.route('/api/v1/exams/<int:exam_id>', methods=['GET'])
def get_exam_by_id(exam_id):
    """Dettaglio singolo esame"""
    log_request()

    exam = mock_exams.get(exam_id)
    if not exam:
        return jsonify({"error": "Esame non trovato"}), 404

    return jsonify(exam)

@app.route('/api/v1/exams/<int:exam_id>/exists', methods=['GET'])
def check_exam_exists(exam_id):
    """Verifica se un esame esiste (per integrazione)"""
    log_request()

    exists = exam_id in mock_exams
    exam_info = mock_exams.get(exam_id) if exists else None

    return jsonify({
        "exists": exists,
        "examId": exam_id,
        "examInfo": {
            "courseId": exam_info.get('courseId'),
            "courseName": exam_info.get('courseName'),
            "teacherId": exam_info.get('teacherId'),
            "teacherName": exam_info.get('teacherName'),
            "status": exam_info.get('status')
        } if exam_info else None
    })

@app.route('/api/v1/exams/<int:exam_id>/info', methods=['GET'])
def get_exam_info(exam_id):
    """Informazioni essenziali esame (per integrazione)"""
    log_request()

    exam = mock_exams.get(exam_id)
    if not exam:
        return jsonify({"error": "Esame non trovato"}), 404

    return jsonify({
        "id": exam['id'],
        "courseId": exam['courseId'],
        "courseName": exam['courseName'],
        "teacherId": exam['teacherId'],
        "teacherName": exam['teacherName'],
        "examDate": exam['examDate'],
        "examTime": exam['examTime'],
        "status": exam['status']
    })

@app.route('/api/v1/exams/course/<int:course_id>', methods=['GET'])
def get_exams_by_course(course_id):
    """Esami per corso"""
    log_request()

    exams = [exam for exam in mock_exams.values() if exam['courseId'] == course_id]
    return jsonify(exams)

@app.route('/api/v1/exams/teacher/<int:teacher_id>', methods=['GET'])
def get_exams_by_teacher(teacher_id):
    """Esami per docente"""
    log_request()

    exams = [exam for exam in mock_exams.values() if exam['teacherId'] == teacher_id]
    return jsonify(exams)

@app.route('/api/v1/exams/calendar', methods=['GET'])
def get_exam_calendar():
    """Calendario esami pubblico"""
    log_request()

    # Parametri di filtro
    start_date = request.args.get('startDate')
    end_date = request.args.get('endDate')
    course_id = request.args.get('courseId')
    teacher_id = request.args.get('teacherId')

    calendar_items = []
    for exam in mock_exams.values():
        # Applica filtri
        if course_id and exam['courseId'] != int(course_id):
            continue
        if teacher_id and exam['teacherId'] != int(teacher_id):
            continue
        if start_date and exam['examDate'] < start_date:
            continue
        if end_date and exam['examDate'] > end_date:
            continue

        calendar_item = {
            "examId": exam['id'],
            "courseCode": f"CORSO{exam['courseId']:03d}",
            "courseName": exam['courseName'],
            "teacherName": exam['teacherName'],
            "examDate": exam['examDate'],
            "examTime": exam['examTime'],
            "classroom": exam['classroom'],
            "availableSlots": exam['maxEnrollments'] - exam['currentEnrollments'],
            "status": exam['status']
        }
        calendar_items.append(calendar_item)

    return jsonify(calendar_items)

@app.route('/api/v1/exams/available', methods=['GET'])
def get_available_exams():
    """Esami disponibili per iscrizione"""
    log_request()

    student_id = request.args.get('studentId')

    # Solo esami SCHEDULED con posti disponibili
    available_exams = []
    for exam in mock_exams.values():
        if (exam['status'] == EXAM_STATUS['SCHEDULED'] and
                exam['currentEnrollments'] < exam['maxEnrollments']):
            available_exams.append(exam)

    return jsonify(available_exams)

@app.route('/api/v1/exams', methods=['POST'])
def create_exam():
    """Crea nuovo esame"""
    log_request()
    global next_exam_id

    data = request.get_json()
    if not data:
        return jsonify({"error": "Dati richiesti"}), 400

    # Crea nuovo esame
    new_exam = {
        "id": next_exam_id,
        "courseId": data.get('courseId'),
        "courseName": data.get('courseName', f"Corso {data.get('courseId')}"),
        "teacherId": data.get('teacherId'),
        "teacherName": data.get('teacherName', f"Docente {data.get('teacherId')}"),
        "examDate": data.get('examDate'),
        "examTime": data.get('examTime', "09:00:00"),
        "classroom": data.get('classroom', "Aula TBD"),
        "status": EXAM_STATUS['SCHEDULED'],
        "maxEnrollments": data.get('maxEnrollments', 30),
        "currentEnrollments": 0,
        "creationDate": get_current_timestamp()
    }

    mock_exams[next_exam_id] = new_exam
    next_exam_id += 1

    return jsonify(new_exam), 201

# ============================================================================
# ENROLLMENTS ENDPOINTS
# ============================================================================

@app.route('/api/v1/exams/<int:exam_id>/enroll', methods=['POST'])
def enroll_to_exam(exam_id):
    """Iscrizione a esame"""
    log_request()
    global next_enrollment_id

    data = request.get_json()
    if not data:
        return jsonify({"error": "Dati richiesti"}), 400

    # Verifica che l'esame esista
    exam = mock_exams.get(exam_id)
    if not exam:
        return jsonify({"error": "Esame non trovato"}), 404

    # Verifica posti disponibili
    if exam['currentEnrollments'] >= exam['maxEnrollments']:
        return jsonify({"error": "Esame al completo"}), 400

    student_id = data.get('studentId')

    # Verifica se già iscritto
    existing_enrollment = None
    for enrollment in mock_enrollments.values():
        if (enrollment['examId'] == exam_id and
                enrollment['studentId'] == student_id and
                enrollment['status'] in [ENROLLMENT_STATUS['ENROLLED'], ENROLLMENT_STATUS['CONFIRMED']]):
            existing_enrollment = enrollment
            break

    if existing_enrollment:
        return jsonify({"error": "Già iscritto a questo esame"}), 400

    # Crea nuova iscrizione
    new_enrollment = {
        "id": next_enrollment_id,
        "examId": exam_id,
        "studentId": student_id,
        "studentName": data.get('studentName', f"Studente {student_id}"),
        "status": ENROLLMENT_STATUS['ENROLLED'],
        "enrollmentDate": get_current_timestamp(),
        "notes": data.get('notes', ''),
        "adminNotes": ""
    }

    mock_enrollments[next_enrollment_id] = new_enrollment
    next_enrollment_id += 1

    # Aggiorna conteggio iscrizioni
    mock_exams[exam_id]['currentEnrollments'] += 1

    return jsonify(new_enrollment), 201

@app.route('/api/v1/enrollments/my', methods=['GET'])
def get_my_enrollments():
    """Le mie iscrizioni (simulato)"""
    log_request()

    # In un vero sistema, lo studentId verrebbe dall'autenticazione
    student_id = request.args.get('studentId', 301)  # Default per testing
    status = request.args.get('status')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    try:
        student_id = int(student_id)
    except:
        student_id = 301

    enrollments = [e for e in mock_enrollments.values() if e['studentId'] == student_id]

    if status:
        enrollments = filter_by_status(enrollments, status)

    paginated_enrollments = paginate_results(enrollments, page, size)

    return jsonify(paginated_enrollments)

@app.route('/api/v1/enrollments/<int:enrollment_id>', methods=['GET'])
def get_enrollment_by_id(enrollment_id):
    """Dettaglio iscrizione"""
    log_request()

    enrollment = mock_enrollments.get(enrollment_id)
    if not enrollment:
        return jsonify({"error": "Iscrizione non trovata"}), 404

    return jsonify(enrollment)

@app.route('/api/v1/enrollments/<int:enrollment_id>', methods=['DELETE'])
def cancel_enrollment(enrollment_id):
    """Cancella iscrizione"""
    log_request()

    enrollment = mock_enrollments.get(enrollment_id)
    if not enrollment:
        return jsonify({"error": "Iscrizione non trovata"}), 404

    if enrollment['status'] == ENROLLMENT_STATUS['CANCELLED']:
        return jsonify({"error": "Iscrizione già cancellata"}), 400

    # Aggiorna status
    enrollment['status'] = ENROLLMENT_STATUS['CANCELLED']
    enrollment['adminNotes'] = f"Cancellata il {get_current_timestamp()}"

    # Decrementa conteggio esame
    exam_id = enrollment['examId']
    if exam_id in mock_exams:
        mock_exams[exam_id]['currentEnrollments'] -= 1

    return '', 204

@app.route('/api/v1/exams/<int:exam_id>/enrollments', methods=['GET'])
def get_exam_enrollments(exam_id):
    """Iscrizioni per esame"""
    log_request()

    status = request.args.get('status')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    enrollments = [e for e in mock_enrollments.values() if e['examId'] == exam_id]

    if status:
        enrollments = filter_by_status(enrollments, status)

    paginated_enrollments = paginate_results(enrollments, page, size)

    return jsonify(paginated_enrollments)

@app.route('/api/v1/enrollments', methods=['GET'])
def get_all_enrollments():
    """Tutte le iscrizioni (admin)"""
    log_request()

    exam_id = request.args.get('examId')
    student_id = request.args.get('studentId')
    status = request.args.get('status')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    enrollments = list(mock_enrollments.values())

    # Applica filtri
    if exam_id:
        enrollments = [e for e in enrollments if e['examId'] == int(exam_id)]
    if student_id:
        enrollments = [e for e in enrollments if e['studentId'] == int(student_id)]
    if status:
        enrollments = filter_by_status(enrollments, status)

    paginated_enrollments = paginate_results(enrollments, page, size)

    return jsonify(paginated_enrollments)

@app.route('/api/v1/enrollments/student/<int:student_id>', methods=['GET'])
def get_student_enrollments(student_id):
    """Iscrizioni per studente"""
    log_request()

    status = request.args.get('status')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    enrollments = [e for e in mock_enrollments.values() if e['studentId'] == student_id]

    if status:
        enrollments = filter_by_status(enrollments, status)

    paginated_enrollments = paginate_results(enrollments, page, size)

    return jsonify(paginated_enrollments)

# ============================================================================
# GRADES ENDPOINTS
# ============================================================================

@app.route('/api/v1/exams/<int:exam_id>/grades', methods=['POST'])
def record_grade(exam_id):
    """Registra voto"""
    log_request()
    global next_grade_id

    data = request.get_json()
    if not data:
        return jsonify({"error": "Dati richiesti"}), 400

    # Verifica che l'esame esista
    if exam_id not in mock_exams:
        return jsonify({"error": "Esame non trovato"}), 404

    enrollment_id = data.get('enrollmentId')
    student_id = data.get('studentId')
    grade = data.get('grade')

    # Validazione voto
    if grade is None or grade < 18 or grade > 30:
        return jsonify({"error": "Voto non valido (18-30)"}), 400

    # Crea nuovo voto
    new_grade = {
        "id": next_grade_id,
        "enrollmentId": enrollment_id,
        "studentId": student_id,
        "studentName": data.get('studentName', f"Studente {student_id}"),
        "examId": exam_id,
        "grade": grade,
        "withHonors": data.get('withHonors', False),
        "recordingDate": get_current_timestamp(),
        "notes": data.get('notes', ''),
        "feedback": data.get('feedback', '')
    }

    mock_grades[next_grade_id] = new_grade
    next_grade_id += 1

    return jsonify(new_grade), 201

@app.route('/api/v1/exams/<int:exam_id>/grades', methods=['GET'])
def get_exam_grades(exam_id):
    """Voti per esame"""
    log_request()

    min_grade = request.args.get('minGrade')
    max_grade = request.args.get('maxGrade')
    with_honors = request.args.get('withHonors')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    grades = [g for g in mock_grades.values() if g['examId'] == exam_id]

    # Applica filtri
    if min_grade:
        grades = [g for g in grades if g['grade'] >= int(min_grade)]
    if max_grade:
        grades = [g for g in grades if g['grade'] <= int(max_grade)]
    if with_honors:
        is_with_honors = with_honors.lower() == 'true'
        grades = [g for g in grades if g['withHonors'] == is_with_honors]

    paginated_grades = paginate_results(grades, page, size)

    return jsonify(paginated_grades)

@app.route('/api/v1/grades/<int:grade_id>', methods=['GET'])
def get_grade_by_id(grade_id):
    """Dettaglio voto"""
    log_request()

    grade = mock_grades.get(grade_id)
    if not grade:
        return jsonify({"error": "Voto non trovato"}), 404

    return jsonify(grade)

@app.route('/api/v1/grades/my', methods=['GET'])
def get_my_grades():
    """I miei voti (simulato)"""
    log_request()

    # In un vero sistema, lo studentId verrebbe dall'autenticazione
    student_id = request.args.get('studentId', 301)  # Default per testing
    course_id = request.args.get('courseId')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    try:
        student_id = int(student_id)
    except:
        student_id = 301

    grades = [g for g in mock_grades.values() if g['studentId'] == student_id]

    # Filtra per corso se specificato
    if course_id:
        course_id = int(course_id)
        # Filtra i voti per esami del corso specificato
        grades = [g for g in grades
                  if mock_exams.get(g['examId'], {}).get('courseId') == course_id]

    paginated_grades = paginate_results(grades, page, size)

    return jsonify(paginated_grades)

@app.route('/api/v1/grades/student/<int:student_id>', methods=['GET'])
def get_student_grades(student_id):
    """Voti per studente (admin/docente)"""
    log_request()

    course_id = request.args.get('courseId')
    page = request.args.get('page', 1)
    size = request.args.get('size', 10)

    grades = [g for g in mock_grades.values() if g['studentId'] == student_id]

    # Filtra per corso se specificato
    if course_id:
        course_id = int(course_id)
        grades = [g for g in grades
                  if mock_exams.get(g['examId'], {}).get('courseId') == course_id]

    paginated_grades = paginate_results(grades, page, size)

    return jsonify(paginated_grades)

@app.route('/api/v1/grades/course/<int:course_id>/statistics', methods=['GET'])
def get_course_grade_statistics(course_id):
    """Statistiche voti corso"""
    log_request()

    # Trova tutti gli esami del corso
    course_exams = [exam_id for exam_id, exam in mock_exams.items()
                    if exam['courseId'] == course_id]

    # Trova tutti i voti per questi esami
    course_grades = [g for g in mock_grades.values()
                     if g['examId'] in course_exams]

    if not course_grades:
        return jsonify({
            "courseId": course_id,
            "totalGrades": 0,
            "averageGrade": 0,
            "minGrade": 0,
            "maxGrade": 0,
            "withHonorsCount": 0,
            "gradeDistribution": {}
        })

    grades_values = [g['grade'] for g in course_grades]
    honors_count = len([g for g in course_grades if g['withHonors']])

    # Distribuzione per fasce di voto
    distribution = {
        "18-20": len([g for g in grades_values if 18 <= g <= 20]),
        "21-23": len([g for g in grades_values if 21 <= g <= 23]),
        "24-26": len([g for g in grades_values if 24 <= g <= 26]),
        "27-29": len([g for g in grades_values if 27 <= g <= 29]),
        "30": len([g for g in grades_values if g == 30]),
        "30L": honors_count
    }

    statistics = {
        "courseId": course_id,
        "totalGrades": len(course_grades),
        "averageGrade": round(sum(grades_values) / len(grades_values), 2),
        "minGrade": min(grades_values),
        "maxGrade": max(grades_values),
        "withHonorsCount": honors_count,
        "gradeDistribution": distribution
    }

    return jsonify(statistics)

# ============================================================================
# MAIN
# ============================================================================

def print_startup_info():
    """Stampa informazioni di avvio del microservizio"""
    print("\n" + "="*60)
    print("🚀 Avvio Microservizio Stub: Gestione Esami")
    print("="*60)
    print(f"📍 URL Base: http://localhost:5002")
    print(f"❤️  Health Check: http://localhost:5002/health")
    print("\n📋 Endpoints Disponibili:")
    print("="*60)

    endpoints = [
        ("GET", "/health", "Health check del servizio"),
        ("", "", ""),
        ("", "🔍 ESAMI - Consultazione", ""),
        ("GET", "/api/v1/exams", "Lista tutti gli esami"),
        ("GET", "/api/v1/exams/{id}", "Dettaglio esame"),
        ("GET", "/api/v1/exams/{id}/exists", "Verifica esistenza esame"),
        ("GET", "/api/v1/exams/{id}/info", "Info essenziali esame"),
        ("GET", "/api/v1/exams/course/{courseId}", "Esami per corso"),
        ("GET", "/api/v1/exams/teacher/{teacherId}", "Esami per docente"),
        ("GET", "/api/v1/exams/calendar", "Calendario esami pubblico"),
        ("GET", "/api/v1/exams/available", "Esami disponibili per iscrizione"),
        ("", "", ""),
        ("", "📝 ESAMI - Gestione", ""),
        ("POST", "/api/v1/exams", "Crea nuovo esame"),
        ("", "", ""),
        ("", "✏️ ISCRIZIONI - Studenti", ""),
        ("POST", "/api/v1/exams/{examId}/enroll", "Iscrizione a esame"),
        ("GET", "/api/v1/enrollments/my", "Le mie iscrizioni"),
        ("GET", "/api/v1/enrollments/{id}", "Dettaglio iscrizione"),
        ("DELETE", "/api/v1/enrollments/{id}", "Cancella iscrizione"),
        ("", "", ""),
        ("", "📊 ISCRIZIONI - Amministrativi", ""),
        ("GET", "/api/v1/exams/{examId}/enrollments", "Iscrizioni per esame"),
        ("GET", "/api/v1/enrollments", "Tutte le iscrizioni"),
        ("GET", "/api/v1/enrollments/student/{studentId}", "Iscrizioni studente"),
        ("", "", ""),
        ("", "🎯 VOTI - Docenti", ""),
        ("POST", "/api/v1/exams/{examId}/grades", "Registra voto"),
        ("GET", "/api/v1/exams/{examId}/grades", "Voti per esame"),
        ("GET", "/api/v1/grades/{id}", "Dettaglio voto"),
        ("", "", ""),
        ("", "📈 VOTI - Studenti", ""),
        ("GET", "/api/v1/grades/my", "I miei voti"),
        ("GET", "/api/v1/grades/student/{studentId}", "Voti studente"),
        ("GET", "/api/v1/grades/course/{courseId}/statistics", "Statistiche corso"),
    ]

    for method, endpoint, description in endpoints:
        if method == "":
            if endpoint == "":
                print()
            else:
                print(f"📌 {endpoint}")
        else:
            print(f"   {method:<6} {endpoint:<35} {description}")

    print("\n" + "="*60)
    print("📊 Dati Mock Disponibili:")
    print("="*60)
    print(f"   🎓 Esami: {len(mock_exams)} esami caricati")
    print(f"   ✏️  Iscrizioni: {len(mock_enrollments)} iscrizioni")
    print(f"   🎯 Voti: {len(mock_grades)} voti registrati")
    print("\n💡 Per testare: curl http://localhost:5002/health")
    print("="*60)
    print("🔥 Microservizio PRONTO per l'integrazione!")
    print("="*60 + "\n")

if __name__ == '__main__':
    print_startup_info()
    app.run(debug=True, host='0.0.0.0', port=5002)