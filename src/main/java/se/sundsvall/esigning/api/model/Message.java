package se.sundsvall.esigning.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Message model")
public class Message {

	@NotBlank
	@Schema(description = "Subject of the message", example = "Please sign the document", requiredMode = REQUIRED)
	private String subject;

	@NotBlank
	@Schema(description = "Body of the message", example = "Dear John Doe, please sign the document.", requiredMode = REQUIRED)
	private String body;

}
