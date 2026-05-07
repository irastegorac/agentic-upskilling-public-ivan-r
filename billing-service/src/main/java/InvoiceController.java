public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public Invoice createInvoice(String customerId, double amount) {
        return invoiceService.create(customerId, amount);
    }

    public Invoice getInvoice(String id) {
        return invoiceService.findById(id);
    }

    public Invoice payInvoice(String id) {
        return invoiceService.markPaid(id);
    }

    public Invoice cancelInvoice(String id) {
        return invoiceService.cancel(id);
    }
}
