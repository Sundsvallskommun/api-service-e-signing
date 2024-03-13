package se.sundsvall.esigning.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/e-signing")
@Tag(name = "eSigning", description = "E-signing")
class SigningResource {

	public SigningResource() {
	}
}
