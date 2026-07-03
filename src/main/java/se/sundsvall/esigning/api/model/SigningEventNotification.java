package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

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
@Schema(description = "Notification that a signing case changed state at the signing provider")
public class SigningEventNotification {

	@NotBlank
	@Schema(description = "The signing provider's case id", examples = "1234567890", requiredMode = REQUIRED)
	private String providerCaseId;

	@Schema(description = "The provider event type that triggered the notification", examples = "signingInstanceCompleted", requiredMode = NOT_REQUIRED)
	private String eventType;

	@DateTimeFormat(iso = DATE_TIME)
	@Schema(description = "When the event occurred", examples = "2026-12-31T23:59:59Z", requiredMode = NOT_REQUIRED)
	private OffsetDateTime occurredAt;

}
