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

class SigningEventNotificationTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(SigningEventNotification.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var providerCaseId = "1234567890";
		final var eventType = "signingInstanceCompleted";
		final var occurredAt = OffsetDateTime.now();

		final var bean = SigningEventNotification.builder()
			.withProviderCaseId(providerCaseId)
			.withEventType(eventType)
			.withOccurredAt(occurredAt)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getProviderCaseId()).isEqualTo(providerCaseId);
		assertThat(bean.getEventType()).isEqualTo(eventType);
		assertThat(bean.getOccurredAt()).isEqualTo(occurredAt);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SigningEventNotification.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SigningEventNotification()).hasAllNullFieldsOrProperties();
	}
}
