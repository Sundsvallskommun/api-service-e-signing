package se.sundsvall.esigning.integration.esigningprocess;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.esigning.integration.esigningprocess.configuration.EsigningProcessConfiguration.CLIENT_ID;

import generated.se.sundsvall.pw_e_signing.SigningRequest;
import generated.se.sundsvall.pw_e_signing.StartResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.esigning.integration.esigningprocess.configuration.EsigningProcessConfiguration;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.esigningprocess.base-url}",
	configuration = EsigningProcessConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface EsigningProcessClient {

	@Retry(name = CLIENT_ID)
	@PostMapping(path = "/{municipalityId}/process/start", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	StartResponse startProcess(@PathVariable("municipalityId") String municipalityId, @RequestBody final SigningRequest request);
}
