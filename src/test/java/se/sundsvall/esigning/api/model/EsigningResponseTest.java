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

class EsigningResponseTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(EsigningResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var processId = "1234";

		final var bean = EsigningResponse.builder()
			.withProcessId(processId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getProcessId()).isEqualTo(processId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EsigningResponse.builder().build())
			.hasAllNullFieldsOrProperties();

		assertThat(new EsigningResponse())
			.hasAllNullFieldsOrProperties();
	}
}
