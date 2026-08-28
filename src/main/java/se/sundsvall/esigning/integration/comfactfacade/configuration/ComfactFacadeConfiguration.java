package se.sundsvall.esigning.integration.comfactfacade.configuration;

import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Import(FeignConfiguration.class)
public class ComfactFacadeConfiguration {

	public static final String CLIENT_ID = "comfactfacade";
	static final String ACCOUNT_KEY_HEADER = "X-Account-Key";
	private static final String BEARER_PREFIX = "Bearer ";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final ClientRegistrationRepository clientRepository, final ComfactFacadeProperties comfactFacadeProperties) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
			.withRequestInterceptor(template -> Optional.ofNullable(accountKeyFromBearerToken())
				.ifPresent(accountKey -> template.header(ACCOUNT_KEY_HEADER, accountKey)))
			.withRequestTimeoutsInSeconds(comfactFacadeProperties.connectTimeout(), comfactFacadeProperties.readTimeout())
			.withRetryableOAuth2InterceptorForClientRegistration(clientRepository.findByRegistrationId(CLIENT_ID))
			.composeCustomizersToOne();
	}

	private static String accountKeyFromBearerToken() {
		if (!(RequestContextHolder.getRequestAttributes() instanceof final ServletRequestAttributes requestAttributes)) {
			return null;
		}

		final var authorization = requestAttributes.getRequest().getHeader(AUTHORIZATION);
		if (authorization == null || !authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
			return null;
		}

		try {
			return JWTParser.parse(authorization.substring(BEARER_PREFIX.length())).getJWTClaimsSet().getSubject();
		} catch (final ParseException e) {
			return null;
		}
	}
}
