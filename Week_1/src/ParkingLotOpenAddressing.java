import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    String status; // EMPTY, OCCUPIED, DELETED

    public ParkingSpot() {
        status = "EMPTY";
    }
}

public class ParkingLotOpenAddressing {

    private ParkingSpot[] table;
    private int capacity = 500;
    private int size = 0;
    private int totalProbes = 0;

    public ParkingLotOpenAddressing() {

        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++)
            table[i] = new ParkingSpot();
    }

    // Hash function
    private int hash(String licensePlate) {

        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY") &&
                !table[index].status.equals("DELETED")) {

            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        size++;
        totalProbes += probes;

        System.out.println(
                "parkVehicle(\"" + licensePlate + "\") → Assigned spot #" +
                        index + " (" + probes + " probes)"
        );
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (!table[index].status.equals("EMPTY")) {

            if (table[index].status.equals("OCCUPIED") &&
                    table[index].licensePlate.equals(licensePlate)) {

                long durationMillis =
                        System.currentTimeMillis() - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);

                double fee = hours * 5; // $5 per hour

                table[index].status = "DELETED";
                size--;

                System.out.println(
                        "exitVehicle(\"" + licensePlate + "\") → Spot #" +
                                index + " freed, Duration: " +
                                String.format("%.2f", hours) +
                                "h, Fee: $" +
                                String.format("%.2f", fee)
                );

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    // Statistics
    public void getStatistics() {

        double occupancy = (size * 100.0) / capacity;

        double avgProbes =
                size == 0 ? 0 : (double) totalProbes / size;

        System.out.println(
                "Occupancy: " +
                        String.format("%.2f", occupancy) +
                        "%, Avg Probes: " +
                        String.format("%.2f", avgProbes)
        );
    }

    public static void main(String[] args) throws InterruptedException {

        ParkingLotOpenAddressing lot =
                new ParkingLotOpenAddressing();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(3000);

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}