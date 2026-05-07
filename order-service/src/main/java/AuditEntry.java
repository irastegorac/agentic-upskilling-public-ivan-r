import java.time.Instant;

public final class AuditEntry {

    public enum Operation { PLACE_ORDER, FIND_ORDER, CANCEL_ORDER, FIND_BY_CUSTOMER }
    public enum Outcome   { SUCCESS, FAILURE }

    private final Instant   timestamp;
    private final Operation operation;
    private final String    orderId;
    private final String    customerId;
    private final Outcome   outcome;
    private final String    errorDetail;

    public AuditEntry(Instant timestamp, Operation operation,
                      String orderId, String customerId,
                      Outcome outcome, String errorDetail) {
        this.timestamp   = timestamp;
        this.operation   = operation;
        this.orderId     = orderId;
        this.customerId  = customerId;
        this.outcome     = outcome;
        this.errorDetail = errorDetail;
    }

    public Instant   getTimestamp()   { return timestamp; }
    public Operation getOperation()   { return operation; }
    public String    getOrderId()     { return orderId; }
    public String    getCustomerId()  { return customerId; }
    public Outcome   getOutcome()     { return outcome; }
    public String    getErrorDetail() { return errorDetail; }
}
