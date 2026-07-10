package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Start signing response model")
public class StartSigningResponse {

	@Schema(description = "The signing provider's case id", examples = "1234567890")
	private String providerCaseId;

	@Schema(description = "The normalized status of the signing case", examples = "INITIATED", allowableValues = {
		"INITIATED", "PENDING", "SIGNED", "EXPIRED", "FAILED"
	})
	private String status;

	@Schema(description = "The id of the signing provider that handled the request", examples = "comfact")
	private String provider;

	@Schema(description = "Direct signing urls per party id, when supplied by the provider")
	private Map<String, String> signatoryUrls;

}
