package se.sundsvall.esigning.integration.esigning;

import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;

import generated.se.sundsvall.pw_e_signing.SigningRequest;
import generated.se.sundsvall.pw_e_signing.StartResponse;


@Component
public class EsigningIntegration {

	private static final String PROCESS_ENGINE_PROBLEM_DETAIL = "Unexpected response from ProcessEngine API.";
	private static final String COULD_NOT_START_PROCESS = "Could not start process for signing request. Error: %s";
	private static final Logger LOGGER = LoggerFactory.getLogger(EsigningIntegration.class);

	private final EsigningClient esigningClient;

	public EsigningIntegration(final EsigningClient esigningClient) {
		this.esigningClient = esigningClient;
	}

	public StartResponse startProcess(final SigningRequest request) {
		try {
			return esigningClient.startProcess(request);
		} catch (final AbstractThrowableProblem e) {
			LOGGER.error(COULD_NOT_START_PROCESS.formatted(e));
			throw Problem.valueOf(SERVICE_UNAVAILABLE, PROCESS_ENGINE_PROBLEM_DETAIL);
		}
	}

}
