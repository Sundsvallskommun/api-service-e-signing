package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Signing instance response model")
public class SigningInstanceResponse {

	@Schema(description = "The signing provider's case id", examples = "1234567890")
	private String providerCaseId;

	@Schema(description = "The normalized status of the signing case", examples = "INVANTAR_SIGNERING", allowableValues = {
		"INITIERAT", "INVANTAR_SIGNERING", "SIGNERAT", "UTGANGET", "FEL"
	})
	private String status;

	@Schema(description = "The id of the signing provider that handled the request", examples = "comfact")
	private String provider;

	@Schema(description = "When the signing case expires", examples = "2026-12-31T23:59:59Z")
	private OffsetDateTime expires;

	@Schema(description = "The signed document, present only when status is SIGNERAT", implementation = SigningDocument.class)
	private SigningDocument signedDocument;

}
