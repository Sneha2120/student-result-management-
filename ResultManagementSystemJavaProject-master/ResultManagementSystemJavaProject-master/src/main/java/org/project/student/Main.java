package org.project.student;
import org.project.student.config.HibernateUtil;
import org.project.student.ui.LoginUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main
{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Student Result Management System...");

        try (Scanner scanner = new Scanner(System.in)) {
            LoginUI loginUI = new LoginUI(scanner);
            loginUI.start();
        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage(), e);
        } finally {
            HibernateUtil.shutdown();
            logger.info("Student Result Management System terminated.");
        }
    }
}
