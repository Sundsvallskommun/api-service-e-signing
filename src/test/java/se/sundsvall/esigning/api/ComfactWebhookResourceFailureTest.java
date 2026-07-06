package se.sundsvall.esigning.api;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.esigning.Application;
import se.sundsvall.esigning.api.model.ComfactEventNotification;
import se.sundsvall.esigning.service.SigningGatewayService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.esigning.TestUtil.createComfactEventNotification;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class ComfactWebhookResourceFailureTest {

	@MockitoBean
	private SigningGatewayService signingGatewayServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	private static Stream<Arguments> provideBadRequests() {
		final var municipalityId = "2281";
		return Stream.of(
			Arguments.of(municipalityId, createComfactEventNotification(n -> n.setEventTrigger("notAnEvent")), "eventTrigger", "The provided event trigger is not a known Comfact event."),
			Arguments.of(municipalityId, createComfactEventNotification(n -> n.setSigningInstanceId(null)), "signingInstanceId", "must not be blank"),
			Arguments.of("not valid", createComfactEventNotification(), "handleComfactEvent.municipalityId", "not a valid municipality ID"));
	}

	@ParameterizedTest
	@MethodSource("provideBadRequests")
	void handleComfactEvent_BadRequest(final String municipalityId, final ComfactEventNotification notification, final String field, final String message) {

		final var response = webTestClient.post()
			.uri("/" + municipalityId + "/e-signing/webhooks/comfact")
			.bodyValue(notification)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON_VALUE)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::field, Violation::message)
				.containsExactlyInAnyOrder(tuple(field, message));
		});

		verifyNoInteractions(signingGatewayServiceMock);
	}
}
