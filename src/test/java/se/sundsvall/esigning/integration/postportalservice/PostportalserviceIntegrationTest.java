package se.sundsvall.esigning.integration.postportalservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static se.sundsvall.esigning.TestUtil.createSigningDocument;

@ExtendWith(MockitoExtension.class)
class PostportalserviceIntegrationTest {

	@Mock
	private PostportalserviceClient mockClient;

	@InjectMocks
	private PostportalserviceIntegration integration;

	@Test
	void sendCallback() {
		final var municipalityId = "2281";
		final var request = new SigningCallbackRequest("case-1", "SIGNERAT", createSigningDocument());

		assertThatNoException().isThrownBy(() -> integration.sendCallback(municipalityId, request));

		verify(mockClient).sendCallback(municipalityId, request);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void sendCallback_whenThrowsPropagatesProblem() {
		final var municipalityId = "2281";
		final var request = new SigningCallbackRequest("case-1", "SIGNERAT", null);
		doThrow(Problem.valueOf(BAD_GATEWAY, "Postportalservice unavailable")).when(mockClient).sendCallback(municipalityId, request);

		assertThatThrownBy(() -> integration.sendCallback(municipalityId, request))
			.isInstanceOf(Problem.class)
			.hasMessage("Bad Gateway: Postportalservice unavailable")
			.hasFieldOrPropertyWithValue("status", BAD_GATEWAY);

		verify(mockClient).sendCallback(municipalityId, request);
		verifyNoMoreInteractions(mockClient);
	}
}
