package se.sundsvall.esigning.integration.document;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.esigning.integration.document.configuration.DocumentConfiguration.CLIENT_ID;

import generated.se.sundsvall.document.Document;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.esigning.integration.document.configuration.DocumentConfiguration;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.document.base-url}",
	configuration = DocumentConfiguration.class)
public interface DocumentClient {

	@Retry(name = CLIENT_ID)
	@GetMapping(path = "/{municipalityId}/documents/{registrationNumber}", produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	Document getDocument(@PathVariable("municipalityId") String municipalityId, @PathVariable(name = "registrationNumber") final String registrationNumber);

}
