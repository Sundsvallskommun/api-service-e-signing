package se.sundsvall.esigning.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Reminder model")
public class Reminder {

	@Valid
	@NotNull
	@Schema(description = "The reminder message", implementation = Message.class, requiredMode = REQUIRED)
	private Message message;

	@Min(1)
	@Schema(description = "The reminder interval in hours", example = "24", requiredMode = REQUIRED)
	private int intervalInHours;

	@NotNull
	@Schema(description = "The reminder start date and time", example = "2021-12-31T23:59:59Z", requiredMode = REQUIRED)
	private OffsetDateTime startDateTime;

}
