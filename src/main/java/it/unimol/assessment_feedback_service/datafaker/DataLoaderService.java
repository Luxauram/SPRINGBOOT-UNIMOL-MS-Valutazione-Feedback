package it.unimol.assessment_feedback_service.datafaker;

import it.unimol.assessment_feedback_service.enums.FeedbackCategory;
import it.unimol.assessment_feedback_service.enums.ReferenceType;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import it.unimol.assessment_feedback_service.model.Assessment;
import it.unimol.assessment_feedback_service.model.DetailedFeedback;
import it.unimol.assessment_feedback_service.model.SurveyResponse;
import it.unimol.assessment_feedback_service.model.TeacherSurvey;
import it.unimol.assessment_feedback_service.repository.AssessmentRepository;
import it.unimol.assessment_feedback_service.repository.DetailedFeedbackRepository;
import it.unimol.assessment_feedback_service.repository.SurveyResponseRepository;
import it.unimol.assessment_feedback_service.repository.TeacherSurveyRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Profile({"dev", "local"})
public class DataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    @Autowired
    private Faker faker;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private DetailedFeedbackRepository detailedFeedbackRepository;

    @Autowired
    private TeacherSurveyRepository teacherSurveyRepository;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.data.faker.assessments.count:50}")
    private int assessmentsCount;

    @Value("${app.data.faker.feedbacks.count:150}")
    private int feedbacksCount;

    @Value("${app.data.faker.reset-on-startup:false}")
    private boolean resetOnStartup;

    @Value("${app.data.faker.force-reload:false}")
    private boolean forceReload;

    private final Random random = new Random();

    // Pool di ID per simulare utenti, corsi, ecc.
    private static final Long[] STUDENT_IDS = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,
            11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L};
    private static final Long[] TEACHER_IDS = {101L, 102L, 103L, 104L, 105L, 106L, 107L, 108L};
    private static final Long[] COURSE_IDS = {201L, 202L, 203L, 204L, 205L, 206L, 207L, 208L, 209L, 210L};
    private static final Long[] REFERENCE_IDS = {301L, 302L, 303L, 304L, 305L, 306L, 307L, 308L, 309L, 310L,
            311L, 312L, 313L, 314L, 315L, 316L, 317L, 318L, 319L, 320L};

    @Transactional
    public void loadInitialData() {
        logger.info("Inizio caricamento dati faker...");

        if (resetOnStartup) {
            clearAllData();
        }

        if (forceReload || shouldLoadData()) {
            generateAssessments();
            generateDetailedFeedbacks();
            generateTeacherSurveys();
            generateSurveyResponses();

            logger.info("Caricamento dati faker completato!");
            logDataStatistics();
        } else {
            logger.info("Dati già presenti, caricamento saltato.");
        }
    }

    private boolean shouldLoadData() {
        long assessmentCount = assessmentRepository.count();
        long feedbackCount = detailedFeedbackRepository.count();
        return assessmentCount == 0 || feedbackCount == 0;
    }

    private void clearAllData() {
        logger.info("Reset dei dati esistenti...");
        surveyResponseRepository.deleteAll();
        detailedFeedbackRepository.deleteAll();
        teacherSurveyRepository.deleteAll();
        assessmentRepository.deleteAll();
        entityManager.flush();
        logger.info("Reset completato.");
    }

    private void generateAssessments() {
        logger.info("Generazione {} valutazioni...", assessmentsCount);

        List<Assessment> assessments = new ArrayList<>();

        for (int i = 0; i < assessmentsCount; i++) {
            Assessment assessment = Assessment.builder()
                    .referenceId(getRandomReferenceId())
                    .referenceType(getRandomReferenceType())
                    .studentId(getRandomStudentId())
                    .teacherId(getRandomTeacherId())
                    .score(generateScore())
                    .assessmentDate(generateRandomDateInPast(30))
                    .notes(generateAssessmentNotes())
                    .courseId(getRandomCourseId())
                    .build();

            assessments.add(assessment);
        }

        assessmentRepository.saveAll(assessments);
        logger.info("Salvate {} valutazioni", assessments.size());
    }

    private void generateDetailedFeedbacks() {
        logger.info("Generazione feedback dettagliati...");

        List<Assessment> assessments = assessmentRepository.findAll();
        if (assessments.isEmpty()) {
            logger.warn("Nessuna valutazione trovata per generare i feedback!");
            return;
        }

        List<DetailedFeedback> feedbacks = new ArrayList<>();

        // Generiamo almeno un feedback per ogni assessment
        for (Assessment assessment : assessments) {
            DetailedFeedback feedback = DetailedFeedback.builder()
                    .assessment(assessment)
                    .feedbackText(generateFeedbackText())
                    .category(getRandomFeedbackCategory())
                    .strengths(generateStrengths())
                    .improvementAreas(generateImprovementAreas())
                    .build();

            feedbacks.add(feedback);
        }

        // Aggiungiamo feedback extra se richiesti
        int extraFeedbacks = feedbacksCount - assessments.size();
        for (int i = 0; i < extraFeedbacks && i < assessments.size(); i++) {
            Assessment randomAssessment = assessments.get(random.nextInt(assessments.size()));

            DetailedFeedback feedback = DetailedFeedback.builder()
                    .assessment(randomAssessment)
                    .feedbackText(generateFeedbackText())
                    .category(getRandomFeedbackCategory())
                    .strengths(generateStrengths())
                    .improvementAreas(generateImprovementAreas())
                    .build();

            feedbacks.add(feedback);
        }

        detailedFeedbackRepository.saveAll(feedbacks);
        logger.info("Salvati {} feedback dettagliati", feedbacks.size());
    }

    private void generateTeacherSurveys() {
        logger.info("Generazione sondaggi docenti...");

        List<TeacherSurvey> surveys = new ArrayList<>();
        String[] academicYears = {"2023/2024", "2024/2025"};

        for (Long teacherId : TEACHER_IDS) {
            for (Long courseId : COURSE_IDS) {
                if (random.nextBoolean()) { // 50% di probabilità
                    TeacherSurvey survey = TeacherSurvey.builder()
                            .courseId(courseId)
                            .teacherId(teacherId)
                            .academicYear(academicYears[random.nextInt(academicYears.length)])
                            .semester(random.nextInt(2) + 1)
                            .status(getRandomSurveyStatus())
                            .creationDate(generateRandomDateInPast(60))
                            .closingDate(random.nextBoolean() ? generateRandomDateInPast(10) : null)
                            .build();

                    surveys.add(survey);
                }
            }
        }

        teacherSurveyRepository.saveAll(surveys);
        logger.info("Salvati {} sondaggi docenti", surveys.size());
    }

    private void generateSurveyResponses() {
        logger.info("Generazione risposte ai sondaggi...");

        List<TeacherSurvey> surveys = teacherSurveyRepository.findAll();
        if (surveys.isEmpty()) {
            logger.warn("Nessun sondaggio trovato per generare le risposte!");
            return;
        }

        List<SurveyResponse> responses = new ArrayList<>();

        for (TeacherSurvey survey : surveys) {
            // Generiamo 3-8 risposte per sondaggio
            int responseCount = random.nextInt(6) + 3;

            for (int i = 0; i < responseCount; i++) {
                SurveyResponse response = SurveyResponse.builder()
                        .survey(survey)
                        .studentId(getRandomStudentId())
                        .questionId(Long.valueOf(random.nextInt(10) + 1)) // Simuliamo 10 possibili domande
                        .numericRating(random.nextInt(5) + 1) // Scala 1-5
                        .textComment(generateSurveyComment())
                        .submissionDate(generateRandomDateAfter(survey.getCreationDate()))
                        .build();

                responses.add(response);
            }
        }

        surveyResponseRepository.saveAll(responses);
        logger.info("Salvate {} risposte ai sondaggi", responses.size());
    }

    // Metodi di utility per la generazione dei dati

    private Long getRandomStudentId() {
        return STUDENT_IDS[random.nextInt(STUDENT_IDS.length)];
    }

    private Long getRandomTeacherId() {
        return TEACHER_IDS[random.nextInt(TEACHER_IDS.length)];
    }

    private Long getRandomCourseId() {
        return COURSE_IDS[random.nextInt(COURSE_IDS.length)];
    }

    private Long getRandomReferenceId() {
        return REFERENCE_IDS[random.nextInt(REFERENCE_IDS.length)];
    }

    private ReferenceType getRandomReferenceType() {
        ReferenceType[] types = ReferenceType.values();
        return types[random.nextInt(types.length)];
    }

    private FeedbackCategory getRandomFeedbackCategory() {
        FeedbackCategory[] categories = FeedbackCategory.values();
        return categories[random.nextInt(categories.length)];
    }

    private SurveyStatus getRandomSurveyStatus() {
        SurveyStatus[] statuses = SurveyStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private Double generateScore() {
        // Generiamo voti più realistici (maggiormente concentrati tra 18-28)
        if (random.nextDouble() < 0.05) return (double) (random.nextInt(8) + 10); // 5% voti bassi (10-17)
        if (random.nextDouble() < 0.15) return (double) (random.nextInt(3) + 28); // 15% voti alti (28-30)
        return (double) (random.nextInt(10) + 18); // 80% voti medi (18-27)
    }

    private LocalDateTime generateRandomDateInPast(int maxDaysAgo) {
        return LocalDateTime.now().minusDays(random.nextInt(maxDaysAgo));
    }

    private LocalDateTime generateRandomDateAfter(LocalDateTime afterDate) {
        long daysBetween = random.nextInt(30) + 1;
        return afterDate.plusDays(daysBetween);
    }

    private String generateAssessmentNotes() {
        String[] templates = {
                "Ottimo lavoro, continua così!",
                "Buona comprensione dell'argomento, ma servono miglioramenti nella presentazione.",
                "Lavoro soddisfacente nel complesso.",
                "Necessario approfondire alcuni concetti fondamentali.",
                "Eccellente analisi critica e originalità.",
                "Buon lavoro di gruppo, coordinazione efficace.",
                "Presentazione chiara e ben strutturata.",
                "Serve maggiore attenzione ai dettagli.",
                "Dimostrata buona padronanza della materia.",
                "Lavoro accurato e ben documentato."
        };
        return templates[random.nextInt(templates.length)];
    }

    private String generateFeedbackText() {
        return faker.lorem().paragraph(3);
    }

    private String generateStrengths() {
        String[] strengths = {
                "Ottima capacità di analisi critica",
                "Buona padronanza degli strumenti tecnici",
                "Eccellente capacità di comunicazione",
                "Creatività e originalità nell'approccio",
                "Precisione e attenzione ai dettagli",
                "Buone capacità collaborative",
                "Gestione efficace del tempo",
                "Solida base teorica"
        };
        return strengths[random.nextInt(strengths.length)];
    }

    private String generateImprovementAreas() {
        String[] areas = {
                "Migliorare la sintesi delle informazioni",
                "Approfondire la conoscenza teorica",
                "Sviluppare maggiore autonomia",
                "Migliorare la presentazione orale",
                "Aumentare la precisione nei calcoli",
                "Sviluppare il pensiero critico",
                "Migliorare la gestione del tempo",
                "Approfondire l'uso degli strumenti digitali"
        };
        return areas[random.nextInt(areas.length)];
    }

    private String generateSurveyComment() {
        String[] comments = {
                "Il corso è stato molto interessante e ben strutturato.",
                "Il docente spiega in modo chiaro e coinvolgente.",
                "Buon equilibrio tra teoria e pratica.",
                "Sarebbe utile avere più esempi pratici.",
                "Materiale didattico di ottima qualità.",
                "Le lezioni sono sempre stimolanti.",
                "Approccio didattico innovativo e efficace.",
                "Ottima disponibilità del docente durante le ore di ricevimento."
        };
        return comments[random.nextInt(comments.length)];
    }

    private void logDataStatistics() {
        long assessments = assessmentRepository.count();
        long feedbacks = detailedFeedbackRepository.count();
        long surveys = teacherSurveyRepository.count();
        long responses = surveyResponseRepository.count();

        logger.info("=== STATISTICHE DATI GENERATI ===");
        logger.info("Valutazioni: {}", assessments);
        logger.info("Feedback dettagliati: {}", feedbacks);
        logger.info("Sondaggi docenti: {}", surveys);
        logger.info("Risposte sondaggi: {}", responses);
        logger.info("================================");
    }
}