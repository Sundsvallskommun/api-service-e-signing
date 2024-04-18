package se.sundsvall.esigning.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static se.sundsvall.esigning.TestUtil.createSigningRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.esigning.Application;
import se.sundsvall.esigning.service.SigningService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceTest {

	@MockBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void startSigningProcess() {
		final var request = createSigningRequest();

		when(signingServiceMock.startSigningProcess(request)).thenReturn("1234");

		final var responseBody = webTestClient.post()
			.uri("/e-signing/start")
			.bodyValue(request)
			.exchange()
			.expectStatus().isAccepted()
			.expectBody(String.class)
			.returnResult()
			.getResponseBody();

		assertThat(responseBody).isEqualTo("1234");
		verify(signingServiceMock).startSigningProcess(request);
		verifyNoMoreInteractions(signingServiceMock);
	}

}
