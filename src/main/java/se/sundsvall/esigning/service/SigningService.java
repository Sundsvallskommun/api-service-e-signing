package se.sundsvall.esigning.service;

import static se.sundsvall.esigning.integration.esigning.util.EsigningMapper.toSigningRequest;

import org.springframework.stereotype.Service;

import se.sundsvall.esigning.api.model.SigningRequest;
import se.sundsvall.esigning.integration.esigning.EsigningIntegration;

@Service
public class SigningService {

	private final EsigningIntegration esigningIntegration;

	public SigningService(final EsigningIntegration esigningIntegration) {
		this.esigningIntegration = esigningIntegration;
	}

	public String startSigningProcess(final SigningRequest signingRequest) {
		return esigningIntegration.startProcess(toSigningRequest(signingRequest)).getProcessId();
	}

}
