package se.sundsvall.esigning.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static se.sundsvall.esigning.TestUtil.createEsigningResponse;
import static se.sundsvall.esigning.TestUtil.createSigningRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.esigning.Application;
import se.sundsvall.esigning.api.model.EsigningResponse;
import se.sundsvall.esigning.service.SigningService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceTest {

	@MockitoBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void startSigningProcess() {
		final var municipalityId = "2281";
		final var request = createSigningRequest();
		final var response = createEsigningResponse(resp -> resp.setProcessId("1234"));

		when(signingServiceMock.startSigningProcess(municipalityId, request)).thenReturn(response);

		final var responseBody = webTestClient.post()
			.uri("/" + municipalityId + "/e-signing/start")
			.bodyValue(request)
			.exchange()
			.expectStatus().isAccepted()
			.expectBody(EsigningResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(responseBody).isEqualTo(response);
		verify(signingServiceMock).startSigningProcess(municipalityId, request);
		verifyNoMoreInteractions(signingServiceMock);
	}

}
