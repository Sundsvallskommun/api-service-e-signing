package se.sundsvall.esigning.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.esigning.api.model.SigningInstanceResponse;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.api.model.StartSigningResponse;
import se.sundsvall.esigning.service.SigningGatewayService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Validated
@RequestMapping("/{municipalityId}/e-signing")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
@Tag(name = "eSigning", description = "E-signing")
class SigningGatewayResource {

	private final SigningGatewayService signingGatewayService;

	SigningGatewayResource(final SigningGatewayService signingGatewayService) {
		this.signingGatewayService = signingGatewayService;
	}

	@Operation(summary = "Create a signing request via the configured signing provider", responses = {
		@ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true)
	})
	@PostMapping(value = "/signings", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ResponseEntity<StartSigningResponse> createSigning(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@Valid @RequestBody final StartSigningRequest request) {
		return status(CREATED).body(signingGatewayService.startSigning(municipalityId, request));
	}

	@Operation(summary = "Get the current status of a signing case", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@GetMapping(value = "/signings/{providerCaseId}", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<SigningInstanceResponse> getSigningInstance(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@PathVariable @Parameter(name = "providerCaseId", description = "The signing provider's case id", example = "1234567890") final String providerCaseId) {
		return ok(signingGatewayService.getSigningInstance(municipalityId, providerCaseId));
	}

}
