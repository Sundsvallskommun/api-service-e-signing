package se.sundsvall.esigning.provider.model;

/**
 * Provider-neutral, normalized signing status. Provider-specific status vocabularies (e.g. Comfact's) are mapped to
 * this set so that consumers and future providers are decoupled from any single provider's terminology.
 */
public enum SigningStatus {
	INITIATED,
	PENDING,
	SIGNED,
	EXPIRED,
	FAILED
}
