package se.sundsvall.esigning.provider.model;

/**
 * Provider-neutral, normalized signing event type. Provider-specific event vocabularies (e.g. Comfact's event triggers)
 * are mapped to this set so consumers and future providers are decoupled from any single provider's terminology.
 */
public enum SigningEventType {
	CASE_CREATED,
	SIGNATORY_APPROVED,
	SIGNATORY_DECLINED,
	CASE_COMPLETED,
	CASE_WITHDRAWN,
	CASE_EXPIRED,
	CASE_HALTED,
	CASE_REACTIVATED
}
