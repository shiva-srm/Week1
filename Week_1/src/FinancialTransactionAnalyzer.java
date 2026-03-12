import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long timestamp;

    public Transaction(int id, int amount, String merchant, String account, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.timestamp = timestamp;
    }

    public String toString() {
        return "Transaction{id=" + id + ", amount=" + amount + ", merchant=" + merchant + "}";
    }
}

public class FinancialTransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();


    // Add transaction
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }


    // Classic Two-Sum
    public void findTwoSum(int target) {

        Map<Integer, Transaction> map = new HashMap<>();

        System.out.println("\nTwo-Sum Results:");

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                System.out.println("(" +
                        map.get(complement) +
                        ", " +
                        t +
                        ")");
            }

            map.put(t.amount, t);
        }
    }


    // Two-Sum with time window (1 hour)
    public void findTwoSumWithTimeWindow(int target) {

        Map<Integer, Transaction> map = new HashMap<>();

        long oneHour = 60 * 60 * 1000;

        System.out.println("\nTwo-Sum Within 1 Hour:");

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                if (Math.abs(t.timestamp - prev.timestamp) <= oneHour) {

                    System.out.println("(" + prev + ", " + t + ")");
                }
            }

            map.put(t.amount, t);
        }
    }


    // Duplicate detection
    public void detectDuplicates() {

        Map<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        System.out.println("\nDuplicate Transactions:");

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.println("Duplicate for " + key + " -> " + list);
            }
        }
    }


    // K-Sum (recursive)
    public void findKSum(int k, int target) {

        System.out.println("\nK-Sum Results:");

        List<Integer> nums = new ArrayList<>();

        for (Transaction t : transactions)
            nums.add(t.amount);

        kSumHelper(nums, new ArrayList<>(), k, target, 0);
    }


    private void kSumHelper(List<Integer> nums, List<Integer> path, int k, int target, int start) {

        if (k == 0 && target == 0) {
            System.out.println(path);
            return;
        }

        if (k == 0 || target < 0)
            return;

        for (int i = start; i < nums.size(); i++) {

            path.add(nums.get(i));

            kSumHelper(nums, path, k - 1, target - nums.get(i), i + 1);

            path.remove(path.size() - 1);
        }
    }


    public static void main(String[] args) {

        FinancialTransactionAnalyzer analyzer = new FinancialTransactionAnalyzer();

        long now = System.currentTimeMillis();

        analyzer.addTransaction(new Transaction(1, 500, "StoreA", "acc1", now));
        analyzer.addTransaction(new Transaction(2, 300, "StoreB", "acc2", now));
        analyzer.addTransaction(new Transaction(3, 200, "StoreC", "acc3", now));
        analyzer.addTransaction(new Transaction(4, 500, "StoreA", "acc4", now));

        analyzer.findTwoSum(500);

        analyzer.findTwoSumWithTimeWindow(500);

        analyzer.detectDuplicates();

        analyzer.findKSum(3, 1000);
    }
}