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

class ComfactSignatoryTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ComfactSignatory.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var partyId = "550e8400-e29b-41d4-a716-446655440000";
		final var action = "declined";
		final var reason = "Not authorised";

		final var bean = ComfactSignatory.builder()
			.withPartyId(partyId)
			.withAction(action)
			.withReason(reason)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getAction()).isEqualTo(action);
		assertThat(bean.getReason()).isEqualTo(reason);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ComfactSignatory.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new ComfactSignatory()).hasAllNullFieldsOrProperties();
	}
}
