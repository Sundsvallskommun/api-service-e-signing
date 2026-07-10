package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
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
@Schema(description = "Start signing request model")
public class StartSigningRequest {

	@OneOf(value = {
		"de-DE", "nb-NO", "ru-RU", "zh-CN", "fi-FI", "uk-UA", "en-US", "sv-SE", "da-DK", "fr-FR"
	}, message = "The provided language is not valid. Valid values are [de-DE, nb-NO, ru-RU, zh-CN, fi-FI, uk-UA, en-US, sv-SE, da-DK, fr-FR].", nullable = true)
	@Schema(description = "The language used for the signing instance. Swedish will be used if no language is provided", examples = "sv-SE", requiredMode = NOT_REQUIRED)
	private String language;

	@Schema(description = "The consumer's own reference for the case (e.g. Postportalen's MessageId). It is passed to the provider and echoed back verbatim in every event callback, so the consumer can correlate the case.",
		examples = "550e8400-e29b-41d4-a716-446655440000",
		requiredMode = NOT_REQUIRED)
	private String customerReference;

	@Future
	@DateTimeFormat(iso = DATE_TIME)
	@Schema(description = "Optional date and time when the signing request expires", examples = "2026-12-31T23:59:59Z", requiredMode = NOT_REQUIRED)
	private OffsetDateTime expires;

	@Valid
	@NotNull
	@Schema(description = "The primary document to sign", implementation = SigningDocument.class, requiredMode = REQUIRED)
	private SigningDocument document;

	@ArraySchema(schema = @Schema(implementation = SigningDocument.class), arraySchema = @Schema(description = "Optional additional documents (attachments) included in the signing alongside the primary document", requiredMode = NOT_REQUIRED))
	private List<@Valid SigningDocument> attachments;

	@Valid
	@NotNull
	@Schema(description = "The initiator of the signing request", implementation = Initiator.class, requiredMode = REQUIRED)
	private Initiator initiator;

	@Valid
	@NotNull
	@Schema(description = "The notification message", implementation = Message.class, requiredMode = REQUIRED)
	private Message notificationMessage;

	@Valid
	@Schema(description = "Reminder object", implementation = Reminder.class, requiredMode = NOT_REQUIRED)
	private Reminder reminder;

	@NotEmpty
	@ArraySchema(schema = @Schema(implementation = Signatory.class), minItems = 1, uniqueItems = true)
	private Set<@Valid Signatory> signatories;

}
