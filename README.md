# Payment Service

A robust Spring Boot microservice for payment processing in an e-commerce ecosystem. This service handles payment link generation using multiple payment gateways (Razorpay & Stripe) with a strategy pattern for gateway selection.

## 🚀 Features

- **Payment Link Generation**: Create payment links for orders via a single unified API
- **Multiple Payment Gateways**: Integrated with Razorpay and Stripe
- **Gateway Strategy Pattern**: Pluggable strategy to choose the best performing payment gateway
- **Environment Configuration**: Secure credential management via `.env.dev` and environment variables
- **Dotenv Integration**: Automatic loading of environment variables from `.env.dev` file
- **Extensible Architecture**: Interface-driven design for easy addition of new payment gateways

## 🛠️ Tech Stack

- **Java**: 17
- **Spring Boot**: 3.5.7
- **Razorpay Java SDK**: 1.4.8
- **Stripe Java SDK**: 30.2.0
- **Dotenv (java-dotenv)**: 5.2.2 (Environment variable management)
- **Lombok**: For reducing boilerplate code
- **Maven**: Build and dependency management
- **JUnit 5 & Mockito**: Testing framework
- **AssertJ**: Fluent assertion library

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed
- **Maven 3.6+** (or use the included Maven wrapper)
- **Razorpay Account**: API key ID and secret from [Razorpay Dashboard](https://dashboard.razorpay.com/)
- **Stripe Account** (optional): API key from [Stripe Dashboard](https://dashboard.stripe.com/)

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/kaustubh43/PaymentService.git
cd PaymentService
```

### 2. Configure Environment Variables

Create a `.env.dev` file in the project root:

```dotenv
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret
STRIPE_API_KEY=your_stripe_api_key
```

Or set them as system environment variables:

```bash
export RAZORPAY_KEY_ID=your_razorpay_key_id
export RAZORPAY_KEY_SECRET=your_razorpay_key_secret
export STRIPE_API_KEY=your_stripe_api_key
```

### 3. Build the Project

Using Maven wrapper (recommended):

```bash
./mvnw clean install
```

Or using Maven:

```bash
mvn clean install
```

### 4. Run the Application

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on **port 8080** (default Spring Boot port).

## 📡 API Endpoints

### Payment Endpoints

#### Initiate Payment
```http
POST /payment
Content-Type: application/json

{
  "amount": 1500.0,
  "orderId": "ORD-12345",
  "phoneNumber": "+919876543210",
  "name": "John Doe",
  "email": "john@example.com"
}
```

**Response**: `200 OK`
```
https://rzp.io/i/abc123
```

Returns a payment link URL that can be used to complete the payment flow.

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

Run specific test class:

```bash
./mvnw test -Dtest=PaymentServiceTest
```

The project includes comprehensive tests for:
- **Controllers** (`PaymentControllersTest`) — MockMvc integration tests
- **Services** (`PaymentServiceTest`) — Unit tests with mocks
- **Payment Gateways** (`RazorpayPaymentGatewayTest`, `StripePaymentGatewayTest`) — Gateway behavior tests
- **Strategy** (`PaymentGatewayChooserStrategyTest`) — Strategy pattern tests
- **Configurations** (`RazorpayConfigurationTest`, `DotenvEnvironmentPostProcessorTest`) — Configuration validation tests

## 📁 Project Structure

```
PaymentService/
├── src/
│   ├── main/
│   │   ├── java/org/ecommerce/paymentservice/
│   │   │   ├── configurations/   # Razorpay config & dotenv post-processor
│   │   │   ├── controllers/      # REST controllers
│   │   │   ├── dtos/             # Data Transfer Objects
│   │   │   ├── paymentgateway/   # Payment gateway implementations & strategy
│   │   │   └── services/         # Business logic
│   │   └── resources/
│   │       ├── application.properties
│   │       └── META-INF/
│   │           └── spring.factories
│   └── test/                     # Unit and integration tests
├── .github/
│   └── workflows/
│       └── ci.yml                # GitHub Actions CI pipeline
├── .mvn/wrapper/                 # Maven wrapper files
├── mvnw                          # Maven wrapper script (Unix)
├── mvnw.cmd                      # Maven wrapper script (Windows)
├── pom.xml                       # Maven dependencies
└── README.md
```

## 🏗️ Architecture

### Payment Gateway Strategy Pattern

The service uses the **Strategy Pattern** to select the best payment gateway at runtime:

```
PaymentControllers → PaymentService → PaymentGatewayChooserStrategy → IPaymentGateway
                                                                          ├── RazorpayPaymentGateway
                                                                          └── StripePaymentGateway
```

- `IPaymentGateway` — Common interface for all gateways
- `PaymentGatewayChooserStrategy` — Selects the best gateway (currently defaults to Razorpay)
- `RazorpayPaymentGateway` — Razorpay SDK integration
- `StripePaymentGateway` — Stripe SDK integration

### Environment Configuration

The `DotenvEnvironmentPostProcessor` automatically loads variables from `.env.dev` into the Spring Environment before application startup, enabling secure local development without committing secrets.

## 🔐 Security

- API keys are loaded from environment variables, never hardcoded
- Credential validation at startup with clear error messages (fail-fast)
- Key masking in log output to prevent accidental exposure
- `.env.dev` file should be added to `.gitignore`

## 🐛 Troubleshooting

### Common Issues

**Razorpay credentials missing:**
```
IllegalStateException: Razorpay credentials are missing or empty.
Set environment variables RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET
```
→ Ensure your `.env.dev` file exists or environment variables are set.

**Port 8080 already in use:**
```bash
# Change the port in application.properties
server.port=8081
```

**Payment link creation fails:**
- Verify your Razorpay/Stripe API keys are valid and active
- Check network connectivity to payment gateway APIs
- Review logs for detailed error messages

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is part of an e-commerce microservices ecosystem.

## 📧 Contact

**Author**: Kaustubh Ajgaonkar  
**Email**: kaustubhajgaonkar43@gmail.com  
**GitHub**: [@kaustubh43](https://github.com/kaustubh43)

## 🔄 CI/CD

This project uses GitHub Actions for continuous integration. The workflow includes:
- Building the project with Maven
- Running all tests on every push to `main`
- Java 17 with Maven caching for fast builds

---

**Note**: This is a microservice designed to work within a larger e-commerce ecosystem. Ensure payment gateway API credentials are properly configured for full functionality.

