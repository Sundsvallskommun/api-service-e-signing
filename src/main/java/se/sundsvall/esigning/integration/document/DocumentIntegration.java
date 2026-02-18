package se.sundsvall.esigning.integration.document;

import generated.se.sundsvall.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

@Component
public class DocumentIntegration {

	private static final String DOCUMENT_PROBLEM_DETAIL = "Unexpected response from Document API";
	private static final String COULD_NOT_RETRIEVE_DOCUMENT = "Could not get document with registration number: %s";
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentIntegration.class);

	private final DocumentClient documentClient;

	public DocumentIntegration(final DocumentClient documentClient) {
		this.documentClient = documentClient;
	}

	public Document getDocument(final String municipalityId, final String registrationNumber) {
		try {
			return documentClient.getDocument(municipalityId, registrationNumber);
		} catch (final ThrowableProblem e) {
			LOGGER.error(COULD_NOT_RETRIEVE_DOCUMENT.formatted(sanitizeForLogging(registrationNumber)), e);
			throw Problem.valueOf(SERVICE_UNAVAILABLE, DOCUMENT_PROBLEM_DETAIL);
		}
	}
}
