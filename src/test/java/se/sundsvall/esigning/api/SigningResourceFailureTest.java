package se.sundsvall.esigning.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.esigning.TestUtil.createSignatory;
import static se.sundsvall.esigning.TestUtil.createSigningRequest;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.esigning.Application;
import se.sundsvall.esigning.api.model.SigningRequest;
import se.sundsvall.esigning.service.SigningService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceFailureTest {

	@MockitoBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	private static Stream<Arguments> provideBadRequests() {
		final var municipalityId = "2281";
		return Stream.of(
			Arguments.of(municipalityId, createSigningRequest(request -> request.setDocument(null)), "document", "must not be null"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getDocument().setFileName(null)), "document.fileName", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getDocument().setRegistrationNumber(null)), "document.registrationNumber", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setLanguage("invalid-language")), "language", "The provided language is not valid. Valid values are [de-DE, nb-NO, ru-RU, zh-CN, fi-FI, uk-UA, en-US, sv-SE, da-DK, fr-FR]."),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setExpires(OffsetDateTime.now().minusDays(3))), "expires", "must be a future date"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setInitiator(null)), "initiator", "must not be null"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getInitiator().setEmail(null)), "initiator.email", "must not be null"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getInitiator().setEmail("Not-a-valid-email")), "initiator.email", "must be a well-formed email address"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getInitiator().setName(null)), "initiator.name", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getInitiator().setPartyId("Not-a-valid-UUID")), "initiator.partyId", "not a valid UUID"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getInitiator().setPartyId(null)), "initiator.partyId", "not a valid UUID"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setNotificationMessage(null)), "notificationMessage", "must not be null"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getNotificationMessage().setBody(null)), "notificationMessage.body", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getNotificationMessage().setSubject(null)), "notificationMessage.subject", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getReminder().setMessage(null)), "reminder.message", "must not be null"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getReminder().getMessage().setSubject(null)), "reminder.message.subject", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getReminder().getMessage().setBody(null)), "reminder.message.body", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getReminder().setIntervalInHours(0)), "reminder.intervalInHours", "must be greater than or equal to 1"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.getReminder().setStartDateTime(null)), "reminder.startDateTime", "must not be null"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setSignatories(null)), "signatories", "must not be empty"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setName(null))))), "signatories[].name", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setPartyId(null))))), "signatories[].partyId", "not a valid UUID"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setEmail(null))))), "signatories[].email", "must not be blank"),
			Arguments.of(municipalityId, createSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setEmail("not-a-valid-email"))))), "signatories[].email", "must be a well-formed email address"),
			Arguments.of("not valid", createSigningRequest(), "startSigningProcess.municipalityId", "not a valid municipality ID"));
	}

	@ParameterizedTest
	@MethodSource("provideBadRequests")
	void startSigningProcess_BadRequest(final String municipalityId, final SigningRequest signingRequest, final String field, final String message) {

		final var path = "/" + municipalityId + "/e-signing/start";

		final var response = webTestClient.post()
			.uri(path)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON_VALUE)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(tuple(field, message));
		});

		verifyNoInteractions(signingServiceMock);
	}
}
