package se.sundsvall.esigning.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Initiator model")
public class Initiator {

	@NotBlank
	@Schema(description = "The initiator name", examples = "John Doe", requiredMode = REQUIRED)
	private String name;

	@ValidUuid
	@Schema(description = "The initiator party id", examples = "550e8400-e29b-41d4-a716-446655440000", requiredMode = REQUIRED)
	private String partyId;

	@Schema(description = "The initiator organization", examples = "Sundsvall Municipality", requiredMode = NOT_REQUIRED)
	private String organization;

	@Email
	@NotNull
	@Schema(description = "The initiator email", examples = "john.doe@sundsvall.se", requiredMode = REQUIRED)
	private String email;

}
