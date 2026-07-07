package se.sundsvall.esigning.integration.postportalservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.esigning.integration.postportalservice.configuration.PostportalserviceConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.esigning.integration.postportalservice.configuration.PostportalserviceConfiguration.CLIENT_ID;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.postportalservice.base-url}",
	configuration = PostportalserviceConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface PostportalserviceClient {

	@Retry(name = CLIENT_ID)
	@PostMapping(path = "/{municipalityId}/e-signing/events", consumes = APPLICATION_JSON_VALUE)
	void sendEvent(@PathVariable String municipalityId, @RequestBody SigningEvent signingEvent);
}
