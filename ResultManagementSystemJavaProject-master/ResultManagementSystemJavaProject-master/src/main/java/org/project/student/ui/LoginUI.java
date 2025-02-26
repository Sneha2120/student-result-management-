package org.project.student.ui;


import org.project.student.dao.AdminDao;
import org.project.student.dao.StudentDao;
import org.project.student.entity.Admin;
import org.project.student.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

@SuppressWarnings("All")
public class LoginUI {
    private static final Logger logger = LoggerFactory.getLogger(LoginUI.class);
    private final Scanner scanner;
    private final AdminDao adminDao;
    private final StudentDao studentDao;
    private final AdminUI adminUI;
    private final StudentUI studentUI;

    public LoginUI(Scanner scanner) {
        this.scanner = scanner;
        this.adminDao = new AdminDao();
        this.studentDao = new StudentDao();
        this.adminUI = new AdminUI(scanner);
        this.studentUI = new StudentUI(scanner);

        // Initialize sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        adminDao.initialize();
        studentDao.initialize();
    }

    public void start() {
        boolean running = true;

        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    studentLogin();
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using the Student Result Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n==========================================================");
        System.out.println("          STUDENT RESULT MANAGEMENT SYSTEM");
        System.out.println("==========================================================");
        System.out.println("1. Admin Login");
        System.out.println("2. Student Login");
        System.out.println("3. Exit");
        System.out.println("==========================================================");
    }

    private void adminLogin() {
        System.out.println("\n--- Admin Login ---");
        String username = getStringInput("Enter username: ");
        String password = getStringInput("Enter password: ");

        Admin admin = adminDao.validateLogin(username, password);

        if (admin != null) {
            System.out.println("Login successful. Welcome, " + admin.getUsername() + "!");
            adminUI.showAdminMenu(admin);
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private void studentLogin() {
        System.out.println("\n--- Student Login ---");
        String usn = getStringInput("Enter USN: ");
        String password = getStringInput("Enter password: ");

        Student student = studentDao.validateLogin(usn, password);

        if (student != null) {
            System.out.println("Login successful. Welcome, " + student.getName() + "!");
            studentUI.showStudentMenu(student);
        } else {
            System.out.println("Invalid USN or password. Please try again.");
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}