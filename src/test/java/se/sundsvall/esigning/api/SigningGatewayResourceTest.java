package se.sundsvall.esigning.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.esigning.Application;
import se.sundsvall.esigning.api.model.StartSigningResponse;
import se.sundsvall.esigning.service.SigningGatewayService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static se.sundsvall.esigning.TestUtil.createStartSigningRequest;
import static se.sundsvall.esigning.TestUtil.createStartSigningResponse;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class SigningGatewayResourceTest {

	@MockitoBean
	private SigningGatewayService signingGatewayServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createSigning() {
		final var municipalityId = "2281";
		final var request = createStartSigningRequest();
		final var response = createStartSigningResponse();

		when(signingGatewayServiceMock.startSigning(municipalityId, request)).thenReturn(response);

		final var responseBody = webTestClient.post()
			.uri("/" + municipalityId + "/e-signing/signings")
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(StartSigningResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(responseBody).isEqualTo(response);
		verify(signingGatewayServiceMock).startSigning(municipalityId, request);
		verifyNoMoreInteractions(signingGatewayServiceMock);
	}
}
