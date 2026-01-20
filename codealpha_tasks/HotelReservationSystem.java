import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Enum for room categories
enum RoomType {
    STANDARD(1000), DELUXE(2000), SUITE(5000);
    
    private final double pricePerNight;
    
    RoomType(double price) {
        this.pricePerNight = price;
    }
    
    public double getPricePerNight() {
        return pricePerNight;
    }
}

// Room class
class Room {
    private int roomNumber;
    private RoomType type;
    private boolean isAvailable;
    
    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.isAvailable = true;
    }
    
    // Getters and setters
    public int getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
    
    public double getPricePerNight() {
        return type.getPricePerNight();
    }
    
    @Override
    public String toString() {
        return String.format("Room %d [%s] - ₹%.0f/night - %s", 
            roomNumber, type, getPricePerNight(), 
            isAvailable ? "Available" : "Booked");
    }
}

// Customer class
class Customer {
    private String name;
    private String phone;
    private String email;
    
    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
    
    // Getters
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", name, phone);
    }
}

// Booking class
class Booking {
    private int bookingId;
    private Customer customer;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalAmount;
    private String status;
    
    public Booking(int bookingId, Customer customer, Room room, 
                   LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = calculateTotal();
        this.status = "CONFIRMED";
    }
    
    private double calculateTotal() {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return nights * room.getPricePerNight();
    }
    
    // Getters
    public int getBookingId() { return bookingId; }
    public Customer getCustomer() { return customer; }
    public Room getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format(
            "Booking #%d | %s | %s | %s to %s | ₹%.0f | %s",
            bookingId, customer, room,
            checkIn.format(formatter), checkOut.format(formatter),
            totalAmount, status
        );
    }
}

// HotelManagementSystem class
class HotelManagementSystem {
    private List<Room> rooms;
    private List<Booking> bookings;
    private List<Customer> customers;
    private int nextBookingId;
    
    public HotelManagementSystem() {
        this.rooms = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.nextBookingId = 1;
        initializeRooms();
    }
    
    private void initializeRooms() {
        // Initialize sample rooms
        RoomType[] types = {RoomType.STANDARD, RoomType.STANDARD, RoomType.DELUXE, 
                           RoomType.DELUXE, RoomType.SUITE};
        for (int i = 1; i <= 5; i++) {
            rooms.add(new Room(i, types[i-1]));
        }
    }
    
    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomers();
    }
    
    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable() && !isRoomBooked(room, checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }
    
    private boolean isRoomBooked(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Booking booking : bookings) {
            if (booking.getRoom() == room && 
                !checkOut.isBefore(booking.getCheckIn()) && 
                !checkIn.isAfter(booking.getCheckOut())) {
                return true;
            }
        }
        return false;
    }
    
    public Booking makeBooking(Customer customer, Room room, 
                              LocalDate checkIn, LocalDate checkOut) {
        if (!room.isAvailable()) {
            return null;
        }
        
        Booking booking = new Booking(nextBookingId++, customer, room, checkIn, checkOut);
        bookings.add(booking);
        room.setAvailable(false);
        
        saveBookings();
        saveRooms();
        return booking;
    }
    
    public boolean cancelBooking(int bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking != null && "CONFIRMED".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            booking.getRoom().setAvailable(true);
            saveBookings();
            saveRooms();
            return true;
        }
        return false;
    }
    
    private Booking findBooking(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }
    
    public List<Booking> getCustomerBookings(String phone) {
        List<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCustomer().getPhone().equals(phone)) {
                customerBookings.add(booking);
            }
        }
        return customerBookings;
    }
    
    // File I/O operations
    private static final String ROOMS_FILE = "rooms.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    
    private void saveRooms() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room room : rooms) {
                writer.println(room.getRoomNumber() + "|" + room.getType() + "|" + room.isAvailable());
            }
        } catch (IOException e) {
            System.out.println("Error saving rooms: " + e.getMessage());
        }
    }
    
    private void saveBookings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                writer.println(booking.getBookingId() + "|" + 
                             booking.getCustomer().getPhone() + "|" +
                             booking.getRoom().getRoomNumber() + "|" +
                             booking.getCheckIn().format(fmt) + "|" +
                             booking.getCheckOut().format(fmt) + "|" +
                             booking.getStatus());
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }
    
    private void saveCustomers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.println(customer.getName() + "|" + customer.getPhone() + "|" + customer.getEmail());
            }
        } catch (IOException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }
    
    // Load methods can be implemented similarly for persistence
}

// Main Menu class with console interface
public class HotelReservationSystem {
    private static HotelManagementSystem hotel;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        hotel = new HotelManagementSystem();
        System.out.println("🏨 Welcome to Hotel Reservation System!");
        showMenu();
    }
    
    private static void showMenu() {
        while (true) {
            System.out.println("\n=== HOTEL RESERVATION SYSTEM ===");
            System.out.println("1. Search Available Rooms");
            System.out.println("2. Make New Booking");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. View All Rooms");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1: searchRooms(); break;
                case 2: makeBooking(); break;
                case 3: viewBookings(); break;
                case 4: cancelBooking(); break;
                case 5: listAllRooms(); break;
                case 6: System.out.println("Thank you! 👋"); return;
                default: System.out.println("Invalid option!");
            }
        }
    }
    
    private static void searchRooms() {
        System.out.print("Enter check-in date (yyyy-MM-dd): ");
        LocalDate checkIn = parseDate();
        System.out.print("Enter check-out date (yyyy-MM-dd): ");
        LocalDate checkOut = parseDate();
        
        if (checkOut.isBefore(checkIn)) {
            System.out.println("Check-out must be after check-in!");
            return;
        }
        
        List<Room> available = hotel.searchAvailableRooms(checkIn, checkOut);
        System.out.println("\nAvailable Rooms:");
        if (available.isEmpty()) {
            System.out.println("No rooms available for selected dates.");
        } else {
            for (Room room : available) {
                System.out.println(room);
            }
        }
    }
    
    private static void makeBooking() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        Customer customer = new Customer(name, phone, email);
        hotel.addCustomer(customer);
        
        System.out.print("Enter room number: ");
        int roomNum = getIntInput();
        Room room = findRoom(roomNum);
        if (room == null) {
            System.out.println("Room not found!");
            return;
        }
        
        System.out.print("Enter check-in date (yyyy-MM-dd): ");
        LocalDate checkIn = parseDate();
        System.out.print("Enter check-out date (yyyy-MM-dd): ");
        LocalDate checkOut = parseDate();
        
        Booking booking = hotel.makeBooking(customer, room, checkIn, checkOut);
        if (booking != null) {
            System.out.println("\n✅ Booking confirmed!");
            System.out.println(booking);
            simulatePayment(booking.getTotalAmount());
        } else {
            System.out.println("❌ Booking failed. Room not available.");
        }
    }
    
    private static void simulatePayment(double amount) {
        System.out.println("\n💳 Payment Simulation:");
        System.out.printf("Total Amount: ₹%.0f\n", amount);
        System.out.println("Payment processed successfully! 🎉");
    }
    
    private static void viewBookings() {
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        List<Booking> bookings = hotel.getCustomerBookings(phone);
        
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            System.out.println("\nYour Bookings:");
            for (Booking b : bookings) {
                System.out.println(b);
            }
        }
    }
    
    private static void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = getIntInput();
        if (hotel.cancelBooking(bookingId)) {
            System.out.println("✅ Booking cancelled successfully!");
        } else {
            System.out.println("❌ Booking not found or already cancelled.");
        }
    }
    
    private static void listAllRooms() {
        System.out.println("\nAll Rooms:");
        for (Room room : hotel.rooms) {
            System.out.println(room);
        }
    }
    
    private static Room findRoom(int roomNum) {
        for (Room room : hotel.rooms) {
            if (room.getRoomNumber() == roomNum) {
                return room;
            }
        }
        return null;
    }
    
    private static LocalDate parseDate() {
        String dateStr = scanner.nextLine();
        return LocalDate.parse(dateStr);
    }
    
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }
}
