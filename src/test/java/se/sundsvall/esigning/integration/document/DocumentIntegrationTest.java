package se.sundsvall.esigning.integration.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import generated.se.sundsvall.document.Document;

@ExtendWith(MockitoExtension.class)
class DocumentIntegrationTest {

	@Mock
	private DocumentClient mockClient;

	@InjectMocks
	private DocumentIntegration integration;

	@Test
	void getDocument() {
		final var registrationNumber = "123-321";
		final var document = new Document().registrationNumber(registrationNumber);
		when(mockClient.getDocument(registrationNumber)).thenReturn(document);

		var result = integration.getDocument(registrationNumber);

		assertThat(result).isEqualTo(document);
		verify(mockClient).getDocument(registrationNumber);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void getDocument_whenThrowsTest() {
		final var registrationNumber = "123-321";
		when(mockClient.getDocument(registrationNumber)).thenThrow(new ThrowableProblem() {
		});

		assertThatThrownBy(() -> integration.getDocument(registrationNumber))
			.isInstanceOf(Problem.class)
			.hasMessage("Service Unavailable: Unexpected response from Document API")
			.hasFieldOrPropertyWithValue("status", SERVICE_UNAVAILABLE);

		verify(mockClient).getDocument(registrationNumber);
		verifyNoMoreInteractions(mockClient);
	}

}
