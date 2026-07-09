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
import static se.sundsvall.esigning.TestUtil.createComfactSignatory;
import static se.sundsvall.esigning.TestUtil.createSigningDocument;

class ComfactEventNotificationTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(ComfactEventNotification.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var eventTrigger = "signingInstanceCompleted";
		final var signingInstanceId = "1234567890";
		final var customerReference = "550e8400-e29b-41d4-a716-446655440000";
		final var statusCode = "completed";
		final var expires = OffsetDateTime.now().plusDays(30);
		final var signatory = createComfactSignatory();
		final var signedDocument = createSigningDocument();
		final var timestamp = OffsetDateTime.now();

		final var bean = ComfactEventNotification.builder()
			.withEventTrigger(eventTrigger)
			.withSigningInstanceId(signingInstanceId)
			.withCustomerReference(customerReference)
			.withStatusCode(statusCode)
			.withExpires(expires)
			.withSignatory(signatory)
			.withSignedDocument(signedDocument)
			.withTimestamp(timestamp)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getEventTrigger()).isEqualTo(eventTrigger);
		assertThat(bean.getSigningInstanceId()).isEqualTo(signingInstanceId);
		assertThat(bean.getCustomerReference()).isEqualTo(customerReference);
		assertThat(bean.getStatusCode()).isEqualTo(statusCode);
		assertThat(bean.getExpires()).isEqualTo(expires);
		assertThat(bean.getSignatory()).isEqualTo(signatory);
		assertThat(bean.getSignedDocument()).isEqualTo(signedDocument);
		assertThat(bean.getTimestamp()).isEqualTo(timestamp);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ComfactEventNotification.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new ComfactEventNotification()).hasAllNullFieldsOrProperties();
	}
}
