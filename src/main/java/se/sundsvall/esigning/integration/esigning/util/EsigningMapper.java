package se.sundsvall.esigning.integration.esigning.util;


import static java.util.Collections.emptySet;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import se.sundsvall.esigning.api.model.Initiator;
import se.sundsvall.esigning.api.model.Message;
import se.sundsvall.esigning.api.model.Reminder;
import se.sundsvall.esigning.api.model.Signatory;
import se.sundsvall.esigning.api.model.SigningRequest;

public final class EsigningMapper {

	private EsigningMapper() {
	}

	public static generated.se.sundsvall.pw_e_signing.SigningRequest toSigningRequest(final SigningRequest signingRequest) {
		if (signingRequest == null) {
			return null;
		}

		final var newRequest = new generated.se.sundsvall.pw_e_signing.SigningRequest();

		Optional.ofNullable(signingRequest.getLanguage()).ifPresent(newRequest::setLanguage);
		Optional.ofNullable(signingRequest.getCallbackUrl()).ifPresent(newRequest::setCallbackUrl);
		Optional.ofNullable(signingRequest.getExpires()).ifPresent(newRequest::setExpires);

		Optional.ofNullable(signingRequest.getDocument()).ifPresent(document -> {
			newRequest.setName(document.getDescriptiveName());
			newRequest.setFileName(document.getFileName());
			newRequest.setRegistrationNumber(document.getRegistrationNumber());
		});
		Optional.ofNullable(signingRequest.getInitiator()).ifPresent(initiator -> newRequest.setInitiator(toInitiator(initiator)));
		Optional.ofNullable(signingRequest.getNotificationMessage()).ifPresent(message -> newRequest.setNotificationMessage(toMessage(message)));
		Optional.ofNullable(signingRequest.getReminder()).ifPresent(reminder -> newRequest.setReminder(toReminder(reminder)));
		Optional.ofNullable(signingRequest.getSignatories()).ifPresent(signatories -> newRequest.setSignatories(toSignatories(signatories)));

		return newRequest;
	}

	public static generated.se.sundsvall.pw_e_signing.Signatory toSignatory(final Signatory signatory) {
		if (signatory == null) {
			return null;
		}
		final var newSignatory = new generated.se.sundsvall.pw_e_signing.Signatory();

		Optional.ofNullable(signatory.getOrganization()).ifPresent(newSignatory::setOrganization);
		Optional.ofNullable(signatory.getPartyId()).ifPresent(newSignatory::setPartyId);
		Optional.ofNullable(signatory.getName()).ifPresent(newSignatory::setName);
		Optional.ofNullable(signatory.getEmail()).ifPresent(newSignatory::setEmail);
		Optional.ofNullable(signatory.getNotificationMessage()).ifPresent(message -> newSignatory.setNotificationMessage(toMessage(message)));

		return newSignatory;

	}

	public static Set<generated.se.sundsvall.pw_e_signing.Signatory> toSignatories(final Set<Signatory> signatories) {
		if (signatories == null) {
			return emptySet();
		}

		return signatories.stream().map(EsigningMapper::toSignatory).collect(Collectors.toSet());
	}


	public static generated.se.sundsvall.pw_e_signing.Initiator toInitiator(final Initiator initiator) {
		if (initiator == null) {
			return null;
		}
		final var newInitiator = new generated.se.sundsvall.pw_e_signing.Initiator();

		Optional.ofNullable(initiator.getOrganization()).ifPresent(newInitiator::setOrganization);
		Optional.ofNullable(initiator.getPartyId()).ifPresent(newInitiator::setPartyId);
		Optional.ofNullable(initiator.getName()).ifPresent(newInitiator::setName);
		Optional.ofNullable(initiator.getEmail()).ifPresent(newInitiator::setEmail);

		return newInitiator;
	}

	public static generated.se.sundsvall.pw_e_signing.Message toMessage(final Message message) {
		if (message == null) {
			return null;
		}
		final var newMessage = new generated.se.sundsvall.pw_e_signing.Message();

		Optional.ofNullable(message.getSubject()).ifPresent(newMessage::setSubject);
		Optional.ofNullable(message.getBody()).ifPresent(newMessage::setBody);

		return newMessage;
	}

	public static generated.se.sundsvall.pw_e_signing.Reminder toReminder(final Reminder reminder) {
		if (reminder == null) {
			return null;
		}

		final var newReminder = new generated.se.sundsvall.pw_e_signing.Reminder();

		Optional.of(reminder.getIntervalInHours()).ifPresent(newReminder::setIntervalInHours);
		Optional.ofNullable(reminder.getStartDateTime()).ifPresent(newReminder::setStartDateTime);
		Optional.ofNullable(reminder.getMessage()).ifPresent(message -> newReminder.setReminderMessage(toMessage(message)));

		return newReminder;
	}

}
