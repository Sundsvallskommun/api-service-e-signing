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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.esigning.api.model.EsigningResponse;
import se.sundsvall.esigning.api.model.SigningRequest;
import se.sundsvall.esigning.service.SigningService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.accepted;

@RestController
@Validated
@RequestMapping("/{municipalityId}/e-signing")
@ApiResponses(value = {
	@ApiResponse(responseCode = "202", description = "Accepted", content = @Content(mediaType = APPLICATION_JSON_VALUE), useReturnTypeSchema = true),
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	@ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
@Tag(name = "eSigning", description = "E-signing")
class SigningResource {

	private final SigningService signingService;

	SigningResource(final SigningService signingService) {
		this.signingService = signingService;
	}

	@Operation(summary = "Start a signing process")
	@PostMapping(value = "/start", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_PROBLEM_JSON_VALUE)
	ResponseEntity<EsigningResponse> startSigningProcess(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Valid @RequestBody final SigningRequest request) {
		return accepted().body(signingService.startSigningProcess(municipalityId, request));
	}

}
