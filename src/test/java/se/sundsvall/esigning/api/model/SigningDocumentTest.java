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

class SigningDocumentTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(SigningDocument.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var name = "name";
		final var fileName = "document.pdf";
		final var mimeType = "application/pdf";
		final var content = "dGVzdA==";

		final var bean = SigningDocument.builder()
			.withName(name)
			.withFileName(fileName)
			.withMimeType(mimeType)
			.withContent(content)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getName()).isEqualTo(name);
		assertThat(bean.getFileName()).isEqualTo(fileName);
		assertThat(bean.getMimeType()).isEqualTo(mimeType);
		assertThat(bean.getContent()).isEqualTo(content);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SigningDocument.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SigningDocument()).hasAllNullFieldsOrProperties();
	}
}
