package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;
import lombok.extern.apachecommons.CommonsLog;

import java.time.LocalDate;

@Entity
@Table(name = "\"warehouse_worker_tasks\"")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="user_id")
    private int userId;
    @Column(name="task_date")
    private LocalDate taskDate;
    @Column(name="deadline")
    private LocalDate deadline;
    @Column(name = "content")
    private String taskContent;
    @Column(name = "if_finished")
    private int ifFinished;
    @Column(name = "remark")
    private String remark;
    @Version
    private Integer version;
    // -------------------- 构造方法 --------------------


    public Task() {
    }

    public Task(int id, int userId, LocalDate taskDate, LocalDate deadline, String taskContent, int ifFinished, String remark) {
        this.id = id;
        this.userId = userId;
        this.taskDate = taskDate;
        this.deadline = deadline;
        this.taskContent = taskContent;
        this.ifFinished = ifFinished;
        this.remark = remark;
    }

    public Task(int id, int userId, LocalDate taskDate, LocalDate deadline, String taskContent, int ifFinished, String remark, Integer version) {
        this.id = id;
        this.userId = userId;
        this.taskDate = taskDate;
        this.deadline = deadline;
        this.taskContent = taskContent;
        this.ifFinished = ifFinished;
        this.remark = remark;
        this.version = version;
    }
    // -------------------- Getter 和 Setter --------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(LocalDate taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public int getIfFinished() {
        return ifFinished;
    }

    public void setIfFinished(int ifFinished) {
        this.ifFinished = ifFinished;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
