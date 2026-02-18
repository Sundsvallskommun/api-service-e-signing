package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Signing request model")
public class SigningRequest {

	@OneOf(value = {
		"de-DE", "nb-NO", "ru-RU", "zh-CN", "fi-FI", "uk-UA", "en-US", "sv-SE", "da-DK", "fr-FR"
	}, message = "The provided language is not valid. Valid values are [de-DE, nb-NO, ru-RU, zh-CN, fi-FI, uk-UA, en-US, sv-SE, da-DK, fr-FR].", nullable = true)
	@Schema(description = "The language used for the signing instance. Valid values are one of [en-US, sv-SE, da-DK, fr-FR, de-DE, nb-NO, ru-RU, zh-CN, fi-FI, uk-UA]. Swedish will be used If no language is provided",
		examples = "sv-SE",
		requiredMode = NOT_REQUIRED)
	private String language;

	@Schema(description = "Optional callback url", examples = "https://example.com/callback", requiredMode = NOT_REQUIRED)
	private String callbackUrl;

	@Future
	@DateTimeFormat(iso = DATE_TIME)
	@Schema(description = "Optional date and time when the signing request expires. If no exipre date is provided, expiretime will be set to 30 days from time when request was received.", examples = "2021-12-31T23:59:59Z")
	private OffsetDateTime expires;

	@Valid
	@NotNull
	@Schema(description = "The document to sign", implementation = Document.class, requiredMode = REQUIRED)
	private Document document;

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
