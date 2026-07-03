package se.sundsvall.esigning.integration.postportalservice;

import se.sundsvall.esigning.api.model.SigningDocument;

/**
 * Payload posted to Postportalservice when a signing case reaches a new state. Postportalservice correlates the case by
 * {@code providerCaseId} (which it stored when creating the case), keeping this service stateless.
 *
 * @param providerCaseId the signing provider's case id
 * @param status         the normalized status of the signing case
 * @param signedDocument the signed document, present only when the case is signed
 */
public record SigningCallbackRequest(String providerCaseId, String status, SigningDocument signedDocument) {
}
