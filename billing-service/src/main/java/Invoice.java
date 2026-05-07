import java.time.LocalDate;

public class Invoice {

    private final String id;
    private final String customerId;
    private final double amount;
    private InvoiceStatus status;
    private final LocalDate createdAt;

    public Invoice(String id, String customerId, double amount) {
        this(id, customerId, amount, LocalDate.now());
    }

    Invoice(String id, String customerId, double amount, LocalDate createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.status = InvoiceStatus.PENDING;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public InvoiceStatus getStatus() { return status; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public enum InvoiceStatus {
        PENDING, PAID, CANCELLED
    }
}
