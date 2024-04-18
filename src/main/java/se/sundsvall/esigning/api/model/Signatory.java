package se.sundsvall.esigning.api.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Signatory model")
public class Signatory {

	@NotBlank
	@Schema(description = "The signatory name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	@Schema(description = "The signatory organization", example = "Sundsvall Municipality", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String organization;

	@ValidUuid
	@Schema(description = "The signatory party id", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
	private String partyId;

	@Email
	@NotBlank
	@Schema(description = "The signatory email", example = "john.doe@sundsvall.se", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String email;

	@Valid
	@Schema(description = "The notification message", implementation = Message.class, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Message notificationMessage;

}
