package se.sundsvall.esigning.integration.postportalservice;

import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

@Component
public class PostportalserviceIntegration {

	private static final String COULD_NOT_SEND_EVENT = "Could not send signing event for case %s to Postportalservice. Error: %s";

	private final PostportalserviceClient postportalserviceClient;

	public PostportalserviceIntegration(final PostportalserviceClient postportalserviceClient) {
		this.postportalserviceClient = postportalserviceClient;
	}

	public void sendEvent(final String municipalityId, final SigningEvent signingEvent) {
		try {
			postportalserviceClient.sendEvent(municipalityId, signingEvent);
		} catch (final ThrowableProblem e) {
			// Rethrow with the case id as context so the whole webhook chain fails and the provider retries the
			// delivery later. The propagated problem is logged upstream by the framework; providerCaseId comes from an
			// inbound webhook payload, so sanitize it to avoid log injection there.
			throw Problem.valueOf(e.getStatus(), COULD_NOT_SEND_EVENT.formatted(sanitizeForLogging(signingEvent.providerCaseId()), e.getDetail()));
		}
	}
}
