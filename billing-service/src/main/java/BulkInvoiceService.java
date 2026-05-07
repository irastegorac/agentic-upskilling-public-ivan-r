import java.util.List;
import java.util.Map;

public class BulkInvoiceService {

    private final InvoiceService invoiceService;

    public BulkInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public List<Invoice> createBulk(List<Map.Entry<String, Double>> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BillingException("requests must not be empty");
        }
        return requests.stream()
                .map(e -> invoiceService.create(e.getKey(), e.getValue()))
                .toList();
    }

    public List<Invoice> cancelAllForCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        List<Invoice> customerInvoices = invoiceService.findByCustomer(customerId);
        return customerInvoices.stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PENDING)
                .map(i -> invoiceService.cancel(i.getId()))
                .toList();
    }

    public List<Invoice> payAllForCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        return invoiceService.findByCustomer(customerId).stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PENDING)
                .map(i -> invoiceService.markPaid(i.getId()))
                .toList();
    }

    public double totalOwedByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        return invoiceService.findByCustomer(customerId).stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PENDING)
                .mapToDouble(Invoice::getAmount)
                .sum();
    }

    public double totalPaidByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        return invoiceService.findByCustomer(customerId).stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PAID)
                .mapToDouble(Invoice::getAmount)
                .sum();
    }

    public double totalCancelledByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        return invoiceService.findByCustomer(customerId).stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.CANCELLED)
                .mapToDouble(Invoice::getAmount)
                .sum();
    }

    public long countPendingByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        return invoiceService.findByCustomer(customerId).stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PENDING)
                .count();
    }

    public long countPaidByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BillingException("customerId must not be blank");
        }
        return invoiceService.findByCustomer(customerId).stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PAID)
                .count();
    }
}
