package se.sundsvall.esigning.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static se.sundsvall.esigning.TestUtil.createMessage;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class SignatoryTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Signatory.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var email = "email";
		final var name = "name";
		final var organization = "organization";
		final var partyId = "partyId";
		final var notificationMessage = createMessage();

		final var bean = Signatory.builder()
			.withEmail(email)
			.withName(name)
			.withOrganization(organization)
			.withPartyId(partyId)
			.withNotificationMessage(notificationMessage)
			.build();

		assertThat(bean).satisfies(b -> {
			assertThat(b.getEmail()).isEqualTo(email);
			assertThat(b.getName()).isEqualTo(name);
			assertThat(b.getOrganization()).isEqualTo(organization);
			assertThat(b.getPartyId()).isEqualTo(partyId);
			assertThat(b.getNotificationMessage()).isEqualTo(notificationMessage);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Signatory.builder().build())
			.hasAllNullFieldsOrProperties();

		assertThat(new Signatory())
			.hasAllNullFieldsOrProperties();
	}
}
