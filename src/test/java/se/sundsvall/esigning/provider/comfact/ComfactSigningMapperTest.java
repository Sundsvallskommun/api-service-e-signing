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
import se.sundsvall.esigning.provider.model.SigningStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;
import static se.sundsvall.esigning.provider.model.SigningStatus.FEL;
import static se.sundsvall.esigning.provider.model.SigningStatus.INITIERAT;
import static se.sundsvall.esigning.provider.model.SigningStatus.INVANTAR_SIGNERING;
import static se.sundsvall.esigning.provider.model.SigningStatus.SIGNERAT;
import static se.sundsvall.esigning.provider.model.SigningStatus.UTGANGET;

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
			Arguments.of("created", INITIERAT),
			Arguments.of("active", INVANTAR_SIGNERING),
			Arguments.of("reactivated", INVANTAR_SIGNERING),
			Arguments.of("approved", INVANTAR_SIGNERING),
			Arguments.of("Completed", SIGNERAT),
			Arguments.of("expired", UTGANGET),
			Arguments.of("withdrawn", FEL),
			Arguments.of("declined", FEL),
			Arguments.of("halted", FEL),
			Arguments.of("faulty", FEL));
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
		assertThat(ComfactSigningMapper.toSigningStatus(comfactCode)).isEqualTo(INVANTAR_SIGNERING);
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
		assertThat(info.status()).isEqualTo(SIGNERAT);
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
		assertThat(info.status()).isEqualTo(INVANTAR_SIGNERING);
		assertThat(info.signedDocument()).isNull();
	}

	@Test
	void toSigningInstanceInfo_dropsSignedDocumentWhenNotSigned() {
		final var instance = new SigningInstance()
			.signingId("comfact-123")
			.status(new Status().code("Active"))
			.signedDocument(new Document().fileName("signed.pdf").mimeType("application/pdf").content("test".getBytes(StandardCharsets.UTF_8)));

		final var info = ComfactSigningMapper.toSigningInstanceInfo(instance);

		assertThat(info.status()).isEqualTo(INVANTAR_SIGNERING);
		assertThat(info.signedDocument()).isNull();
	}
}
