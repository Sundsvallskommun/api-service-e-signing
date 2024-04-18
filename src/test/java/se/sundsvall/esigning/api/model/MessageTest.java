package se.sundsvall.esigning.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class MessageTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Message.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var subject = "subject";
		final var body = "body";

		final var bean = Message.builder()
			.withSubject(subject)
			.withBody(body)
			.build();

		assertThat(bean).satisfies(b -> {
			assertThat(b.getBody()).isEqualTo(body);
			assertThat(b.getSubject()).isEqualTo(subject);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Message.builder().build())
			.hasAllNullFieldsOrProperties();

		assertThat(new Message())
			.hasAllNullFieldsOrProperties();
	}
}
