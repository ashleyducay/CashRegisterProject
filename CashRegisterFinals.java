import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

class Product {
    String name;
    double price;

    Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Product)) return false;
        Product p = (Product) obj;
        return name.equals(p.name) && price == p.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}

class User {
    String username;
    String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<User> users = new ArrayList<>();
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,15}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$");
    private static String loggedInUser = null;

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("     KFC CASH REGISTER SYSTEM   ");
        System.out.println("================================\n");

        while (true) {
            System.out.println("1. Sign Up\n2. Log In\n3. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    signUp();
                    break;
                case "2":
                    if (logIn()) {
                        runCashRegister();
                    }
                    break;
                case "3":
                    System.out.println("Goodbye! Thank you for using KFC Register.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.\n");
            }
        }
    }

    private static void signUp() {
        System.out.println("\n------ SIGN UP ------");
        String username;
        while (true) {
            System.out.print("Enter username (5–15 alphanumeric): ");
            String inputUsername = sc.nextLine();
            if (!USERNAME_PATTERN.matcher(inputUsername).matches()) {
                System.out.println("Invalid username format.");
                continue;
            }

            boolean usernameExists = users.stream().anyMatch(u -> u.username.equals(inputUsername));
            if (usernameExists) {
                System.out.println("Username already exists.");
                continue;
            }

            username = inputUsername;
            break;
        }

        String password;
        while (true) {
            System.out.print("Enter password (8–20 chars, 1 uppercase, 1 digit): ");
            password = sc.nextLine();
            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                System.out.println("Weak password format.");
                continue;
            }
            break;
        }

        users.add(new User(username, password));
        System.out.println("Account created! You can now log in.\n");
    }

    private static boolean logIn() {
        System.out.println("\n------ LOG IN ------");
        while (true) {
            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            boolean valid = users.stream().anyMatch(u -> u.username.equals(username) && u.password.equals(password));
            if (valid) {
                loggedInUser = username;
                System.out.println("Login successful!\n");
                return true;
            }
            System.out.println("Incorrect credentials. Try again.\n");
        }
    }

    private static void runCashRegister() {
        ArrayList<Product> menu = new ArrayList<>();
        menu.add(new Product("1-PC Chicken Meal", 120.00));
        menu.add(new Product("1-PC Chicken Ala Carte", 95.00));
        menu.add(new Product("Spaghetti Meal", 85.00));
        menu.add(new Product("Flavor Shots Meal", 95.00));
        menu.add(new Product("Flavor Shots Ala Carte", 65.00));
        menu.add(new Product("Chicken Burger Combo", 105.00));
        menu.add(new Product("Chicken Burger", 45.00));

        boolean continueUsing = true;

        while (continueUsing) {
            ArrayList<Product> cart = new ArrayList<>();
            boolean inSession = true;

            while (inSession) {
                System.out.println("\n============= KFC MENU =============");
                System.out.println("1. Order");
                System.out.println("2. Update Quantity");
                System.out.println("3. Remove Order");
                System.out.println("4. Display Orders");
                System.out.println("5. Checkout");
                System.out.println("6. Logout");
                System.out.print("Enter your choice: ");

                int choice = getIntInput();
                if (choice == -1) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        showMenuAndOrder(menu, cart);
                        break;
                    case 2:
                        updateQuantity(cart);
                        break;
                    case 3:
                        removeFromCart(cart);
                        displayCart(cart);  
                        break;
                    case 4:
                        displayCart(cart);
                        break;
                    case 5:
                        if (cart.isEmpty()) {
                            System.out.println("Your cart is empty. Cannot checkout.");
                        } else {
                            checkout(cart);
                            inSession = false;
                        }
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        inSession = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            if (!askAnotherTransaction()) {
                System.out.println("Logging out from the system. Returning to main menu.\n");
                continueUsing = false;
            }
        }
    }

    private static void showMenuAndOrder(ArrayList<Product> menu, ArrayList<Product> cart) {
        System.out.println("\n========= KFC MENU =========");
        System.out.printf("%-5s %-25s %s\n", "No.", "Item Name", "Price");
        System.out.println("----------------------------------");
        for (int i = 0; i < menu.size(); i++) {
            System.out.printf("%-5d %-25s PHP %.2f\n", i + 1, menu.get(i).name, menu.get(i).price);
        }
        System.out.println("Enter 'done' when finished ordering.");

        while (true) {
            System.out.print("Enter product number: ");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("done")) {
                System.out.println("Finished ordering.");
                break;
            }

            int productChoice;
            try {
                productChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or 'done'.");
                continue;
            }

            if (productChoice < 1 || productChoice > menu.size()) {
                System.out.println("Invalid product number.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantity = getIntInput();
            if (quantity <= 0) {
                System.out.println("Invalid quantity. Must be greater than zero.");
                continue;
            }

            for (int i = 0; i < quantity; i++) {
                cart.add(menu.get(productChoice - 1));
            }
            System.out.println("Added to cart.");
        }
    }

    private static void updateQuantity(ArrayList<Product> cart) {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        List<Product> uniqueProducts = new ArrayList<>();
        for (Product p : cart) {
            if (!uniqueProducts.contains(p)) {
                uniqueProducts.add(p);
            }
        }

        System.out.println("Your cart items:");
        for (int i = 0; i < uniqueProducts.size(); i++) {
            final int index = i;
            int count = (int) cart.stream().filter(p -> p.equals(uniqueProducts.get(index))).count();
            System.out.printf("%d. %s - Quantity: %d\n", i + 1, uniqueProducts.get(i).name, count);
        }

        System.out.print("Enter the number of the item to update quantity: ");
        int itemNum = getIntInput();
        if (itemNum < 1 || itemNum > uniqueProducts.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Product selectedProduct = uniqueProducts.get(itemNum - 1);
        System.out.print("Enter new quantity (0 to remove): ");
        int newQty = getIntInput();
        if (newQty < 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        cart.removeIf(p -> p.equals(selectedProduct));

        for (int i = 0; i < newQty; i++) {
            cart.add(selectedProduct);
        }
        System.out.println("Quantity updated.");
        displayCart(cart); 
    }

    private static void removeFromCart(ArrayList<Product> cart) {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        List<Product> uniqueProducts = new ArrayList<>();
        for (Product p : cart) {
            if (!uniqueProducts.contains(p)) {
                uniqueProducts.add(p);
            }
        }

        System.out.println("Your cart items:");
        for (int i = 0; i < uniqueProducts.size(); i++) {
            final int index = i;
            int count = (int) cart.stream().filter(p -> p.equals(uniqueProducts.get(index))).count();
            System.out.printf("%d. %s - Quantity: %d\n", i + 1, uniqueProducts.get(i).name, count);
        }

        System.out.print("Enter the number of the item to remove: ");
        int itemNum = getIntInput();
        if (itemNum < 1 || itemNum > uniqueProducts.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Product selectedProduct = uniqueProducts.get(itemNum - 1);
        cart.removeIf(p -> p.equals(selectedProduct));
        System.out.println("Item removed from cart.");
    }

    private static void displayCart(ArrayList<Product> cart) {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        List<Product> uniqueProducts = new ArrayList<>();
        for (Product p : cart) {
            if (!uniqueProducts.contains(p)) {
                uniqueProducts.add(p);
            }
        }

        double total = 0;
        System.out.println("\n========= YOUR CART =========");
        for (Product p : uniqueProducts) {
            int count = (int) cart.stream().filter(x -> x.equals(p)).count();
            System.out.printf("%dX %-25s PHP %.2f\n", count, p.name, p.price);
            total += p.price * count;
        }
        System.out.printf("TOTAL: PHP %.2f\n", total);
    }

    private static void checkout(ArrayList<Product> cart) {
        double total = 0;
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n========= RECEIPT =========\n");

        List<Product> uniqueProducts = new ArrayList<>();
        for (Product p : cart) {
            if (!uniqueProducts.contains(p)) {
                uniqueProducts.add(p);
            }
        }

        for (Product p : uniqueProducts) {
            int count = (int) cart.stream().filter(x -> x.equals(p)).count();
            receipt.append(String.format("%2dX %-25s PHP %.2f\n", count, p.name, p.price));
            total += p.price * count;
        }
        receipt.append(String.format("TOTAL: PHP %.2f\n", total));
        System.out.print(receipt.toString());

        double payment;
        while (true) {
            System.out.print("Enter payment amount: PHP ");
            if (sc.hasNextDouble()) {
                payment = sc.nextDouble();
                sc.nextLine();
                if (payment < total) {
                    System.out.println("Insufficient payment. Please enter a sufficient amount.");
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid input. Please enter a valid amount.");
                sc.nextLine();
            }
        }

        double change = payment - total;
        System.out.printf("CHANGE: PHP %.2f\n", change);
        System.out.println("Transaction complete. Thank you!");

        logTransaction(cart, total);
    }

    private static void logTransaction(ArrayList<Product> cart, double total) {
        String fileName = "transactions.txt";
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("=== Transaction ===\n");
            writer.write("Date & Time: " + timestamp + "\n");
            writer.write("Cashier: " + loggedInUser + "\n");

            List<Product> uniqueProducts = new ArrayList<>();
            for (Product p : cart) {
                if (!uniqueProducts.contains(p)) {
                    uniqueProducts.add(p);
                }
            }

            for (Product p : uniqueProducts) {
                int count = (int) cart.stream().filter(x -> x.equals(p)).count();
                writer.write(String.format("%2dX %-25s PHP %.2f\n", count, p.name, p.price));
            }
            writer.write(String.format("TOTAL: PHP %.2f\n", total));
            writer.write("----------------------------\n");
        } catch (IOException e) {
            System.out.println("Error writing transaction log: " + e.getMessage());
        }
    }

    private static boolean askAnotherTransaction() {
        while (true) {
            System.out.print("Would you like to make another transaction? (y/n): ");
            String answer = sc.nextLine().trim().toLowerCase();
            if (answer.equals("y")) return true;
            else if (answer.equals("n")) return false;
            else System.out.println("Invalid input. Enter 'y' or 'n'.");
        }
    }

    private static int getIntInput() {
        String input = sc.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
