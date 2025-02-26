package org.project.student.dao;



import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.project.student.config.HibernateUtil;
import org.project.student.entity.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
public class SubjectDao {
    private static final Logger logger = LoggerFactory.getLogger(SubjectDao.class);

    public Subject findByCode(String code) {
        Transaction transaction = null;
        Subject subject = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Subject> query = session.createQuery("FROM Subject WHERE code = :code", Subject.class);
            query.setParameter("code", code);
            subject = query.uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding subject by code: {}", e.getMessage(), e);
        }

        return subject;
    }

    public void saveSubject(Subject subject) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.saveOrUpdate(subject);

            transaction.commit();
            logger.info("Subject saved successfully: {}", subject);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving subject: {}", e.getMessage(), e);
        }
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            subjects = session.createQuery("FROM Subject", Subject.class).list();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error getting all subjects: {}", e.getMessage(), e);
        }

        return subjects;
    }

    public boolean initialize() {
        long subjectCount = countSubjects();

        if (subjectCount == 0) {
            saveSubject(new Subject("CS101", "Computer Science Fundamentals", 100, 35));
            saveSubject(new Subject("MA101", "Engineering Mathematics", 100, 35));
            saveSubject(new Subject("PH101", "Engineering Physics", 100, 35));
            logger.info("Default subjects created");
            return true;
        }

        return false;
    }

    private long countSubjects() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(s) FROM Subject s", Long.class).uniqueResult();
        } catch (Exception e) {
            logger.error("Error counting subjects: {}", e.getMessage(), e);
            return 0;
        }
    }
}