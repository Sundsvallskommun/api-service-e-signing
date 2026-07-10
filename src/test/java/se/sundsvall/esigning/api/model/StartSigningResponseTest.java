package se.sundsvall.esigning.api.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.util.Map;
import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class StartSigningResponseTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> Map.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()), Map.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(StartSigningResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var providerCaseId = "1234567890";
		final var status = "INITIATED";
		final var provider = "comfact";
		final var signatoryUrls = Map.of("550e8400-e29b-41d4-a716-446655440000", "https://sign.example/abc");

		final var bean = StartSigningResponse.builder()
			.withProviderCaseId(providerCaseId)
			.withStatus(status)
			.withProvider(provider)
			.withSignatoryUrls(signatoryUrls)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getProviderCaseId()).isEqualTo(providerCaseId);
		assertThat(bean.getStatus()).isEqualTo(status);
		assertThat(bean.getProvider()).isEqualTo(provider);
		assertThat(bean.getSignatoryUrls()).isEqualTo(signatoryUrls);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(StartSigningResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new StartSigningResponse()).hasAllNullFieldsOrProperties();
	}
}
