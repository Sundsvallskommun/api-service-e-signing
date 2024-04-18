package se.sundsvall.esigning.service;

import static se.sundsvall.esigning.integration.esigningprocess.util.EsigningProcessMapper.toSigningRequest;

import org.springframework.stereotype.Service;

import se.sundsvall.esigning.api.model.EsigningResponse;
import se.sundsvall.esigning.api.model.SigningRequest;
import se.sundsvall.esigning.integration.esigningprocess.EsigningProcessIntegration;

@Service
public class SigningService {

	private final EsigningProcessIntegration esigningProcessIntegration;

	public SigningService(final EsigningProcessIntegration esigningProcessIntegration) {
		this.esigningProcessIntegration = esigningProcessIntegration;
	}

	public EsigningResponse startSigningProcess(final SigningRequest signingRequest) {
		final var processId = esigningProcessIntegration.startProcess(toSigningRequest(signingRequest)).getProcessId();

		return EsigningResponse.builder()
			.withProcessId(processId)
			.build();
	}

}
