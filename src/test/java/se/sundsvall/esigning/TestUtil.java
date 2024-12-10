package se.sundsvall.esigning;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import se.sundsvall.esigning.api.model.Document;
import se.sundsvall.esigning.api.model.EsigningResponse;
import se.sundsvall.esigning.api.model.Initiator;
import se.sundsvall.esigning.api.model.Message;
import se.sundsvall.esigning.api.model.Reminder;
import se.sundsvall.esigning.api.model.Signatory;
import se.sundsvall.esigning.api.model.SigningRequest;

public final class TestUtil {

	private TestUtil() {}

	public static Initiator createInitiator(final Consumer<Initiator> modifier) {
		final var bean = Initiator.builder()
			.withEmail("email@email.com")
			.withName("name")
			.withOrganization("organization")
			.withPartyId(UUID.randomUUID().toString())
			.build();

		Optional.ofNullable(modifier).ifPresent(m -> m.accept(bean));

		return bean;
	}

	public static Initiator createInitiator() {
		return createInitiator(null);
	}

	public static Message createMessage(final Consumer<Message> modifier) {
		final var bean = Message.builder()
			.withBody("body")
			.withSubject("subject")
			.build();

		Optional.ofNullable(modifier).ifPresent(m -> m.accept(bean));

		return bean;
	}

	public static Message createMessage() {
		return createMessage(null);
	}

	public static Reminder createReminder(final Consumer<Reminder> modifier) {
		final var bean = Reminder.builder()
			.withMessage(createMessage())
			.withStartDateTime(OffsetDateTime.now().plusDays(1))
			.withIntervalInHours(24)
			.build();

		Optional.ofNullable(modifier).ifPresent(m -> m.accept(bean));

		return bean;
	}

	public static Reminder createReminder() {
		return createReminder(null);
	}

	public static Signatory createSignatory(final Consumer<Signatory> modifier) {
		final var bean = Signatory.builder()
			.withNotificationMessage(createMessage())
			.withPartyId(UUID.randomUUID().toString())
			.withEmail("email@email.com")
			.withName("name")
			.withOrganization("organization")
			.build();

		Optional.ofNullable(modifier).ifPresent(m -> m.accept(bean));

		return bean;
	}

	public static Signatory createSignatory() {
		return createSignatory(null);
	}

	public static Document createDocument() {
		return createDocument(null);
	}

	public static Document createDocument(final Consumer<Document> modifier) {
		final var bean = Document.builder()
			.withFileName("test.pdf")
			.withRegistrationNumber("registrationNumber")
			.withDescriptiveName("descriptiveName")
			.build();

		Optional.ofNullable(modifier).ifPresent(m -> m.accept(bean));

		return bean;
	}

	public static SigningRequest createSigningRequest(final Consumer<SigningRequest> modifier) {
		final var bean = SigningRequest.builder()
			.withExpires(OffsetDateTime.now().plusDays(1))
			.withLanguage("sv-SE")
			.withCallbackUrl("callbackUrl")
			.withNotificationMessage(createMessage())
			.withInitiator(createInitiator())
			.withReminder(createReminder())
			.withSignatories(Set.of(createSignatory()))
			.withDocument(createDocument())
			.build();

		Optional.ofNullable(modifier).ifPresent(m -> m.accept(bean));

		return bean;
	}

	public static SigningRequest createSigningRequest() {
		return createSigningRequest(null);
	}

	public static EsigningResponse createEsigningResponse(final Consumer<EsigningResponse> modifier) {
		final var bean = EsigningResponse.builder()
			.withProcessId("1234")
			.build();

		Optional.ofNullable(modifier).ifPresent(m -> m.accept(bean));

		return bean;
	}

	public static EsigningResponse createEsigningResponse() {
		return createEsigningResponse(null);
	}

}
