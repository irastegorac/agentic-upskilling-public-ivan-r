import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SubscriptionService {

    private final Map<String, Subscription> store = new HashMap<>();

    public SubscriptionService() {}

    public Subscription create(String customerId, String plan) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        if (plan == null || plan.isBlank()) {
            throw new BillingException("plan must not be blank");
        }
        String id = UUID.randomUUID().toString();
        Subscription subscription = new Subscription(id, customerId, plan);
        store.put(id, subscription);
        return subscription;
    }

    public Subscription findById(String id) {
        Subscription subscription = store.get(id);
        if (subscription == null) {
            throw new BillingException("Subscription not found: " + id);
        }
        return subscription;
    }

    public List<Subscription> findAll() {
        return List.copyOf(store.values());
    }

    public List<Subscription> findByCustomer(String customerId) {
        return store.values().stream()
                .filter(s -> s.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public Subscription cancel(String id) {
        Subscription subscription = findById(id);
        if (subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            throw new BillingException("Only ACTIVE subscriptions can be cancelled");
        }
        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        return subscription;
    }
}
