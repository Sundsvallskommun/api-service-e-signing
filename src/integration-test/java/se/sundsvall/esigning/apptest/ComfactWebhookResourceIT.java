package se.sundsvall.esigning.apptest;

import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.esigning.Application;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@WireMockAppTestSuite(files = "classpath:/ComfactWebhookResourceIT/", classes = Application.class)
class ComfactWebhookResourceIT extends AbstractAppTest {

	private static final String PATH = "/2281/e-signing/webhooks/comfact";
	private static final String REQUEST_FILE = "request.json";

	/**
	 * A completed Comfact event forwarded from api-comfact-facade is normalized to a provider-neutral SigningEvent and
	 * relayed to Postportalservice. The mapped event is asserted in the Postportalservice stub's body patterns, and
	 * AbstractAppTest verifies the stub was actually called.
	 */
	@Test
	void test01_relayCompletedEvent() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A Comfact event whose customer reference is not a Postportalen message id is acknowledged without being relayed,
	 * so the provider stops redelivering it. No Postportalservice stub is set up - a relay attempt would show up as an
	 * unmatched request and fail the test.
	 */
	@Test
	void test02_acknowledgeEventWithUnroutableCustomerReference() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

}
