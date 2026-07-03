package se.sundsvall.esigning.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.dept44.common.validators.annotation.ValidBase64;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Document to be signed")
public class SigningDocument {

	@Schema(description = "Optional descriptive name for the document, visible to the signatory", examples = "Employment contract", requiredMode = NOT_REQUIRED)
	private String name;

	@NotBlank
	@Schema(description = "The document file name including extension", examples = "document.pdf", requiredMode = REQUIRED)
	private String fileName;

	@OneOf(value = "application/pdf", message = "The provided mime type is not valid. Only application/pdf is supported.")
	@Schema(description = "The document mime type. Must be application/pdf", examples = "application/pdf", requiredMode = REQUIRED)
	private String mimeType;

	@NotBlank
	@ValidBase64(nullable = true)
	@Schema(description = "Base64-encoded content of the document to be signed", examples = "dGVzdA==", requiredMode = REQUIRED)
	private String content;

}
