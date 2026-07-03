package se.sundsvall.esigning.integration.comfactfacade;

import generated.se.sundsvall.comfactfacade.CreateSigningResponse;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class ComfactFacadeIntegrationTest {

	@Mock
	private ComfactFacadeClient mockClient;

	@InjectMocks
	private ComfactFacadeIntegration integration;

	@Test
	void createSigning() {
		final var municipalityId = "2281";
		final var request = new SigningRequest();
		final var response = new CreateSigningResponse().signingId("123");
		when(mockClient.createSigningRequest(anyString(), any())).thenReturn(response);

		final var result = integration.createSigning(municipalityId, request);

		assertThat(result).isEqualTo(response);
		verify(mockClient).createSigningRequest(municipalityId, request);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void createSigning_whenThrowsPropagatesProblem() {
		final var municipalityId = "2281";
		final var request = new SigningRequest();
		when(mockClient.createSigningRequest(municipalityId, request)).thenThrow(Problem.valueOf(BAD_REQUEST, "Bad partyId"));

		assertThatThrownBy(() -> integration.createSigning(municipalityId, request))
			.isInstanceOf(Problem.class)
			.hasMessage("Bad Request: Bad partyId")
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);

		verify(mockClient).createSigningRequest(municipalityId, request);
		verifyNoMoreInteractions(mockClient);
	}
}
