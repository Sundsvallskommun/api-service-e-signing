package se.sundsvall.esigning.provider.model;

import java.time.OffsetDateTime;
import se.sundsvall.esigning.api.model.SigningDocument;

/**
 * Provider-neutral snapshot of a signing case.
 *
 * @param providerCaseId the signing provider's own case/instance id
 * @param status         the normalized status of the signing case
 * @param expires        when the signing case expires, if known
 * @param signedDocument the signed document, present only once the case is signed
 */
public record SigningInstanceInfo(String providerCaseId, SigningStatus status, OffsetDateTime expires, SigningDocument signedDocument) {
}
