import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryAuditLogger implements AuditLogger {

    private final List<AuditEntry> entries = new CopyOnWriteArrayList<>();

    @Override
    public void log(AuditEntry entry) {
        entries.add(entry);
    }

    public List<AuditEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
