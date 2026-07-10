package se.sundsvall.esigning.api.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.OffsetDateTime;
import java.util.Random;
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
import static se.sundsvall.esigning.TestUtil.createSigningDocument;

class SigningInstanceResponseTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(SigningInstanceResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var providerCaseId = "1234567890";
		final var status = "SIGNED";
		final var provider = "comfact";
		final var expires = OffsetDateTime.now().plusDays(30);
		final var signedDocument = createSigningDocument();

		final var bean = SigningInstanceResponse.builder()
			.withProviderCaseId(providerCaseId)
			.withStatus(status)
			.withProvider(provider)
			.withExpires(expires)
			.withSignedDocument(signedDocument)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getProviderCaseId()).isEqualTo(providerCaseId);
		assertThat(bean.getStatus()).isEqualTo(status);
		assertThat(bean.getProvider()).isEqualTo(provider);
		assertThat(bean.getExpires()).isEqualTo(expires);
		assertThat(bean.getSignedDocument()).isEqualTo(signedDocument);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SigningInstanceResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SigningInstanceResponse()).hasAllNullFieldsOrProperties();
	}
}
