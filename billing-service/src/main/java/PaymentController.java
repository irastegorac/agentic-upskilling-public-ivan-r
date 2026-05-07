import java.util.List;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Payment createPayment(String customerId, double amount) {
        return paymentService.create(customerId, amount);
    }

    public Payment getPayment(String id) {
        return paymentService.findById(id);
    }

    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentService.findByCustomer(customerId);
    }

    public Payment completePayment(String id) {
        return paymentService.complete(id);
    }
}
