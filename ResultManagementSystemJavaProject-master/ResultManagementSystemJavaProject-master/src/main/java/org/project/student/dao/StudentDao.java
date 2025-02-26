package org.project.student.dao;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.project.student.config.HibernateUtil;
import org.project.student.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("All")
public class StudentDao {
    private static final Logger logger = LoggerFactory.getLogger(StudentDao.class);

    public Student findByUsn(String usn) {
        Transaction transaction = null;
        Student student = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Student> query = session.createQuery("FROM Student WHERE usn = :usn", Student.class);
            query.setParameter("usn", usn);
            student = query.uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding student by USN: {}", e.getMessage(), e);
        }

        return student;
    }

    public Student validateLogin(String usn, String password) {
        Student student = findByUsn(usn);

        if (student != null && password.equals(student.getPassword())) {
            return student;
        }

        return null;
    }

    public void saveStudent(Student student) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.saveOrUpdate(student);

            transaction.commit();
            logger.info("Student saved successfully: {}", student);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving student: {}", e.getMessage(), e);
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            students = session.createQuery("FROM Student", Student.class).list();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error getting all students: {}", e.getMessage(), e);
        }

        return students;
    }

    public boolean initialize() {
        // Check if any student exists, if not create a sample student
        long studentCount = countStudents();

        if (studentCount == 0) {
            Student sampleStudent = new Student("1MS17CS001", "Sample Student", "password", "sample@example.com");
            saveStudent(sampleStudent);
            logger.info("Sample student created with USN: 1MS17CS001 and password: password");
            return true;
        }

        return false;
    }

    private long countStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(s) FROM Student s", Long.class).uniqueResult();
        } catch (Exception e) {
            logger.error("Error counting students: {}", e.getMessage(), e);
            return 0;
        }
    }
}