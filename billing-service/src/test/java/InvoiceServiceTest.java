import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceServiceTest {

    private InvoiceService service;

    @BeforeEach
    void setUp() {
        service = new InvoiceService();
    }

    @Test
    void findAll_multipleInvoices_returnsAll() {
        service.create("cust-1", 100.0);
        service.create("cust-2", 200.0);
        assertEquals(2, service.findAll().size());
    }

    @Test
    void create_validInput_returnsInvoiceWithPendingStatus() {
        Invoice invoice = service.create("cust-1", 100.0);
        assertNotNull(invoice.getId());
        assertEquals(Invoice.InvoiceStatus.PENDING, invoice.getStatus());
    }

    @Test
    void create_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.create("", 100.0));
    }

    @Test
    void markPaid_existingInvoice_updatesStatus() {
        Invoice invoice = service.create("cust-1", 50.0);
        Invoice paid = service.markPaid(invoice.getId());
        assertEquals(Invoice.InvoiceStatus.PAID, paid.getStatus());
    }

    @Test
    void cancel_pendingInvoice_updatesToCancelled() {
        Invoice invoice = service.create("cust-1", 75.0);
        Invoice cancelled = service.cancel(invoice.getId());
        assertEquals(Invoice.InvoiceStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void cancel_paidInvoice_throwsBillingException() {
        Invoice invoice = service.create("cust-1", 75.0);
        service.markPaid(invoice.getId());
        assertThrows(BillingException.class, () -> service.cancel(invoice.getId()));
    }

    @Test
    void overdue_pendingInvoiceOlderThanDays_returnsIt() {
        service.create("cust-1", 100.0, java.time.LocalDate.now().minusDays(40));
        assertEquals(1, service.overdue(30).size());
    }

    @Test
    void overdue_pendingInvoiceNotOldEnough_excludesIt() {
        service.create("cust-1", 100.0, java.time.LocalDate.now().minusDays(10));
        assertEquals(0, service.overdue(30).size());
    }

    @Test
    void overdue_paidInvoiceOlderThanDays_excludesIt() {
        Invoice invoice = service.create("cust-1", 100.0, java.time.LocalDate.now().minusDays(40));
        service.markPaid(invoice.getId());
        assertEquals(0, service.overdue(30).size());
    }

    @Test
    void overdue_zeroDays_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.overdue(0));
    }

    @Test
    void summary_mixedStatuses_returnsCorrectCounts() {
        Invoice i1 = service.create("cust-1", 100.0);
        Invoice i2 = service.create("cust-2", 200.0);
        Invoice i3 = service.create("cust-3", 300.0);
        service.markPaid(i1.getId());
        service.cancel(i2.getId());

        var result = service.summary();
        assertEquals(1L, result.get(Invoice.InvoiceStatus.PENDING));
        assertEquals(1L, result.get(Invoice.InvoiceStatus.PAID));
        assertEquals(1L, result.get(Invoice.InvoiceStatus.CANCELLED));
    }

    @Test
    void summary_emptyStore_returnsEmptyMap() {
        assertTrue(service.summary().isEmpty());
    }
}
