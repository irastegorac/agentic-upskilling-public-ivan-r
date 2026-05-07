public class Payment {

    private final String id;
    private final String customerId;
    private final double amount;
    private PaymentStatus status;

    public Payment(String id, String customerId, double amount) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public enum PaymentStatus {
        PENDING, COMPLETED
    }
}
