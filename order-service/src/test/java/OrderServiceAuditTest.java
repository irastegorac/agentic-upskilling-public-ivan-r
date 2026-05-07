import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceAuditTest {

    private InMemoryAuditLogger auditLogger;
    private StubCustomerRepository customerRepository;
    private OrderService orderService;

    private static final OrderItem ITEM = new OrderItem("prod-1", 2, 9.99);

    @BeforeEach
    void setUp() {
        auditLogger = new InMemoryAuditLogger();
        customerRepository = new StubCustomerRepository();
        orderService = new OrderService(customerRepository, auditLogger);
    }

    @Test
    void placeOrder_success_logsOneEntryWithCorrectFields() {
        Order order = orderService.placeOrder("c1", List.of(ITEM));

        List<AuditEntry> entries = auditLogger.getEntries();
        assertEquals(1, entries.size());
        AuditEntry entry = entries.get(0);
        assertEquals(AuditEntry.Operation.PLACE_ORDER, entry.getOperation());
        assertEquals(AuditEntry.Outcome.SUCCESS, entry.getOutcome());
        assertEquals("c1", entry.getCustomerId());
        assertEquals(order.getId(), entry.getOrderId());
        assertNull(entry.getErrorDetail());
        assertNotNull(entry.getTimestamp());
    }

    @Test
    void placeOrder_unknownCustomer_logsFailureEntry() {
        customerRepository.returnCustomer = false;

        assertThrows(OrderException.class, () -> orderService.placeOrder("c1", List.of(ITEM)));

        List<AuditEntry> entries = auditLogger.getEntries();
        assertEquals(1, entries.size());
        AuditEntry entry = entries.get(0);
        assertEquals(AuditEntry.Operation.PLACE_ORDER, entry.getOperation());
        assertEquals(AuditEntry.Outcome.FAILURE, entry.getOutcome());
        assertEquals("c1", entry.getCustomerId());
        assertNull(entry.getOrderId());
        assertTrue(entry.getErrorDetail().contains("Customer not found"));
    }

    @Test
    void placeOrder_emptyItems_logsFailureEntry() {
        assertThrows(OrderException.class, () -> orderService.placeOrder("c1", List.of()));

        AuditEntry entry = auditLogger.getEntries().get(0);
        assertEquals(AuditEntry.Operation.PLACE_ORDER, entry.getOperation());
        assertEquals(AuditEntry.Outcome.FAILURE, entry.getOutcome());
        assertTrue(entry.getErrorDetail().contains("at least one item"));
    }

    @Test
    void findById_success_logsEntry() {
        Order placed = orderService.placeOrder("c1", List.of(ITEM));
        orderService.findById(placed.getId());

        // entry 0 = PLACE_ORDER, entry 1 = FIND_ORDER
        AuditEntry entry = auditLogger.getEntries().get(1);
        assertEquals(AuditEntry.Operation.FIND_ORDER, entry.getOperation());
        assertEquals(AuditEntry.Outcome.SUCCESS, entry.getOutcome());
        assertEquals(placed.getId(), entry.getOrderId());
        assertNull(entry.getCustomerId());
    }

    @Test
    void findById_notFound_logsFailureEntry() {
        assertThrows(OrderException.class, () -> orderService.findById("nonexistent"));

        AuditEntry entry = auditLogger.getEntries().get(0);
        assertEquals(AuditEntry.Operation.FIND_ORDER, entry.getOperation());
        assertEquals(AuditEntry.Outcome.FAILURE, entry.getOutcome());
        assertEquals("nonexistent", entry.getOrderId());
        assertTrue(entry.getErrorDetail().contains("Order not found"));
    }

    @Test
    void cancelOrder_success_logsFindAndCancelEntries() {
        Order placed = orderService.placeOrder("c1", List.of(ITEM));
        orderService.cancelOrder(placed.getId());

        // entry 0 = PLACE_ORDER, entry 1 = FIND_ORDER (from findById delegation), entry 2 = CANCEL_ORDER
        List<AuditEntry> entries = auditLogger.getEntries();
        assertEquals(3, entries.size());
        AuditEntry cancelEntry = entries.get(2);
        assertEquals(AuditEntry.Operation.CANCEL_ORDER, cancelEntry.getOperation());
        assertEquals(AuditEntry.Outcome.SUCCESS, cancelEntry.getOutcome());
        assertEquals(placed.getId(), cancelEntry.getOrderId());
        assertNull(cancelEntry.getErrorDetail());
    }

    @Test
    void cancelOrder_notFound_producesTwoEntries_lastIsCancelFailure() {
        assertThrows(OrderException.class, () -> orderService.cancelOrder("nonexistent"));

        List<AuditEntry> entries = auditLogger.getEntries();
        assertEquals(2, entries.size());
        assertEquals(AuditEntry.Operation.FIND_ORDER,   entries.get(0).getOperation());
        assertEquals(AuditEntry.Outcome.FAILURE,        entries.get(0).getOutcome());
        assertEquals(AuditEntry.Operation.CANCEL_ORDER, entries.get(1).getOperation());
        assertEquals(AuditEntry.Outcome.FAILURE,        entries.get(1).getOutcome());
        assertTrue(entries.get(1).getErrorDetail().contains("Order not found"));
    }

    @Test
    void findByCustomer_withResults_logsSuccess() {
        orderService.placeOrder("c1", List.of(ITEM));
        orderService.findByCustomer("c1");

        AuditEntry entry = auditLogger.getEntries().get(1);
        assertEquals(AuditEntry.Operation.FIND_BY_CUSTOMER, entry.getOperation());
        assertEquals(AuditEntry.Outcome.SUCCESS, entry.getOutcome());
        assertEquals("c1", entry.getCustomerId());
    }

    @Test
    void findByCustomer_noResults_stillLogsSuccess() {
        orderService.findByCustomer("unknown");

        AuditEntry entry = auditLogger.getEntries().get(0);
        assertEquals(AuditEntry.Operation.FIND_BY_CUSTOMER, entry.getOperation());
        assertEquals(AuditEntry.Outcome.SUCCESS, entry.getOutcome());
        assertEquals("unknown", entry.getCustomerId());
    }

    @Test
    void multipleOperations_producesEntriesInOrder() {
        Order order = orderService.placeOrder("c1", List.of(ITEM));
        orderService.findById(order.getId());
        orderService.cancelOrder(order.getId());

        // place(1) + findById(1) + cancelOrder→findById(1) + cancelOrder(1) = 4
        List<AuditEntry> entries = auditLogger.getEntries();
        assertEquals(4, entries.size());
        assertEquals(AuditEntry.Operation.PLACE_ORDER,   entries.get(0).getOperation());
        assertEquals(AuditEntry.Operation.FIND_ORDER,    entries.get(1).getOperation());
        assertEquals(AuditEntry.Operation.FIND_ORDER,    entries.get(2).getOperation());
        assertEquals(AuditEntry.Operation.CANCEL_ORDER,  entries.get(3).getOperation());
        entries.forEach(e -> assertEquals(AuditEntry.Outcome.SUCCESS, e.getOutcome()));
    }

    private static class StubCustomerRepository implements CustomerRepository {
        boolean returnCustomer = true;

        @Override
        public Optional<Customer> findById(String customerId) {
            return returnCustomer ? Optional.of(new Customer(customerId)) : Optional.empty();
        }

        @Override
        public Customer save(Customer customer) {
            return customer;
        }
    }
}
