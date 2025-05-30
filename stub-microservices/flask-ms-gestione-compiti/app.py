from flask import Flask, jsonify, request
from datetime import datetime, timedelta
import random

app = Flask(__name__)

# Mock data - Simulazione database
assignments = [
    {
        "id": 1,
        "title": "Progetto Microservizi",
        "description": "Sviluppare un'architettura a microservizi per la gestione universitaria",
        "courseId": 101,
        "teacherId": 201,
        "dueDate": "2024-06-15T23:59:00",
        "creationDate": "2024-05-01T10:00:00",
        "status": "ACTIVE",
        "maxScore": 30.0,
        "attachments": ["project_requirements.pdf", "grading_rubric.pdf"]
    },
    {
        "id": 2,
        "title": "Analisi Database Relazionali",
        "description": "Progettare e implementare un database per un sistema e-commerce",
        "courseId": 102,
        "teacherId": 202,
        "dueDate": "2024-06-20T23:59:00",
        "creationDate": "2024-05-05T14:30:00",
        "status": "ACTIVE",
        "maxScore": 25.0,
        "attachments": ["database_schema.sql"]
    },
    {
        "id": 3,
        "title": "Presentazione Spring Boot",
        "description": "Presentare le funzionalità avanzate di Spring Boot",
        "courseId": 101,
        "teacherId": 201,
        "dueDate": "2024-05-30T18:00:00",
        "creationDate": "2024-05-10T09:15:00",
        "status": "CLOSED",
        "maxScore": 20.0,
        "attachments": []
    }
]

submissions = [
    {
        "id": 1,
        "assignmentId": 1,
        "studentId": 301,
        "submissionDate": "2024-05-25T15:30:00",
        "status": "SUBMITTED",
        "files": ["progetto_microservizi.zip"],
        "notes": "Progetto completato con documentazione"
    },
    {
        "id": 2,
        "assignmentId": 1,
        "studentId": 302,
        "submissionDate": "2024-05-26T20:45:00",
        "status": "SUBMITTED",
        "files": ["microservices_project.zip", "documentation.pdf"],
        "notes": "Implementazione con Docker"
    },
    {
        "id": 3,
        "assignmentId": 2,
        "studentId": 301,
        "submissionDate": "2024-05-28T12:00:00",
        "status": "SUBMITTED",
        "files": ["database_project.sql", "er_diagram.png"],
        "notes": "Database ottimizzato per performance"
    }
]

# Endpoint per recuperare tutti i compiti
@app.route('/api/v1/assignments', methods=['GET'])
def get_all_assignments():
    return jsonify(assignments)

# Endpoint per recuperare un compito specifico
@app.route('/api/v1/assignments/<int:assignment_id>', methods=['GET'])
def get_assignment_by_id(assignment_id):
    assignment = next((a for a in assignments if a['id'] == assignment_id), None)
    if assignment:
        return jsonify(assignment)
    return jsonify({"error": "Assignment not found"}), 404

# Endpoint per recuperare compiti per corso
@app.route('/api/v1/assignments/course/<int:course_id>', methods=['GET'])
def get_assignments_by_course(course_id):
    course_assignments = [a for a in assignments if a['courseId'] == course_id]
    return jsonify(course_assignments)

# Endpoint per recuperare compiti per docente
@app.route('/api/v1/assignments/teacher/<int:teacher_id>', methods=['GET'])
def get_assignments_by_teacher(teacher_id):
    teacher_assignments = [a for a in assignments if a['teacherId'] == teacher_id]
    return jsonify(teacher_assignments)

# Endpoint per creare un nuovo compito
@app.route('/api/v1/assignments', methods=['POST'])
def create_assignment():
    data = request.get_json()
    new_assignment = {
        "id": len(assignments) + 1,
        "title": data.get('title'),
        "description": data.get('description'),
        "courseId": data.get('courseId'),
        "teacherId": data.get('teacherId'),
        "dueDate": data.get('dueDate'),
        "creationDate": datetime.now().isoformat(),
        "status": "ACTIVE",
        "maxScore": data.get('maxScore', 30.0),
        "attachments": data.get('attachments', [])
    }
    assignments.append(new_assignment)
    return jsonify(new_assignment), 201

# Endpoint per recuperare tutte le consegne
@app.route('/api/v1/submissions', methods=['GET'])
def get_all_submissions():
    return jsonify(submissions)

# Endpoint per recuperare consegne per un compito specifico
@app.route('/api/v1/submissions/assignment/<int:assignment_id>', methods=['GET'])
def get_submissions_by_assignment(assignment_id):
    assignment_submissions = [s for s in submissions if s['assignmentId'] == assignment_id]
    return jsonify(assignment_submissions)

# Endpoint per recuperare consegne di uno studente
@app.route('/api/v1/submissions/student/<int:student_id>', methods=['GET'])
def get_submissions_by_student(student_id):
    student_submissions = [s for s in submissions if s['studentId'] == student_id]
    return jsonify(student_submissions)

# Endpoint per recuperare una consegna specifica
@app.route('/api/v1/submissions/<int:submission_id>', methods=['GET'])
def get_submission_by_id(submission_id):
    submission = next((s for s in submissions if s['id'] == submission_id), None)
    if submission:
        return jsonify(submission)
    return jsonify({"error": "Submission not found"}), 404

# Endpoint per creare una nuova consegna
@app.route('/api/v1/submissions', methods=['POST'])
def create_submission():
    data = request.get_json()
    new_submission = {
        "id": len(submissions) + 1,
        "assignmentId": data.get('assignmentId'),
        "studentId": data.get('studentId'),
        "submissionDate": datetime.now().isoformat(),
        "status": "SUBMITTED",
        "files": data.get('files', []),
        "notes": data.get('notes', "")
    }
    submissions.append(new_submission)
    return jsonify(new_submission), 201

# Endpoint per aggiornare lo stato di una consegna
@app.route('/api/v1/submissions/<int:submission_id>/status', methods=['PUT'])
def update_submission_status(submission_id):
    data = request.get_json()
    submission = next((s for s in submissions if s['id'] == submission_id), None)
    if submission:
        submission['status'] = data.get('status', submission['status'])
        return jsonify(submission)
    return jsonify({"error": "Submission not found"}), 404

# Endpoint di supporto per verificare se un compito esiste (usato dal tuo microservizio)
@app.route('/api/v1/assignments/<int:assignment_id>/exists', methods=['GET'])
def check_assignment_exists(assignment_id):
    exists = any(a['id'] == assignment_id for a in assignments)
    return jsonify({"exists": exists, "assignmentId": assignment_id})

# Endpoint per recuperare informazioni base di un compito (per le valutazioni)
@app.route('/api/v1/assignments/<int:assignment_id>/info', methods=['GET'])
def get_assignment_info(assignment_id):
    assignment = next((a for a in assignments if a['id'] == assignment_id), None)
    if assignment:
        return jsonify({
            "id": assignment['id'],
            "title": assignment['title'],
            "courseId": assignment['courseId'],
            "teacherId": assignment['teacherId'],
            "maxScore": assignment['maxScore'],
            "dueDate": assignment['dueDate']
        })
    return jsonify({"error": "Assignment not found"}), 404

# Health check endpoint
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "service": "Gestione Compiti",
        "status": "UP",
        "timestamp": datetime.now().isoformat()
    })

if __name__ == '__main__':
    print("🚀 Avvio Microservizio Stub: Gestione Compiti")
    print("📍 Endpoints disponibili:")
    print("   - GET  /api/v1/assignments")
    print("   - GET  /api/v1/assignments/{id}")
    print("   - GET  /api/v1/assignments/course/{id}")
    print("   - GET  /api/v1/assignments/teacher/{id}")
    print("   - POST /api/v1/assignments")
    print("   - GET  /api/v1/submissions")
    print("   - GET  /api/v1/submissions/assignment/{id}")
    print("   - GET  /api/v1/submissions/student/{id}")
    print("   - POST /api/v1/submissions")
    print("   - GET  /health")
    print("")
    app.run(debug=True, host='0.0.0.0', port=5001)