package se.sundsvall.esigning.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static se.sundsvall.esigning.TestUtil.createDocument;
import static se.sundsvall.esigning.TestUtil.createInitiator;
import static se.sundsvall.esigning.TestUtil.createMessage;
import static se.sundsvall.esigning.TestUtil.createReminder;
import static se.sundsvall.esigning.TestUtil.createSignatory;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SigningRequestTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(SigningRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var expires = OffsetDateTime.now();
		final var language = "sv";
		final var callbackUrl = "callbackUrl";
		final var reminder = createReminder();
		final var signatories = Set.of(createSignatory());
		final var message = createMessage();
		final var initiator = createInitiator();
		final var document = createDocument();

		final var bean = SigningRequest.builder()
			.withExpires(expires)
			.withDocument(document)
			.withLanguage(language)
			.withCallbackUrl(callbackUrl)
			.withReminder(reminder)
			.withSignatories(signatories)
			.withNotificationMessage(message)
			.withInitiator(initiator)
			.build();

		assertThat(bean).satisfies(b -> {
			assertThat(b.getCallbackUrl()).isEqualTo(callbackUrl);
			assertThat(b.getExpires()).isEqualTo(expires);
			assertThat(b.getLanguage()).isEqualTo(language);
			assertThat(b.getReminder()).isEqualTo(reminder);
			assertThat(b.getSignatories()).isEqualTo(signatories);
			assertThat(b.getNotificationMessage()).isEqualTo(message);
			assertThat(b.getInitiator()).isEqualTo(initiator);
			assertThat(b.getDocument()).isEqualTo(document);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SigningRequest.builder().build())
			.hasAllNullFieldsOrProperties();

		assertThat(new SigningRequest())
			.hasAllNullFieldsOrProperties();
	}
}
