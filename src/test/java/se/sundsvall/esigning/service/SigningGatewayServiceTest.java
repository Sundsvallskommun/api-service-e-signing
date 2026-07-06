package se.sundsvall.esigning.service;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.esigning.provider.SigningProvider;
import se.sundsvall.esigning.provider.SigningProviderRegistry;
import se.sundsvall.esigning.provider.model.SigningResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;
import static se.sundsvall.esigning.provider.model.SigningStatus.INITIERAT;

@ExtendWith(MockitoExtension.class)
class SigningGatewayServiceTest {

	@Mock
	private SigningProviderRegistry mockRegistry;

	@Mock
	private SigningProvider mockProvider;

	@InjectMocks
	private SigningGatewayService service;

	@Test
	void startSigning() {
		final var municipalityId = "2281";
		final var request = createStartSigningRequest();
		final var signatoryUrls = Map.of("party", "url");
		when(mockRegistry.resolve(municipalityId)).thenReturn(mockProvider);
		when(mockProvider.startSigning(municipalityId, request)).thenReturn(new SigningResult("case-1", INITIERAT, signatoryUrls));
		when(mockProvider.getId()).thenReturn("comfact");

		final var response = service.startSigning(municipalityId, request);

		assertThat(response.getProviderCaseId()).isEqualTo("case-1");
		assertThat(response.getStatus()).isEqualTo("INITIERAT");
		assertThat(response.getProvider()).isEqualTo("comfact");
		assertThat(response.getSignatoryUrls()).isEqualTo(signatoryUrls);

		verify(mockRegistry).resolve(municipalityId);
		verify(mockProvider).startSigning(municipalityId, request);
		verify(mockProvider).getId();
		verifyNoMoreInteractions(mockRegistry, mockProvider);
	}
}
