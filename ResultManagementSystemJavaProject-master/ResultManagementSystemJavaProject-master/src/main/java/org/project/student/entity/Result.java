package org.project.student.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
@Entity
@Table(name = "results")
public class Result implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "semester", nullable = false)
    private int semester;

    @Column(name = "total_marks")
    private int totalMarks;

    @Column(name = "status", length = 10)
    private String status;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Marks> marksList = new ArrayList<>();

    public Result() {
    }

    public Result(Student student, int semester) {
        this.student = student;
        this.semester = semester;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Marks> getMarksList() {
        return marksList;
    }

    public void setMarksList(List<Marks> marksList) {
        this.marksList = marksList;
    }

    public void addMarks(Marks marks) {
        marksList.add(marks);
        marks.setResult(this);
    }

    public void removeMarks(Marks marks) {
        marksList.remove(marks);
        marks.setResult(null);
    }

    public void calculateResult() {
        totalMarks = 0;
        boolean hasFailed = false;

        for (Marks marks : marksList) {
            totalMarks += marks.getMarksObtained();
            if ("FAIL".equals(marks.getStatus())) {
                hasFailed = true;
            }
        }

        status = hasFailed ? "FAIL" : "PASS";
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", student=" + student.getName() +
                ", semester=" + semester +
                ", totalMarks=" + totalMarks +
                ", status='" + status + '\'' +
                '}';
    }
}