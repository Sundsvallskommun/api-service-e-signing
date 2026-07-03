package se.sundsvall.esigning.provider.comfact;

import generated.se.sundsvall.comfactfacade.CreateSigningResponse;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.esigning.integration.comfactfacade.ComfactFacadeIntegration;
import se.sundsvall.esigning.provider.model.SigningStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;

@ExtendWith(MockitoExtension.class)
class ComfactSigningProviderTest {

	@Mock
	private ComfactFacadeIntegration mockIntegration;

	@InjectMocks
	private ComfactSigningProvider provider;

	@Captor
	private ArgumentCaptor<SigningRequest> requestCaptor;

	@Test
	void getId() {
		assertThat(provider.getId()).isEqualTo("comfact");
	}

	@Test
	void startSigning() {
		final var municipalityId = "2281";
		final var request = createStartSigningRequest();
		final var signatoryUrls = Map.of("550e8400-e29b-41d4-a716-446655440000", "https://sign.example/abc");
		when(mockIntegration.createSigning(eq(municipalityId), any(SigningRequest.class)))
			.thenReturn(new CreateSigningResponse().signingId("comfact-123").signatoryUrls(signatoryUrls));

		final var result = provider.startSigning(municipalityId, request);

		assertThat(result.providerCaseId()).isEqualTo("comfact-123");
		assertThat(result.status()).isEqualTo(SigningStatus.INITIERAT);
		assertThat(result.signatoryUrls()).isEqualTo(signatoryUrls);

		verify(mockIntegration).createSigning(eq(municipalityId), requestCaptor.capture());
		assertThat(requestCaptor.getValue().getDocument().getFileName()).isEqualTo("test.pdf");
		assertThat(requestCaptor.getValue().getSignatories()).hasSize(1);
		verifyNoMoreInteractions(mockIntegration);
	}

	@Test
	void startSigning_nullSignatoryUrlsDefaultsToEmptyMap() {
		final var municipalityId = "2281";
		final var request = createStartSigningRequest();
		when(mockIntegration.createSigning(eq(municipalityId), any(SigningRequest.class)))
			.thenReturn(new CreateSigningResponse().signingId("comfact-123").signatoryUrls(null));

		final var result = provider.startSigning(municipalityId, request);

		assertThat(result.signatoryUrls()).isEmpty();
	}
}
