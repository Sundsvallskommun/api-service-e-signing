package se.sundsvall.esigning.integration.comfactfacade;

import generated.se.sundsvall.comfactfacade.CreateSigningResponse;
import generated.se.sundsvall.comfactfacade.SigningInstance;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.esigning.integration.comfactfacade.configuration.ComfactFacadeConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.esigning.integration.comfactfacade.configuration.ComfactFacadeConfiguration.CLIENT_ID;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.comfactfacade.base-url}",
	configuration = ComfactFacadeConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface ComfactFacadeClient {

	@Retry(name = CLIENT_ID)
	@PostMapping(path = "/{municipalityId}/signings", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	CreateSigningResponse createSigningRequest(@PathVariable("municipalityId") String municipalityId, @RequestBody SigningRequest request);

	@Retry(name = CLIENT_ID)
	@GetMapping(path = "/{municipalityId}/signings/{signingId}", produces = APPLICATION_JSON_VALUE)
	SigningInstance getSigningInstance(@PathVariable("municipalityId") String municipalityId, @PathVariable("signingId") String signingId);
}
