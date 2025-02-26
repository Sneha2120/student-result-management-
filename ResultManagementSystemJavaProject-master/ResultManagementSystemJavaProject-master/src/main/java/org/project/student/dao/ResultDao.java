package org.project.student.dao;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.project.student.config.HibernateUtil;
import org.project.student.entity.Marks;
import org.project.student.entity.Result;
import org.project.student.entity.Student;
import org.project.student.entity.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
public class ResultDao {
    private static final Logger logger = LoggerFactory.getLogger(ResultDao.class);

    public Result findByStudentAndSemester(Student student, int semester) {
        Transaction transaction = null;
        Result result = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Result> query = session.createQuery(
                    "FROM Result WHERE student.id = :studentId AND semester = :semester",
                    Result.class
            );
            query.setParameter("studentId", student.getId());
            query.setParameter("semester", semester);
            result = query.uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding result by student and semester: {}", e.getMessage(), e);
        }
        return  result;
    }

    public void saveResult(Result result) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            result.calculateResult();
            session.saveOrUpdate(result);

            transaction.commit();
            logger.info("Result saved successfully: {}", result);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving result: {}", e.getMessage(), e);
        }
    }

    public List<Result> findByStudent(Student student) {
        List<Result> results = new ArrayList<>();
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Result> query = session.createQuery(
                    "FROM Result WHERE student.id = :studentId",
                    Result.class
            );
            query.setParameter("studentId", student.getId());
            results = query.list();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding results by student: {}", e.getMessage(), e);
        }

        return results;
    }

    public Result createResult(Student student, int semester, List<Subject> subjects, List<Integer> marksObtained) {
        Transaction transaction = null;
        Result result = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            result = findByStudentAndSemester(student, semester);

            if (result == null) {
                result = new Result(student, semester);
            } else {
                result.getMarksList().clear();
            }

            for (int i = 0; i < subjects.size(); i++) {
                Subject subject = subjects.get(i);
                int marks = marksObtained.get(i);

                Marks newMarks = new Marks(result, subject, marks);
                result.addMarks(newMarks);
            }

            result.calculateResult();

            session.saveOrUpdate(result);

            transaction.commit();
            logger.info("Result created/updated successfully: {}", result);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error creating/updating result: {}", e.getMessage(), e);
            result = null;
        }

        return result;
    }
}
