import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTest {

    private SubscriptionService service;

    @BeforeEach
    void setUp() {
        service = new SubscriptionService();
    }

    @Test
    void findAll_multipleSubscriptions_returnsAll() {
        service.create("cust-1", "BASIC");
        service.create("cust-2", "PRO");
        assertEquals(2, service.findAll().size());
    }

    @Test
    void create_validInput_returnsSubscriptionWithActiveStatus() {
        Subscription subscription = service.create("cust-1", "BASIC");
        assertNotNull(subscription.getId());
        assertEquals(Subscription.SubscriptionStatus.ACTIVE, subscription.getStatus());
    }

    @Test
    void create_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.create("", "BASIC"));
    }

    @Test
    void create_blankPlan_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.create("cust-1", ""));
    }

    @Test
    void findById_existingSubscription_returnsSubscription() {
        Subscription created = service.create("cust-1", "PRO");
        Subscription found = service.findById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void findById_unknownId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.findById("unknown-id"));
    }

    @Test
    void findByCustomer_multipleSubscriptions_returnsOnlyMatching() {
        service.create("cust-1", "BASIC");
        service.create("cust-1", "PRO");
        service.create("cust-2", "BASIC");
        assertEquals(2, service.findByCustomer("cust-1").size());
    }

    @Test
    void cancel_activeSubscription_updatesToCancelled() {
        Subscription subscription = service.create("cust-1", "BASIC");
        Subscription cancelled = service.cancel(subscription.getId());
        assertEquals(Subscription.SubscriptionStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void cancel_alreadyCancelledSubscription_throwsBillingException() {
        Subscription subscription = service.create("cust-1", "BASIC");
        service.cancel(subscription.getId());
        assertThrows(BillingException.class, () -> service.cancel(subscription.getId()));
    }
}
