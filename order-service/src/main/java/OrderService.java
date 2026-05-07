import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private final CustomerRepository customerRepository;
    private final AuditLogger auditLogger;
    private final Map<String, Order> orders = new HashMap<>();

    public OrderService(CustomerRepository customerRepository, AuditLogger auditLogger) {
        this.customerRepository = customerRepository;
        this.auditLogger = auditLogger;
    }

    public Order placeOrder(String customerId, List<OrderItem> items) {
        try {
            customerRepository.findById(customerId)
                    .orElseThrow(() -> new OrderException("Customer not found: " + customerId));
            if (items == null || items.isEmpty()) {
                throw new OrderException("Order must contain at least one item");
            }
            String orderId = java.util.UUID.randomUUID().toString();
            Order order = new Order(orderId, customerId, new ArrayList<>(items));
            orders.put(orderId, order);
            auditLogger.log(entry(AuditEntry.Operation.PLACE_ORDER, orderId, customerId, AuditEntry.Outcome.SUCCESS, null));
            return order;
        } catch (Exception e) {
            auditLogger.log(entry(AuditEntry.Operation.PLACE_ORDER, null, customerId, AuditEntry.Outcome.FAILURE, e.getMessage()));
            throw e;
        }
    }

    public Order findById(String orderId) {
        try {
            Order order = orders.get(orderId);
            if (order == null) {
                throw new OrderException("Order not found: " + orderId);
            }
            auditLogger.log(entry(AuditEntry.Operation.FIND_ORDER, orderId, null, AuditEntry.Outcome.SUCCESS, null));
            return order;
        } catch (Exception e) {
            auditLogger.log(entry(AuditEntry.Operation.FIND_ORDER, orderId, null, AuditEntry.Outcome.FAILURE, e.getMessage()));
            throw e;
        }
    }

    public List<Order> findByCustomer(String customerId) {
        try {
            List<Order> result = orders.values().stream()
                    .filter(o -> o.getCustomerId().equals(customerId))
                    .toList();
            auditLogger.log(entry(AuditEntry.Operation.FIND_BY_CUSTOMER, null, customerId, AuditEntry.Outcome.SUCCESS, null));
            return result;
        } catch (Exception e) {
            auditLogger.log(entry(AuditEntry.Operation.FIND_BY_CUSTOMER, null, customerId, AuditEntry.Outcome.FAILURE, e.getMessage()));
            throw e;
        }
    }

    public Order cancelOrder(String orderId) {
        try {
            Order order = findById(orderId);
            order.setStatus(Order.OrderStatus.CANCELLED);
            auditLogger.log(entry(AuditEntry.Operation.CANCEL_ORDER, orderId, null, AuditEntry.Outcome.SUCCESS, null));
            return order;
        } catch (Exception e) {
            auditLogger.log(entry(AuditEntry.Operation.CANCEL_ORDER, orderId, null, AuditEntry.Outcome.FAILURE, e.getMessage()));
            throw e;
        }
    }

    private AuditEntry entry(AuditEntry.Operation op, String orderId, String customerId,
                              AuditEntry.Outcome outcome, String errorDetail) {
        return new AuditEntry(Instant.now(), op, orderId, customerId, outcome, errorDetail);
    }
}
