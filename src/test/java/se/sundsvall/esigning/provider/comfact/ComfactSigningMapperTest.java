package se.sundsvall.esigning.provider.comfact;

import generated.se.sundsvall.comfactfacade.Document;
import generated.se.sundsvall.comfactfacade.SigningInstance;
import generated.se.sundsvall.comfactfacade.Status;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.esigning.provider.model.SigningEventType;
import se.sundsvall.esigning.provider.model.SigningStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static se.sundsvall.esigning.TestUtil.createComfactEventNotification;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;
import static se.sundsvall.esigning.provider.model.SigningStatus.EXPIRED;
import static se.sundsvall.esigning.provider.model.SigningStatus.FAILED;
import static se.sundsvall.esigning.provider.model.SigningStatus.INITIATED;
import static se.sundsvall.esigning.provider.model.SigningStatus.PENDING;
import static se.sundsvall.esigning.provider.model.SigningStatus.SIGNED;

class ComfactSigningMapperTest {

	@Test
	void toComfactSigningRequest() {
		final var request = createStartSigningRequest();
		final var signatory = request.getSignatories().iterator().next();

		final var result = ComfactSigningMapper.toComfactSigningRequest(request);

		assertThat(result.getLanguage()).isEqualTo("sv-SE");
		assertThat(result.getCustomerReference()).isEqualTo(request.getCustomerReference());
		assertThat(result.getExpires()).isEqualTo(request.getExpires());

		assertThat(result.getNotificationMessage()).isNotNull();
		assertThat(result.getNotificationMessage().getSubject()).isEqualTo(request.getNotificationMessage().getSubject());
		assertThat(result.getNotificationMessage().getBody()).isEqualTo(request.getNotificationMessage().getBody());
		assertThat(result.getNotificationMessage().getLanguage()).isEqualTo("sv-SE");

		assertThat(result.getInitiator()).isNotNull();
		assertThat(result.getInitiator().getName()).isEqualTo(request.getInitiator().getName());
		assertThat(result.getInitiator().getPartyId()).isEqualTo(request.getInitiator().getPartyId());
		assertThat(result.getInitiator().getEmail()).isEqualTo(request.getInitiator().getEmail());

		assertThat(result.getReminder()).isNotNull();
		assertThat(result.getReminder().getEnabled()).isTrue();
		assertThat(result.getReminder().getIntervalInHours()).isEqualTo(request.getReminder().getIntervalInHours());
		assertThat(result.getReminder().getStartDateTime()).isEqualTo(request.getReminder().getStartDateTime());

		assertThat(result.getDocument()).isNotNull();
		assertThat(result.getDocument().getName()).isEqualTo(request.getDocument().getName());
		assertThat(result.getDocument().getFileName()).isEqualTo("test.pdf");
		assertThat(result.getDocument().getMimeType()).isEqualTo("application/pdf");
		assertThat(result.getDocument().getContent()).isEqualTo("test".getBytes(StandardCharsets.UTF_8));

		assertThat(result.getSignatories()).hasSize(1);
		final var mappedSignatory = result.getSignatories().getFirst();
		assertThat(mappedSignatory.getName()).isEqualTo(signatory.getName());
		assertThat(mappedSignatory.getPartyId()).isEqualTo(signatory.getPartyId());
		assertThat(mappedSignatory.getEmail()).isEqualTo(signatory.getEmail());
		assertThat(mappedSignatory.getIdentifications()).hasSize(1);
		assertThat(mappedSignatory.getIdentifications().getFirst().getAlias()).isEqualTo(ComfactSigningMapper.IDENTIFICATION_ALIAS);
	}

	@Test
	void toComfactSigningRequest_defaultsLanguageAndOmitsNullReminder() {
		final var request = createStartSigningRequest(r -> {
			r.setLanguage(null);
			r.setReminder(null);
		});

		final var result = ComfactSigningMapper.toComfactSigningRequest(request);

		assertThat(result.getLanguage()).isEqualTo(ComfactSigningMapper.DEFAULT_LANGUAGE);
		assertThat(result.getNotificationMessage().getLanguage()).isEqualTo(ComfactSigningMapper.DEFAULT_LANGUAGE);
		assertThat(result.getReminder()).isNull();
	}

	private static Stream<Arguments> statusMappings() {
		return Stream.of(
			Arguments.of("created", INITIATED),
			Arguments.of("active", PENDING),
			Arguments.of("reactivated", PENDING),
			Arguments.of("approved", PENDING),
			Arguments.of("Completed", SIGNED),
			Arguments.of("expired", EXPIRED),
			Arguments.of("withdrawn", FAILED),
			Arguments.of("declined", FAILED),
			Arguments.of("halted", FAILED),
			Arguments.of("faulty", FAILED));
	}

	@ParameterizedTest
	@MethodSource("statusMappings")
	void toSigningStatus(final String comfactCode, final SigningStatus expected) {
		assertThat(ComfactSigningMapper.toSigningStatus(comfactCode)).isEqualTo(expected);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"", "something-unexpected"
	})
	void toSigningStatus_unknownOrNullIsNotFinalized(final String comfactCode) {
		assertThat(ComfactSigningMapper.toSigningStatus(comfactCode)).isEqualTo(PENDING);
	}

	@Test
	void toSigningInstanceInfo() {
		final var expires = OffsetDateTime.now().plusDays(10);
		final var instance = new SigningInstance()
			.signingId("comfact-123")
			.status(new Status().code("Completed"))
			.expires(expires)
			.signedDocument(new Document().name("Contract").fileName("signed.pdf").mimeType("application/pdf").content("test".getBytes(StandardCharsets.UTF_8)));

		final var info = ComfactSigningMapper.toSigningInstanceInfo(instance);

		assertThat(info.providerCaseId()).isEqualTo("comfact-123");
		assertThat(info.status()).isEqualTo(SIGNED);
		assertThat(info.expires()).isEqualTo(expires);
		assertThat(info.signedDocument()).isNotNull();
		assertThat(info.signedDocument().getName()).isEqualTo("Contract");
		assertThat(info.signedDocument().getFileName()).isEqualTo("signed.pdf");
		assertThat(info.signedDocument().getMimeType()).isEqualTo("application/pdf");
		assertThat(info.signedDocument().getContent()).isEqualTo("dGVzdA==");
	}

	@Test
	void toSigningInstanceInfo_noSignedDocumentAndUnknownStatus() {
		final var instance = new SigningInstance().signingId("comfact-123");

		final var info = ComfactSigningMapper.toSigningInstanceInfo(instance);

		assertThat(info.providerCaseId()).isEqualTo("comfact-123");
		assertThat(info.status()).isEqualTo(PENDING);
		assertThat(info.signedDocument()).isNull();
	}

	@Test
	void toSigningInstanceInfo_dropsSignedDocumentWhenNotSigned() {
		final var instance = new SigningInstance()
			.signingId("comfact-123")
			.status(new Status().code("Active"))
			.signedDocument(new Document().fileName("signed.pdf").mimeType("application/pdf").content("test".getBytes(StandardCharsets.UTF_8)));

		final var info = ComfactSigningMapper.toSigningInstanceInfo(instance);

		assertThat(info.status()).isEqualTo(PENDING);
		assertThat(info.signedDocument()).isNull();
	}

	@Test
	void toSigningEvent_completed() {
		final var notification = createComfactEventNotification();

		final var event = ComfactSigningMapper.toSigningEvent(notification);

		assertThat(event.customerReference()).isEqualTo(notification.getCustomerReference());
		assertThat(event.providerCaseId()).isEqualTo(notification.getSigningInstanceId());
		assertThat(event.provider()).isEqualTo("comfact");
		assertThat(event.eventType()).isEqualTo("CASE_COMPLETED");
		assertThat(event.status()).isEqualTo("SIGNED");
		assertThat(event.occurredAt()).isEqualTo(notification.getTimestamp());
		assertThat(event.signedDocument()).isNotNull();
		assertThat(event.signedDocument().getFileName()).isEqualTo("test.pdf");
	}

	@Test
	void toSigningEvent_signatoryApprovedMapsSignatoryAndDropsDocument() {
		final var notification = createComfactEventNotification(n -> {
			n.setEventTrigger("signatoryActionApproved");
			n.setStatusCode("active");
			n.getSignatory().setAction("approved");
		});

		final var event = ComfactSigningMapper.toSigningEvent(notification);

		assertThat(event.eventType()).isEqualTo("SIGNATORY_APPROVED");
		assertThat(event.status()).isEqualTo("PENDING");
		assertThat(event.signedDocument()).isNull();
		assertThat(event.signatory()).isNotNull();
		assertThat(event.signatory().partyId()).isEqualTo(notification.getSignatory().getPartyId());
		assertThat(event.signatory().action()).isEqualTo("APPROVED");
		assertThat(event.signatory().reason()).isEqualTo("reason");
	}

	@Test
	void toSigningEvent_declinedMapsAction() {
		final var notification = createComfactEventNotification(n -> {
			n.setEventTrigger("signatoryActionDeclined");
			n.getSignatory().setAction("declined");
		});

		final var event = ComfactSigningMapper.toSigningEvent(notification);

		assertThat(event.eventType()).isEqualTo("SIGNATORY_DECLINED");
		assertThat(event.signatory().action()).isEqualTo("DECLINED");
	}

	@Test
	void toSigningEvent_noSignatory() {
		final var notification = createComfactEventNotification(n -> n.setSignatory(null));

		final var event = ComfactSigningMapper.toSigningEvent(notification);

		assertThat(event.signatory()).isNull();
	}

	private static Stream<Arguments> eventTypeMappings() {
		return Stream.of(
			Arguments.of("signingInstanceCreated", SigningEventType.CASE_CREATED),
			Arguments.of("signatoryActionApproved", SigningEventType.SIGNATORY_APPROVED),
			Arguments.of("signatoryActionDeclined", SigningEventType.SIGNATORY_DECLINED),
			Arguments.of("signingInstanceCompleted", SigningEventType.CASE_COMPLETED),
			Arguments.of("signingInstanceWithdrawn", SigningEventType.CASE_WITHDRAWN),
			Arguments.of("signingInstanceExpired", SigningEventType.CASE_EXPIRED),
			Arguments.of("signingInstanceHalted", SigningEventType.CASE_HALTED),
			Arguments.of("signingInstanceReactivated", SigningEventType.CASE_REACTIVATED));
	}

	@ParameterizedTest
	@MethodSource("eventTypeMappings")
	void toEventType(final String comfactTrigger, final SigningEventType expected) {
		assertThat(ComfactSigningMapper.toEventType(comfactTrigger)).isEqualTo(expected);
	}

	@Test
	void toEventType_unknownThrows() {
		assertThatThrownBy(() -> ComfactSigningMapper.toEventType("notAnEvent"))
			.isInstanceOf(Problem.class)
			.hasMessage("Bad Request: Unsupported Comfact event trigger")
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}

	@Test
	void toSignatoryAction_nullReturnsNull() {
		assertThat(ComfactSigningMapper.toSignatoryAction(null)).isNull();
	}
}
