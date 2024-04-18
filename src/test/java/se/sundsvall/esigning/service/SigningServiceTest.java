package se.sundsvall.esigning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.esigning.TestUtil.createSigningRequest;
import static se.sundsvall.esigning.integration.esigning.util.EsigningMapper.toSigningRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.esigning.integration.esigning.EsigningIntegration;

import generated.se.sundsvall.pw_e_signing.StartResponse;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

	@InjectMocks
	private SigningService signingService;

	@Mock
	private EsigningIntegration mockIntegration;

	@Test
	void startSigningProcess() {
		final var signingRequest = createSigningRequest();
		when(mockIntegration.startProcess(toSigningRequest(signingRequest)))
			.thenReturn(new StartResponse().processId("123"));

		final var processId = signingService.startSigningProcess(signingRequest);

		assertThat(processId).isEqualTo("123");
		verify(mockIntegration).startProcess(toSigningRequest(signingRequest));
		verifyNoMoreInteractions(mockIntegration);
	}

}
