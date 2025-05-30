from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime, timedelta
import json
from typing import Dict, List, Any

# Configurazione Flask
app = Flask(__name__)
CORS(app)

# 📊 DATI MOCK PER TESTING
# Statistiche e dati aggregati precaricati

# Report studenti (performance per studente)
student_reports = {
    301: {
        "studentId": 301,
        "studentName": "Mario Rossi",
        "totalAssignments": 8,
        "completedAssignments": 7,
        "averageScore": 24.5,
        "totalExams": 3,
        "passedExams": 3,
        "averageExamScore": 27.8,
        "totalFeedbacks": 5,
        "courses": [101, 102],
        "lastActivity": "2024-05-25T14:30:00",
        "performance": "EXCELLENT",
        "trends": {
            "improving": True,
            "consistentPerformance": True,
            "averageProgressRate": 8.5
        }
    },
    302: {
        "studentId": 302,
        "studentName": "Giulia Bianchi",
        "totalAssignments": 6,
        "completedAssignments": 5,
        "averageScore": 21.2,
        "totalExams": 2,
        "passedExams": 2,
        "averageExamScore": 25.0,
        "totalFeedbacks": 3,
        "courses": [101, 103],
        "lastActivity": "2024-05-24T16:45:00",
        "performance": "GOOD",
        "trends": {
            "improving": True,
            "consistentPerformance": False,
            "averageProgressRate": 6.2
        }
    },
    303: {
        "studentId": 303,
        "studentName": "Luca Verdi",
        "totalAssignments": 4,
        "completedAssignments": 3,
        "averageScore": 18.7,
        "totalExams": 1,
        "passedExams": 1,
        "averageExamScore": 22.5,
        "totalFeedbacks": 2,
        "courses": [102],
        "lastActivity": "2024-05-23T11:20:00",
        "performance": "AVERAGE",
        "trends": {
            "improving": False,
            "consistentPerformance": True,
            "averageProgressRate": 4.1
        }
    }
}

# Report per corso (performance degli studenti in un corso)
course_reports = {
    101: {
        "courseId": 101,
        "courseName": "Ingegneria del Software",
        "totalStudents": 25,
        "activeStudents": 23,
        "totalAssignments": 5,
        "totalExams": 2,
        "averageCourseScore": 23.8,
        "passRate": 92.0,
        "completionRate": 88.0,
        "studentsPerformance": {
            "excellent": 8,
            "good": 12,
            "average": 3,
            "poor": 2
        },
        "assignmentStats": {
            "averageSubmissionTime": 2.3,  # giorni prima della scadenza
            "onTimeSubmissions": 89.5,
            "lateSubmissions": 10.5
        },
        "topPerformers": [301, 302],
        "lastUpdated": "2024-05-29T09:00:00"
    },
    102: {
        "courseId": 102,
        "courseName": "Database e Sistemi Informativi",
        "totalStudents": 18,
        "activeStudents": 17,
        "totalAssignments": 4,
        "totalExams": 2,
        "averageCourseScore": 21.5,
        "passRate": 85.0,
        "completionRate": 82.0,
        "studentsPerformance": {
            "excellent": 4,
            "good": 8,
            "average": 4,
            "poor": 2
        },
        "assignmentStats": {
            "averageSubmissionTime": 1.8,
            "onTimeSubmissions": 76.3,
            "lateSubmissions": 23.7
        },
        "topPerformers": [301, 303],
        "lastUpdated": "2024-05-29T09:00:00"
    },
    103: {
        "courseId": 103,
        "courseName": "Programmazione Web",
        "totalStudents": 30,
        "activeStudents": 28,
        "totalAssignments": 6,
        "totalExams": 1,
        "averageCourseScore": 26.2,
        "passRate": 95.0,
        "completionRate": 93.0,
        "studentsPerformance": {
            "excellent": 12,
            "good": 14,
            "average": 2,
            "poor": 2
        },
        "assignmentStats": {
            "averageSubmissionTime": 3.1,
            "onTimeSubmissions": 94.2,
            "lateSubmissions": 5.8
        },
        "topPerformers": [302],
        "lastUpdated": "2024-05-29T09:00:00"
    }
}

# Report per docente (valutazioni ricevute dal docente)
teacher_reports = {
    201: {
        "teacherId": 201,
        "teacherName": "Prof. Alessandro Neri",
        "totalCourses": 2,
        "totalStudents": 43,
        "coursesIds": [101, 103],
        "averageTeacherRating": 4.2,  # su 5
        "totalFeedbacks": 38,
        "teachingStats": {
            "clarity": 4.1,
            "availability": 4.5,
            "courseOrganization": 4.0,
            "examPreparation": 4.3
        },
        "studentSatisfaction": 85.2,  # percentuale
        "feedbackTrends": {
            "improving": True,
            "consistentRating": True,
            "lastMonthRating": 4.4
        },
        "coursesPerformance": {
            101: {"averageScore": 23.8, "passRate": 92.0},
            103: {"averageScore": 26.2, "passRate": 95.0}
        },
        "lastUpdated": "2024-05-29T09:00:00"
    },
    202: {
        "teacherId": 202,
        "teacherName": "Prof.ssa Maria Giovanna Pecci",
        "totalCourses": 1,
        "totalStudents": 18,
        "coursesIds": [102],
        "averageTeacherRating": 3.8,
        "totalFeedbacks": 15,
        "teachingStats": {
            "clarity": 3.9,
            "availability": 3.7,
            "courseOrganization": 3.8,
            "examPreparation": 3.8
        },
        "studentSatisfaction": 76.5,
        "feedbackTrends": {
            "improving": False,
            "consistentRating": True,
            "lastMonthRating": 3.6
        },
        "coursesPerformance": {
            102: {"averageScore": 21.5, "passRate": 85.0}
        },
        "lastUpdated": "2024-05-29T09:00:00"
    }
}

# Dati delle valutazioni (alimentati dal tuo microservizio)
evaluations_data = [
    {
        "id": 1,
        "type": "ASSIGNMENT",
        "referenceId": 1,
        "studentId": 301,
        "courseId": 101,
        "teacherId": 201,
        "score": 28.5,
        "maxScore": 30.0,
        "feedback": "Ottimo lavoro sul progetto microservizi",
        "timestamp": "2024-05-20T15:30:00",
        "evaluatedBy": 201
    },
    {
        "id": 2,
        "type": "ASSIGNMENT",
        "referenceId": 1,
        "studentId": 302,
        "courseId": 101,
        "teacherId": 201,
        "score": 25.0,
        "maxScore": 30.0,
        "feedback": "Buon lavoro, ma manca documentazione",
        "timestamp": "2024-05-21T10:15:00",
        "evaluatedBy": 201
    },
    {
        "id": 3,
        "type": "EXAM",
        "referenceId": 1,
        "studentId": 301,
        "courseId": 101,
        "teacherId": 201,
        "score": 29.0,
        "maxScore": 30.0,
        "feedback": "Preparazione eccellente",
        "timestamp": "2024-05-22T14:00:00",
        "evaluatedBy": 201
    }
]

# 🔧 UTILITY FUNCTIONS

def log_request(endpoint: str, method: str, params: Dict = None):
    """Log delle richieste ricevute"""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"📍 [{timestamp}] {method} {endpoint}" + (f" - {params}" if params else ""))

def calculate_student_stats(student_id: int) -> Dict[str, Any]:
    """Calcola statistiche aggiornate per uno studente"""
    student_evals = [e for e in evaluations_data if e['studentId'] == student_id]

    if not student_evals:
        return None

    total_score = sum(e['score'] for e in student_evals)
    max_total_score = sum(e['maxScore'] for e in student_evals)
    average_percentage = (total_score / max_total_score) * 100 if max_total_score > 0 else 0

    return {
        "totalEvaluations": len(student_evals),
        "averagePercentage": round(average_percentage, 2),
        "lastEvaluation": max(student_evals, key=lambda x: x['timestamp'])['timestamp']
    }

# 🚀 ENDPOINTS

@app.route('/health', methods=['GET'])
def health_check():
    """Health check del microservizio"""
    log_request('/health', 'GET')
    return jsonify({
        "service": "Analisi e Reportistica",
        "status": "UP",
        "timestamp": datetime.now().isoformat(),
        "version": "1.0.0"
    })

# 📊 REPORT STUDENTI

@app.route('/api/v1/reports/students', methods=['GET'])
def get_all_student_reports():
    """Recupera tutti i report degli studenti"""
    log_request('/api/v1/reports/students', 'GET')
    return jsonify({
        "students": list(student_reports.values()),
        "total": len(student_reports),
        "timestamp": datetime.now().isoformat()
    })

@app.route('/api/v1/reports/students/<int:student_id>', methods=['GET'])
def get_student_report(student_id):
    """Recupera report dettagliato di uno studente specifico"""
    log_request(f'/api/v1/reports/students/{student_id}', 'GET')

    if student_id not in student_reports:
        return jsonify({"error": "Student report not found"}), 404

    report = student_reports[student_id].copy()

    # Aggiungi statistiche in tempo reale
    live_stats = calculate_student_stats(student_id)
    if live_stats:
        report["liveStats"] = live_stats

    return jsonify(report)

@app.route('/api/v1/reports/students/<int:student_id>/performance', methods=['GET'])
def get_student_performance_summary(student_id):
    """Recupera sommario delle performance di uno studente"""
    log_request(f'/api/v1/reports/students/{student_id}/performance', 'GET')

    if student_id not in student_reports:
        return jsonify({"error": "Student not found"}), 404

    student = student_reports[student_id]
    return jsonify({
        "studentId": student_id,
        "performance": student["performance"],
        "averageScore": student["averageScore"],
        "completionRate": (student["completedAssignments"] / student["totalAssignments"]) * 100,
        "trends": student["trends"],
        "lastActivity": student["lastActivity"]
    })

# 📚 REPORT CORSI

@app.route('/api/v1/reports/courses', methods=['GET'])
def get_all_course_reports():
    """Recupera tutti i report dei corsi"""
    log_request('/api/v1/reports/courses', 'GET')
    return jsonify({
        "courses": list(course_reports.values()),
        "total": len(course_reports),
        "timestamp": datetime.now().isoformat()
    })

@app.route('/api/v1/reports/courses/<int:course_id>', methods=['GET'])
def get_course_report(course_id):
    """Recupera report dettagliato di un corso specifico"""
    log_request(f'/api/v1/reports/courses/{course_id}', 'GET')

    if course_id not in course_reports:
        return jsonify({"error": "Course report not found"}), 404

    return jsonify(course_reports[course_id])

@app.route('/api/v1/reports/courses/<int:course_id>/students', methods=['GET'])
def get_course_students_performance(course_id):
    """Recupera performance degli studenti in un corso"""
    log_request(f'/api/v1/reports/courses/{course_id}/students', 'GET')

    if course_id not in course_reports:
        return jsonify({"error": "Course not found"}), 404

    # Trova studenti iscritti al corso
    course_students = [s for s in student_reports.values() if course_id in s["courses"]]

    return jsonify({
        "courseId": course_id,
        "courseName": course_reports[course_id]["courseName"],
        "students": course_students,
        "summary": course_reports[course_id]["studentsPerformance"],
        "totalStudents": len(course_students)
    })

# 👨‍🏫 REPORT DOCENTI

@app.route('/api/v1/reports/teachers', methods=['GET'])
def get_all_teacher_reports():
    """Recupera tutti i report dei docenti"""
    log_request('/api/v1/reports/teachers', 'GET')
    return jsonify({
        "teachers": list(teacher_reports.values()),
        "total": len(teacher_reports),
        "timestamp": datetime.now().isoformat()
    })

@app.route('/api/v1/reports/teachers/<int:teacher_id>', methods=['GET'])
def get_teacher_report(teacher_id):
    """Recupera report dettagliato di un docente"""
    log_request(f'/api/v1/reports/teachers/{teacher_id}', 'GET')

    if teacher_id not in teacher_reports:
        return jsonify({"error": "Teacher report not found"}), 404

    return jsonify(teacher_reports[teacher_id])

@app.route('/api/v1/reports/teachers/<int:teacher_id>/evaluations', methods=['GET'])
def get_teacher_evaluations_summary(teacher_id):
    """Recupera sommario delle valutazioni date da un docente"""
    log_request(f'/api/v1/reports/teachers/{teacher_id}/evaluations', 'GET')

    if teacher_id not in teacher_reports:
        return jsonify({"error": "Teacher not found"}), 404

    # Valutazioni date dal docente
    teacher_evals = [e for e in evaluations_data if e['evaluatedBy'] == teacher_id]

    if not teacher_evals:
        return jsonify({
            "teacherId": teacher_id,
            "totalEvaluations": 0,
            "message": "No evaluations found"
        })

    total_score = sum(e['score'] for e in teacher_evals)
    max_total = sum(e['maxScore'] for e in teacher_evals)
    avg_percentage = (total_score / max_total) * 100 if max_total > 0 else 0

    return jsonify({
        "teacherId": teacher_id,
        "totalEvaluations": len(teacher_evals),
        "averageScoreGiven": round(total_score / len(teacher_evals), 2),
        "averagePercentageGiven": round(avg_percentage, 2),
        "evaluationTypes": {
            "assignments": len([e for e in teacher_evals if e['type'] == 'ASSIGNMENT']),
            "exams": len([e for e in teacher_evals if e['type'] == 'EXAM'])
        },
        "lastEvaluation": max(teacher_evals, key=lambda x: x['timestamp'])['timestamp']
    })

# 📈 INVIO DATI (per il tuo microservizio)

@app.route('/api/v1/data/evaluations', methods=['POST'])
def receive_evaluation_data():
    """Riceve dati di valutazione dal microservizio Valutazione e Feedback"""
    log_request('/api/v1/data/evaluations', 'POST', request.json)

    try:
        evaluation = request.json

        # Validazione base
        required_fields = ['type', 'referenceId', 'studentId', 'courseId', 'score', 'maxScore']
        if not all(field in evaluation for field in required_fields):
            return jsonify({"error": "Missing required fields"}), 400

        # Aggiungi timestamp se non presente
        if 'timestamp' not in evaluation:
            evaluation['timestamp'] = datetime.now().isoformat()

        # Aggiungi ID sequenziale
        evaluation['id'] = len(evaluations_data) + 1

        # Salva i dati (in memoria per questo stub)
        evaluations_data.append(evaluation)

        return jsonify({
            "message": "Evaluation data received successfully",
            "id": evaluation['id'],
            "timestamp": evaluation['timestamp']
        }), 201

    except Exception as e:
        return jsonify({"error": f"Invalid data format: {str(e)}"}), 400

@app.route('/api/v1/data/evaluations', methods=['GET'])
def get_evaluations_data():
    """Recupera tutti i dati delle valutazioni"""
    log_request('/api/v1/data/evaluations', 'GET')

    # Filtri opzionali
    student_id = request.args.get('studentId', type=int)
    course_id = request.args.get('courseId', type=int)
    teacher_id = request.args.get('teacherId', type=int)
    eval_type = request.args.get('type')

    filtered_data = evaluations_data.copy()

    if student_id:
        filtered_data = [e for e in filtered_data if e['studentId'] == student_id]
    if course_id:
        filtered_data = [e for e in filtered_data if e['courseId'] == course_id]
    if teacher_id:
        filtered_data = [e for e in filtered_data if e.get('evaluatedBy') == teacher_id]
    if eval_type:
        filtered_data = [e for e in filtered_data if e['type'] == eval_type]

    return jsonify({
        "evaluations": filtered_data,
        "total": len(filtered_data),
        "filters": {
            "studentId": student_id,
            "courseId": course_id,
            "teacherId": teacher_id,
            "type": eval_type
        }
    })

# 📊 STATISTICHE GENERALI

@app.route('/api/v1/analytics/summary', methods=['GET'])
def get_analytics_summary():
    """Recupera sommario generale delle analisi"""
    log_request('/api/v1/analytics/summary', 'GET')

    total_evaluations = len(evaluations_data)
    total_students = len(student_reports)
    total_courses = len(course_reports)
    total_teachers = len(teacher_reports)

    # Calcola media generale
    if evaluations_data:
        total_score = sum(e['score'] for e in evaluations_data)
        max_total = sum(e['maxScore'] for e in evaluations_data)
        overall_average = (total_score / max_total) * 100 if max_total > 0 else 0
    else:
        overall_average = 0

    return jsonify({
        "totalEvaluations": total_evaluations,
        "totalStudents": total_students,
        "totalCourses": total_courses,
        "totalTeachers": total_teachers,
        "overallAveragePercentage": round(overall_average, 2),
        "lastUpdated": datetime.now().isoformat(),
        "systemStats": {
            "activeStudents": sum(1 for s in student_reports.values() if s["performance"] != "POOR"),
            "excellentPerformers": sum(1 for s in student_reports.values() if s["performance"] == "EXCELLENT"),
            "averageTeacherRating": round(sum(t["averageTeacherRating"] for t in teacher_reports.values()) / len(teacher_reports), 2) if teacher_reports else 0
        }
    })

# 🎯 ENDPOINT SPECIFICI PER INTEGRAZIONE

@app.route('/api/v1/integration/student/<int:student_id>/exists', methods=['GET'])
def check_student_exists_in_analytics(student_id):
    """Verifica se esistono dati analitici per uno studente"""
    log_request(f'/api/v1/integration/student/{student_id}/exists', 'GET')

    exists = student_id in student_reports
    return jsonify({
        "exists": exists,
        "studentId": student_id,
        "hasAnalytics": exists
    })

@app.route('/api/v1/integration/course/<int:course_id>/exists', methods=['GET'])
def check_course_exists_in_analytics(course_id):
    """Verifica se esistono dati analitici per un corso"""
    log_request(f'/api/v1/integration/course/{course_id}/exists', 'GET')

    exists = course_id in course_reports
    return jsonify({
        "exists": exists,
        "courseId": course_id,
        "hasAnalytics": exists
    })

# 🔧 GESTIONE ERRORI

@app.errorhandler(404)
def not_found(error):
    return jsonify({"error": "Endpoint not found"}), 404

@app.errorhandler(500)
def internal_error(error):
    return jsonify({"error": "Internal server error"}), 500

# 🚀 AVVIO DEL SERVIZIO

if __name__ == '__main__':
    print("\n" + "="*60)
    print("🚀 Avvio Microservizio Stub: Analisi e Reportistica")
    print("="*60)
    print("📊 Porta: 5004")
    print("🌐 URL Base: http://localhost:5004")
    print("❤️  Health Check: http://localhost:5004/health")
    print("\n📋 Endpoints Disponibili:")
    print("   📊 Report Studenti:")
    print("      GET /api/v1/reports/students")
    print("      GET /api/v1/reports/students/{id}")
    print("      GET /api/v1/reports/students/{id}/performance")
    print("   📚 Report Corsi:")
    print("      GET /api/v1/reports/courses")
    print("      GET /api/v1/reports/courses/{id}")
    print("      GET /api/v1/reports/courses/{id}/students")
    print("   👨‍🏫 Report Docenti:")
    print("      GET /api/v1/reports/teachers")
    print("      GET /api/v1/reports/teachers/{id}")
    print("      GET /api/v1/reports/teachers/{id}/evaluations")
    print("   📈 Invio Dati:")
    print("      POST /api/v1/data/evaluations")
    print("      GET /api/v1/data/evaluations")
    print("   📊 Analisi:")
    print("      GET /api/v1/analytics/summary")
    print("   🔗 Integrazione:")
    print("      GET /api/v1/integration/student/{id}/exists")
    print("      GET /api/v1/integration/course/{id}/exists")
    print("="*60)
    print("💡 Usa Ctrl+C per fermare il servizio")
    print("="*60 + "\n")

    app.run(debug=True, host='0.0.0.0', port=5004)