package se.sundsvall.esigning.api;

import java.time.OffsetDateTime;
import java.util.Set;
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
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.service.SigningGatewayService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.esigning.TestUtil.createSignatory;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class SigningGatewayResourceFailureTest {

	@MockitoBean
	private SigningGatewayService signingGatewayServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	private static Stream<Arguments> provideBadRequests() {
		final var municipalityId = "2281";
		return Stream.of(
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setDocument(null)), "document", "must not be null"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getDocument().setFileName(null)), "document.fileName", "must not be blank"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getDocument().setMimeType("text/plain")), "document.mimeType", "The provided mime type is not valid. Only application/pdf is supported."),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getDocument().setContent("@@@@")), "document.content", "not a valid BASE64-encoded string"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setLanguage("invalid-language")), "language",
				"The provided language is not valid. Valid values are [de-DE, nb-NO, ru-RU, zh-CN, fi-FI, uk-UA, en-US, sv-SE, da-DK, fr-FR]."),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setExpires(OffsetDateTime.now().minusDays(3))), "expires", "must be a future date"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setInitiator(null)), "initiator", "must not be null"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getInitiator().setEmail(null)), "initiator.email", "must not be null"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getInitiator().setEmail("Not-a-valid-email")), "initiator.email", "must be a well-formed email address"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getInitiator().setName(null)), "initiator.name", "must not be blank"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getInitiator().setPartyId("Not-a-valid-UUID")), "initiator.partyId", "not a valid UUID"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setNotificationMessage(null)), "notificationMessage", "must not be null"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getNotificationMessage().setBody(null)), "notificationMessage.body", "must not be blank"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getNotificationMessage().setSubject(null)), "notificationMessage.subject", "must not be blank"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getReminder().setMessage(null)), "reminder.message", "must not be null"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getReminder().setIntervalInHours(0)), "reminder.intervalInHours", "must be greater than or equal to 1"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.getReminder().setStartDateTime(null)), "reminder.startDateTime", "must not be null"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setSignatories(null)), "signatories", "must not be empty"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setName(null))))), "signatories[].name", "must not be blank"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setPartyId(null))))), "signatories[].partyId", "not a valid UUID"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setEmail(null))))), "signatories[].email", "must not be blank"),
			Arguments.of(municipalityId, createStartSigningRequest(request -> request.setSignatories(Set.of(createSignatory(signatory -> signatory.setEmail("not-a-valid-email"))))), "signatories[].email", "must be a well-formed email address"),
			Arguments.of("not valid", createStartSigningRequest(), "createSigning.municipalityId", "not a valid municipality ID"));
	}

	@ParameterizedTest
	@MethodSource("provideBadRequests")
	void createSigning_BadRequest(final String municipalityId, final StartSigningRequest request, final String field, final String message) {

		final var path = "/" + municipalityId + "/e-signing/signings";

		final var response = webTestClient.post()
			.uri(path)
			.bodyValue(request)
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
