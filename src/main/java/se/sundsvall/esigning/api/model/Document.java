package se.sundsvall.esigning.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
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
@Schema(description = "Document model")
public class Document {

	@Schema(description = "Optional descriptive name for the document that is to be signed.", example = "Employment contract", requiredMode = NOT_REQUIRED)
	private String descriptiveName;

	@NotBlank
	@Schema(description = "The document registration number, the value must not be blank or null", example = "12345-2022", requiredMode = REQUIRED)
	private String registrationNumber;

	@NotBlank
	@Schema(description = "The document file name, the value must not be blank or null", example = "document.pdf", requiredMode = REQUIRED)
	private String fileName;

}
