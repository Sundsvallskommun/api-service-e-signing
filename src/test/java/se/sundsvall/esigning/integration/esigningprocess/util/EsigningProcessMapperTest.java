package se.sundsvall.esigning.integration.esigningprocess.util;

import java.util.Set;
import org.junit.jupiter.api.Test;
import se.sundsvall.esigning.api.model.Signatory;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.esigning.TestUtil.createInitiator;
import static se.sundsvall.esigning.TestUtil.createMessage;
import static se.sundsvall.esigning.TestUtil.createReminder;
import static se.sundsvall.esigning.TestUtil.createSignatory;
import static se.sundsvall.esigning.TestUtil.createSigningRequest;

class EsigningProcessMapperTest {

	@Test
	void toSigningRequest() {
		final var signingRequest = createSigningRequest();
		final var signatory = signingRequest.getSignatories().stream().findFirst().get();

		final var result = EsigningProcessMapper.toSigningRequest(signingRequest);

		assertThat(result).isNotNull();
		assertThat(result.getCallbackUrl()).isEqualTo(signingRequest.getCallbackUrl());
		assertThat(result.getLanguage()).isEqualTo(signingRequest.getLanguage());
		assertThat(result.getExpires()).isEqualTo(signingRequest.getExpires());
		assertThat(result.getRegistrationNumber()).isEqualTo(signingRequest.getDocument().getRegistrationNumber());
		assertThat(result.getFileName()).isEqualTo(signingRequest.getDocument().getFileName());
		assertThat(result.getName()).isEqualTo(signingRequest.getDocument().getDescriptiveName());

		assertThat(result.getInitiator()).satisfies(initiator -> {
			assertThat(initiator.getName()).isEqualTo(signingRequest.getInitiator().getName());
			assertThat(initiator.getEmail()).isEqualTo(signingRequest.getInitiator().getEmail());
			assertThat(initiator.getPartyId()).isEqualTo(signingRequest.getInitiator().getPartyId());
			assertThat(initiator.getOrganization()).isEqualTo(signingRequest.getInitiator().getOrganization());
		});

		assertThat(result.getNotificationMessage()).satisfies(message -> {
			assertThat(message.getSubject()).isEqualTo(signingRequest.getNotificationMessage().getSubject());
			assertThat(message.getBody()).isEqualTo(signingRequest.getNotificationMessage().getBody());
		});

		assertThat(result.getReminder()).satisfies(reminder -> {
			assertThat(reminder.getIntervalInHours()).isEqualTo(signingRequest.getReminder().getIntervalInHours());
			assertThat(reminder.getStartDateTime()).isEqualTo(signingRequest.getReminder().getStartDateTime());
		});

		assertThat(result.getSignatories()).satisfies(signatories -> {
			final var mappedSignatory = signatories.stream().findFirst().get();
			assertThat(mappedSignatory.getEmail()).isEqualTo(signatory.getEmail());
			assertThat(mappedSignatory.getName()).isEqualTo(signatory.getName());
			assertThat(mappedSignatory.getOrganization()).isEqualTo(signatory.getOrganization());
			assertThat(mappedSignatory.getPartyId()).isEqualTo(signatory.getPartyId());
		});
	}

	@Test
	void toSigningRequestWithNull() {
		assertThat(EsigningProcessMapper.toSigningRequest(null)).isNull();
	}

	@Test
	void toSignatory() {
		final var signatory = createSignatory();

		final var result = EsigningProcessMapper.toSignatory(signatory);

		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(signatory.getEmail());
		assertThat(result.getName()).isEqualTo(signatory.getName());
		assertThat(result.getOrganization()).isEqualTo(signatory.getOrganization());
		assertThat(result.getPartyId()).isEqualTo(signatory.getPartyId());
		assertThat(result.getNotificationMessage()).satisfies(message -> {
			assertThat(message.getSubject()).isEqualTo(signatory.getNotificationMessage().getSubject());
			assertThat(message.getBody()).isEqualTo(signatory.getNotificationMessage().getBody());
		});
	}

	@Test
	void toSignatoryWithNull() {
		assertThat(EsigningProcessMapper.toSignatory(null)).isNull();
	}

	@Test
	void toSignatories() {
		final var signatories = Set.of(createSignatory(), createSignatory());
		final var result = EsigningProcessMapper.toSignatories(signatories);

		assertThat(result).isNotEmpty().hasSize(2);

		for (var signatory : result) {
			assertThat(signatory.getEmail()).isIn(signatories.stream().map(Signatory::getEmail).toArray());
			assertThat(signatory.getName()).isIn(signatories.stream().map(Signatory::getName).toArray());
			assertThat(signatory.getOrganization()).isIn(signatories.stream().map(Signatory::getOrganization).toArray());
			assertThat(signatory.getPartyId()).isIn(signatories.stream().map(Signatory::getPartyId).toArray());
		}
	}

	@Test
	void toSignatoriesWithNull() {
		assertThat(EsigningProcessMapper.toSignatories(null)).isEmpty();
	}

	@Test
	void toInitiator() {
		final var initiator = createInitiator();

		final var result = EsigningProcessMapper.toInitiator(initiator);

		assertThat(result).isNotNull().satisfies(i -> {
			assertThat(i.getEmail()).isEqualTo(initiator.getEmail());
			assertThat(i.getName()).isEqualTo(initiator.getName());
			assertThat(i.getOrganization()).isEqualTo(initiator.getOrganization());
			assertThat(i.getPartyId()).isEqualTo(initiator.getPartyId());
		});
	}

	@Test
	void toInitiatorWithNull() {
		assertThat(EsigningProcessMapper.toInitiator(null)).isNull();
	}

	@Test
	void toMessage() {
		final var message = createMessage();

		final var result = EsigningProcessMapper.toMessage(message);

		assertThat(result).isNotNull().satisfies(m -> {
			assertThat(m.getSubject()).isEqualTo(message.getSubject());
			assertThat(m.getBody()).isEqualTo(message.getBody());
		});
	}

	@Test
	void toMessageWithNull() {
		assertThat(EsigningProcessMapper.toMessage(null)).isNull();
	}

	@Test
	void toReminder() {
		final var reminder = createReminder();

		final var result = EsigningProcessMapper.toReminder(reminder);

		assertThat(result).isNotNull().satisfies(r -> {
			assertThat(r.getIntervalInHours()).isEqualTo(reminder.getIntervalInHours());
			assertThat(r.getStartDateTime()).isEqualTo(reminder.getStartDateTime());
			assertThat(r.getReminderMessage()).satisfies(m -> {
				assertThat(m.getSubject()).isEqualTo(reminder.getMessage().getSubject());
				assertThat(m.getBody()).isEqualTo(reminder.getMessage().getBody());
			});
		});
	}

	@Test
	void toReminderWithNull() {
		assertThat(EsigningProcessMapper.toReminder(null)).isNull();
	}

}
