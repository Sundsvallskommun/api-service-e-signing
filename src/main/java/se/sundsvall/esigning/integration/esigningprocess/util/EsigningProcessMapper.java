package se.sundsvall.esigning.integration.esigningprocess.util;


import static java.util.Collections.emptySet;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import se.sundsvall.esigning.api.model.Initiator;
import se.sundsvall.esigning.api.model.Message;
import se.sundsvall.esigning.api.model.Reminder;
import se.sundsvall.esigning.api.model.Signatory;
import se.sundsvall.esigning.api.model.SigningRequest;

public final class EsigningProcessMapper {

	private EsigningProcessMapper() {
	}

	public static generated.se.sundsvall.pw_e_signing.SigningRequest toSigningRequest(final SigningRequest signingRequest) {
		if (signingRequest == null) {
			return null;
		}
		final var newRequest = new generated.se.sundsvall.pw_e_signing.SigningRequest();

		newRequest.setLanguage(signingRequest.getLanguage());
		newRequest.setCallbackUrl(signingRequest.getCallbackUrl());
		newRequest.setExpires(signingRequest.getExpires());
		newRequest.setInitiator(toInitiator(signingRequest.getInitiator()));
		newRequest.setNotificationMessage(toMessage(signingRequest.getNotificationMessage()));
		newRequest.setReminder(toReminder(signingRequest.getReminder()));
		newRequest.setSignatories(toSignatories(signingRequest.getSignatories()));

		Optional.ofNullable(signingRequest.getDocument()).ifPresent(document -> {
			newRequest.setName(document.getDescriptiveName());
			newRequest.setFileName(document.getFileName());
			newRequest.setRegistrationNumber(document.getRegistrationNumber());
		});
		return newRequest;
	}

	public static generated.se.sundsvall.pw_e_signing.Signatory toSignatory(final Signatory signatory) {
		if (signatory == null) {
			return null;
		}
		final var newSignatory = new generated.se.sundsvall.pw_e_signing.Signatory();

		newSignatory.setOrganization(signatory.getOrganization());
		newSignatory.setPartyId(signatory.getPartyId());
		newSignatory.setName(signatory.getName());
		newSignatory.setEmail(signatory.getEmail());
		newSignatory.setNotificationMessage(toMessage(signatory.getNotificationMessage()));
		return newSignatory;
	}

	public static Set<generated.se.sundsvall.pw_e_signing.Signatory> toSignatories(final Set<Signatory> signatories) {
		return Optional.ofNullable(signatories).orElse(emptySet()).stream()
			.map(EsigningProcessMapper::toSignatory)
			.collect(Collectors.toSet());
	}


	public static generated.se.sundsvall.pw_e_signing.Initiator toInitiator(final Initiator initiator) {
		if (initiator == null) {
			return null;
		}
		final var newInitiator = new generated.se.sundsvall.pw_e_signing.Initiator();

		newInitiator.setOrganization(initiator.getOrganization());
		newInitiator.setPartyId(initiator.getPartyId());
		newInitiator.setName(initiator.getName());
		newInitiator.setEmail(initiator.getEmail());
		return newInitiator;
	}

	public static generated.se.sundsvall.pw_e_signing.Message toMessage(final Message message) {
		if (message == null) {
			return null;
		}
		final var newMessage = new generated.se.sundsvall.pw_e_signing.Message();

		newMessage.setBody(message.getBody());
		newMessage.setSubject(message.getSubject());
		return newMessage;
	}

	public static generated.se.sundsvall.pw_e_signing.Reminder toReminder(final Reminder reminder) {
		if (reminder == null) {
			return null;
		}
		final var newReminder = new generated.se.sundsvall.pw_e_signing.Reminder();

		newReminder.setIntervalInHours(reminder.getIntervalInHours());
		newReminder.setStartDateTime(reminder.getStartDateTime());
		newReminder.setReminderMessage(toMessage(reminder.getMessage()));
		return newReminder;
	}

}
