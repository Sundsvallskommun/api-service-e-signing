package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "A signing event forwarded from Comfact (via api-comfact-facade)")
public class ComfactEventNotification {

	@OneOf(value = {
		"signingInstanceCreated", "signatoryActionApproved", "signatoryActionDeclined", "signingInstanceCompleted",
		"signingInstanceWithdrawn", "signingInstanceExpired", "signingInstanceHalted", "signingInstanceReactivated"
	}, message = "The provided event trigger is not a known Comfact event.")
	@Schema(description = "The Comfact event trigger", examples = "signingInstanceCompleted", requiredMode = REQUIRED)
	private String eventTrigger;

	@NotBlank
	@Schema(description = "The Comfact signing instance id", examples = "1234567890", requiredMode = REQUIRED)
	private String signingInstanceId;

	@Schema(description = "The customer reference (Postportalen MessageId) supplied at create and echoed back by Comfact", examples = "550e8400-e29b-41d4-a716-446655440000", requiredMode = NOT_REQUIRED)
	private String customerReference;

	@Schema(description = "The Comfact status code of the signing instance", examples = "completed", requiredMode = NOT_REQUIRED)
	private String statusCode;

	@DateTimeFormat(iso = DATE_TIME)
	@Schema(description = "When the signing instance expires", examples = "2026-12-31T23:59:59Z", requiredMode = NOT_REQUIRED)
	private OffsetDateTime expires;

	@Valid
	@Schema(description = "The acting signatory, present on signatory events", implementation = ComfactSignatory.class, requiredMode = NOT_REQUIRED)
	private ComfactSignatory signatory;

	@Valid
	@Schema(description = "The signed document, present on a completed event", implementation = SigningDocument.class, requiredMode = NOT_REQUIRED)
	private SigningDocument signedDocument;

	@DateTimeFormat(iso = DATE_TIME)
	@Schema(description = "When the event occurred", examples = "2026-12-31T23:59:59Z", requiredMode = NOT_REQUIRED)
	private OffsetDateTime timestamp;

}
