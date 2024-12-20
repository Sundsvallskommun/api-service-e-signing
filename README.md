# eSigning

_The service acts as the starting point of the process carried out to sign a document electronically._

_A prerequisite for being able to initiate a signing process is that the document to be signed has been stored in the document service. In the call to this service's API, the reference to the document to be signed is passed, which is later used by the signing process to retrieve and update the actual document stored in the document service._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **Maven**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Sundsvallskommun/api-service-e-signing.git
   cd api-service-e-signing
   ```
2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   - Using Maven:

     ```bash
     mvn spring-boot:run
     ```
   - Using Gradle:

     ```bash
     gradle bootRun
     ```

## Dependencies

This microservice depends on the following services:

- **Document**
  - **Purpose:** The service is used to verify that a signing process can be initiated for the document matching the provided id
  - **Repository:** [https://github.com/Sundsvallskommun/api-service-document](https://github.com/Sundsvallskommun/api-service-document)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.
- **E-signing Process**
  - **Purpose:** Service handling the process of signing a document, to which a start request is made from this service
  - **Repository:** [https://github.com/Sundsvallskommun/pw-e-signing](https://github.com/Sundsvallskommun/pw-e-signing)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Alternatively, see the `openapi.yml` file located in directory `/src/main/resources` for the OpenAPI specification.

## Usage

### API Endpoints

See [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X 'POST' 'http://localhost:8080/2281/e-signing/start' -H 'Content-Type: application/json' -d 
'{
	"document": {
		"fileName": "some-document.pdf",
		"descriptiveName": "Some description",
		"registrationNumber": "Some document registration number"
	},
	"notificationMessage": {
		"subject": "Document to sign",
		"body": "Dear person, please sign the document."
	},
	"initiator": {
		"name": "Some name",
		"partyId": "440e8400-e29b-41d4-a716-446655440000",
		"email": "some.email@somedomain.com"
	},
	"signatories": [
		{
			"name": "Some name",
			"partyId": "550e8400-e29b-41d4-a716-446655440000",
			"email": "some.email@somedomain.com"
		}
	],
	"expires": "2024-12-31T23:59:59Z",
	"language": "en-US"
}'
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

  ```yaml
  server:
    port: 8080
  ```
- **Database Settings:**

  No database is used by this microservice

- **External Service URLs:**

  ```yaml
  config:
    document:
      base-url: http://dependency_service_url
      token-url: http://token_url
      client-id: some-client-id
      client-secret: some-client-secret
      connect-timeout: <seconds until connect timeout>
      read-timeout: <seconds until read timeout>
    esigningprocess:
      base-url: http://dependency_service_url
      token-url: http://token_url
      client-id: some-client-id
      client-secret: some-client-secret
      connect-timeout: <seconds until connect timeout>
      read-timeout: <seconds until read timeout>
  ```

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-e-signing&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-e-signing)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-e-signing&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-e-signing)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-e-signing&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-e-signing)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-e-signing&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-e-signing)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-e-signing&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-e-signing)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-e-signing&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-e-signing)

## 

&copy; 2023 Sundsvalls kommun
