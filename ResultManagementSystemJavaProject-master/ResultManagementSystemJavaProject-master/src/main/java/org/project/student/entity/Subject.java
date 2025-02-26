package org.project.student.entity;

import javax.persistence.*;
import java.io.Serializable;
@SuppressWarnings("All")
@Entity
@Table(name = "subjects")
public class Subject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "max_marks", nullable = false)
    private int maxMarks = 100;

    @Column(name = "pass_marks", nullable = false)
    private int passMarks = 35;

    public Subject() {
    }

    public Subject(String code, String name, int maxMarks, int passMarks) {
        this.code = code;
        this.name = name;
        this.maxMarks = maxMarks;
        this.passMarks = passMarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public int getPassMarks() {
        return passMarks;
    }

    public void setPassMarks(int passMarks) {
        this.passMarks = passMarks;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", maxMarks=" + maxMarks +
                ", passMarks=" + passMarks +
                '}';
    }
}