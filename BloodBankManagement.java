import java.io.*;
import java.util.*;
public class BloodBankManagement implements Runnable {
    private List<Donor> donors = new ArrayList<>();
    private Map<String, Integer> inventory = new HashMap<>();
    private static final String DONOR_FILE = "donors.txt";
    private static final String INVENTORY_FILE = "inventory.txt";
    public BloodBankManagement() {
        loadDonorsFromFile();
        loadInventoryFromFile();
    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\n--- Blood Bank Management System ---");
                System.out.println("1. Add Donor");
                System.out.println("2. Remove Donor");
                System.out.println("3. Update Inventory");
                System.out.println("4. Display Inventory");
                System.out.println("5. List Donors");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        addDonor(scanner);
                        break;
                    case 2:
                        removeDonor(scanner);
                        break;
                    case 3:
                        updateInventory(scanner);
                        break;
                    case 4:
                        displayInventory();
                        break;
                    case 5:
                        listDonors();
                        break;
                    case 6:
                        scanner.close();
                        saveDataToFiles();
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    private void addDonor(Scanner scanner) {
        try {
            System.out.print("Enter Donor ID: ");
            int donorID = scanner.nextInt();
            scanner.nextLine(); 
            System.out.print("Enter Donor Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Blood Type: ");
            String bloodType = scanner.nextLine().trim().toUpperCase(); 
            Donor donor = new Donor(donorID, name, bloodType);
            donors.add(donor);
            System.out.println("Donor added: " + donor.getName());
        } catch (Exception e) {
            System.out.println("Error adding donor: " + e.getMessage());
        }
    }
    private void removeDonor(Scanner scanner) {
        try {
            System.out.print("Enter Donor ID to remove: ");
            int donorID = scanner.nextInt();
            Donor donorToRemove = null;
            for (Donor donor : donors) {
                if (donor.getDonorID() == donorID) {
                    donorToRemove = donor;
                    break;
                }
            }
            if (donorToRemove != null) {
                donors.remove(donorToRemove);
                System.out.println("Donor removed: " + donorToRemove.getName());
            } else {
                System.out.println("Donor ID not found!");
            }
        } catch (Exception e) {
            System.out.println("Error removing donor: " + e.getMessage());
        }
    }
    private void updateInventory(Scanner scanner) {
        try {
            System.out.println("Do you want to (1) Add or (2) Remove blood units?");
            int action = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Blood Type: ");
            String bloodType = scanner.nextLine().trim().toUpperCase(); 

            if (action == 1) { 
                System.out.print("Enter Quantity: ");
                int quantity = scanner.nextInt();
                inventory.put(bloodType, inventory.getOrDefault(bloodType, 0) + quantity);
                System.out.println("Updated inventory: " + bloodType + " = " + inventory.get(bloodType));
            } else if (action == 2) { 
                if (!inventory.containsKey(bloodType)) {
                    System.out.println("Blood type not found in inventory.");
                    return;
                }
                System.out.print("Enter Quantity: ");
                int quantity = scanner.nextInt();
                int currentQuantity = inventory.get(bloodType);
                if (quantity > currentQuantity) {
                    System.out.println("Not enough units available to remove.");
                } else {
                    inventory.put(bloodType, currentQuantity - quantity);
                    System.out.println("Updated inventory: " + bloodType + " = " + inventory.get(bloodType));
                }
            } else {
                System.out.println("Invalid action. Please select 1 to Add or 2 to Remove.");
            }
        } catch (Exception e) {
            System.out.println("Error updating inventory: " + e.getMessage());
        }
    }
    private void displayInventory() {
        try {
            System.out.println("\n--- Blood Inventory ---");
            if (inventory.isEmpty()) {
                System.out.println("No blood types available in the inventory.");
                return;
            }
            for (String bloodType : inventory.keySet()) {
                int quantity = inventory.get(bloodType);
                System.out.println("Blood Type: " + bloodType + " | Units: " + quantity);
            }
        } catch (Exception e) {
            System.out.println("Error displaying inventory: " + e.getMessage());
        }
    }
    private void listDonors() {
        try {
            System.out.println("\n--- List of Donors ---");
            if (donors.isEmpty()) {
                System.out.println("No donors registered.");
                return;
            }
            for (Donor donor : donors) {
                donor.displayDonorDetails();
            }
        } catch (Exception e) {
            System.out.println("Error listing donors: " + e.getMessage());
        }
    }
    private void loadDonorsFromFile() {
        try (Scanner fileScanner = new Scanner(new File(DONOR_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] donorData = line.split(",");
                int donorID = Integer.parseInt(donorData[0]);
                String name = donorData[1];
                String bloodType = donorData[2];
                donors.add(new Donor(donorID, name, bloodType));
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing donor data found, starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading donors from file: " + e.getMessage());
        }
    }
    private void loadInventoryFromFile() {
        try (Scanner fileScanner = new Scanner(new File(INVENTORY_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] inventoryData = line.split(",");
                String bloodType = inventoryData[0];
                int quantity = Integer.parseInt(inventoryData[1]);
                inventory.put(bloodType, quantity);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing inventory data found, starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading inventory from file: " + e.getMessage());
        }
    }
    private void saveDataToFiles() {
        try (BufferedWriter donorWriter = new BufferedWriter(new FileWriter(DONOR_FILE));
             BufferedWriter inventoryWriter = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            for (Donor donor : donors) {
                donorWriter.write(donor.getDonorID() + "," + donor.getName() + "," + donor.getBloodType());
                donorWriter.newLine();
            }
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                inventoryWriter.write(entry.getKey() + "," + entry.getValue());
                inventoryWriter.newLine();
            }
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving data to files: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        BloodBankManagement bloodBank = new BloodBankManagement();
        Thread thread = new Thread(bloodBank);
        thread.start();
    }
}
class Donor {
    private int donorID;
    private String name;
    private String bloodType;
    public Donor(int donorID, String name, String bloodType) {
        this.donorID = donorID;
        this.name = name;
        this.bloodType = bloodType;
    }
    public int getDonorID() {
        return donorID;
    }
    public String getName() {
        return name;
    }
    public String getBloodType() {
        return bloodType;
    }
    public void displayDonorDetails() {
        System.out.println("Donor ID: " + donorID + ", Name: " + name + ", Blood Type: " + bloodType);
    }
}
