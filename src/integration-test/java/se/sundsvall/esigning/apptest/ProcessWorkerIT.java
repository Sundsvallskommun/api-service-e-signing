package se.sundsvall.esigning.apptest;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;

import org.junit.jupiter.api.Test;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.esigning.Application;

@WireMockAppTestSuite(files = "classpath:/ProcessWorkerIT/", classes = Application.class)
class ProcessWorkerIT extends AbstractAppTest {

	private static final String REQUEST_FILE = "request.json";

	@Test
	void test01_startProcess() {
		setupCall()
			.withServicePath("/e-signing/start")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(ACCEPTED)
			.withExpectedResponse("123")
			.sendRequestAndVerifyResponse();
	}
				
}
