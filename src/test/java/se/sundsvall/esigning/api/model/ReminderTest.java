package se.sundsvall.esigning.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static se.sundsvall.esigning.TestUtil.createMessage;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReminderTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(Reminder.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var intervalInHours = 24;
		final var message = createMessage();
		final var startDateTime = OffsetDateTime.now().plusDays(2);

		final var bean = Reminder.builder()
			.withIntervalInHours(intervalInHours)
			.withMessage(message)
			.withStartDateTime(startDateTime)
			.build();

		assertThat(bean).satisfies(b -> {
			assertThat(b.getStartDateTime()).isEqualTo(startDateTime);
			assertThat(b.getIntervalInHours()).isEqualTo(intervalInHours);
			assertThat(b.getMessage()).isEqualTo(message);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Reminder.builder().build())
			.hasAllNullFieldsOrPropertiesExcept("intervalInHours");

		assertThat(new Reminder())
			.hasAllNullFieldsOrPropertiesExcept("intervalInHours");
	}
}
