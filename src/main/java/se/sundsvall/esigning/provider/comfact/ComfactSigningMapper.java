package se.sundsvall.esigning.provider.comfact;

import generated.se.sundsvall.comfactfacade.Document;
import generated.se.sundsvall.comfactfacade.Identification;
import generated.se.sundsvall.comfactfacade.NotificationMessage;
import generated.se.sundsvall.comfactfacade.Party;
import generated.se.sundsvall.comfactfacade.Reminder;
import generated.se.sundsvall.comfactfacade.Signatory;
import generated.se.sundsvall.comfactfacade.SigningInstance;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import generated.se.sundsvall.comfactfacade.Status;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import se.sundsvall.esigning.api.model.Initiator;
import se.sundsvall.esigning.api.model.Message;
import se.sundsvall.esigning.api.model.SigningDocument;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.provider.model.SigningInstanceInfo;
import se.sundsvall.esigning.provider.model.SigningStatus;

import static se.sundsvall.esigning.provider.model.SigningStatus.FEL;
import static se.sundsvall.esigning.provider.model.SigningStatus.INITIERAT;
import static se.sundsvall.esigning.provider.model.SigningStatus.INVANTAR_SIGNERING;
import static se.sundsvall.esigning.provider.model.SigningStatus.SIGNERAT;
import static se.sundsvall.esigning.provider.model.SigningStatus.UTGANGET;

/**
 * Maps between the provider-neutral API/domain models and Comfact facade's generated models.
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

	public static SigningInstanceInfo toSigningInstanceInfo(final SigningInstance instance) {
		final var statusCode = Optional.ofNullable(instance.getStatus()).map(Status::getCode).orElse(null);
		final var status = toSigningStatus(statusCode);

		// The signed document is only exposed once the case is signed (see SigningInstanceInfo / SigningInstanceResponse).
		final var signedDocument = Optional.of(status)
			.filter(SIGNERAT::equals)
			.map(_ -> toSignedDocument(instance.getSignedDocument()))
			.orElse(null);

		return new SigningInstanceInfo(
			instance.getSigningId(),
			status,
			instance.getExpires(),
			signedDocument);
	}

	static SigningDocument toSignedDocument(final Document document) {
		return Optional.ofNullable(document)
			.map(d -> SigningDocument.builder()
				.withName(d.getName())
				.withFileName(d.getFileName())
				.withMimeType(d.getMimeType())
				.withContent(Optional.ofNullable(d.getContent()).map(Base64.getEncoder()::encodeToString).orElse(null))
				.build())
			.orElse(null);
	}

	/**
	 * Normalizes a Comfact status code to the domain status vocabulary. Matching is case-insensitive.
	 */
	static SigningStatus toSigningStatus(final String comfactStatusCode) {
		return switch (Optional.ofNullable(comfactStatusCode).map(code -> code.toLowerCase(Locale.ROOT)).orElse("")) {
			case "created" -> INITIERAT;
			case "active", "reactivated", "approved" -> INVANTAR_SIGNERING;
			case "completed" -> SIGNERAT;
			case "expired" -> UTGANGET;
			case "withdrawn", "declined", "halted", "faulty" -> FEL;
			// Unknown/unset: do not finalize the case - the authoritative terminal status arrives via a known code.
			default -> INVANTAR_SIGNERING;
		};
	}
}
