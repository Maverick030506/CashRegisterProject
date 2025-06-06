import java.util.*;
import java.util.regex.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat; 


class Product {
    String name;
    int quantity;
    double price;

    public Product(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    
    public double getTotalPrice() {
        return quantity * price;
    }
}


class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

public class Main {
    
    static Scanner input = new Scanner(System.in);
    
    static ArrayList<User> users = new ArrayList<>();
   
    static User loggedInUser = null;

    public static void main(String[] args) {
        String choice;

        
        do {
            System.out.println("\n==== Welcome to Cash Register ====");
            System.out.println("1. Sign Up");
            System.out.println("2. Log In");
            System.out.println("3. Log Out");
            System.out.println("4. Use Cash Register (must be logged in)");
            System.out.println("E. Exit");
            System.out.print("Choose an option: ");
            choice = input.nextLine();

            switch (choice) {
                case "1":
                    signup(); 
                    break;
                case "2":
                    login(); 
                    break;
                case "3":
                    logout(); 
                    break;
                case "4":
                    
                    if (loggedInUser != null) {
                        runCashRegister(); 
                    } else {
                        System.out.println("You must be logged in to use the cash register.");
                    }
                    break;
                case "e":
                case "E":
                    System.out.println("Thank you for using the Cash Register!");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        } while (!choice.equalsIgnoreCase("e")); 

        input.close(); 
    }

   
    public static void signup() {
        
        Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]{5,15}$");
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,20}$");

        System.out.println("==== Signup ====");
        while (true) { 
            System.out.print("Enter new username (5-15 alphanumeric characters): ");
            String username = input.nextLine();
            Matcher userMatch = usernamePattern.matcher(username);

            if (!userMatch.matches()) {
                System.out.println("Invalid username format. Username must be 5-15 alphanumeric characters.");
                continue; 
            }

            
            for (User u : users) {
                if (u.username.equals(username)) {
                    System.out.println("Username already exists. Please choose a different one.");
                    return; 
                }
            }

            System.out.print("Enter new password (8-20 characters, at least one uppercase, one digit): ");
            String password = input.nextLine();
            Matcher passMatch = passwordPattern.matcher(password);

            if (!passMatch.matches()) {
                System.out.println("Invalid password format. Password must be 8-20 characters long, include at least one uppercase letter and one digit.");
                continue; 
            }

            
            users.add(new User(username, password));
            System.out.println("Signup successful!\n");
            break; 
        }
    }

    
    public static void login() {
        System.out.println("==== Login ====");
        System.out.print("Enter username: ");
        String enteredUsername = input.nextLine();
        System.out.print("Enter password: ");
        String enteredPassword = input.nextLine();

        
        for (User user : users) {
            if (user.username.equals(enteredUsername) && user.password.equals(enteredPassword)) {
                loggedInUser = user; 
                System.out.println("Login successful! Welcome, " + user.username + "\n");
                return; 
            }
        }

        System.out.println("Incorrect username or password.\n"); 
    }

    
    public static void logout() {
        if (loggedInUser != null) {
            System.out.println("User " + loggedInUser.username + " has been logged out.");
            loggedInUser = null; 
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    
    public static void runCashRegister() {
        boolean continueTransaction;

        do { 
            ArrayList<Product> cart = new ArrayList<>(); 
            String cartChoice;

            
            do {
                System.out.println("\n==== Cash Register - Manage Cart ====");
                System.out.println("1. Add Product to Cart");
                System.out.println("2. Update Product Quantity in Cart");
                System.out.println("3. Remove Product from Cart");
                System.out.println("4. Display Current Cart");
                System.out.println("P. Proceed to Checkout");
                System.out.print("Choose an option: ");
                cartChoice = input.nextLine();

                switch (cartChoice) {
                    case "1":
                        addProductToCart(cart); 
                        break;
                    case "2":
                        updateProductInCart(cart); 
                        break;
                    case "3":
                        removeProductFromCart(cart); 
                        break;
                    case "4":
                        displayCart(cart); 
                        break;
                    case "p":
                    case "P":
                        if (cart.isEmpty()) { 
                            System.out.println("Cart is empty. Please add items before proceeding to checkout.");
                            cartChoice = ""; 
                        } else {
                            System.out.println("Proceeding to checkout...");
                        }
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } while (!cartChoice.equalsIgnoreCase("p")); 

            
            double total = 0;
            System.out.println("\n==== Receipt ====");
            if (cart.isEmpty()) { 
                System.out.println("No items in the cart.");
            } else {
                for (Product p : cart) {
                    
                    System.out.println(p.quantity + " x " + p.name + " @ P" + String.format("%.2f", p.price) + " each = P" + String.format("%.2f", p.getTotalPrice()));
                    total += p.getTotalPrice();
                }
            }
            System.out.println("------------------------------------------");
            System.out.println("Total: P" + String.format("%.2f", total)); 

            
            double payment = -1.0;
            do {
                try {
                    System.out.print("Enter payment amount: ");
                    payment = Double.parseDouble(input.nextLine());
                    if (payment < total) {
                        System.out.println("Insufficient payment. Please enter at least P" + String.format("%.2f", total));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number for payment.");
                    payment = -1.0; 
                }
            } while (payment < total);

            double change = payment - total;
            System.out.println("Change: P" + String.format("%.2f", change)); 

            
            logTransaction(cart, total, loggedInUser.username); 

            
            System.out.print("Do you want to perform another transaction? (yes/no): ");
            continueTransaction = input.nextLine().equalsIgnoreCase("yes");

        } while (continueTransaction); 
    }

    
    public static void addProductToCart(ArrayList<Product> cart) {
        System.out.println("------------------------------------------");
        System.out.print("Enter product name: ");
        String name = input.nextLine();

        int quantity = -1;
        while (quantity < 0) { 
            try {
                System.out.print("Enter quantity: ");
                quantity = Integer.parseInt(input.nextLine());
                if (quantity < 0) {
                    System.out.println("Quantity cannot be negative. Please enter a valid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number for quantity.");
            }
        }

        double price = -1.0;
        while (price < 0) { 
            try {
                System.out.print("Enter price per unit: ");
                price = Double.parseDouble(input.nextLine());
                if (price < 0) {
                    System.out.println("Price cannot be negative. Please enter a valid number.");
                }
            } catch (NumberFormatException e) { 
                System.out.println("Invalid input. Please enter a valid number for price.");
            }
        }

        
        boolean found = false;
        for (Product p : cart) {
            if (p.name.equalsIgnoreCase(name)) {
                p.quantity += quantity; 
                System.out.println("Updated quantity for " + name + ". New quantity: " + p.quantity + ".");
                found = true;
                break;
            }
        }
        if (!found) { 
            cart.add(new Product(name, quantity, price));
            System.out.println(name + " added to cart.");
        }
    }

    
    public static void updateProductInCart(ArrayList<Product> cart) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. No products to update.");
            return;
        }
        displayCart(cart); 
        System.out.println("------------------------------------------");
        System.out.print("Enter name of product to update: ");
        String nameToUpdate = input.nextLine();

        boolean found = false;
        for (Product p : cart) {
            if (p.name.equalsIgnoreCase(nameToUpdate)) {
                int newQuantity = -1;
                while (newQuantity < 0) { 
                    try {
                        System.out.print("Enter new quantity for " + p.name + ": ");
                        newQuantity = Integer.parseInt(input.nextLine());
                        if (newQuantity < 0) {
                            System.out.println("Quantity cannot be negative. Please enter a valid number.");
                        }
                    } catch (NumberFormatException e) { 
                        System.out.println("Invalid input. Please enter a whole number for quantity.");
                    }
                }
                p.quantity = newQuantity; 
                System.out.println("Quantity for " + p.name + " updated to " + p.quantity + ".");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Product '" + nameToUpdate + "' not found in cart.");
        }
    }

    
    public static void removeProductFromCart(ArrayList<Product> cart) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. No products to remove.");
            return;
        }
        displayCart(cart); 
        System.out.println("------------------------------------------");
        System.out.print("Enter name of product to remove: ");
        String nameToRemove = input.nextLine();

        boolean removed = false;
        
        Iterator<Product> iterator = cart.iterator();
        while (iterator.hasNext()) {
            Product p = iterator.next();
            if (p.name.equalsIgnoreCase(nameToRemove)) {
                iterator.remove(); 
                System.out.println("Product '" + nameToRemove + "' removed from cart.");
                removed = true;
                break;
            }
        }
        if (!removed) {
            System.out.println("Product '" + nameToRemove + "' not found in cart.");
        }
    }

    
    public static void displayCart(ArrayList<Product> cart) {
        System.out.println("\n==== Current Cart Contents ====");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        double currentTotal = 0;
        for (int i = 0; i < cart.size(); i++) {
            Product p = cart.get(i);
            
            System.out.println((i + 1) + ". " + p.quantity + " x " + p.name + " @ P" + String.format("%.2f", p.price) + " each = P" + String.format("%.2f", p.getTotalPrice()));
            currentTotal += p.getTotalPrice();
        }
        System.out.println("------------------------------------------");
        System.out.println("Current Cart Total: P" + String.format("%.2f", currentTotal));
        System.out.println("------------------------------------------");
    }

    
    public static void logTransaction(ArrayList<Product> cart, double totalAmount, String username) {
        
        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(); 
            writer.write("-------------------- Transaction --------------------\n");
            writer.write("Date and Time: " + formatter.format(date) + "\n");
            writer.write("Cashier: " + username + "\n");
            writer.write("Items Purchased:\n");
            for (Product p : cart) {
                
                writer.write(String.format("  - %s (Qty: %d, Price: P%.2f) = P%.2f\n", p.name, p.quantity, p.price, p.getTotalPrice()));
            }
            writer.write(String.format("Total Amount: P%.2f\n", totalAmount)); 
            writer.write("---------------------------------------------------\n\n");
            System.out.println("Transaction logged successfully to transactions.txt");
        } catch (IOException e) {
            
            System.err.println("Error writing to transaction log file: " + e.getMessage());
        }
    }
}