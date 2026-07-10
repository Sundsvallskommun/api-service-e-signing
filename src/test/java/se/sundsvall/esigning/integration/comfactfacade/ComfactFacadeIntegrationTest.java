package se.sundsvall.esigning.integration.comfactfacade;

import generated.se.sundsvall.comfactfacade.CreateSigningResponse;
import generated.se.sundsvall.comfactfacade.SigningInstance;
import generated.se.sundsvall.comfactfacade.SigningRequest;
import generated.se.sundsvall.comfactfacade.UpdateSigningRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
			.hasMessage("Bad Request: Could not create signing instance at Comfact facade. Error: Bad partyId")
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);

		verify(mockClient).createSigningRequest(municipalityId, request);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void getSigning() {
		final var municipalityId = "2281";
		final var signingId = "comfact-123";
		final var instance = new SigningInstance().signingId(signingId);
		when(mockClient.getSigningInstance(municipalityId, signingId)).thenReturn(instance);

		final var result = integration.getSigning(municipalityId, signingId);

		assertThat(result).isEqualTo(instance);
		verify(mockClient).getSigningInstance(municipalityId, signingId);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void getSigning_whenThrowsPropagatesProblem() {
		final var municipalityId = "2281";
		final var signingId = "comfact-123";
		when(mockClient.getSigningInstance(municipalityId, signingId)).thenThrow(Problem.valueOf(NOT_FOUND, "No such signing instance"));

		assertThatThrownBy(() -> integration.getSigning(municipalityId, signingId))
			.isInstanceOf(Problem.class)
			.hasMessage("Not Found: Could not fetch signing instance comfact-123 at Comfact facade. Error: No such signing instance")
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);

		verify(mockClient).getSigningInstance(municipalityId, signingId);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void withdrawSigning() {
		final var municipalityId = "2281";
		final var signingId = "comfact-123";

		integration.withdrawSigning(municipalityId, signingId);

		final var captor = ArgumentCaptor.forClass(UpdateSigningRequest.class);
		verify(mockClient).updateSigningRequest(eq(municipalityId), eq(signingId), captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo("withdrawn");
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void withdrawSigning_whenThrowsPropagatesProblem() {
		final var municipalityId = "2281";
		final var signingId = "comfact-123";
		doThrow(Problem.valueOf(NOT_FOUND, "No such signing instance")).when(mockClient).updateSigningRequest(eq(municipalityId), eq(signingId), any());

		assertThatThrownBy(() -> integration.withdrawSigning(municipalityId, signingId))
			.isInstanceOf(Problem.class)
			.hasMessage("Not Found: Could not withdraw signing instance comfact-123 at Comfact facade. Error: No such signing instance")
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);

		verify(mockClient).updateSigningRequest(eq(municipalityId), eq(signingId), any());
		verifyNoMoreInteractions(mockClient);
	}
}
