package it.unimol.microservice_assessment_feedback;

import it.unimol.microservice_assessment_feedback.config.TestRabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRabbitConfig.class)
class MicroserviceAssessmentFeedbackApplicationTests {
	@Test
	void contextLoads() {
	}
}
