public class Subscription {

    private final String id;
    private final String customerId;
    private final String plan;
    private SubscriptionStatus status;

    public Subscription(String id, String customerId, String plan) {
        this.id = id;
        this.customerId = customerId;
        this.plan = plan;
        this.status = SubscriptionStatus.ACTIVE;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getPlan() { return plan; }
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public enum SubscriptionStatus {
        ACTIVE, CANCELLED
    }
}
