import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    int tokens;
    int maxTokens;
    double refillRate; // tokens per second
    long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // refill tokens based on time passed
    private void refill() {

        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        int tokensToAdd = (int) (seconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return tokens;
    }
}

public class DistributedRateLimiter {

    // clientId -> TokenBucket
    private Map<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    private static final int LIMIT = 1000;
    private static final int REFILL_PER_HOUR = 1000;

    public boolean checkRateLimit(String clientId) {

        TokenBucket bucket = clientBuckets.computeIfAbsent(
                clientId,
                k -> new TokenBucket(LIMIT, REFILL_PER_HOUR / 3600.0)
        );

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println(
                    "Allowed (" +
                            bucket.getRemainingTokens() +
                            " requests remaining)"
            );
        } else {
            System.out.println(
                    "Denied (0 requests remaining)"
            );
        }

        return allowed;
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int used = LIMIT - bucket.getRemainingTokens();

        System.out.println(
                "{used: " + used +
                        ", limit: " + LIMIT +
                        ", remaining: " + bucket.getRemainingTokens() + "}"
        );
    }

    public static void main(String[] args) {

        DistributedRateLimiter limiter =
                new DistributedRateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {
            limiter.checkRateLimit(client);
        }

        limiter.getRateLimitStatus(client);
    }
}