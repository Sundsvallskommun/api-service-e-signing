package se.sundsvall.esigning.provider;

import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.provider.model.SigningResult;

/**
 * Abstraction over an e-signing provider. A new provider is added by implementing this interface and registering it as
 * a Spring component - no existing logic needs to change. The active provider per municipality is resolved by
 * {@link SigningProviderRegistry}.
 */
public interface SigningProvider {

	/**
	 * @return the unique id of this provider (e.g. "comfact"), used for configuration-driven selection.
	 */
	String getId();

	/**
	 * Starts a signing process at the provider.
	 *
	 * @param  municipalityId the municipality the signing belongs to
	 * @param  request        the signing request
	 * @return                the provider-neutral result including the provider's case id and the normalized status
	 */
	SigningResult startSigning(String municipalityId, StartSigningRequest request);
}
