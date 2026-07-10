package se.sundsvall.esigning.integration.comfactfacade;

import generated.se.sundsvall.comfactfacade.CreateSigningResponse;
import generated.se.sundsvall.comfactfacade.SigningInstance;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import generated.se.sundsvall.comfactfacade.UpdateSigningRequest;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

@Component
public class ComfactFacadeIntegration {

	// Comfact status a signing instance is patched to in order to cancel it (see api-comfact-facade UpdateSigningRequest).
	private static final String STATUS_WITHDRAWN = "withdrawn";

	private static final String COULD_NOT_CREATE_SIGNING = "Could not create signing instance at Comfact facade. Error: %s";
	private static final String COULD_NOT_GET_SIGNING = "Could not fetch signing instance %s at Comfact facade. Error: %s";
	private static final String COULD_NOT_WITHDRAW_SIGNING = "Could not withdraw signing instance %s at Comfact facade. Error: %s";

	private final ComfactFacadeClient comfactFacadeClient;

	public ComfactFacadeIntegration(final ComfactFacadeClient comfactFacadeClient) {
		this.comfactFacadeClient = comfactFacadeClient;
	}

	public CreateSigningResponse createSigning(final String municipalityId, final SigningRequest request) {
		try {
			return comfactFacadeClient.createSigningRequest(municipalityId, request);
		} catch (final ThrowableProblem e) {
			throw Problem.valueOf(e.getStatus(), COULD_NOT_CREATE_SIGNING.formatted(e.getDetail()));
		}
	}

	public SigningInstance getSigning(final String municipalityId, final String signingId) {
		try {
			return comfactFacadeClient.getSigningInstance(municipalityId, signingId);
		} catch (final ThrowableProblem e) {
			throw Problem.valueOf(e.getStatus(), COULD_NOT_GET_SIGNING.formatted(sanitizeForLogging(signingId), e.getDetail()));
		}
	}

	public void withdrawSigning(final String municipalityId, final String signingId) {
		try {
			comfactFacadeClient.updateSigningRequest(municipalityId, signingId, new UpdateSigningRequest().status(STATUS_WITHDRAWN));
		} catch (final ThrowableProblem e) {
			throw Problem.valueOf(e.getStatus(), COULD_NOT_WITHDRAW_SIGNING.formatted(sanitizeForLogging(signingId), e.getDetail()));
		}
	}
}
