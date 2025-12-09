package se.sundsvall.esigning.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Signatory model")
public class Signatory {

	@NotBlank
	@Schema(description = "The signatory name", examples = "John Doe", requiredMode = REQUIRED)
	private String name;

	@Schema(description = "The signatory organization", examples = "Sundsvall Municipality", requiredMode = NOT_REQUIRED)
	private String organization;

	@ValidUuid
	@Schema(description = "The signatory party id", examples = "550e8400-e29b-41d4-a716-446655440000", requiredMode = REQUIRED)
	private String partyId;

	@Email
	@NotBlank
	@Schema(description = "The signatory email", examples = "john.doe@sundsvall.se", requiredMode = NOT_REQUIRED)
	private String email;

	@Valid
	@Schema(description = "The notification message", implementation = Message.class, requiredMode = NOT_REQUIRED)
	private Message notificationMessage;

}
