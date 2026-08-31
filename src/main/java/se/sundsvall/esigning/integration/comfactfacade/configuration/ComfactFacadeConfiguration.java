package se.sundsvall.esigning.integration.comfactfacade.configuration;

import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
public class ComfactFacadeConfiguration {

	public static final String CLIENT_ID = "comfactfacade";
	static final String ACCOUNT_KEY_HEADER = "X-Account-Key";
	static final String JWT_ASSERTION_HEADER = "X-JWT-Assertion";
	private static final Logger LOG = LoggerFactory.getLogger(ComfactFacadeConfiguration.class);

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final ClientRegistrationRepository clientRepository, final ComfactFacadeProperties comfactFacadeProperties) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
			.withRequestInterceptor(template -> Optional.ofNullable(accountKeyFromJwtAssertion())
				.ifPresent(accountKey -> template.header(ACCOUNT_KEY_HEADER, accountKey)))
			.withRequestTimeoutsInSeconds(comfactFacadeProperties.connectTimeout(), comfactFacadeProperties.readTimeout())
			.withRetryableOAuth2InterceptorForClientRegistration(clientRepository.findByRegistrationId(CLIENT_ID))
			.composeCustomizersToOne();
	}

	private static String accountKeyFromJwtAssertion() {
		if (!(RequestContextHolder.getRequestAttributes() instanceof final ServletRequestAttributes requestAttributes)) {
			LOG.debug("No account key to forward: the call is not made from within a servlet request");
			return null;
		}

		final var assertion = requestAttributes.getRequest().getHeader(JWT_ASSERTION_HEADER);
		if (assertion == null) {
			LOG.debug("No account key to forward: the incoming request has no {} header", JWT_ASSERTION_HEADER);
			return null;
		}

		try {
			final var subject = JWTParser.parse(assertion).getJWTClaimsSet().getSubject();
			if (subject == null) {
				LOG.warn("No account key to forward: the {} header has no subject claim", JWT_ASSERTION_HEADER);
			}
			return subject;
		} catch (final ParseException e) {
			LOG.warn("No account key to forward: the {} header could not be parsed as a JWT: {}", JWT_ASSERTION_HEADER, e.getMessage());
			return null;
		}
	}
}
