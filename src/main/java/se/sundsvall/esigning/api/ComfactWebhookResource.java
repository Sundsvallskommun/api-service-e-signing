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
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.esigning.api.model.ComfactEventNotification;
import se.sundsvall.esigning.provider.comfact.ComfactSigningMapper;
import se.sundsvall.esigning.service.SigningGatewayService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

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
class ComfactWebhookResource {

	private final SigningGatewayService signingGatewayService;

	ComfactWebhookResource(final SigningGatewayService signingGatewayService) {
		this.signingGatewayService = signingGatewayService;
	}

	@Operation(summary = "Receive a signing event from Comfact (forwarded by api-comfact-facade)", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	})
	@PostMapping(value = "/webhooks/comfact", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> handleComfactEvent(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@Valid @RequestBody final ComfactEventNotification notification) {
		signingGatewayService.relaySigningEvent(municipalityId, ComfactSigningMapper.toSigningEvent(notification));
		return ok().build();
	}

}
