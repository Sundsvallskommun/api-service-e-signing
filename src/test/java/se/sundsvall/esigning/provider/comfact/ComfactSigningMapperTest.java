package se.sundsvall.esigning.provider.comfact;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;

class ComfactSigningMapperTest {

	@Test
	void toComfactSigningRequest() {
		final var request = createStartSigningRequest();
		final var signatory = request.getSignatories().iterator().next();

		final var result = ComfactSigningMapper.toComfactSigningRequest(request);

		assertThat(result.getLanguage()).isEqualTo("sv-SE");
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
}
