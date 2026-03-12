import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    // LRU cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    private int capacity = 5;
    private int hits = 0;
    private int misses = 0;

    public DNSCache() {

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > capacity;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null) {

            if (!entry.isExpired()) {

                hits++;

                long end = System.nanoTime();

                System.out.println(
                        "Cache HIT → " + entry.ipAddress +
                                " (retrieved in " + ((end - start)/1000000.0) + " ms)"
                );

                return entry.ipAddress;
            }

            System.out.println("Cache EXPIRED → Removing entry");
            cache.remove(domain);
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 5));

        System.out.println("Cache MISS → Query upstream → " + ip + " (TTL:5s)");

        return ip;
    }

    // Simulated upstream DNS
    private String queryUpstreamDNS(String domain) {

        Map<String, String> dnsDB = new HashMap<>();

        dnsDB.put("google.com", "172.217.14.206");
        dnsDB.put("youtube.com", "142.250.183.46");
        dnsDB.put("facebook.com", "157.240.22.35");

        return dnsDB.getOrDefault(domain, "8.8.8.8");
    }

    // Background cleanup thread
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {

                try {

                    Thread.sleep(3000);

                    synchronized (DNSCache.this) {

                        Iterator<Map.Entry<String, DNSEntry>> it =
                                cache.entrySet().iterator();

                        while (it.hasNext()) {

                            Map.Entry<String, DNSEntry> e = it.next();

                            if (e.getValue().isExpired()) {

                                System.out.println(
                                        "Cleanup → Removed expired entry: " +
                                                e.getKey()
                                );

                                it.remove();
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    break;
                }
            }

        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("\nCache Statistics:");

        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCache dns = new DNSCache();

        dns.resolve("google.com");

        dns.resolve("google.com");

        dns.resolve("youtube.com");

        Thread.sleep(6000);

        dns.resolve("google.com");

        dns.getCacheStats();
    }
}