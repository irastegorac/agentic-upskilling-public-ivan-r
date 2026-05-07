import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(String customerId);
    Customer save(Customer customer);
}
