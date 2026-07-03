package se.sundsvall.esigning.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.esigning.Application;
import se.sundsvall.esigning.service.SigningGatewayService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static se.sundsvall.esigning.TestUtil.createSigningEventNotification;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class SigningWebhookResourceTest {

	@MockitoBean
	private SigningGatewayService signingGatewayServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void handleProviderEvent() {
		final var municipalityId = "2281";
		final var provider = "comfact";
		final var notification = createSigningEventNotification();

		webTestClient.post()
			.uri("/" + municipalityId + "/e-signing/webhooks/" + provider)
			.bodyValue(notification)
			.exchange()
			.expectStatus().isOk();

		verify(signingGatewayServiceMock).handleProviderEvent(municipalityId, provider, notification);
		verifyNoMoreInteractions(signingGatewayServiceMock);
	}
}
