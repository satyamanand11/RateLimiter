import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class LeakyBucketRateLimiter implements RateLimiter{
    private final long capacity;
    private final long leakRate;
    private long lastLeakTimeStamp;
    private final Queue<Long> requestQueue;

    public LeakyBucketRateLimiter(long capacity, long leakRate, long lastLeakTimeStamp, Queue<Long> requestQueue) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.lastLeakTimeStamp = System.nanoTime();
        this.requestQueue = new LinkedList<>();
    }

    private void leak(){
        long now = System.nanoTime();
        long timeSinceLastLeak = lastLeakTimeStamp - now;
        long tokensLeaked = leakRate*(timeSinceLastLeak)/ TimeUnit.SECONDS.toNanos(1);
        for(int i=0;i<tokensLeaked && !requestQueue.isEmpty();i++){
            requestQueue.poll();
        }
        if(tokensLeaked>0){
            lastLeakTimeStamp = now;
        }

    }

    @Override
    public synchronized boolean allowRequest() {
        leak();
        if(requestQueue.size()<capacity){
            requestQueue.add(System.nanoTime());
            return true;
        }
        return false;
    }
}
