package it.unimol.assessment_feedback_service.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    // Costruttore vuoto (sostituisce @NoArgsConstructor di Lombok)
    public ErrorResponse() {
    }

    // Costruttore con tutti i parametri (sostituisce @AllArgsConstructor di Lombok)
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getter e Setter (sostituiscono @Data di Lombok)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Metodi equals, hashCode e toString (facoltativi, ma spesso utili)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorResponse that = (ErrorResponse) o;

        if (status != that.status) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + status;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}