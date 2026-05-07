---
name: new-service
description: Scaffold a new Java service class with its controller and JUnit 5 test class,
  following the patterns established in InvoiceService, InvoiceController, and InvoiceServiceTest
  in the billing-service project. Use when the user asks to create a new service, add a service,
  generate boilerplate, scaffold a new endpoint, or create a new CRUD class in billing-service.
---

Scaffold a new service for the billing-service project.

## Steps

1. Read InvoiceService.java, InvoiceController.java, and InvoiceServiceTest.java to understand the established patterns.
2. Create {Name}.java — entity class with final fields, a status enum, and getters.
3. Create {Name}Service.java — in-memory service with a HashMap store, constructor injection only, input validation that throws BillingException, and CRUD operations.
4. Create {Name}Controller.java — thin delegation layer; no business logic; constructor injection only.
5. Create {Name}ServiceTest.java — JUnit 5 tests using @BeforeEach setUp, naming convention methodName_scenario_expectedResult, covering happy paths and all exception cases.
6. Run the tests to confirm they pass before reporting back.

## Constraints

- Always use constructor injection — never field injection.
- All custom exceptions must extend BillingException, not RuntimeException directly.
- All service methods must validate inputs and throw BillingException with a descriptive message.
- Controller methods must only delegate to the service — no business logic in controllers.
- Do not add any external dependencies beyond what is already in the project.

## Patterns

Constructor injection (required):
```java
public class PaymentService {
    private final InvoiceService invoiceService;

    public PaymentService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
}
```

BillingException usage (required):
```java
if (customerId == null || customerId.isBlank()) {
    throw new BillingException("customerId must not be blank");
}
```

Test naming convention (required):
```java
@Test
void findById_existingId_returnsPayment() { ... }

@Test
void findById_unknownId_throwsBillingException() { ... }
```

## Example

Input: "Create a PaymentService for managing payments"

Output:
- Payment.java — entity with id, customerId, amount, PaymentStatus enum (PENDING, COMPLETED, FAILED)
- PaymentService.java — create, findById, findByCustomer, complete, fail methods
- PaymentController.java — delegates to PaymentService
- PaymentServiceTest.java — tests for each method following methodName_scenario_expectedResult
