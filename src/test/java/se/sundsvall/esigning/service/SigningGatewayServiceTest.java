package se.sundsvall.esigning.service;

import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.esigning.integration.postportalservice.PostportalserviceIntegration;
import se.sundsvall.esigning.provider.SigningProvider;
import se.sundsvall.esigning.provider.SigningProviderRegistry;
import se.sundsvall.esigning.provider.model.SigningInstanceInfo;
import se.sundsvall.esigning.provider.model.SigningResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.esigning.TestUtil.createSigningDocument;
import static se.sundsvall.esigning.TestUtil.createSigningEvent;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;
import static se.sundsvall.esigning.provider.model.SigningStatus.INITIATED;
import static se.sundsvall.esigning.provider.model.SigningStatus.SIGNED;

@ExtendWith(MockitoExtension.class)
class SigningGatewayServiceTest {

	@Mock
	private SigningProviderRegistry mockRegistry;

	@Mock
	private SigningProvider mockProvider;

	@Mock
	private PostportalserviceIntegration mockPostportalserviceIntegration;

	@InjectMocks
	private SigningGatewayService service;

	@Test
	void startSigning() {
		final var municipalityId = "2281";
		final var request = createStartSigningRequest();
		final var signatoryUrls = Map.of("party", "url");
		when(mockRegistry.resolve(municipalityId)).thenReturn(mockProvider);
		when(mockProvider.startSigning(municipalityId, request)).thenReturn(new SigningResult("case-1", INITIATED, signatoryUrls));
		when(mockProvider.getId()).thenReturn("comfact");

		final var response = service.startSigning(municipalityId, request);

		assertThat(response.getProviderCaseId()).isEqualTo("case-1");
		assertThat(response.getStatus()).isEqualTo("INITIATED");
		assertThat(response.getProvider()).isEqualTo("comfact");
		assertThat(response.getSignatoryUrls()).isEqualTo(signatoryUrls);

		verify(mockRegistry).resolve(municipalityId);
		verify(mockProvider).startSigning(municipalityId, request);
		verify(mockProvider).getId();
		verifyNoMoreInteractions(mockRegistry, mockProvider, mockPostportalserviceIntegration);
	}

	@Test
	void getSigningInstance() {
		final var municipalityId = "2281";
		final var providerCaseId = "case-1";
		final var expires = OffsetDateTime.now().plusDays(10);
		final var signedDocument = createSigningDocument();
		when(mockRegistry.resolve(municipalityId)).thenReturn(mockProvider);
		when(mockProvider.getSigningInstance(municipalityId, providerCaseId)).thenReturn(new SigningInstanceInfo(providerCaseId, SIGNED, expires, signedDocument));
		when(mockProvider.getId()).thenReturn("comfact");

		final var response = service.getSigningInstance(municipalityId, providerCaseId);

		assertThat(response.getProviderCaseId()).isEqualTo(providerCaseId);
		assertThat(response.getStatus()).isEqualTo("SIGNED");
		assertThat(response.getProvider()).isEqualTo("comfact");
		assertThat(response.getExpires()).isEqualTo(expires);
		assertThat(response.getSignedDocument()).isEqualTo(signedDocument);

		verify(mockRegistry).resolve(municipalityId);
		verify(mockProvider).getSigningInstance(municipalityId, providerCaseId);
		verify(mockProvider).getId();
		verifyNoMoreInteractions(mockRegistry, mockProvider, mockPostportalserviceIntegration);
	}

	@Test
	void relaySigningEvent() {
		final var municipalityId = "2281";
		final var event = createSigningEvent();

		service.relaySigningEvent(municipalityId, event);

		verify(mockPostportalserviceIntegration).sendEvent(municipalityId, event);
		verifyNoMoreInteractions(mockRegistry, mockProvider, mockPostportalserviceIntegration);
	}
}
