import java.util.ArrayList;
import java.util.Scanner;

class Student {
    private String name;
    private int score; // 0 - 100

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }
}

public class StudentGradeManager {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        int choice;
        do {
            System.out.println("\n=== Student Grade Manager ===");
            System.out.println("1. Add student");
            System.out.println("2. Show summary report");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                scanner.next(); // discard invalid input
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addStudent(scanner, students);
                    break;
                case 2:
                    showSummary(students);
                    break;
                case 3:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 3);

        scanner.close();
    }

    private static void addStudent(Scanner scanner, ArrayList<Student> students) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        int score;
        while (true) {
            System.out.print("Enter student score (0-100): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid integer score (0-100): ");
                scanner.next(); // discard invalid input
            }
            score = scanner.nextInt();
            scanner.nextLine(); // consume newline
            if (score >= 0 && score <= 100) {
                break;
            } else {
                System.out.println("Score must be between 0 and 100.");
            }
        }

        students.add(new Student(name, score));
        System.out.println("Student added successfully.");
    }

    private static void showSummary(ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        int total = 0;
        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;
        Student highestStudent = null;
        Student lowestStudent = null;

        for (Student s : students) {
            int score = s.getScore();
            total += score;

            if (score > highest) {
                highest = score;
                highestStudent = s;
            }
            if (score < lowest) {
                lowest = score;
                lowestStudent = s;
            }
        }

        double average = (double) total / students.size();

        // Top summary
        System.out.println("\n--- Summary Report ---");
        System.out.println("Number of students: " + students.size());
        System.out.printf("Average score: %.2f%n", average);
        System.out.println("Highest score: " + highest + " (Student: " + highestStudent.getName() + ")");
        System.out.println("Lowest score: " + lowest + " (Student: " + lowestStudent.getName() + ")");

        // Table of all students
        System.out.println("\nAll students:");
        System.out.printf("%-5s %-20s %-5s%n", "No.", "Name", "Score");
        int index = 1;
        for (Student s : students) {
            System.out.printf("%-5d %-20s %-5d%n",
                    index++, s.getName(), s.getScore());
        }
        System.out.println("\n--- Summary ---");
        System.out.printf("Average score: %.2f%n", average);
        System.out.println("Highest score: " + highest + " (Student: " + highestStudent.getName() + ")");
        System.out.println("Lowest score: " + lowest + " (Student: " + lowestStudent.getName() + ")");
    }
}