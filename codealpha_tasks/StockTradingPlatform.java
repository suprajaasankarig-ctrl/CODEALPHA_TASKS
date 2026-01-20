import java.util.*;
import java.io.*;

class Stock {
    private String symbol;
    private String name;
    private double price;
    private Random rand = new Random();

    public Stock(String symbol, String name, double initialPrice) {
        this.symbol = symbol;
        this.name = name;
        this.price = initialPrice;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void updatePrice() {
        // Simulate price fluctuation ±5%
        double change = (rand.nextDouble() - 0.5) * 0.1;
        price += price * change;
        if (price < 1.0) price = 1.0; // Minimum price
    }
}

class PortfolioItem {
    private Stock stock;
    private int quantity;
    private double avgBuyPrice;

    public PortfolioItem(Stock stock, int quantity, double buyPrice) {
        this.stock = stock;
        this.quantity = quantity;
        this.avgBuyPrice = buyPrice;
    }

    public Stock getStock() { return stock; }
    public int getQuantity() { return quantity; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public double getCurrentValue() { return quantity * stock.getPrice(); }
    public double getProfitLoss() { return getCurrentValue() - (quantity * avgBuyPrice); }

    public void addShares(int qty, double price) {
        // Update average buy price
        double totalCost = (quantity * avgBuyPrice) + (qty * price);
        avgBuyPrice = totalCost / (quantity + qty);
        quantity += qty;
    }

    public boolean sellShares(int qty) {
        if (qty > quantity) return false;
        quantity -= qty;
        return quantity == 0;
    }
}

class Transaction {
    private String type; // "BUY" or "SELL"
    private Stock stock;
    private int quantity;
    private double price;
    private Date date;

    public Transaction(String type, Stock stock, int quantity, double price) {
        this.type = type;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.date = new Date();
    }

    @Override
    public String toString() {
        return String.format("%s %s: %d shares @ %.2f on %s", type, stock.getSymbol(), quantity, price, date);
    }
}

class User {
    private String name;
    private double cash;
    private List<PortfolioItem> portfolio = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, PortfolioItem> portfolioMap = new HashMap<>();

    public User(String name, double initialCash) {
        this.name = name;
        this.cash = initialCash;
    }

    public String getName() { return name; }
    public double getCash() { return cash; }
    public List<PortfolioItem> getPortfolio() { return portfolio; }
    public List<Transaction> getTransactions() { return transactions; }
    public double getTotalValue() {
        double total = cash;
        for (PortfolioItem item : portfolio) {
            total += item.getCurrentValue();
        }
        return total;
    }

    public boolean buyStock(Stock stock, int quantity) {
        double cost = stock.getPrice() * quantity;
        if (cost > cash) return false;

        cash -= cost;
        PortfolioItem item = portfolioMap.get(stock.getSymbol());
        if (item == null) {
            item = new PortfolioItem(stock, quantity, stock.getPrice());
            portfolio.add(item);
            portfolioMap.put(stock.getSymbol(), item);
        } else {
            item.addShares(quantity, stock.getPrice());
        }
        transactions.add(new Transaction("BUY", stock, quantity, stock.getPrice()));
        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {
        PortfolioItem item = portfolioMap.get(stock.getSymbol());
        if (item == null || item.getQuantity() < quantity) return false;

        double revenue = stock.getPrice() * quantity;
        cash += revenue;
        item.sellShares(quantity);
        if (item.getQuantity() == 0) {
            portfolio.remove(item);
            portfolioMap.remove(stock.getSymbol());
        }
        transactions.add(new Transaction("SELL", stock, quantity, stock.getPrice()));
        return true;
    }
}

public class StockTradingPlatform {
    private static List<Stock> market = new ArrayList<>();
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);
    private static final String FILENAME = "portfolio.txt";

    public static void main(String[] args) {
        initializeMarket();
        loadPortfolio();
        System.out.println("Welcome to Stock Trading Platform!");
        showMenu();
    }

    private static void initializeMarket() {
        market.add(new Stock("AAPL", "Apple Inc.", 150.0));
        market.add(new Stock("GOOGL", "Google", 2800.0));
        market.add(new Stock("MSFT", "Microsoft", 300.0));
        market.add(new Stock("TSLA", "Tesla", 700.0));
    }

    private static void showMenu() {
        while (true) {
            System.out.println("\n=== STOCK TRADING PLATFORM ===");
            System.out.printf("Cash: $%.2f | Total Value: $%.2f%n", currentUser.getCash(), currentUser.getTotalValue());
            System.out.println("1. View Market");
            System.out.println("2. View Portfolio");
            System.out.println("3. Buy Stock");
            System.out.println("4. Sell Stock");
            System.out.println("5. View Transactions");
            System.out.println("6. Update Market Prices");
            System.out.println("7. Exit & Save");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: displayMarket(); break;
                case 2: displayPortfolio(); break;
                case 3: buyStock(); break;
                case 4: sellStock(); break;
                case 5: displayTransactions(); break;
                case 6: updateMarket(); break;
                case 7: savePortfolio(); System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void displayMarket() {
        System.out.println("\n--- MARKET DATA ---");
        for (Stock stock : market) {
            System.out.printf("%s (%s): $%.2f%n", stock.getSymbol(), stock.getName(), stock.getPrice());
        }
    }

    private static void displayPortfolio() {
        System.out.println("\n--- PORTFOLIO ---");
        System.out.printf("%-8s %-10s %-8s %-10s %-10s%n", "Symbol", "Qty", "AvgBuy", "Current", "P&L");
        for (PortfolioItem item : currentUser.getPortfolio()) {
            System.out.printf("%-8s %-10d $%-7.2f $%-7.2f $%.2f%n",
                item.getStock().getSymbol(), item.getQuantity(), item.getAvgBuyPrice(),
                item.getStock().getPrice(), item.getProfitLoss());
        }
    }

    private static void buyStock() {
        displayMarket();
        System.out.print("Enter symbol: ");
        String symbol = scanner.nextLine().toUpperCase();
        Stock stock = findStock(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        System.out.print("Quantity: ");
        int qty = scanner.nextInt();
        if (currentUser.buyStock(stock, qty)) {
            System.out.println("Buy successful!");
        } else {
            System.out.println("Insufficient cash.");
        }
    }

    private static void sellStock() {
        displayPortfolio();
        System.out.print("Enter symbol: ");
        String symbol = scanner.nextLine().toUpperCase();
        PortfolioItem item = getPortfolioItem(symbol);
        if (item == null) {
            System.out.println("Stock not found in portfolio.");
            return;
        }
        System.out.print("Quantity: ");
        int qty = scanner.nextInt();
        if (currentUser.sellStock(item.getStock(), qty)) {
            System.out.println("Sell successful!");
        } else {
            System.out.println("Insufficient quantity.");
        }
    }

    private static void displayTransactions() {
        System.out.println("\n--- TRANSACTIONS ---");
        for (Transaction t : currentUser.getTransactions()) {
            System.out.println(t);
        }
    }

    private static void updateMarket() {
        for (Stock stock : market) {
            stock.updatePrice();
        }
        System.out.println("Market updated!");
        displayMarket();
    }

    private static Stock findStock(String symbol) {
        for (Stock s : market) {
            if (s.getSymbol().equals(symbol)) return s;
        }
        return null;
    }

    private static PortfolioItem getPortfolioItem(String symbol) {
        for (PortfolioItem item : currentUser.getPortfolio()) {
            if (item.getStock().getSymbol().equals(symbol)) return item;
        }
        return null;
    }

    private static void loadPortfolio() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        currentUser = new User(name, 10000.0); // $10k initial cash
        try (Scanner fileScanner = new Scanner(new File(FILENAME))) {
            while (fileScanner.hasNextLine()) {
                // Simple load: assume format "name cash" for now, extend as needed
            }
        } catch (FileNotFoundException e) {
            // New user
        }
        System.out.println("Portfolio loaded.");
    }

    private static void savePortfolio() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME))) {
            writer.println(currentUser.getName() + " " + currentUser.getCash());
            // Save portfolio and transactions
        } catch (IOException e) {
            System.out.println("Save failed.");
        }
    }
}