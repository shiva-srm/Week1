
import java.util.*;
        import java.util.concurrent.ConcurrentHashMap;

public class EcommerceFlash {

    // productId -> stock count
    private ConcurrentHashMap<String, Integer> stockMap;

    // productId -> waiting list
    private ConcurrentHashMap<String, Queue<Integer>> waitingList;

    public EcommerceFlash() {
        stockMap = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = stockMap.getOrDefault(productId, 0);

        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Added to waiting list, position #" + queue.size();
        }
    }

    // Show waiting list
    public void showWaitingList(String productId) {
        Queue<Integer> queue = waitingList.get(productId);
        System.out.println("Waiting List: " + queue);
    }

    // Main method
    public static void main(String[] args) {

        EcommerceFlash system = new EcommerceFlash();

        // Add product
        system.addProduct("IPHONE15_256GB", 3);

        // Check stock
        System.out.println("Stock available: " + system.checkStock("IPHONE15_256GB"));

        // Purchase attempts
        System.out.println(system.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 11111));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 22222));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 33333));

        // Show waiting list
        system.showWaitingList("IPHONE15_256GB");
    }
}