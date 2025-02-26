package org.project.student.dao;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.project.student.config.HibernateUtil;
import org.project.student.entity.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("All")
public class AdminDao {
    private static final Logger logger = LoggerFactory.getLogger(AdminDao.class);

    public Admin findByUsername(String username) {
        Transaction transaction = null;
        Admin admin = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Admin> query = session.createQuery("FROM Admin WHERE username = :username", Admin.class);
            query.setParameter("username", username);
            admin = query.uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding admin by username: {}", e.getMessage(), e);
        }

        return admin;
    }

    public Admin validateLogin(String username, String password) {
        Admin admin = findByUsername(username);

        if (admin != null && password.equals(admin.getPassword())) {
            return admin;
        }

        return null;
    }

    public void saveAdmin(Admin admin) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.saveOrUpdate(admin);

            transaction.commit();
            logger.info("Admin saved successfully: {}", admin);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving admin: {}", e.getMessage(), e);
        }
    }

    public boolean initialize() {
        long adminCount = countAdmins();

        if (adminCount == 0) {
            Admin defaultAdmin = new Admin("admin", "admin123");
            saveAdmin(defaultAdmin);
            logger.info("Default admin created with username: admin and password: admin123");
            return true;
        }

        return false;
    }

    private long countAdmins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(a) FROM Admin a", Long.class).uniqueResult();
        } catch (Exception e) {
            logger.error("Error counting admins: {}", e.getMessage(), e);
            return 0;
        }
    }
}