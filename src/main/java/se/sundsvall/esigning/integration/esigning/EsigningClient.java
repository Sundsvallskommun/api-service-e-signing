package se.sundsvall.esigning.integration.esigning;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.esigning.integration.esigning.configuration.EsigningConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import se.sundsvall.esigning.integration.esigning.configuration.EsigningConfiguration;

import generated.se.sundsvall.pw_e_signing.SigningRequest;
import generated.se.sundsvall.pw_e_signing.StartResponse;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.esigning.base-url}",
	configuration = EsigningConfiguration.class
)
public interface EsigningClient {

	@Retry(name = CLIENT_ID)
	@PostMapping(path = "/process/start", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	StartResponse startProcess(@RequestBody final SigningRequest request);

}
