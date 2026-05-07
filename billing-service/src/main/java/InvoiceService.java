import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.EnumMap;

public class InvoiceService {

    private final Map<String, Invoice> store = new HashMap<>();

    public InvoiceService() {}

    public Invoice create(String customerId, double amount) {
        return create(customerId, amount, LocalDate.now());
    }

    Invoice create(String customerId, double amount, LocalDate createdAt) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        if (amount <= 0) {
            throw new BillingException("amount must be positive");
        }
        String id = java.util.UUID.randomUUID().toString();
        Invoice invoice = new Invoice(id, customerId, amount, createdAt);
        store.put(id, invoice);
        return invoice;
    }

    public Invoice findById(String id) {
        Invoice invoice = store.get(id);
        if (invoice == null) {
            throw new BillingException("Invoice not found: " + id);
        }
        return invoice;
    }

    public List<Invoice> findAll() {
        return List.copyOf(store.values());
    }

    public Map<Invoice.InvoiceStatus, Long> summary() {
        return store.values().stream()
                .collect(Collectors.groupingBy(Invoice::getStatus, () -> new EnumMap<>(Invoice.InvoiceStatus.class), Collectors.counting()));
    }

    public List<Invoice> overdue(int days) {
        if (days <= 0) {
            throw new BillingException("days must be positive");
        }
        LocalDate cutoff = LocalDate.now().minusDays(days);
        return store.values().stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PENDING)
                .filter(i -> i.getCreatedAt().isBefore(cutoff))
                .collect(Collectors.toList());
    }

    public List<Invoice> findByCustomer(String customerId) {
        return store.values().stream()
                .filter(i -> i.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public Invoice markPaid(String id) {
        Invoice invoice = findById(id);
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        return invoice;
    }

    public Invoice cancel(String id) {
        Invoice invoice = findById(id);
        if (invoice.getStatus() != Invoice.InvoiceStatus.PENDING) {
            throw new BillingException("Only PENDING invoices can be cancelled");
        }
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        return invoice;
    }
}
