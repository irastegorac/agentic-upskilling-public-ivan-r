import java.time.Instant;
import java.util.List;

public class Order {

    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Instant createdAt;

    public Order(String id, String customerId, List<OrderItem> items) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, CANCELLED
    }
}
