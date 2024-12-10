package se.sundsvall.esigning.integration.esigningprocess;

import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;

import generated.se.sundsvall.pw_e_signing.SigningRequest;
import generated.se.sundsvall.pw_e_signing.StartResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

@Component
public class EsigningProcessIntegration {

	private static final String PROCESS_ENGINE_PROBLEM_DETAIL = "Unexpected response from ProcessEngine API.";
	private static final String COULD_NOT_START_PROCESS = "Could not start process for signing request. Error: %s";
	private static final Logger LOGGER = LoggerFactory.getLogger(EsigningProcessIntegration.class);

	private final EsigningProcessClient esigningProcessClient;

	public EsigningProcessIntegration(final EsigningProcessClient esigningProcessClient) {
		this.esigningProcessClient = esigningProcessClient;
	}

	public StartResponse startProcess(final String municipalityId, final SigningRequest request) {
		try {
			return esigningProcessClient.startProcess(municipalityId, request);
		} catch (final ThrowableProblem e) {
			LOGGER.error(COULD_NOT_START_PROCESS.formatted(e));
			throw Problem.valueOf(SERVICE_UNAVAILABLE, PROCESS_ENGINE_PROBLEM_DETAIL);
		}
	}
}
