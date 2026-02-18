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

class InitiatorTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Initiator.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var partyId = "partyId";
		final var organization = "organization";
		final var name = "name";
		final var email = "email";
		final var bean = Initiator.builder()
			.withName(name)
			.withEmail(email)
			.withPartyId(partyId)
			.withOrganization(organization)
			.build();

		assertThat(bean).satisfies(b -> {
			assertThat(b.getPartyId()).isEqualTo(partyId);
			assertThat(b.getOrganization()).isEqualTo(organization);
			assertThat(b.getName()).isEqualTo(name);
			assertThat(b.getEmail()).isEqualTo(email);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Initiator.builder().build())
			.hasAllNullFieldsOrProperties();

		assertThat(new Initiator())
			.hasAllNullFieldsOrProperties();
	}
}
