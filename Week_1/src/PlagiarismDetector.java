import java.util.*;
import java.nio.file.*;
import java.io.IOException;

public class PlagiarismDetector {

    // n-gram size
    private static final int N = 5;

    // HashMap<n-gram , Set<documentId>>
    private Map<String, Set<String>> ngramIndex = new HashMap<>();

    // Store each document's ngrams
    private Map<String, List<String>> documentNgrams = new HashMap<>();


    // Read file and clean text
    private String readFile(String path) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(path)));
        return text.toLowerCase().replaceAll("[^a-z0-9 ]", " ");
    }


    // Generate n-grams
    private List<String> generateNgrams(String text) {

        List<String> ngrams = new ArrayList<>();
        String[] words = text.split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }


    // Add document to database
    public void addDocument(String docId, String path) throws IOException {

        String text = readFile(path);

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            ngramIndex
                    .computeIfAbsent(gram, k -> new HashSet<>())
                    .add(docId);
        }

        System.out.println(docId + " indexed with " + ngrams.size() + " n-grams");
    }


    // Analyze new document
    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        if (ngrams == null) {
            System.out.println("Document not found");
            return;
        }

        Map<String, Integer> similarityCount = new HashMap<>();

        for (String gram : ngrams) {

            Set<String> docs = ngramIndex.get(gram);

            if (docs != null) {

                for (String d : docs) {

                    if (!d.equals(docId)) {

                        similarityCount.put(
                                d,
                                similarityCount.getOrDefault(d, 0) + 1
                        );
                    }
                }
            }
        }

        System.out.println("\nAnalyzing: " + docId);
        System.out.println("Extracted " + ngrams.size() + " n-grams\n");

        for (String otherDoc : similarityCount.keySet()) {

            int matches = similarityCount.get(otherDoc);

            double similarity =
                    (matches * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + matches +
                            " matching n-grams with " + otherDoc
            );

            System.out.printf(
                    "Similarity: %.2f%% ",
                    similarity
            );

            if (similarity > 60)
                System.out.println("(PLAGIARISM DETECTED)");
            else if (similarity > 10)
                System.out.println("(Suspicious)");
            else
                System.out.println("(Low similarity)");

            System.out.println();
        }
    }


    // Main method
    public static void main(String[] args) throws Exception {

        PlagiarismDetector detector =
                new PlagiarismDetector();

        detector.addDocument("essay_089", "essay_089.txt");
        detector.addDocument("essay_092", "essay_092.txt");
        detector.addDocument("essay_123", "essay_123.txt");

        detector.analyzeDocument("essay_123");
    }
}