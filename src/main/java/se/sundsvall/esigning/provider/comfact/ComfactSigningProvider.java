package se.sundsvall.esigning.provider.comfact;

import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.integration.comfactfacade.ComfactFacadeIntegration;
import se.sundsvall.esigning.provider.SigningProvider;
import se.sundsvall.esigning.provider.model.SigningInstanceInfo;
import se.sundsvall.esigning.provider.model.SigningResult;

import static java.util.Collections.emptyMap;
import static se.sundsvall.esigning.provider.comfact.ComfactSigningMapper.toComfactSigningRequest;
import static se.sundsvall.esigning.provider.comfact.ComfactSigningMapper.toSigningInstanceInfo;
import static se.sundsvall.esigning.provider.model.SigningStatus.INITIERAT;

/**
 * Reference {@link SigningProvider} implementation backed by Comfact (via api-comfact-facade).
 */
@Component
public class ComfactSigningProvider implements SigningProvider {

	public static final String PROVIDER_ID = "comfact";

	private final ComfactFacadeIntegration comfactFacadeIntegration;

	public ComfactSigningProvider(final ComfactFacadeIntegration comfactFacadeIntegration) {
		this.comfactFacadeIntegration = comfactFacadeIntegration;
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public SigningResult startSigning(final String municipalityId, final StartSigningRequest request) {
		final var response = comfactFacadeIntegration.createSigning(municipalityId, toComfactSigningRequest(request));

		return new SigningResult(
			response.getSigningId(),
			INITIERAT,
			Optional.ofNullable(response.getSignatoryUrls()).orElse(emptyMap()));
	}

	@Override
	public SigningInstanceInfo getSigningInstance(final String municipalityId, final String providerCaseId) {
		return toSigningInstanceInfo(comfactFacadeIntegration.getSigning(municipalityId, providerCaseId));
	}

	@Override
	public void cancelSigning(final String municipalityId, final String providerCaseId) {
		comfactFacadeIntegration.withdrawSigning(municipalityId, providerCaseId);
	}
}
