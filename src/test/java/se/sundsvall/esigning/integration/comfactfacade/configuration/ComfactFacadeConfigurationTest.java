package se.sundsvall.esigning.integration.comfactfacade.configuration;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static se.sundsvall.esigning.integration.comfactfacade.configuration.ComfactFacadeConfiguration.ACCOUNT_KEY_HEADER;
import static se.sundsvall.esigning.integration.comfactfacade.configuration.ComfactFacadeConfiguration.CLIENT_ID;

@ExtendWith(MockitoExtension.class)
class ComfactFacadeConfigurationTest {

	@Mock
	private ClientRegistrationRepository clientRepositoryMock;

	@Mock
	private ClientRegistration clientRegistrationMock;

	@Mock
	private ComfactFacadeProperties propertiesMock;

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Captor
	private ArgumentCaptor<ErrorDecoder> errorDecoderCaptor;

	@Captor
	private ArgumentCaptor<RequestInterceptor> requestInterceptorCaptor;

	@InjectMocks
	private ComfactFacadeConfiguration configuration;

	@AfterEach
	void resetRequestContext() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	void testFeignBuilderCustomizer() {
		final var connectTimeout = 123;
		final var readTimeout = 321;

		when(propertiesMock.connectTimeout()).thenReturn(connectTimeout);
		when(propertiesMock.readTimeout()).thenReturn(readTimeout);
		when(clientRepositoryMock.findByRegistrationId(CLIENT_ID)).thenReturn(clientRegistrationMock);

		try (MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			configuration.feignBuilderCustomizer(clientRepositoryMock, propertiesMock);

			feignMultiCustomizerMock.verify(FeignMultiCustomizer::create);
		}

		verify(propertiesMock).connectTimeout();
		verify(propertiesMock).readTimeout();
		verify(clientRepositoryMock).findByRegistrationId(CLIENT_ID);
		verify(feignMultiCustomizerSpy).withErrorDecoder(errorDecoderCaptor.capture());
		verify(feignMultiCustomizerSpy).withRequestInterceptor(requestInterceptorCaptor.capture());
		verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(connectTimeout, readTimeout);
		verify(feignMultiCustomizerSpy).withRetryableOAuth2InterceptorForClientRegistration(clientRegistrationMock);
		verify(feignMultiCustomizerSpy).composeCustomizersToOne();

		assertThat(errorDecoderCaptor.getValue())
			.isInstanceOf(ProblemErrorDecoder.class)
			.hasFieldOrPropertyWithValue("integrationName", CLIENT_ID);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"Bearer ", "bearer "
	})
	void testAccountKeyHeaderIsSetFromBearerTokenSubject(final String bearerPrefix) {
		setUpRequestWithAuthorizationHeader(bearerPrefix + tokenWithSubject("postportalservice"));

		assertThat(applyRequestInterceptor().headers()).containsEntry(ACCOUNT_KEY_HEADER, List.of("postportalservice"));
	}

	@Test
	void testAccountKeyHeaderIsNotSetWhenThereIsNoRequestContext() {
		assertThat(applyRequestInterceptor().headers()).doesNotContainKey(ACCOUNT_KEY_HEADER);
	}

	@Test
	void testAccountKeyHeaderIsNotSetWhenThereIsNoAuthorizationHeader() {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

		assertThat(applyRequestInterceptor().headers()).doesNotContainKey(ACCOUNT_KEY_HEADER);
	}

	@ParameterizedTest
	@MethodSource("provideUnusableAuthorizationHeaders")
	void testAccountKeyHeaderIsNotSetWhenSubjectCannotBeRead(final String authorizationHeader) {
		setUpRequestWithAuthorizationHeader(authorizationHeader);

		assertThat(applyRequestInterceptor().headers()).doesNotContainKey(ACCOUNT_KEY_HEADER);
	}

	private static Stream<String> provideUnusableAuthorizationHeaders() {
		return Stream.of(
			"Basic dXNlcjpwYXNzd29yZA==",
			"Bearer MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
			"Bearer " + new PlainJWT(new JWTClaimsSet.Builder().build()).serialize());
	}

	private RequestTemplate applyRequestInterceptor() {
		try (MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			configuration.feignBuilderCustomizer(clientRepositoryMock, propertiesMock);
		}

		verify(feignMultiCustomizerSpy).withRequestInterceptor(requestInterceptorCaptor.capture());

		final var template = new RequestTemplate();
		requestInterceptorCaptor.getValue().apply(template);
		return template;
	}

	private static String tokenWithSubject(final String subject) {
		return new PlainJWT(new JWTClaimsSet.Builder().subject(subject).build()).serialize();
	}

	private static void setUpRequestWithAuthorizationHeader(final String authorizationHeader) {
		final var request = new MockHttpServletRequest();
		request.addHeader(AUTHORIZATION, authorizationHeader);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}
}
