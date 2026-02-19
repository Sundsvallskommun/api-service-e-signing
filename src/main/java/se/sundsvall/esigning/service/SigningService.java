package se.sundsvall.esigning.service;

import generated.se.sundsvall.document.Document;
import java.util.Collections;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.esigning.api.model.EsigningResponse;
import se.sundsvall.esigning.api.model.SigningRequest;
import se.sundsvall.esigning.integration.document.DocumentIntegration;
import se.sundsvall.esigning.integration.esigningprocess.EsigningProcessIntegration;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.esigning.integration.esigningprocess.util.EsigningProcessMapper.toSigningRequest;

@Service
public class SigningService {

	private static final String DOCUMENT_NOT_FOUND = "Document not found with name %s not found";
	private static final String DOCUMENT_BEING_SIGNED = "Document have an ongoing signing process";
	private static final String NOT_A_PDF = "Only PDF files are supported";
	private final DocumentIntegration documentIntegration;
	private final EsigningProcessIntegration esigningProcessIntegration;

	public SigningService(final DocumentIntegration documentIntegration, final EsigningProcessIntegration esigningProcessIntegration) {
		this.documentIntegration = documentIntegration;
		this.esigningProcessIntegration = esigningProcessIntegration;
	}

	public EsigningResponse startSigningProcess(final String municipalityId, final SigningRequest signingRequest) {
		validateDocument(municipalityId, signingRequest);

		final var processId = esigningProcessIntegration.startProcess(municipalityId, toSigningRequest(signingRequest)).getProcessId();

		return EsigningResponse.builder()
			.withProcessId(processId)
			.build();
	}

	void validateDocument(final String municipalityId, final SigningRequest signingRequest) {
		final var registrationNumber = signingRequest.getDocument().getRegistrationNumber();
		final var fileName = signingRequest.getDocument().getFileName();
		final var document = documentIntegration.getDocument(municipalityId, registrationNumber);

		checkForSigningInProgress(document);
		validateMimeType(document, fileName);
	}

	void checkForSigningInProgress(final Document document) {
		Optional.ofNullable(document.getMetadataList()).orElse(Collections.emptyList()).stream()
			.filter(documentMetadata -> "signingInProgress".equals(documentMetadata.getKey()))
			.filter(documentMetadata -> "true".equals(documentMetadata.getValue()))
			.findFirst()
			.ifPresent(value -> {
				throw Problem.valueOf(BAD_REQUEST, DOCUMENT_BEING_SIGNED);
			});
	}

	void validateMimeType(final Document document, final String fileName) {
		final var documentToBeSigned = document.getDocumentData().stream()
			.filter(documentData -> documentData.getFileName().equals(fileName))
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, DOCUMENT_NOT_FOUND.formatted(fileName)));

		if (!"application/pdf".equals(documentToBeSigned.getMimeType())) {
			throw Problem.valueOf(BAD_REQUEST, NOT_A_PDF);
		}
	}
}
