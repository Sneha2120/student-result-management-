package org.project.student.ui;



import org.project.student.dao.ResultDao;
import org.project.student.dao.StudentDao;
import org.project.student.dao.SubjectDao;
import org.project.student.entity.Admin;
import org.project.student.entity.Result;
import org.project.student.entity.Student;
import org.project.student.entity.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("All")
public class AdminUI {
    private static final Logger logger = LoggerFactory.getLogger(AdminUI.class);
    private final Scanner scanner;
    private final StudentDao studentDao;
    private final SubjectDao subjectDao;
    private final ResultDao resultDao;

    public AdminUI(Scanner scanner) {
        this.scanner = scanner;
        this.studentDao = new StudentDao();
        this.subjectDao = new SubjectDao();
        this.resultDao = new ResultDao();

        // Initialize subjects
        subjectDao.initialize();
    }

    public void showAdminMenu(Admin admin) {
        boolean running = true;

        while (running) {
            displayAdminMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    addSubject();
                    break;
                case 3:
                    addResult();
                    break;
                case 4:
                    viewAllStudents();
                    break;
                case 5:
                    viewAllSubjects();
                    break;
                case 6:
                    running = false;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayAdminMenu() {
        System.out.println("\n==========================================================");
        System.out.println("                    ADMIN DASHBOARD");
        System.out.println("==========================================================");
        System.out.println("1. Add New Student");
        System.out.println("2. Add New Subject");
        System.out.println("3. Add Result");
        System.out.println("4. View All Students");
        System.out.println("5. View All Subjects");
        System.out.println("6. Logout");
        System.out.println("==========================================================");
    }

    private void addStudent() {
        System.out.println("\n--- Add New Student ---");

        String usn = getStringInput("Enter USN: ");
        // Check if student with this USN already exists
        if (studentDao.findByUsn(usn) != null) {
            System.out.println("A student with this USN already exists!");
            return;
        }

        String name = getStringInput("Enter Name: ");
        String password = getStringInput("Enter Password: ");
        String email = getStringInput("Enter Email: ");

        Student student = new Student(usn, name, password, email);
        studentDao.saveStudent(student);

        System.out.println("Student added successfully!");
    }

    private void addSubject() {
        System.out.println("\n--- Add New Subject ---");

        String code = getStringInput("Enter Subject Code: ");
        // Check if subject with this code already exists
        if (subjectDao.findByCode(code) != null) {
            System.out.println("A subject with this code already exists!");
            return;
        }

        String name = getStringInput("Enter Subject Name: ");
        int maxMarks = getIntInput("Enter Maximum Marks (default 100): ");
        if (maxMarks <= 0) maxMarks = 100;

        int passMarks = getIntInput("Enter Pass Marks (default 35): ");
        if (passMarks <= 0) passMarks = 35;

        Subject subject = new Subject(code, name, maxMarks, passMarks);
        subjectDao.saveSubject(subject);

        System.out.println("Subject added successfully!");
    }

    private void addResult() {
        System.out.println("\n--- Add Result ---");

        // Get student
        String usn = getStringInput("Enter Student USN: ");
        Student student = studentDao.findByUsn(usn);

        if (student == null) {
            System.out.println("No student found with this USN!");
            return;
        }

        // Get semester
        int semester = getIntInput("Enter Semester: ");
        if (semester <= 0 || semester > 8) {
            System.out.println("Invalid semester! Must be between 1 and 8.");
            return;
        }

        // Get available subjects
        List<Subject> allSubjects = subjectDao.getAllSubjects();
        if (allSubjects.isEmpty()) {
            System.out.println("No subjects found! Please add subjects first.");
            return;
        }

        // Show available subjects
        System.out.println("\nAvailable Subjects:");
        for (int i = 0; i < allSubjects.size(); i++) {
            System.out.println((i + 1) + ". " + allSubjects.get(i).getCode() + " - " + allSubjects.get(i).getName());
        }

        // Select subjects for result (maximum 3)
        int subjectCount = Math.min(3, allSubjects.size());
        List<Subject> selectedSubjects = new ArrayList<>();
        List<Integer> marksObtained = new ArrayList<>();

        System.out.println("\nEnter marks for " + subjectCount + " subjects:");

        for (int i = 0; i < subjectCount; i++) {
            int subjectIndex;

            // Get valid subject index
            while (true) {
                subjectIndex = getIntInput("Select subject #" + (i + 1) + " (1-" + allSubjects.size() + "): ") - 1;

                if (subjectIndex >= 0 && subjectIndex < allSubjects.size() &&
                        !selectedSubjects.contains(allSubjects.get(subjectIndex))) {
                    break;
                }

                System.out.println("Invalid selection! Please select a valid, non-duplicate subject.");
            }

            Subject subject = allSubjects.get(subjectIndex);
            selectedSubjects.add(subject);

            // Get marks
            int marks;
            while (true) {
                marks = getIntInput("Enter marks for " + subject.getName() + " (0-" + subject.getMaxMarks() + "): ");

                if (marks >= 0 && marks <= subject.getMaxMarks()) {
                    break;
                }

                System.out.println("Invalid marks! Must be between 0 and " + subject.getMaxMarks() + ".");
            }

            marksObtained.add(marks);
        }

        // Create/update result
        Result result = resultDao.createResult(student, semester, selectedSubjects, marksObtained);

        if (result != null) {
            System.out.println("Result added successfully!");

            // Show result summary
            System.out.println("\n--- Result Summary ---");
            System.out.println("Student: " + student.getName() + " (" + student.getUsn() + ")");
            System.out.println("Semester: " + semester);
            System.out.println("Total Marks: " + result.getTotalMarks());
            System.out.println("Status: " + result.getStatus());

            System.out.println("\nSubject-wise Marks:");
            for (int i = 0; i < result.getMarksList().size(); i++) {
                System.out.println(
                        result.getMarksList().get(i).getSubject().getName() + ": " +
                                result.getMarksList().get(i).getMarksObtained() + "/" +
                                result.getMarksList().get(i).getSubject().getMaxMarks() + " (" +
                                result.getMarksList().get(i).getStatus() + ")"
                );
            }
        } else {
            System.out.println("Failed to add result. Please try again.");
        }
    }

    private void viewAllStudents() {
        System.out.println("\n--- All Students ---");

        List<Student> students = studentDao.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students found!");
            return;
        }

        System.out.println("\nTotal Students: " + students.size());
        System.out.println("-------------------------------");
        System.out.printf("%-15s %-30s %-25s\n", "USN", "Name", "Email");
        System.out.println("-------------------------------");

        for (Student student : students) {
            System.out.printf("%-15s %-30s %-25s\n",
                    student.getUsn(),
                    student.getName(),
                    student.getEmail()
            );
        }
    }

    private void viewAllSubjects() {
        System.out.println("\n--- All Subjects ---");

        List<Subject> subjects = subjectDao.getAllSubjects();

        if (subjects.isEmpty()) {
            System.out.println("No subjects found!");
            return;
        }

        System.out.println("\nTotal Subjects: " + subjects.size());
        System.out.println("----------------------------------------------------------");
        System.out.printf("%-10s %-30s %-15s %-15s\n", "Code", "Name", "Max Marks", "Pass Marks");
        System.out.println("----------------------------------------------------------");

        for (Subject subject : subjects) {
            System.out.printf("%-10s %-30s %-15d %-15d\n",
                    subject.getCode(),
                    subject.getName(),
                    subject.getMaxMarks(),
                    subject.getPassMarks()
            );
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