package com.rev.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SECURITY_QUESTIONS")
public class SecurityQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USER_ID", nullable = false)
    private int userId;

    private String question;

    @Column(name = "ANSWER_HASH", nullable = false)
    private String answerHash;

    public SecurityQuestion() {}

    public SecurityQuestion(int userId, String question, String answerHash) {
        this.userId = userId;
        this.question = question;
        this.answerHash = answerHash;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswerHash() {
        return answerHash;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswerHash(String answerHash) {
        this.answerHash = answerHash;
    }
}
