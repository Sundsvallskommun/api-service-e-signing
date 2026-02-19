package se.sundsvall.esigning.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class DocumentTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Document.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var fileName = "fileName";
		final var registrationNumber = "registrationNumber";
		final var descriptiveName = "descriptiveName";
		final var bean = Document.builder()
			.withFileName(fileName)
			.withRegistrationNumber(registrationNumber)
			.withDescriptiveName(descriptiveName)
			.build();

		assertThat(bean).satisfies(b -> {
			assertThat(b.getFileName()).isEqualTo(fileName);
			assertThat(b.getRegistrationNumber()).isEqualTo(registrationNumber);
			assertThat(b.getDescriptiveName()).isEqualTo(descriptiveName);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Document.builder().build())
			.hasAllNullFieldsOrProperties();

		assertThat(new Document())
			.hasAllNullFieldsOrProperties();
	}
}
