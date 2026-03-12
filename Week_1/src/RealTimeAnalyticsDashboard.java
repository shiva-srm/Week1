
import java.util.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {

    // page -> total visits
    private Map<String, Integer> pageViews = new HashMap<>();

    // page -> unique users
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private Map<String, Integer> trafficSources = new HashMap<>();


    // Process incoming event
    public void processEvent(PageViewEvent event) {

        // count page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // unique visitors
        uniqueVisitors
                .computeIfAbsent(event.url, k -> new HashSet<>())
                .add(event.userId);

        // traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }


    // Get Top 10 Pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.add(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);
        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }


    // Print dashboard
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> page : topPages) {

            String url = page.getKey();
            int views = page.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url +
                    " - " + views +
                    " views (" + unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (String source : trafficSources.keySet()) {

            System.out.println(
                    source + " -> " + trafficSources.get(source)
            );
        }

        System.out.println("===============================");
    }


    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalyticsDashboard dashboard =
                new RealTimeAnalyticsDashboard();

        dashboard.processEvent(new PageViewEvent(
                "/article/breaking-news", "user_123", "google"));

        dashboard.processEvent(new PageViewEvent(
                "/article/breaking-news", "user_456", "facebook"));

        dashboard.processEvent(new PageViewEvent(
                "/sports/championship", "user_789", "google"));

        dashboard.processEvent(new PageViewEvent(
                "/sports/championship", "user_999", "direct"));

        dashboard.processEvent(new PageViewEvent(
                "/sports/championship", "user_111", "google"));

        dashboard.processEvent(new PageViewEvent(
                "/article/breaking-news", "user_777", "google"));

        // simulate dashboard update every 5 seconds
        dashboard.getDashboard();
    }
}