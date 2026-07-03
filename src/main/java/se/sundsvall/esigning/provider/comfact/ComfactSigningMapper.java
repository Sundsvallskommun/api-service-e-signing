package se.sundsvall.esigning.provider.comfact;

import generated.se.sundsvall.comfactfacade.Document;
import generated.se.sundsvall.comfactfacade.Identification;
import generated.se.sundsvall.comfactfacade.NotificationMessage;
import generated.se.sundsvall.comfactfacade.Party;
import generated.se.sundsvall.comfactfacade.Reminder;
import generated.se.sundsvall.comfactfacade.Signatory;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import se.sundsvall.esigning.api.model.Initiator;
import se.sundsvall.esigning.api.model.Message;
import se.sundsvall.esigning.api.model.SigningDocument;
import se.sundsvall.esigning.api.model.StartSigningRequest;

/**
 * Maps the provider-neutral API request to Comfact facade's generated request model.
 */
public final class ComfactSigningMapper {

	static final String DEFAULT_LANGUAGE = "sv-SE";
	static final String IDENTIFICATION_ALIAS = "SvensktEId"; // Swedish e-identification (BankID)

	private ComfactSigningMapper() {}

	public static SigningRequest toComfactSigningRequest(final StartSigningRequest request) {
		final var language = Optional.ofNullable(request.getLanguage()).orElse(DEFAULT_LANGUAGE);

		return new SigningRequest()
			.language(language)
			.expires(request.getExpires())
			.document(toDocument(request.getDocument()))
			.initiator(toInitiator(request.getInitiator()))
			.notificationMessage(toNotificationMessage(request.getNotificationMessage(), language))
			.reminder(toReminder(request.getReminder(), language))
			.signatories(toSignatories(request, language));
	}

	static Document toDocument(final SigningDocument document) {
		return new Document()
			.name(document.getName())
			.fileName(document.getFileName())
			.mimeType(document.getMimeType())
			.content(Base64.getDecoder().decode(document.getContent()));
	}

	static Party toInitiator(final Initiator initiator) {
		return new Party()
			.name(initiator.getName())
			.partyId(initiator.getPartyId())
			.organization(initiator.getOrganization())
			.email(initiator.getEmail());
	}

	static List<Signatory> toSignatories(final StartSigningRequest request, final String language) {
		return request.getSignatories().stream()
			.map(signatory -> toSignatory(signatory, language))
			.toList();
	}

	static Signatory toSignatory(final se.sundsvall.esigning.api.model.Signatory signatory, final String language) {
		return new Signatory()
			.name(signatory.getName())
			.partyId(signatory.getPartyId())
			.organization(signatory.getOrganization())
			.email(signatory.getEmail())
			.notificationMessage(toNotificationMessage(signatory.getNotificationMessage(), language))
			.identifications(List.of(new Identification().alias(IDENTIFICATION_ALIAS)));
	}

	static NotificationMessage toNotificationMessage(final Message message, final String language) {
		return Optional.ofNullable(message)
			.map(m -> new NotificationMessage()
				.subject(m.getSubject())
				.body(m.getBody())
				.language(language))
			.orElse(null);
	}

	static Reminder toReminder(final se.sundsvall.esigning.api.model.Reminder reminder, final String language) {
		return Optional.ofNullable(reminder)
			.map(r -> new Reminder()
				.enabled(true)
				.intervalInHours(r.getIntervalInHours())
				.startDateTime(r.getStartDateTime())
				.message(toNotificationMessage(r.getMessage(), language)))
			.orElse(null);
	}
}
