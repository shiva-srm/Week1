import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    List<String> queries = new ArrayList<>();
}

public class SearchAutocompleteSystem {

    // query -> frequency
    private Map<String, Integer> frequencyMap = new HashMap<>();

    private TrieNode root = new TrieNode();

    // Insert query into trie
    public void insertQuery(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.add(query);
        }

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    // Get top 10 suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(a) - frequencyMap.get(b));

        for (String q : node.queries) {

            pq.add(q);

            if (pq.size() > 10)
                pq.poll();
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty())
            result.add(pq.poll());

        Collections.reverse(result);

        return result;
    }

    // Update frequency
    public void updateFrequency(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);

        insertQuery(query);

        System.out.println(query +
                " → Frequency: " + frequencyMap.get(query));
    }

    public static void main(String[] args) {

        SearchAutocompleteSystem system =
                new SearchAutocompleteSystem();

        system.insertQuery("java tutorial");
        system.insertQuery("javascript");
        system.insertQuery("java download");
        system.insertQuery("java tutorial");
        system.insertQuery("java features");
        system.insertQuery("java tutorial");

        List<String> suggestions = system.search("jav");

        System.out.println("Suggestions:");

        int rank = 1;

        for (String s : suggestions) {

            System.out.println(
                    rank + ". " + s +
                            " (" + system.frequencyMap.get(s) + " searches)"
            );

            rank++;
        }

        system.updateFrequency("java 21 features");
    }
}