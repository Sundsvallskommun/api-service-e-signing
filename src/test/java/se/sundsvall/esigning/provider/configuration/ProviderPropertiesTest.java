package se.sundsvall.esigning.provider.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.esigning.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
@ActiveProfiles("junit")
class ProviderPropertiesTest {

	@Autowired
	private ProviderProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.defaultProvider()).isEqualTo("comfact");
		assertThat(properties.byMunicipalityId()).containsEntry("2281", "comfact");
	}
}
