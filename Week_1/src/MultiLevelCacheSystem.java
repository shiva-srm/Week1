import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCacheSystem {

    // L1 cache (memory)
    private LinkedHashMap<String, VideoData> L1Cache;

    // L2 cache (SSD simulation)
    private LinkedHashMap<String, VideoData> L2Cache;

    // L3 database simulation
    private Map<String, VideoData> database = new HashMap<>();

    // access counters
    private Map<String, Integer> accessCount = new HashMap<>();

    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;

    private int L1Capacity = 10000;
    private int L2Capacity = 100000;

    public MultiLevelCacheSystem() {

        L1Cache = new LinkedHashMap<>(L1Capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1Capacity;
            }
        };

        L2Cache = new LinkedHashMap<>(L2Capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L2Capacity;
            }
        };
    }

    // Add video to database
    public void addVideo(VideoData video) {
        database.put(video.videoId, video);
    }

    // Get video
    public VideoData getVideo(String videoId) {

        long startTime = System.currentTimeMillis();

        // L1
        if (L1Cache.containsKey(videoId)) {

            L1Hits++;

            System.out.println("L1 Cache HIT (0.5ms)");

            return L1Cache.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2
        if (L2Cache.containsKey(videoId)) {

            L2Hits++;

            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2Cache.get(videoId);

            promoteToL1(video);

            return video;
        }

        System.out.println("L2 Cache MISS");

        // L3 Database
        VideoData video = database.get(videoId);

        if (video != null) {

            L3Hits++;

            System.out.println("L3 Database HIT (150ms)");

            L2Cache.put(videoId, video);

            accessCount.put(videoId,
                    accessCount.getOrDefault(videoId, 0) + 1);

            return video;
        }

        System.out.println("Video not found");

        return null;
    }

    // Promote video to L1
    private void promoteToL1(VideoData video) {

        int count = accessCount.getOrDefault(video.videoId, 0) + 1;

        accessCount.put(video.videoId, count);

        if (count > 2) {

            L1Cache.put(video.videoId, video);

            System.out.println("Promoted to L1");
        }
    }

    // Statistics
    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double L1Rate = total == 0 ? 0 : (L1Hits * 100.0 / total);
        double L2Rate = total == 0 ? 0 : (L2Hits * 100.0 / total);
        double L3Rate = total == 0 ? 0 : (L3Hits * 100.0 / total);

        System.out.println("\nCache Statistics:");

        System.out.println("L1 Hit Rate: " + String.format("%.2f", L1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", L2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", L3Rate) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        cache.addVideo(new VideoData("video_123", "Movie Data"));
        cache.addVideo(new VideoData("video_999", "Series Data"));

        cache.getVideo("video_123");

        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.getStatistics();
    }
}