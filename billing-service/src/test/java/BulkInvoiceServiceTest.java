import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BulkInvoiceServiceTest {

    private InvoiceService invoiceService;
    private BulkInvoiceService bulkService;

    @BeforeEach
    void setUp() {
        invoiceService = new InvoiceService();
        bulkService = new BulkInvoiceService(invoiceService);
    }

    @Test
    void createBulk_validRequests_createsAllInvoices() {
        List<Map.Entry<String, Double>> requests = List.of(
                Map.entry("cust-1", 100.0),
                Map.entry("cust-2", 200.0)
        );
        List<Invoice> created = bulkService.createBulk(requests);
        assertEquals(2, created.size());
        assertEquals(2, invoiceService.findAll().size());
    }

    @Test
    void createBulk_allCreatedWithPendingStatus() {
        List<Map.Entry<String, Double>> requests = List.of(
                Map.entry("cust-1", 50.0),
                Map.entry("cust-1", 75.0)
        );
        List<Invoice> created = bulkService.createBulk(requests);
        assertTrue(created.stream().allMatch(i -> i.getStatus() == Invoice.InvoiceStatus.PENDING));
    }

    @Test
    void createBulk_emptyList_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.createBulk(List.of()));
    }

    @Test
    void createBulk_nullList_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.createBulk(null));
    }

    @Test
    void createBulk_invalidAmount_throwsBillingException() {
        List<Map.Entry<String, Double>> requests = List.of(Map.entry("cust-1", -10.0));
        assertThrows(BillingException.class, () -> bulkService.createBulk(requests));
    }

    @Test
    void cancelAllForCustomer_pendingInvoices_cancelsAll() {
        invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);

        List<Invoice> cancelled = bulkService.cancelAllForCustomer("cust-1");

        assertEquals(2, cancelled.size());
        assertTrue(cancelled.stream().allMatch(i -> i.getStatus() == Invoice.InvoiceStatus.CANCELLED));
    }

    @Test
    void cancelAllForCustomer_mixedStatuses_cancelsOnlyPending() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        Invoice i2 = invoiceService.create("cust-1", 200.0);
        invoiceService.markPaid(i1.getId());

        List<Invoice> cancelled = bulkService.cancelAllForCustomer("cust-1");

        assertEquals(1, cancelled.size());
        assertEquals(i2.getId(), cancelled.get(0).getId());
    }

    @Test
    void cancelAllForCustomer_noInvoices_returnsEmptyList() {
        List<Invoice> cancelled = bulkService.cancelAllForCustomer("unknown-cust");
        assertTrue(cancelled.isEmpty());
    }

    @Test
    void cancelAllForCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.cancelAllForCustomer(""));
    }

    @Test
    void cancelAllForCustomer_doesNotAffectOtherCustomers() {
        invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-2", 200.0);

        bulkService.cancelAllForCustomer("cust-1");

        List<Invoice> cust2Invoices = invoiceService.findByCustomer("cust-2");
        assertEquals(Invoice.InvoiceStatus.PENDING, cust2Invoices.get(0).getStatus());
    }

    @Test
    void payAllForCustomer_pendingInvoices_paysAll() {
        invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);

        List<Invoice> paid = bulkService.payAllForCustomer("cust-1");

        assertEquals(2, paid.size());
        assertTrue(paid.stream().allMatch(i -> i.getStatus() == Invoice.InvoiceStatus.PAID));
    }

    @Test
    void payAllForCustomer_mixedStatuses_paysOnlyPending() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        Invoice i2 = invoiceService.create("cust-1", 200.0);
        invoiceService.cancel(i1.getId());

        List<Invoice> paid = bulkService.payAllForCustomer("cust-1");

        assertEquals(1, paid.size());
        assertEquals(i2.getId(), paid.get(0).getId());
    }

    @Test
    void payAllForCustomer_noInvoices_returnsEmptyList() {
        List<Invoice> paid = bulkService.payAllForCustomer("unknown-cust");
        assertTrue(paid.isEmpty());
    }

    @Test
    void payAllForCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.payAllForCustomer(""));
    }

    @Test
    void payAllForCustomer_doesNotAffectOtherCustomers() {
        invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-2", 200.0);

        bulkService.payAllForCustomer("cust-1");

        List<Invoice> cust2Invoices = invoiceService.findByCustomer("cust-2");
        assertEquals(Invoice.InvoiceStatus.PENDING, cust2Invoices.get(0).getStatus());
    }

    @Test
    void totalOwedByCustomer_pendingInvoices_returnsSum() {
        invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);

        assertEquals(300.0, bulkService.totalOwedByCustomer("cust-1"));
    }

    @Test
    void totalOwedByCustomer_excludesNonPending() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);
        invoiceService.markPaid(i1.getId());

        assertEquals(200.0, bulkService.totalOwedByCustomer("cust-1"));
    }

    @Test
    void totalOwedByCustomer_noInvoices_returnsZero() {
        assertEquals(0.0, bulkService.totalOwedByCustomer("unknown-cust"));
    }

    @Test
    void totalOwedByCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.totalOwedByCustomer(""));
    }

    @Test
    void totalPaidByCustomer_paidInvoices_returnsSum() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        Invoice i2 = invoiceService.create("cust-1", 200.0);
        invoiceService.markPaid(i1.getId());
        invoiceService.markPaid(i2.getId());

        assertEquals(300.0, bulkService.totalPaidByCustomer("cust-1"));
    }

    @Test
    void totalPaidByCustomer_excludesNonPaid() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);
        invoiceService.markPaid(i1.getId());

        assertEquals(100.0, bulkService.totalPaidByCustomer("cust-1"));
    }

    @Test
    void totalPaidByCustomer_noInvoices_returnsZero() {
        assertEquals(0.0, bulkService.totalPaidByCustomer("unknown-cust"));
    }

    @Test
    void totalPaidByCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.totalPaidByCustomer(""));
    }

    @Test
    void totalCancelledByCustomer_cancelledInvoices_returnsSum() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        Invoice i2 = invoiceService.create("cust-1", 200.0);
        invoiceService.cancel(i1.getId());
        invoiceService.cancel(i2.getId());

        assertEquals(300.0, bulkService.totalCancelledByCustomer("cust-1"));
    }

    @Test
    void totalCancelledByCustomer_excludesNonCancelled() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);
        invoiceService.cancel(i1.getId());

        assertEquals(100.0, bulkService.totalCancelledByCustomer("cust-1"));
    }

    @Test
    void totalCancelledByCustomer_noInvoices_returnsZero() {
        assertEquals(0.0, bulkService.totalCancelledByCustomer("unknown-cust"));
    }

    @Test
    void totalCancelledByCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.totalCancelledByCustomer(""));
    }

    @Test
    void countPendingByCustomer_pendingInvoices_returnsCount() {
        invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);

        assertEquals(2L, bulkService.countPendingByCustomer("cust-1"));
    }

    @Test
    void countPendingByCustomer_excludesNonPending() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);
        invoiceService.markPaid(i1.getId());

        assertEquals(1L, bulkService.countPendingByCustomer("cust-1"));
    }

    @Test
    void countPendingByCustomer_noInvoices_returnsZero() {
        assertEquals(0L, bulkService.countPendingByCustomer("unknown-cust"));
    }

    @Test
    void countPendingByCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.countPendingByCustomer(""));
    }

    @Test
    void countPaidByCustomer_paidInvoices_returnsCount() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        Invoice i2 = invoiceService.create("cust-1", 200.0);
        invoiceService.markPaid(i1.getId());
        invoiceService.markPaid(i2.getId());

        assertEquals(2L, bulkService.countPaidByCustomer("cust-1"));
    }

    @Test
    void countPaidByCustomer_excludesNonPaid() {
        Invoice i1 = invoiceService.create("cust-1", 100.0);
        invoiceService.create("cust-1", 200.0);
        invoiceService.markPaid(i1.getId());

        assertEquals(1L, bulkService.countPaidByCustomer("cust-1"));
    }

    @Test
    void countPaidByCustomer_noInvoices_returnsZero() {
        assertEquals(0L, bulkService.countPaidByCustomer("unknown-cust"));
    }

    @Test
    void countPaidByCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> bulkService.countPaidByCustomer(""));
    }
}
