package se.sundsvall.esigning.apptest;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.esigning.Application;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * Verifies that the account key identifying the calling system is passed on to api-comfact-facade, which uses it to
 * pick the Comfact account the signing is created on. The account key is asserted in the Comfact facade stub's request
 * matchers, and AbstractAppTest fails the test when a stub is not called.
 */
@WireMockAppTestSuite(files = "classpath:/SigningGatewayResourceIT/", classes = Application.class)
class SigningGatewayResourceIT extends AbstractAppTest {

	private static final String PATH = "/2281/e-signing/signings";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_createSigningForwardsAccountKey() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withHeader(AUTHORIZATION, "Bearer " + tokenWithSubject("postportalservice"))
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Without a readable token there is no account key to forward, and the facade falls back to its default account.
	 */
	@Test
	void test02_createSigningWithoutBearerToken() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	private static String tokenWithSubject(final String subject) {
		return new PlainJWT(new JWTClaimsSet.Builder().subject(subject).build()).serialize();
	}
}
