package se.sundsvall.esigning.provider.model;

import java.util.Map;

/**
 * Provider-neutral result of starting a signing process.
 *
 * @param providerCaseId the signing provider's own case/instance id
 * @param status         the normalized status of the signing case
 * @param signatoryUrls  direct signing urls per party id, when the provider supplies them
 */
public record SigningResult(String providerCaseId, SigningStatus status, Map<String, String> signatoryUrls) {
}
