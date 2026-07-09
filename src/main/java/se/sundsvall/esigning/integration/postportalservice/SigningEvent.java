package se.sundsvall.esigning.integration.postportalservice;

import java.time.OffsetDateTime;
import se.sundsvall.esigning.api.model.SigningDocument;

/**
 * Provider-neutral signing event posted to Postportalservice, one per provider event. pps correlates the message by
 * {@code customerReference} (the MessageId it supplied at create) and the recipient by {@code signatory.partyId}, which
 * keeps this service stateless.
 *
 * @param customerReference the consumer's own reference (Postportalen MessageId) echoed back by the provider
 * @param providerCaseId    the signing provider's case id
 * @param provider          the id of the signing provider that produced the event (e.g. "comfact")
 * @param eventType         the normalized event type ({@link se.sundsvall.esigning.provider.model.SigningEventType})
 * @param status            the normalized case status ({@link se.sundsvall.esigning.provider.model.SigningStatus})
 * @param signatory         the acting signatory, present on signatory events
 * @param signedDocument    the signed document, present only on a completed event
 * @param occurredAt        when the event occurred at the provider
 */
public record SigningEvent(
	String customerReference,
	String providerCaseId,
	String provider,
	String eventType,
	String status,
	EventSignatory signatory,
	SigningDocument signedDocument,
	OffsetDateTime occurredAt) {
}
