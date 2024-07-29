## Payment Processing System


## Description
This application consists of 3 different systems -
1. Payment Processing Service (PPS) - This service is responsible for processing the payment going through its entire lifecycle. It recieves the payment request from the user and validates the requedt on the basis of (ISO 8601, duplication checks, etc). After then the payment is sent to the Broker System for fraud check. Post this it is sent for clearing.
2. Broker Service (BS) - This service acts as a mediator bewtween PPS and FCS, which parses the request and transfer it to the further system.
3. Fraud Check Service (FCS) - This service identifies a fradulent payment request and reject or approve it on the basis of rules. This sends the payment status back to BS which in then shared with PPS to clear or halt the payment.

#### Tech Stack:
Java, Spring Boot, Rest APIs, H2Db(in memory), Maven, Jackson (for JSON and XML processing), SLF4J (for logging), JMS (Java Message Service),
JPA, ActiveMQ, etc.

## Pre-requisite
- Java 11 or higher
- Gradle/Maven
- Spring Boot 3.0.0 or higher
- A running instance of a JMS broker (e.g., ActiveMQ)
- An IDE like IntelliJ IDEA

## Steps to Setup
1. **Clone the repository:**
   ```sh
   cd <project-directory>
   git clone https://github.com/AnkitaArya/payment.git
   ```
2. **Build the project:**
   mvn clean install
3. **Configure application properties:**
   Update the application.properties file with the appropriate JMS broker details and other configurations.
4. **Run the application:**
   ```sh
   mvn spring-boot:run
   ```
## Features
- **Payment Processing:** The system processes payment requests and updates the payment status based on the results of fraud checks.
- **Fraud Detection:** The system performs fraud checks on payment requests to detect fraudulent transactions.
- **Payment Status Update:** The system updates the payment status based on the results of fraud checks.
- **Communication Modes:** This app supports multiple of modes of internal communication like messaging and network. It leverages Rest APIs or JMS for these communication modes.
- **Logging:** The system logs payment processing events and fraud detection results.
- **JMS Integration:** The system integrates with a JMS broker to send and receive messages for payment processing.
- **JSON and XML Processing:** The system processes JSON and XML messages for payment requests and fraud detection results.
- **Error Handling:** The system handles errors and exceptions gracefully to ensure smooth operation.

## Future Scope

## Project Documentation
- **Swagger Documentation:** This document provides detailed information about the various APIs provided by the Payment Fraud Detection System.

## Payment Request Properties

## Validation
- **Payment Request ID:** A unique identifier for the payment request.
- **Payment Amount:** The amount of the payment request.
- **Payment Currency:** The currency of the payment request.
- Request Validations: Uses javax.validation.constraints for validating incoming requests.
  Error Handling: Custom exceptions and error handling mechanisms are in place to manage invalid requests and processing errors.
