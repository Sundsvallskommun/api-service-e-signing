package se.sundsvall.esigning.integration.comfactfacade;

import generated.se.sundsvall.comfactfacade.CreateSigningResponse;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.ThrowableProblem;

@Component
public class ComfactFacadeIntegration {

	private static final String COULD_NOT_CREATE_SIGNING = "Could not create signing instance at Comfact facade. Error: %s";
	private static final Logger LOGGER = LoggerFactory.getLogger(ComfactFacadeIntegration.class);

	private final ComfactFacadeClient comfactFacadeClient;

	public ComfactFacadeIntegration(final ComfactFacadeClient comfactFacadeClient) {
		this.comfactFacadeClient = comfactFacadeClient;
	}

	public CreateSigningResponse createSigning(final String municipalityId, final SigningRequest request) {
		try {
			return comfactFacadeClient.createSigningRequest(municipalityId, request);
		} catch (final ThrowableProblem e) {
			// Preserve the provider's original problem (status + detail) so the caller gets a clear error.
			LOGGER.error(COULD_NOT_CREATE_SIGNING.formatted(e.getMessage()));
			throw e;
		}
	}
}
