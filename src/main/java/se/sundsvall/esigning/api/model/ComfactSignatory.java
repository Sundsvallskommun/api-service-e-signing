package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "The signatory referenced by a Comfact event")
public class ComfactSignatory {

	@Schema(description = "The party id of the signatory", examples = "550e8400-e29b-41d4-a716-446655440000", requiredMode = NOT_REQUIRED)
	private String partyId;

	@Schema(description = "The action taken by the signatory", examples = "approved", allowableValues = {
		"approved", "declined"
	}, requiredMode = NOT_REQUIRED)
	private String action;

	@Schema(description = "The reason given for the action, when provided", examples = "Not authorised to sign", requiredMode = NOT_REQUIRED)
	private String reason;

}
