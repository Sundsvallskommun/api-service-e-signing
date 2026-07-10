package se.sundsvall.esigning.api;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.esigning.Application;
import se.sundsvall.esigning.integration.postportalservice.SigningEvent;
import se.sundsvall.esigning.service.SigningGatewayService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static se.sundsvall.esigning.TestUtil.createComfactEventNotification;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class ComfactWebhookResourceTest {

	@MockitoBean
	private SigningGatewayService signingGatewayServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void handleComfactEvent() {
		final var municipalityId = "2281";
		final var notification = createComfactEventNotification();

		webTestClient.post()
			.uri("/" + municipalityId + "/e-signing/webhooks/comfact")
			.bodyValue(notification)
			.exchange()
			.expectStatus().isOk();

		final var captor = ArgumentCaptor.forClass(SigningEvent.class);
		verify(signingGatewayServiceMock).relaySigningEvent(eq(municipalityId), captor.capture());
		verifyNoMoreInteractions(signingGatewayServiceMock);

		assertThat(captor.getValue()).satisfies(event -> {
			assertThat(event.customerReference()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
			assertThat(event.providerCaseId()).isEqualTo("1234567890");
			assertThat(event.provider()).isEqualTo("comfact");
			assertThat(event.eventType()).isEqualTo("CASE_COMPLETED");
			assertThat(event.status()).isEqualTo("SIGNED");
			assertThat(event.signedDocument()).isNotNull();
		});
	}
}
