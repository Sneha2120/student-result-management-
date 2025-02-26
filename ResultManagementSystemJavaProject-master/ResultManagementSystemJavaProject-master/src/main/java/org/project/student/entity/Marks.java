package org.project.student.entity;
import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("All")
@Entity
@Table(name = "marks")
public class Marks implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "marks_obtained", nullable = false)
    private int marksObtained;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    public Marks() {
    }

    public Marks(Result result, Subject subject, int marksObtained) {
        this.result = result;
        this.subject = subject;
        this.marksObtained = marksObtained;
        this.status = marksObtained >= subject.getPassMarks() ? "PASS" : "FAIL";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(int marksObtained) {
        this.marksObtained = marksObtained;
        this.status = marksObtained >= subject.getPassMarks() ? "PASS" : "FAIL";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Marks{" +
                "id=" + id +
                ", subject=" + subject.getName() +
                ", marksObtained=" + marksObtained +
                ", status='" + status + '\'' +
                '}';
    }
}