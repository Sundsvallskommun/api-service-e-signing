package se.sundsvall.esigning.integration.postportalservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.ThrowableProblem;

@Component
public class PostportalserviceIntegration {

	private static final String COULD_NOT_SEND_CALLBACK = "Could not send signing callback for case %s to Postportalservice. Error: %s";
	private static final Logger LOGGER = LoggerFactory.getLogger(PostportalserviceIntegration.class);

	private final PostportalserviceClient postportalserviceClient;

	public PostportalserviceIntegration(final PostportalserviceClient postportalserviceClient) {
		this.postportalserviceClient = postportalserviceClient;
	}

	public void sendCallback(final String municipalityId, final SigningCallbackRequest request) {
		try {
			postportalserviceClient.sendCallback(municipalityId, request);
		} catch (final ThrowableProblem e) {
			// Propagate so the whole webhook chain fails and the provider retries the delivery later.
			LOGGER.error(COULD_NOT_SEND_CALLBACK.formatted(request.providerCaseId(), e.getMessage()));
			throw e;
		}
	}
}
