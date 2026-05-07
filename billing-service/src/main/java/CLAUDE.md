Constructor injection is required — never field injection
- Custom exceptions must extend `BillingException`, not `RuntimeException` directly
- All service methods must validate inputs and throw `BillingException` with a descriptive message
- Controller methods must only delegate to the service — no business logic in controllers
- Test method names must follow `methodName_scenario_expectedResult`
## Service Behaviour
- Methods that operate on a filtered subset (e.g. cancel only PENDING)
  return silently for non-matching records — do not throw exceptions. 