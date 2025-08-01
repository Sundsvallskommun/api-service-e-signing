package se.sundsvall.esigning.integration.esigningprocess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;
import static se.sundsvall.esigning.TestUtil.createSigningRequest;

import generated.se.sundsvall.pw_e_signing.StartResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.esigning.integration.esigningprocess.util.EsigningProcessMapper;

@ExtendWith(MockitoExtension.class)
class EsigningProcessIntegrationTest {

	@Mock
	private EsigningProcessClient mockClient;

	@InjectMocks
	private EsigningProcessIntegration integration;

	@Test
	void startProcessTest() {
		final var municipalityId = "municipalityId";
		final var signingRequest = EsigningProcessMapper.toSigningRequest(createSigningRequest());
		final var response = new StartResponse().processId("123");
		when(mockClient.startProcess(anyString(), any())).thenReturn(response);

		var result = integration.startProcess(municipalityId, signingRequest);

		assertThat(result).isEqualTo(response);
		verify(mockClient).startProcess(municipalityId, signingRequest);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void startProcess_whenThrowsTest() {
		final var municipalityId = "municipalityId";
		final var signingRequest = EsigningProcessMapper.toSigningRequest(createSigningRequest());
		when(mockClient.startProcess(municipalityId, signingRequest)).thenThrow(new ThrowableProblem() {
			private static final long serialVersionUID = 94942837028478614L;
		});

		assertThatThrownBy(() -> integration.startProcess(municipalityId, signingRequest))
			.isInstanceOf(Problem.class)
			.hasMessage("Service Unavailable: Unexpected response from ProcessEngine API.")
			.hasFieldOrPropertyWithValue("status", SERVICE_UNAVAILABLE);

		verify(mockClient).startProcess(municipalityId, signingRequest);
		verifyNoMoreInteractions(mockClient);
	}

}
