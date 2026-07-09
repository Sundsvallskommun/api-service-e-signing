package se.sundsvall.esigning.integration.postportalservice;

/**
 * A signatory's action within a {@link SigningEvent}.
 *
 * @param partyId the party that acted (used by pps to resolve the recipient)
 * @param action  the normalized action: {@code APPROVED} or {@code DECLINED}
 * @param reason  the reason given for the action, when provided (typically on a decline)
 */
public record EventSignatory(String partyId, String action, String reason) {
}
