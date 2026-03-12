import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SocialMedia {

    // username -> userId
    private ConcurrentHashMap<String, Integer> usernameToUserId;

    private ConcurrentHashMap<String, Integer> attemptFrequency;

    public SocialMedia() {
        usernameToUserId = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
    }

    public boolean checkAvailability(String username) {

        // Increase attempt count
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        // Check if username exists
        return !usernameToUserId.containsKey(username);
    }

    public void registerUser(String username, int userId) {
        usernameToUserId.put(username, userId);
    }

    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // Append numbers
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameToUserId.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        if (username.contains("_")) {
            String alt = username.replace("_", ".");
            if (!usernameToUserId.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        return suggestions;
    }

    public String getMostAttempted() {

        String mostPopular = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                mostPopular = entry.getKey();
            }
        }

        return mostPopular + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        SocialMedia system = new SocialMedia();

        // Register users
        system.registerUser("john_doe", 1);
        system.registerUser("admin", 2);

        System.out.println("john_doe available? " + system.checkAvailability("john_doe"));
        System.out.println("jane_smith available? " + system.checkAvailability("jane_smith"));

        // Suggestions
        System.out.println("Suggestions for john_doe:");
        List<String> suggestions = system.suggestAlternatives("john_doe");

        for (String s : suggestions) {
            System.out.println(s);
        }

        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("guest");

        // Most attempted
        System.out.println("Most attempted username: " + system.getMostAttempted());
    }
}