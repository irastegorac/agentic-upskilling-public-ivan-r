import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentService {

    private final Map<String, Payment> store = new HashMap<>();

    public PaymentService() {}

    public Payment create(String customerId, double amount) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        if (amount <= 0) {
            throw new BillingException("amount must be positive");
        }
        String id = java.util.UUID.randomUUID().toString();
        Payment payment = new Payment(id, customerId, amount);
        store.put(id, payment);
        return payment;
    }

    public Payment findById(String id) {
        Payment payment = store.get(id);
        if (payment == null) {
            throw new BillingException("Payment not found: " + id);
        }
        return payment;
    }

    public List<Payment> findByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        return store.values().stream()
                .filter(p -> p.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public Payment complete(String id) {
        Payment payment = findById(id);
        if (payment.getStatus() == Payment.PaymentStatus.PENDING) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
        }
        return payment;
    }
}
