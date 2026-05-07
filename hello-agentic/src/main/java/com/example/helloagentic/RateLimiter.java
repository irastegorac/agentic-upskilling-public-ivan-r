package com.example.helloagentic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread-safe, per-client rate limiter using the sliding window log algorithm.
 *
 * <h2>Algorithm</h2>
 * <p>Each call to {@link #allow(String)} records the current timestamp in a per-client list.
 * When a new request arrives, all timestamps older than the sliding window are purged, and the
 * remaining count is compared against {@code maxRequests}. If the window limit has not been
 * reached, a secondary burst check ensures no more than {@code burstLimit} requests have
 * occurred within the last one second. This two-tier approach prevents traffic spikes from
 * consuming the entire window quota in a short burst.</p>
 *
 * <h2>Thread safety</h2>
 * <p>All public methods are guarded by a {@link ReentrantLock}, making this class safe for
 * concurrent use from multiple threads.</p>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * // Allow 100 requests per 60-second window, with a burst limit of 10 per second
 * RateLimiter limiter = new RateLimiter(100, 60, 10);
 *
 * if (limiter.allow("client-42")) {
 *     // process request
 * } else {
 *     // reject or queue request
 * }
 *
 * // Check how many requests a client has left
 * int remaining = limiter.remaining("client-42");
 *
 * // Reset a single client
 * limiter.reset("client-42");
 *
 * // Reset all clients
 * limiter.reset(null);
 * }</pre>
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li><b>Memory growth</b> — Every allowed request stores a timestamp in an unbounded
 *       {@link ArrayList}. With high request rates or large windows, per-client lists can grow
 *       large. Expired entries are only purged when that client's next call triggers
 *       {@code cleanup}, so idle clients retain stale data until they are accessed again.
 *       There is no background eviction.</li>
 *
 *   <li><b>Single-node only</b> — All state is held in-process in a {@link HashMap}. In a
 *       distributed or multi-instance deployment, each node maintains independent counts and
 *       clients can exceed the intended limit by spreading requests across nodes. A shared
 *       store (e.g. Redis) would be needed for distributed rate limiting.</li>
 *
 *   <li><b>Global lock contention</b> — A single {@link ReentrantLock} serializes all
 *       operations across all clients. Under high concurrency this becomes a bottleneck
 *       because unrelated clients block each other. A striped or per-client locking scheme
 *       (e.g. {@link java.util.concurrent.ConcurrentHashMap} with per-key locks) would
 *       improve throughput.</li>
 *
 *   <li><b>Wall-clock dependency</b> — Timestamps come from {@link Instant#now()}, which
 *       is not injected or abstracted. This makes the class difficult to unit test
 *       deterministically (tests must use {@link Thread#sleep} or real-time waits) and
 *       vulnerable to system clock adjustments (NTP jumps, daylight saving, etc.).</li>
 *
 *   <li><b>No persistence</b> — All state is lost on JVM restart. A client that was near
 *       its limit immediately regains full quota after a redeploy or crash.</li>
 *
 *   <li><b>Fixed one-second burst window</b> — The burst check hardcodes a 1000ms sub-window.
 *       This is not configurable, which limits flexibility for use cases that need a different
 *       burst granularity.</li>
 *
 *   <li><b>No input validation</b> — The constructor accepts zero or negative values for
 *       {@code maxRequests} and {@code windowSeconds} without error. A zero {@code maxRequests}
 *       blocks all traffic; a negative {@code windowSeconds} causes timestamps to never
 *       expire.</li>
 * </ul>
 */
public class RateLimiter {

    private final int maxRequests;
    private final long windowMillis;
    private final int burstLimit;
    private final Map<String, List<Long>> requests = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a new rate limiter.
     *
     * @param maxRequests   the maximum number of requests allowed per sliding window
     * @param windowSeconds the length of the sliding window in seconds
     * @param burstLimit    the maximum number of requests allowed within any single second,
     *                      or {@code null} to default to {@code maxRequests} (no burst restriction
     *                      beyond the window limit)
     */
    public RateLimiter(int maxRequests, long windowSeconds, Integer burstLimit) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowSeconds * 1000;
        this.burstLimit = burstLimit != null ? burstLimit : maxRequests;
    }

    /**
     * Removes expired timestamps for the given client. A timestamp is considered expired when
     * it falls at or before the cutoff time ({@code now - windowMillis}). If all timestamps
     * are expired, the client entry is removed entirely.
     *
     * @param clientId the client whose timestamps should be cleaned up
     */
    private void cleanup(String clientId) {
        long cutoff = Instant.now().toEpochMilli() - windowMillis;
        List<Long> timestamps = requests.get(clientId);
        if (timestamps != null) {
            timestamps.removeIf(t -> t <= cutoff);
            if (timestamps.isEmpty()) {
                requests.remove(clientId);
            }
        }
    }

    /**
     * Checks whether a request from the given client should be allowed, and if so,
     * records the request timestamp.
     *
     * <p>A request is denied if either:</p>
     * <ul>
     *   <li>The client has reached {@code maxRequests} within the current sliding window.</li>
     *   <li>The client has reached {@code burstLimit} within the last one second.</li>
     * </ul>
     *
     * @param clientId a unique identifier for the client (e.g. IP address, API key, or user ID)
     * @return {@code true} if the request is allowed, {@code false} if it should be rate-limited
     */
    public boolean allow(String clientId) {
        lock.lock();
        try {
            cleanup(clientId);
            List<Long> timestamps = requests.getOrDefault(clientId, new ArrayList<>());

            if (timestamps.size() >= maxRequests) {
                return false;
            }

            long now = Instant.now().toEpochMilli();
            long recentCutoff = now - 1000;
            long recentCount = timestamps.stream().filter(t -> t > recentCutoff).count();
            if (recentCount >= burstLimit) {
                return false;
            }

            requests.computeIfAbsent(clientId, k -> new ArrayList<>()).add(now);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the number of requests the client can still make within the current sliding window.
     *
     * <p>Expired timestamps are purged before computing the count, so the returned value
     * reflects only active (non-expired) request history.</p>
     *
     * @param clientId a unique identifier for the client
     * @return the number of remaining allowed requests, always {@code >= 0}
     */
    public int remaining(String clientId) {
        lock.lock();
        try {
            cleanup(clientId);
            int used = requests.getOrDefault(clientId, new ArrayList<>()).size();
            return Math.max(0, maxRequests - used);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resets rate-limiting state for a specific client or for all clients.
     *
     * <p>Pass a non-null {@code clientId} to clear only that client's history.
     * Pass {@code null} to clear all tracked state.</p>
     *
     * @param clientId the client to reset, or {@code null} to reset all clients
     */
    public void reset(String clientId) {
        lock.lock();
        try {
            if (clientId != null) {
                requests.remove(clientId);
            } else {
                requests.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a snapshot of current rate-limiter statistics after purging expired entries.
     *
     * <p>The returned map contains:</p>
     * <ul>
     *   <li>{@code "activeClients"} — the number of clients with at least one non-expired request.</li>
     *   <li>{@code "totalTrackedRequests"} — the total number of non-expired request timestamps
     *       across all clients.</li>
     * </ul>
     *
     * @return an unmodifiable-by-contract map of statistic names to their integer values
     */
    public Map<String, Integer> getStats() {
        lock.lock();
        try {
            for (String cid : new ArrayList<>(requests.keySet())) {
                cleanup(cid);
            }
            int totalRequests = requests.values().stream().mapToInt(List::size).sum();
            Map<String, Integer> stats = new HashMap<>();
            stats.put("activeClients", requests.size());
            stats.put("totalTrackedRequests", totalRequests);
            return stats;
        } finally {
            lock.unlock();
        }
    }
}
