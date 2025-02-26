package org.project.student.ui;


import org.hibernate.Hibernate;
import org.project.student.dao.ResultDao;
import org.project.student.entity.Marks;
import org.project.student.entity.Result;
import org.project.student.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
@SuppressWarnings("All")
public class StudentUI {
    private static final Logger logger = LoggerFactory.getLogger(StudentUI.class);
    private final Scanner scanner;
    private final ResultDao resultDao;

    public StudentUI(Scanner scanner) {
        this.scanner = scanner;
        this.resultDao = new ResultDao();
    }

    public void showStudentMenu(Student student) {
        boolean running = true;

        while (running) {
            displayStudentMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewAllResults(student);
                    break;
                case 2:
                    viewResultBySemester(student);
                    break;
                case 3:
                    running = false;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayStudentMenu() {
        System.out.println("\n==========================================================");
        System.out.println("                  STUDENT DASHBOARD");
        System.out.println("==========================================================");
        System.out.println("1. View All Results");
        System.out.println("2. View Result by Semester");
        System.out.println("3. Logout");
        System.out.println("==========================================================");
    }

    private void viewAllResults(Student student) {
        System.out.println("\n--- All Results for " + student.getName() + " (" + student.getUsn() + ") ---");

        List<Result> results = resultDao.findByStudent(student);

        if (results.isEmpty()) {
            System.out.println("No results found for you!");
            return;
        }

        for (Result result : results) {
            // Add this line to initialize marksList before displaying
            Hibernate.initialize(result.getMarksList());
            displayResult(result);
        }
    }

    private void viewResultBySemester(Student student) {
        System.out.println("\n--- View Result by Semester ---");

        int semester = getIntInput("Enter semester number (1-8): ");

        if (semester < 1 || semester > 8) {
            System.out.println("Invalid semester number. Must be between 1 and 8.");
            return;
        }

        Result result = resultDao.findByStudentAndSemester(student, semester);

        if (result == null) {
            System.out.println("No result found for semester " + semester + "!");
            return;
        }

        displayResult(result);
    }

    private void displayResult(Result result) {
        System.out.println("\n==========================================================");
        System.out.println("                     RESULT CARD");
        System.out.println("==========================================================");
        System.out.println("Student Name: " + result.getStudent().getName());
        System.out.println("USN: " + result.getStudent().getUsn());
        System.out.println("Semester: " + result.getSemester());
        System.out.println("----------------------------------------------------------");
        System.out.printf("%-30s %-15s %-15s\n", "Subject", "Marks", "Status");
        System.out.println("----------------------------------------------------------");
        Hibernate.initialize(result.getMarksList());
        for (Marks marks : result.getMarksList()) {
            System.out.printf("%-30s %-15d %-15s\n",
                    marks.getSubject().getName(),
                    marks.getMarksObtained(),
                    marks.getStatus()
            );
        }

        System.out.println("----------------------------------------------------------");
        System.out.println("Total Marks: " + result.getTotalMarks());
        System.out.println("Overall Status: " + result.getStatus());
        System.out.println("==========================================================\n");
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