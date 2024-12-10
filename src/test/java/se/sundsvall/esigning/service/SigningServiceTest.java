package se.sundsvall.esigning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.esigning.TestUtil.createSigningRequest;
import static se.sundsvall.esigning.integration.esigningprocess.util.EsigningProcessMapper.toSigningRequest;

import generated.se.sundsvall.document.Document;
import generated.se.sundsvall.document.DocumentData;
import generated.se.sundsvall.document.DocumentMetadata;
import generated.se.sundsvall.pw_e_signing.StartResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import se.sundsvall.esigning.integration.document.DocumentIntegration;
import se.sundsvall.esigning.integration.esigningprocess.EsigningProcessIntegration;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

	@InjectMocks
	private SigningService signingService;

	@Mock
	private EsigningProcessIntegration mockEsigningProcessIntegration;

	@Mock
	private DocumentIntegration mockDocumentIntegration;

	@Test
	void startSigningProcess() {
		final var municipalityId = "municipalityId";
		final var signingRequest = createSigningRequest();
		final var spy = Mockito.spy(signingService);

		when(mockEsigningProcessIntegration.startProcess(municipalityId, toSigningRequest(signingRequest)))
			.thenReturn(new StartResponse().processId("123"));
		when(mockDocumentIntegration.getDocument(municipalityId, signingRequest.getDocument().getRegistrationNumber()))
			.thenReturn(new Document().documentData(List.of(new DocumentData().fileName("test.pdf").mimeType("application/pdf"))));

		final var response = spy.startSigningProcess(municipalityId, signingRequest);

		assertThat(response.getProcessId()).isEqualTo("123");
		verify(mockEsigningProcessIntegration).startProcess(municipalityId, toSigningRequest(signingRequest));
		verify(spy).validateDocument(municipalityId, signingRequest);
		verifyNoMoreInteractions(mockEsigningProcessIntegration, mockDocumentIntegration);
	}

	@Test
	void validateDocument() {
		final var municipalityId = "municipalityId";
		final var signingRequest = createSigningRequest();
		final var documentData = new DocumentData().fileName("test.pdf").mimeType("application/pdf");
		final var document = new Document().documentData(List.of(documentData));
		final var spy = Mockito.spy(signingService);

		when(mockDocumentIntegration.getDocument(municipalityId, signingRequest.getDocument().getRegistrationNumber())).thenReturn(document);

		spy.validateDocument(municipalityId, signingRequest);

		verifyNoMoreInteractions(mockDocumentIntegration);
		verify(spy).validateDocument(municipalityId, signingRequest);
		verify(spy).checkForSigningInProgress(document);
		verify(spy).validateMimeType(document, signingRequest.getDocument().getFileName());
		verifyNoInteractions(mockEsigningProcessIntegration);
	}

	@Test
	void validateMimeType() {
		final var document = new Document().documentData(List.of(new DocumentData().fileName("test.pdf").mimeType("application/pdf")));
		final var fileName = "test.pdf";

		assertThatNoException().isThrownBy(() -> signingService.validateMimeType(document, fileName));

		verifyNoInteractions(mockDocumentIntegration, mockEsigningProcessIntegration);
	}

	@Test
	void checkForSigningInProgress_throws() {
		final var document = new Document().metadataList(List.of(new DocumentMetadata().key("signingInProgress").value("true")));

		assertThatThrownBy(() -> signingService.checkForSigningInProgress(document))
			.isInstanceOf(Problem.class)
			.hasMessage("Bad Request: Document have an ongoing signing process");
	}

	@Test
	void checkForSigningInProgress() {
		final var document = new Document().metadataList(List.of(new DocumentMetadata().key("signingInProgress").value("false")));

		assertThatNoException().isThrownBy(() -> signingService.checkForSigningInProgress(document));
	}

}
