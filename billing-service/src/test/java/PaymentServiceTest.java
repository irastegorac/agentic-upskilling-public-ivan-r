import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService service;

    @BeforeEach
    void setUp() {
        service = new PaymentService();
    }

    @Test
    void create_validInput_returnsPaymentWithPendingStatus() {
        Payment payment = service.create("cust-1", 100.0);
        assertNotNull(payment.getId());
        assertEquals("cust-1", payment.getCustomerId());
        assertEquals(100.0, payment.getAmount());
        assertEquals(Payment.PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void create_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.create("", 100.0));
    }

    @Test
    void create_nullCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.create(null, 100.0));
    }

    @Test
    void create_negativeAmount_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.create("cust-1", -50.0));
    }

    @Test
    void create_zeroAmount_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.create("cust-1", 0.0));
    }

    @Test
    void findById_existingId_returnsPayment() {
        Payment created = service.create("cust-1", 200.0);
        Payment found = service.findById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void findById_unknownId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.findById("nonexistent-id"));
    }

    @Test
    void findByCustomer_existingCustomer_returnsPayments() {
        service.create("cust-1", 100.0);
        service.create("cust-1", 200.0);
        service.create("cust-2", 300.0);

        List<Payment> result = service.findByCustomer("cust-1");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getCustomerId().equals("cust-1")));
    }

    @Test
    void findByCustomer_noMatchingCustomer_returnsEmptyList() {
        service.create("cust-1", 100.0);
        List<Payment> result = service.findByCustomer("cust-99");
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCustomer_blankCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.findByCustomer(""));
    }

    @Test
    void findByCustomer_nullCustomerId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.findByCustomer(null));
    }

    @Test
    void complete_pendingPayment_updatesToCompleted() {
        Payment payment = service.create("cust-1", 150.0);
        Payment completed = service.complete(payment.getId());
        assertEquals(Payment.PaymentStatus.COMPLETED, completed.getStatus());
    }

    @Test
    void complete_alreadyCompletedPayment_returnsWithoutChange() {
        Payment payment = service.create("cust-1", 150.0);
        service.complete(payment.getId());
        Payment result = service.complete(payment.getId());
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());
    }

    @Test
    void complete_unknownId_throwsBillingException() {
        assertThrows(BillingException.class, () -> service.complete("nonexistent-id"));
    }
}
