import java.io.IOException;
import java.util.logging.Logger;

public class PaymentService {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 200;

    private double balance;

    public PaymentService(double initialBalance) {
        this.balance = initialBalance;
    }

    public boolean charge(String customerId, double amount) throws IOException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Charge amount must be positive");
        }

        IOException lastException = null;
        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            if (attempt > 0) {
                long delay = INITIAL_BACKOFF_MS * (1L << (attempt - 1)); // 200, 400, 800
                logger.warning("Retry attempt " + attempt + " of " + MAX_RETRIES
                        + " for customer " + customerId + ", waiting " + delay + "ms before next attempt");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted during retry", ie);
                }
            }
            try {
                return attemptCharge(customerId, amount);
            } catch (IOException e) {
                lastException = e;
            }
        }

        logger.severe("Charge failed after " + MAX_RETRIES + " retries for customer " + customerId);
        throw lastException;
    }

    private synchronized boolean attemptCharge(String customerId, double amount) throws IOException {
        if (amount > balance) {
            logger.warning("Insufficient balance for customer " + customerId);
            return false;
        }
        balance -= amount;
        logger.info("Charged " + amount + " to customer " + customerId);
        return true;
    }

    public synchronized void refund(String customerId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
        balance += amount;
        logger.info("Refunded " + amount + " to customer " + customerId);
    }

    public synchronized double getBalance() {
        return balance;
    }
}
