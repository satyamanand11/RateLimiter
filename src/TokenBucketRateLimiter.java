import java.util.concurrent.TimeUnit;

public class TokenBucketRateLimiter implements RateLimiter{
    private final long bucketSize;
    private final long refillRate;
    private long availableTokens;
    private long lastRefillTimestamp;

    public TokenBucketRateLimiter(long bucketSize, long refillRate, long availableTokens, long lastRefillTimestamp) {
        this.bucketSize = bucketSize;
        this.refillRate = refillRate;
        this.availableTokens = availableTokens;
        this.lastRefillTimestamp = System.nanoTime();
    }

    private void refill(){
        long now = System.nanoTime();
        long tokensToAdd = refillRate*(lastRefillTimestamp-now)/(TimeUnit.SECONDS.toNanos(1));
        availableTokens = Math.min(bucketSize,availableTokens+tokensToAdd);
        if(tokensToAdd>0){
            lastRefillTimestamp = now;
        }
    }

    @Override
    public synchronized boolean allowRequest() {
        refill();
        if(availableTokens>0){
            availableTokens--;
            return true;
        }
        return false;
    }
}