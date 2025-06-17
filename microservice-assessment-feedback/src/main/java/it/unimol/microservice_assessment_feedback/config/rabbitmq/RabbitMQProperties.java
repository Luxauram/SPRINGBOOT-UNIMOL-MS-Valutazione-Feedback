package it.unimol.microservice_assessment_feedback.config.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private Exchange exchange = new Exchange();
    private Queue queue = new Queue();
    private Message message = new Message();

    // ===================================================================
    //  EXCHANGE CONFIGURATION
    // ===================================================================
    public static class Exchange {
        private String assessments;
        private String dlx = "unimol.dlx";

        public String getAssessments() {
            return assessments;
        }

        public void setAssessments(String assessments) {
            this.assessments = assessments;
        }

        public String getDlx() {
            return dlx;
        }

        public void setDlx(String dlx) {
            this.dlx = dlx;
        }
    }

    // ===================================================================
    //  QUEUE CONFIGURATION
    // ===================================================================
    public static class Queue {
        private String dlq = "assessments.dlq";
        private Assessment assessment = new Assessment();
        private Feedback feedback = new Feedback();
        private Survey survey = new Survey();

        // CONSUMERS QUEUES - Default Values
        private String assignmentSubmitted = "assignment.submitted";
        private String assignmentCreated = "assignment.created.queue";
        private String assignmentUpdated = "assignment.updated.queue";
        private String examCompleted = "exam.completed";
        private String examGradeRegistered = "exam.grade.registered";
        private String courseCreated = "course.created";
        private String courseDeleted = "course.deleted";
        private String teacherCreated = "teacher.created";
        private String studentCreated = "student.created";
        private String userCreated = "user.created.queue";
        private String userUpdated = "user.updated.queue";
        private String userDeleted = "user.deleted.queue";
        private String roleAssigned = "role.assigned.queue";

        public static class Assessment {
            private String created;
            private String updated;
            private String deleted;

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public String getUpdated() {
                return updated;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public String getDeleted() {
                return deleted;
            }

            public void setDeleted(String deleted) {
                this.deleted = deleted;
            }
        }

        public static class Feedback {
            private String created;
            private String updated;
            private String deleted;

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public String getUpdated() {
                return updated;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public String getDeleted() {
                return deleted;
            }

            public void setDeleted(String deleted) {
                this.deleted = deleted;
            }
        }

        public static class Survey {
            private String completed;
            private Response response = new Response();

            public static class Response {
                private String submitted;
                private String bulkSubmitted = "survey.responses.bulk.submitted";

                public String getSubmitted() {
                    return submitted;
                }

                public void setSubmitted(String submitted) {
                    this.submitted = submitted;
                }

                public String getBulkSubmitted() {
                    return bulkSubmitted;
                }

                public void setBulkSubmitted(String bulkSubmitted) {
                    this.bulkSubmitted = bulkSubmitted;
                }
            }

            private String resultsRequested = "survey.results.requested";
            private String commentsRequested = "survey.comments.requested";

            public String getCompleted() {
                return completed;
            }

            public void setCompleted(String completed) {
                this.completed = completed;
            }

            public Response getResponse() {
                return response;
            }

            public void setResponse(Response response) {
                this.response = response;
            }

            public String getResultsRequested() {
                return resultsRequested;
            }

            public void setResultsRequested(String resultsRequested) {
                this.resultsRequested = resultsRequested;
            }

            public String getCommentsRequested() {
                return commentsRequested;
            }

            public void setCommentsRequested(String commentsRequested) {
                this.commentsRequested = commentsRequested;
            }
        }

        // Getters e Setters - Queue Class Main
        public String getDlq() {
            return dlq;
        }
        public void setDlq(String dlq) {
            this.dlq = dlq;
        }

        public Assessment getAssessment() {
            return assessment;
        }
        public void setAssessment(Assessment assessment) {
            this.assessment = assessment;
        }

        public Feedback getFeedback() {
            return feedback;
        }
        public void setFeedback(Feedback feedback) {
            this.feedback = feedback;
        }

        public Survey getSurvey() {
            return survey;
        }
        public void setSurvey(Survey survey) {
            this.survey = survey;
        }

        public String getAssignmentSubmitted() {
            return assignmentSubmitted;
        }
        public void setAssignmentSubmitted(String assignmentSubmitted) {
            this.assignmentSubmitted = assignmentSubmitted;
        }

        public String getAssignmentCreated() {
            return assignmentCreated;
        }
        public void setAssignmentCreated(String assignmentCreated) {
            this.assignmentCreated = assignmentCreated;
        }

        public String getAssignmentUpdated() {
            return assignmentUpdated;
        }
        public void setAssignmentUpdated(String assignmentUpdated) {
            this.assignmentUpdated = assignmentUpdated;
        }

        public String getExamCompleted() {
            return examCompleted;
        }
        public void setExamCompleted(String examCompleted) {
            this.examCompleted = examCompleted;
        }

        public String getExamGradeRegistered() {
            return examGradeRegistered;
        }
        public void setExamGradeRegistered(String examGradeRegistered) {
            this.examGradeRegistered = examGradeRegistered;
        }

        public String getCourseCreated() {
            return courseCreated;
        }
        public void setCourseCreated(String courseCreated) {
            this.courseCreated = courseCreated;
        }

        public String getCourseDeleted() {
            return courseDeleted;
        }
        public void setCourseDeleted(String courseDeleted) {
            this.courseDeleted = courseDeleted;
        }

        public String getTeacherCreated() {
            return teacherCreated;
        }
        public void setTeacherCreated(String teacherCreated) {
            this.teacherCreated = teacherCreated;
        }

        public String getStudentCreated() {
            return studentCreated;
        }
        public void setStudentCreated(String studentCreated) {
            this.studentCreated = studentCreated;
        }

        public String getUserCreated() {
            return userCreated;
        }
        public void setUserCreated(String userCreated) {
            this.userCreated = userCreated;
        }

        public String getUserUpdated() {
            return userUpdated;
        }
        public void setUserUpdated(String userUpdated) {
            this.userUpdated = userUpdated;
        }

        public String getUserDeleted() {
            return userDeleted;
        }
        public void setUserDeleted(String userDeleted) {
            this.userDeleted = userDeleted;
        }

        public String getRoleAssigned() {
            return roleAssigned;
        }
        public void setRoleAssigned(String roleAssigned) {
            this.roleAssigned = roleAssigned;
        }
    }

    // ===================================================================
    //  MESSAGE CONFIGURATION
    // ===================================================================
    public static class Message {
        private int ttl = 86400000;

        public int getTtl() { return ttl; }
        public void setTtl(int ttl) { this.ttl = ttl; }
    }

    // Getters e Setters
    public Exchange getExchange() { return exchange; }
    public void setExchange(Exchange exchange) { this.exchange = exchange; }

    public Queue getQueue() { return queue; }
    public void setQueue(Queue queue) { this.queue = queue; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
}